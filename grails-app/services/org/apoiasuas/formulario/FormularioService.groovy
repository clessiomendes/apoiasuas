package org.apoiasuas.formulario

import fr.opensagres.xdocreport.document.registry.XDocReportRegistry
import fr.opensagres.xdocreport.template.TemplateEngineKind
import grails.transaction.Transactional
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.CidadaoService
import org.apoiasuas.cidadao.Endereco
import org.apoiasuas.cidadao.Familia
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

    public static final Date ANO_CEM = Date.parse("dd/MM/yyyy", "01/01/100")

    CidadaoService cidadaoService
    SegurancaService segurancaService
    def familiaService

    public List<Formulario> getFormulariosDisponiveis() {
        return Formulario.list().findAll({it.formularioPreDefinido.habilitado}).sort({ it.nome })
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
        formulario.enderecoEquipamento = segurancaService.servicoLogado?.endereco?.enderecoCompleto
        formulario.telefoneEquipamento = segurancaService.servicoLogado?.telefone
    }

    @Transactional
    public ReportDTO prepararImpressao(Formulario formulario) {
//        Hibernate.initialize(formulario.template)
//        Hibernate.initialize(formulario.campos)

        ReportDTO result = new ReportDTO()
        result.nomeArquivo = formulario.geraNomeArquivo()

// 1) Load doc file and set Velocity template engine and cache it to the registry
        InputStream template = new ByteArrayInputStream(formulario.template);
        result.report = XDocReportRegistry.getRegistry().loadReport(template, TemplateEngineKind.Velocity);

// 2) Create Java model context
        result.context = result.report.createContext();
        result.fieldsMetadata = result.report.createFieldsMetadata();

        valoresFixos(formulario)
        result.formularioEmitido = registraEmissao(formulario) //chamar antes de transferir conteudo
        transfereConteudo(formulario, result)
        eventoPosEmissao(formulario)

        return result
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
//        Hibernate.initialize(formulario.campos)
        formulario.cidadao = idCidadao ? Cidadao.get(idCidadao) : null
        if (formulario.cidadao) {
            Hibernate.initialize(formulario.cidadao.familia)
            Hibernate.initialize(formulario.cidadao.familia.endereco)
        } else {            //Se nenhum cidadao foi selecionado,
            formulario.cidadao = new Cidadao()
            formulario.cidadao.familia = familiaService.obtemFamilia(idFamilia)
            if (formulario.cidadao.familia) {   //Nao selecionou cidadao mas selecionou familia
                Hibernate.initialize(formulario.cidadao.familia.endereco)
            } else {        //Se nenhuma familia foi selecionada
                formulario.cidadao.familia = new Familia()
                formulario.cidadao.familia.endereco = new Endereco()
            }
        }
        formulario.dataPreenchimento = new Date()
//        formulario.nomeResponsavelPreenchimento = segurancaService.getUsuarioLogado().nomeCompleto

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
        formularioEmitido.familia = formulario.cidadao?.familia ? Familia.get(formulario.cidadao?.familia?.id) : null
        formularioEmitido.cidadao = formulario.cidadao ? Cidadao.get(formulario.cidadao.id) : null

        //Caso não seja preenchida uma data explicitamente pelo operador, assumir a data atual:
        formularioEmitido.dataPreenchimento = formulario.dataPreenchimento ?: new Date()
//        formularioEmitido.responsavelPreenchimento = TODO: obter usuario selecionado

        //Limpa campos antigos (em caso de ser uma reemissao)
        if (formularioEmitido.campos)
            formularioEmitido.campos.each { it.delete() }
        formularioEmitido.campos = []

        formulario.getCamposOrdenados(false).each { campoPrevisto ->
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

    /**
     * Verifica se todos os campos obrigatorios foram preenchidos
     */
    public boolean validarPreenchimento(Formulario formulario) {
        formulario.validate()
        Set<String> mensagensErro = new HashSet()
        formulario.campos.each { campo ->
            campo.validate()
            if (!campo.valorArmazenado && campo.obrigatorio) {
                campo.errors.reject(null)
                mensagensErro << "Campo obrigatório"
            }
            if (CampoFormulario.Tipo.DATA == campo.tipo) {
                if (((Date) campo.valorArmazenado)?.before(ANO_CEM)) {
                    campo.errors.reject(null)
                    mensagensErro.add("O ano deve ser preenchido com quatro dígitos")
                }
            }
        }
        mensagensErro.each { formulario.errors.reject(null, it) }
        return mensagensErro.size() == 0
    }

    @Transactional
    def inicializaFormularios(UsuarioSistema usuarioSistema) {

        PreDefinidos.values().each { enumForm ->
            try {
                Formulario formulario = Formulario.findByFormularioPreDefinido(enumForm)
                Closure closure = enumForm.definicaoFormulario.newInstance().run()
                Formulario transiente = new FormularioBuilder(closure).build()
                transiente.setFormularioPreDefinido(enumForm)
                if (formulario) { //atualiza formulário existente
                    formulario.campos?.each { it.delete() } //Apagar todos os filhos
                    formulario.campos?.clear()
/*
                    manterIdAntigo: {//sobrescreve todas as informacoes no BD exceto, obviamente, o id e a versao
                        //Mantendo o id (e a versao) para preservar eventuais relacionamentos com a instância anterior:
                        transiente.id = formulario.id
                        transiente.version = formulario.version
                        formulario = transiente.merge()
                    }
*/
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
                formulario.campos.each { it.save() }
                enumForm.instanciaPersistida = formulario
                //grava os campos gerando novos ids, mesmo que o formulario já exista
            } catch (Throwable t) {
                throw new RuntimeException("Erro inicializando formulário ${enumForm}. Abortando", t)
            }
        }
    }

/*
    public Formulario getFormularioPreDefinidoECampos(PreDefinidos codigo) {
        Formulario result = null
        if (codigo) {
            result = Formulario.findByFormularioPreDefinido(codigo)//Carrega colecao de campos
            Hibernate.initialize(result.campos)
        }
        return result
    }
*/

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
     * Grava permanentemente eventuais alteracoes no cidadao ou familia afetados pelo formulario
     */
    @Transactional
    public void gravarAlteracoes(Formulario formulario) {
        if (!formulario.cidadao.id)
            return

        Cidadao cidadaoPersistente = Cidadao.get(formulario.cidadao.id)
        formulario.campos.each { campo ->
            Object instanciaAfetada = {
                switch (campo.origem) {
                    case CampoFormulario.Origem.CIDADAO: return cidadaoPersistente
                    case CampoFormulario.Origem.FAMILIA: return cidadaoPersistente.familia
                    case CampoFormulario.Origem.ENDERECO: return cidadaoPersistente.familia.endereco
                    case CampoFormulario.Origem.AVULSO: return null
                }
            }.call()

            if (!instanciaAfetada)
                return //esse return pula para o proximo passo do closure formulario.campos.each{}

            def conteudoAnterior = instanciaAfetada."${campo.nomeCampoPersistente}"
            def novoContedudo = campo.valorArmazenado
            //FIXME https://github.com/clessiomendes/apoiasuas/issues/16
            log.debug("Comparando ${campo.caminhoCampo}: ${conteudoAnterior} => ${novoContedudo} ?")

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
}
