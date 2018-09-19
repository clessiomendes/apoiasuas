package org.apoiasuas.formulario

import fr.opensagres.xdocreport.document.registry.XDocReportRegistry
import fr.opensagres.xdocreport.template.TemplateEngineKind
import grails.transaction.Transactional
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.Endereco
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.cidadao.FamiliaService
import org.apoiasuas.formulario.builder.FormularioBuilder
import org.apoiasuas.seguranca.SegurancaService
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.Docx4jUtils
import org.apoiasuas.util.StringUtils
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.hibernate.Hibernate

@Transactional(readOnly = true)
class FormularioService {

    SegurancaService segurancaService
    FamiliaService familiaService

    public List<Formulario> getFormulariosDisponiveis() {
        return Formulario.list().findAll({it.formularioPreDefinido?.habilitado});//.sort({ it.nome })
    }

    public Formulario getFormulario(Long id, boolean carregaCampos = false) {
        Map fetchMap = [:];
        if (carregaCampos)
            fetchMap << [campos: 'join'];
        if (fetchMap)
            fetchMap = [fetch: fetchMap];
        return Formulario.findById(id, fetchMap);
    }

    private void valoresFixos(Formulario formulario) {
        formulario.nomeEquipamento = segurancaService.servicoLogado?.nome
        formulario.enderecoEquipamento = segurancaService.servicoLogado?.endereco?.obtemEnderecoCompleto()
        formulario.telefoneEquipamento = segurancaService.servicoLogado?.telefone
        formulario.emailEquipamento = segurancaService.servicoLogado?.email
        formulario.cidadeEquipamento = segurancaService.servicoLogado?.endereco?.municipio
        formulario.ufEquipamento = segurancaService.servicoLogado?.endereco?.UF
    }

    @Transactional
    public List<ReportDTO> prepararImpressao(Formulario formulario, Long idModelo) {

        ReportDTO reportDTO = new ReportDTO()
        reportDTO.nomeArquivo = formulario.geraNomeArquivo()

// 1) Load doc file and set Velocity template engine and cache it to the registry
        InputStream template = new ByteArrayInputStream(formulario.modelos.find {it.id == idModelo}.arquivo );
        reportDTO.report = XDocReportRegistry.getRegistry().loadReport(template, TemplateEngineKind.Velocity);

// 2) Create Java model context
        reportDTO.context = reportDTO.report.createContext();
        reportDTO.fieldsMetadata = reportDTO.report.createFieldsMetadata();

        valoresFixos(formulario)
        reportDTO.formularioEmitido = registraEmissao(formulario) //chamar antes de transferir conteudo
        transfereConteudo(formulario, reportDTO)
        eventoPosEmissao(formulario)

        return [reportDTO]
    }

    /**
     * Evento a ser implementado nas especializações para disparar outras açoes no sistema. Como gerar um processo de
     * pedido de certidao de nascimento, por exemplo.
     */
    @Transactional
    protected void eventoPosEmissao(Formulario formulario) {

    }

    @Transactional
    public Formulario preparaPreenchimentoFormulario(Long idFormulario, Long idCidadao, Long idFamilia) {
        if (!idFormulario)
            return null
        Formulario formulario = getFormulario(idFormulario, true);
        if (! formulario)
            return null
        if (idCidadao && idCidadao >= 0) {
            formulario.cidadao = Cidadao.get(idCidadao);
            formulario.familia = formulario.cidadao.familia;
            //TODO: precisa mesmo?
            Hibernate.initialize(formulario.familia)
            Hibernate.initialize(formulario.familia.endereco)
        } else if (idFamilia && idFamilia >= 0) {  //Se nenhum cidadao foi selecionado,
            formulario.familia = familiaService.obtemFamilia(idFamilia);
            //TODO: precisa mesmo?
            Hibernate.initialize(formulario.familia.endereco)
        }

        //TODO: precisa mesmo?
        if (! formulario.cidadao)
            formulario.cidadao = new Cidadao()
        if (! formulario.familia) {
            formulario.familia = new Familia()
            formulario.familia.endereco = new Endereco()
        }

        formulario.dataPreenchimento = new Date()

        FormularioEmitido formularioEmitido = new FormularioEmitido()
        formularioEmitido.descricao = formulario.nome
        formularioEmitido.operadorLogado = segurancaService.usuarioLogado
        formularioEmitido.servicoSistemaSeguranca = segurancaService.servicoLogado
        formularioEmitido.formularioPreDefinido = formulario.formularioPreDefinido
        formularioEmitido.save()

        formulario.formularioEmitido = formularioEmitido

        return formulario
    }

    @Transactional
    protected FormularioEmitido registraEmissao(Formulario formulario) {
        FormularioEmitido formularioEmitido = formulario.formularioEmitido
        formularioEmitido.familia = formulario.familia ? Familia.get(formulario.familia.id) : null
        formularioEmitido.cidadao = formulario.cidadao ? Cidadao.get(formulario.cidadao.id) : null

        //Caso não seja preenchida uma data explicitamente pelo operador, assumir a data atual:
        formularioEmitido.dataPreenchimento = formulario.dataPreenchimento ?: new Date()
//        formularioEmitido.responsavelPreenchimento = TODO: obter usuario selecionado

        //Limpa campos antigos (em caso de ser uma reemissao)
        if (formularioEmitido.campos)
            formularioEmitido.campos.each { it.delete() }
        formularioEmitido.campos = []

        formulario.getCamposOrdenados(false).each { CampoFormulario campoPrevisto ->
            CampoFormularioEmitido campoPreenchido = new CampoFormularioEmitido()
            formularioEmitido.campos << campoPreenchido
            campoPreenchido.formulario = formularioEmitido

            campoPreenchido.descricao = campoPrevisto.descricao
            if (campoPrevisto.valorArmazenado)
                campoPreenchido.conteudoImpresso = campoPrevisto.tipo.data ? ((Date)campoPrevisto.valorArmazenado).format("dd/MM/yyyy")
                    : campoPrevisto.valorArmazenado
            campoPreenchido.grupo = campoPrevisto.grupo
            campoPreenchido.save()
        }

        return formularioEmitido
    }

    /**
     * Metodo que transfere os valores preenchidos em "Formulario" para um mapa (mergeField -> valor a substituir) esperado pelo template
     */
    protected void transfereConteudo(Formulario formulario, ReportDTO reportDTO) {
        formulario.campos.each { campo ->
            def valor = campo.valorArmazenado
            if (campo.tipo == CampoFormulario.Tipo.DATA)
                valor = ((Date) valor)?.format("dd/MM/yyyy")
            //No campo RESPONSAVEL_PREENCHIMENTO, buscar o nome completo do usuario armazenado como transiente no formulario
            switch (campo.codigo) {
                case CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO:
                    valor = formulario.usuarioSistema?.nomeCompleto; break
                case CampoFormulario.CODIGO_MATRICULA_RESPONSAVEL_PREENCHIMENTO:
                    valor = formulario.usuarioSistema?.matricula; break
            }
            def chave = StringUtils.upperToCamelCase(campo.origem.toString()) + "." + campo.codigo
            //remove caracteres que não são interpretados corretamente no arquivo word (como TAB)
            if (valor && campo.getTipo() == CampoFormulario.Tipo.TEXTO) {
                String temp = valor;
                valor = temp.replaceAll("\\t", " ");
            }

            reportDTO.context.put(chave, valor)
        }
        log.debug(formulario)
    }

    @Transactional
    def inicializaFormularios(UsuarioSistema usuarioSistema) {
        log.debug("inicializando formularios")
        Formulario.executeUpdate("delete Formulario f where f.formularioPreDefinido is not null and f.formularioPreDefinido not in :pres", [pres: PreDefinidos.values()])
//        Formulario.findAllByFormularioPreDefinidoNotInList(PreDefinidos.values())*.delete();
        PreDefinidos.values().each { enumForm ->
            if (enumForm.habilitado) try {
                Formulario formulario = Formulario.findByFormularioPreDefinido(enumForm)
                Closure closure = enumForm.definicaoFormulario.newInstance().run()
                Formulario transiente = new FormularioBuilder(closure).build()
                transiente.setFormularioPreDefinido(enumForm)
                if (formulario) { //atualiza formulário existente
                    formulario.campos?.each { it.delete() } //Apagar todos os filhos
                    formulario.campos?.clear()
                    criarComNovoId: {
                        formulario.delete(flush: true)
                        formulario = transiente.save()
                    }
                    log.debug("Formulário " + enumForm + " reinicializado")
                } else if (! formulario) {
                    formulario = transiente.save()
                    log.debug("Formulário " + enumForm + " criado")
                } else {
                    log.debug("Formulário " + enumForm + " mantido sem alteracoes")
                }
/*
                ModeloFormulario modeloPadrao = ModeloFormulario.findByFormularioAndPadrao(formulario, true);
                if (! modeloPadrao) {
                    modeloPadrao = new ModeloFormulario(descricao: ModeloFormulario.DESCRICAO_PADRAO, formulario: formulario,
                            padrao: true, arquivo: transiente.template);
                    formulario.modelos << modeloPadrao;
                } else {
                    modeloPadrao.arquivo = transiente.template;
                }
                modeloPadrao.save();
*/
                formulario.campos.each { it.save() }
                enumForm.instanciaPersistida = formulario
                //grava os campos gerando novos ids, mesmo que o formulario já exista
            } catch (Throwable t) {
                log.error(t.message);
                throw new RuntimeException("Erro inicializando formulário ${enumForm}. Abortando", t)
            }
        }
    }

    public Formulario getFormularioPreDefinido(PreDefinidos codigo) {
        return Formulario.findByFormularioPreDefinido(codigo)
    }

    public WordprocessingMLPackage simularTemplate(Formulario formulario) {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage()
        MainDocumentPart mainPart = wordMLPackage.getMainDocumentPart()
        mainPart.addParagraphOfText(formulario.nome)
        if (formulario.descricao)
            mainPart.addParagraphOfText(formulario.descricao)
        mainPart.addParagraphOfText("A seguir, estão listados os campos disponíveis para o formulário. " +
                "Copie e cole cada campo da coluna 'conteúdo' abaixo no local desejado. " +
                "Um mesmo campo pode ser repetido em mais de um local no formulário.")

        List linhas = []
        linhas.add(['descrição', 'conteúdo'])
        formulario.camposOrdenados.each { campo ->
            def nome = "\$!" + StringUtils.upperToCamelCase(campo.origem.toString()) + "." + campo.codigo
            linhas.add([
                    campo.descricao,
                    Docx4jUtils.geraMergeField(nome)
            ]);
        }

        Docx4jUtils.adicionaTabela(linhas, mainPart)

        return wordMLPackage
    }

    /**
     * Verifica se algum dos campos preenchidos no formulario deve(m) ser atualizado(s) no cadastro
     * Retorna uma lista destes campos
     */
    public List camposAlterados(Formulario formulario) {
        List result = [];
        if (!formulario.familia?.id)
            return result;
        Familia familiaPersistente = Familia.get(formulario.familia.id)
        Cidadao cidadaoPersistente = formulario.cidadao.id ? Cidadao.get(formulario.cidadao.id) : null;

        formulario.campos.sort{ it.ordem }.each { campo ->
            Object instanciaAfetada = {
                switch (campo.origem) {
                    case CampoFormulario.Origem.CIDADAO: return cidadaoPersistente
                    case CampoFormulario.Origem.FAMILIA: return familiaPersistente
                    case CampoFormulario.Origem.ENDERECO: return familiaPersistente.endereco
                    case CampoFormulario.Origem.AVULSO: return null
                }
            }.call()

            if (!instanciaAfetada)
                return //esse return pula para o proximo passo do closure formulario.campos.each{}

            def conteudoAnterior = instanciaAfetada."${campo.nomeCampoPersistente}"
            def novoConteudo = campo.valorArmazenado
            //FIXME https://github.com/clessiomendes/apoiasuas/issues/16
            log.debug("Comparando ${campo.caminhoCampo}: ${conteudoAnterior} => ${novoConteudo} ?")

            boolean atualizarInstanciaPersistente
            if (!campo.isAtualizavel())
                atualizarInstanciaPersistente = false
            else if (CampoFormulario.Tipo.TELEFONE == campo.tipo)
                atualizarInstanciaPersistente = false //TODO: Pensar um caso de uso de inclusao de telefones (no BD) a ser usado na tela de preenchimento de formulario (e reutilizado em outros casos de uso)
            else if (novoConteudo == null)
                atualizarInstanciaPersistente = false
            else if ((! conteudoAnterior) && novoConteudo) { //vazio antes, preenchido depois
                atualizarInstanciaPersistente = true
                //Converte o novo conteudo para um formato String, necessario para ser exibido no HTML
                if (campo.tipo == CampoFormulario.Tipo.DATA)
                    novoConteudo = ((Date) novoConteudo).format("dd/MM/yyyy");
            }
            else if ((! conteudoAnterior) && (! novoConteudo)) //vazio antes, vazio depois
                atualizarInstanciaPersistente = false
            else {
                if (campo.tipo == CampoFormulario.Tipo.TEXTO) {
                    if (! conteudoAnterior.toString().equalsIgnoreCase(novoConteudo.toString()))
                        atualizarInstanciaPersistente = true;
                } else if (campo.tipo == CampoFormulario.Tipo.DATA) {
                    conteudoAnterior = ((Date) conteudoAnterior).format("dd/MM/yyyy");
                    //Converte o novo conteudo para um formato String, necessario para ser exibido no HTML
                    novoConteudo = ((Date) novoConteudo).format("dd/MM/yyyy");
                    if (conteudoAnterior  != novoConteudo)
                        atualizarInstanciaPersistente = true;
                }
            }
            if (atualizarInstanciaPersistente)
                result << [campoFormulario: campo, nomeCampoPersistente: campo.nomeCampoPersistente, conteudoAnterior: conteudoAnterior, novoContedudo: novoConteudo]
        }

        return result;
    }

     /**
     * Grava permanentemente eventuais alteracoes no cidadao ou familia afetados pelo formulario
     */
    @Transactional
    public void gravarAlteracoesAntigo(Formulario formulario) {
        //somente gravar se houver alguma familia associada
        if (! formulario.familia?.id)
            return;

        Cidadao cidadaoPersistente = Cidadao.get(formulario.cidadao.id)
        Familia familiaPersistente = Familia.get(formulario.familia.id)
        formulario.campos.sort{ it.ordem }.each { campo ->
            Object instanciaAfetada = {
                switch (campo.origem) {
                    case CampoFormulario.Origem.CIDADAO: return cidadaoPersistente
                    case CampoFormulario.Origem.FAMILIA: return familiaPersistente
                    case CampoFormulario.Origem.ENDERECO: return familiaPersistente.endereco
                    case CampoFormulario.Origem.AVULSO: return null
                }
            }.call()

            if (!instanciaAfetada)
                return //esse return pula para o proximo passo do closure formulario.campos.each{}

            def conteudoAnterior = instanciaAfetada."${campo.nomeCampoPersistente}"
            def novoContedudo = campo.valorArmazenado
            //FIXME https://github.com/clessiomendes/apoiasuas/issues/16
            log.debug("Comparando  ${campo.caminhoCampo}: ${conteudoAnterior} => ${novoContedudo} ?")

            boolean atualizarInstanciaPersistente
            if (!campo.isAtualizavel())
                atualizarInstanciaPersistente = false
            else if (CampoFormulario.Tipo.TELEFONE == campo.tipo)
                atualizarInstanciaPersistente = false //TODO: Pensar um caso de uso de inclusao de telefones (no BD) a ser usado na tela de preenchimento de formulario (e reutilizado em outros casos de uso)
            else if (novoContedudo == null)
                atualizarInstanciaPersistente = false
            else if (conteudoAnterior == null && novoContedudo != null)
                atualizarInstanciaPersistente = true
            else
                atualizarInstanciaPersistente = {
                    switch (campo.tipo) {
                        case CampoFormulario.Tipo.TEXTO: return !conteudoAnterior.toString().equalsIgnoreCase(novoContedudo.toString())
                        case CampoFormulario.Tipo.DATA: return ((Date) conteudoAnterior).format("dd/MM/yyyy") != ((Date) novoContedudo).format("dd/MM/yyyy")
                        default: return false
                    }
                }.call()

            new Date().clone()
            if (atualizarInstanciaPersistente) {
                log.debug("atualizado!")
                instanciaAfetada."${campo.nomeCampoPersistente}" = novoContedudo
            }
        }
    }

    /**
     * Grava permanentemente eventuais alteracoes no cidadao ou familia afetados pelo formulario
     */
    @Transactional
    public void gravarAlteracoes(Formulario formulario, List<CampoFormularioCommand> camposSelecionados) {
        //somente gravar se houver alguma familia associada
        if (! formulario.familia?.id)
            return;
        Cidadao cidadaoPersistente = formulario.cidadao?.id ? Cidadao.get(formulario.cidadao.id) : null;
        Familia familiaPersistente = Familia.get(formulario.familia.id);

        camposSelecionados.each { CampoFormularioCommand campoSelecionado ->
            if (! campoSelecionado?.id)
                return;

            CampoFormulario campo = formulario.campos.find{ it.id.toString() == campoSelecionado.id };
//
//        }
//        formulario.campos.sort{ it.ordem }.each { campo ->
            Object instanciaAfetada = {
                switch (campo.origem) {
                    case CampoFormulario.Origem.CIDADAO: return cidadaoPersistente
                    case CampoFormulario.Origem.FAMILIA: return familiaPersistente
                    case CampoFormulario.Origem.ENDERECO: return familiaPersistente.endereco
                    case CampoFormulario.Origem.AVULSO: return null
                }
            }.call()

            if (!instanciaAfetada)
                return //esse return pula para o proximo passo do closure formulario.campos.each{}

            def conteudoAnterior = instanciaAfetada."${campo.nomeCampoPersistente}"
//            def novoContedudo = campo.valorArmazenado
            def novoContedudo
            if (campo.tipo == CampoFormulario.Tipo.TEXTO) {
                novoContedudo = campoSelecionado.novoConteudo;
            } else if (campo.tipo == CampoFormulario.Tipo.DATA) {
                novoContedudo = new Date().parse('dd/MM/yyyy', campoSelecionado.novoConteudo.toString());
            }

            //FIXME https://github.com/clessiomendes/apoiasuas/issues/16
            boolean atualizarInstanciaPersistente
            if (!campo.isAtualizavel())
                atualizarInstanciaPersistente = false
            else if (CampoFormulario.Tipo.TELEFONE == campo.tipo)
                atualizarInstanciaPersistente = false //TODO: Pensar um caso de uso de inclusao de telefones (no BD) a ser usado na tela de preenchimento de formulario (e reutilizado em outros casos de uso)
            else if (conteudoAnterior && ! novoContedudo)
                atualizarInstanciaPersistente = true
            else if ((! conteudoAnterior) && novoContedudo)
                atualizarInstanciaPersistente = true
            else if (campo.tipo == CampoFormulario.Tipo.TEXTO) {
                    atualizarInstanciaPersistente = !conteudoAnterior.toString().equalsIgnoreCase(novoContedudo.toString());
                } else if (campo.tipo == CampoFormulario.Tipo.DATA) {
                    atualizarInstanciaPersistente = conteudoAnterior != novoContedudo
                } else atualizarInstanciaPersistente = false;

            if (atualizarInstanciaPersistente) {
                log.debug("atualizado !")
                instanciaAfetada."${campo.nomeCampoPersistente}" = novoContedudo
            }
        }
    }
}
