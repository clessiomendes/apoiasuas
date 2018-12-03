package org.apoiasuas.cidadao

import com.gc.iotools.stream.is.InputStreamFromOutputStream
import fr.opensagres.xdocreport.document.IXDocReport
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry
import fr.opensagres.xdocreport.template.IContext
import fr.opensagres.xdocreport.template.TemplateEngineKind
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata
import grails.transaction.Transactional
import groovy.json.JsonOutput
import groovy.json.StringEscapeUtils
import org.apoiasuas.ApoiaSuasService
import org.apoiasuas.CustomizacoesService
import org.apoiasuas.cidadao.detalhe.CampoDetalhe
import org.apoiasuas.cidadao.detalhe.CampoDetalheLookup
import org.apoiasuas.cidadao.detalhe.CampoDetalheMultiLookup
import org.apoiasuas.cidadao.detalhe.ErrosCidadao
import org.apoiasuas.cidadao.detalhe.ErrosFamilia
import org.apoiasuas.cidadao.detalhe.MensagensErro
import org.apoiasuas.formulario.FormularioEmitido
import org.apoiasuas.formulario.ReportDTO
import org.apoiasuas.lookup.DetalhesJSON
import org.apoiasuas.marcador.Acao
import org.apoiasuas.marcador.AcaoFamilia
import org.apoiasuas.marcador.AssociacaoMarcador
import org.apoiasuas.marcador.OutroMarcador
import org.apoiasuas.marcador.OutroMarcadorFamilia
import org.apoiasuas.marcador.Vulnerabilidade
import org.apoiasuas.marcador.VulnerabilidadeFamilia
import org.apoiasuas.processo.PedidoCertidaoProcessoDTO
import org.apoiasuas.marcador.Programa
import org.apoiasuas.marcador.ProgramaFamilia
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.AuditoriaService
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.ambienteExecucao.AmbienteExecucao
import org.apoiasuas.util.CollectionUtils
import org.apoiasuas.util.SimNao
import org.apoiasuas.util.StringUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.hibernate.Hibernate
import org.springframework.core.io.Resource
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib

@Transactional(readOnly = true)
class FamiliaService {

    public static final int MAX_AUTOCOMPLETE_LOGRADOUROS = 10
    public static final String TEMPLATE_PLANO_ACOMPANHAMENTO = "/org/apoiasuas/report/TemplatePlanoAcompanhamento.docx";
    public static final String TEMPLATE_CADASTRO_FAMILIAR = "/org/apoiasuas/report/TemplateCadastroFamiliar.docx";
    public static final String TEMPLATE_CADASTRO_FAMILIAR_CRJ = "/org/apoiasuas/report/TemplateCadastroFamiliarCRJ.docx";
    public static final String TEMPLATE_CADASTRO_FAMILIAR_MEMBRO = "/org/apoiasuas/report/TemplateCadastroFamiliar-Membro.docx";
    public static final String TEMPLATE_CADASTRO_FAMILIAR_MEMBRO_CRJ = "/org/apoiasuas/report/TemplateCadastroFamiliar-MembroCRJ.docx";

    def segurancaService
    def cidadaoService
    def pedidoCertidaoProcessoService
    def messageSource
    def marcadorService
    def servicoSistemaService
    //Toda chamada a um servico no escopo de sessao ou de request aa partir de um servico de escopo singleton (ou de uma taglib) precisa passar por um proxy (já chamadas de controllers, nao precisam)
    def customizacoesServiceProxy
    ApoiaSuasService apoiaSuasService
    AuditoriaService auditoriaService

    @Transactional
    public Familia gravaNovo(Familia familia, Cidadao novaReferenciaFamiliar) {
//        if (familia.codigoLegado == null && segurancaService.servicoLogado.acessoSeguranca.identificacaoPeloCodigoLegado)
//            familia.codigoLegado = servicoSistemaService.proximoCodigoLegado();

        novaReferenciaFamiliar.familia = familia;
        familia.membros.add(novaReferenciaFamiliar);
        Familia result = grava(familia, null, null, null, null);
        novaReferenciaFamiliar.save();
        gravaTelefones(familia);
        return result;
    }

    @Transactional
    public Familia gravaFamiliaEMembros(Familia familia) {
        log.debug("gravaFamiliaEMembros ${familia.id}")
        familia.membros.each { Cidadao membro ->
            membro.familia = familia;
            membro.save();
        }
        return grava(familia, null, null, null, null);
    }

    @Transactional
    public Familia grava(Familia familia, MarcadoresCommand programasCommand, MarcadoresCommand acoesCommand,
                         MarcadoresCommand vulnerabilidadesCommand, MarcadoresCommand outrosMarcadoresCommand) {
        marcadorService.gravaMarcadoresFamilia(programasCommand, familia.programas, familia, Programa.class, ProgramaFamilia.class);
        marcadorService.gravaMarcadoresFamilia(vulnerabilidadesCommand, familia.vulnerabilidades, familia, Vulnerabilidade.class, VulnerabilidadeFamilia.class);
        marcadorService.gravaMarcadoresFamilia(acoesCommand, familia.acoes, familia, Acao.class, AcaoFamilia.class);
        marcadorService.gravaMarcadoresFamilia(outrosMarcadoresCommand, familia.outrosMarcadores, familia, OutroMarcador.class, OutroMarcadorFamilia.class);

/*
        //Detectando mudanca no tecnico de referencia: AUDITAR
        if (familia.isDirty('tecnicoReferencia'))
            auditoriaService.registraTecnicoFamilia(familia, familia.getPersistentValue('tecnicoReferencia'), familia.tecnicoReferencia);
*/

        familia.acompanhamentoFamiliar?.save();
//        familia.errors.reject("some.error.code");
        familia.save();
        familia.membros?.each { cidadaoService.grava(it) };
        gravaTelefones(familia);
        return familia;
    }

    @Transactional
    public boolean apaga(Familia familia) {
        //Necessario remover primeiro os programas, pois estes geram novos registros de auditoria
        Set<ProgramaFamilia> tempProgramas = familia.programas.collect()
        tempProgramas.each {
            familia.programas.remove(it);
            //forcar remocao no banco de dados
            it.delete(flush: true);
        }
        //Na sequencia, atualizar a instancia de familia para obter as novas auditorias geradas
        familia.refresh();
        //Apagar um a um os registros de auditoria antes de apagar a familia
        Set<Auditoria> tempAuditoria = familia.auditoria.collect()
        tempAuditoria.each {
            familia.auditoria.remove(it);
            it.delete(flush: true);
        }
        //remover formularios emitidos associados aa familia (não são cascateados automaticamente)
        FormularioEmitido.findAllByFamilia(familia).each {
            it.delete();
        }
        //Finalmente, apagar a familia e todas as demais colecoes associadas
        familia.delete()
        return true
    }

    @Transactional(readOnly = true)
    List procurarLogradouros(String logradouro) {
        if (!logradouro)
            return []
        //HQL Busca todos os logradouros, primeiro os mais usados
        String hql = 'select a.endereco.nomeLogradouro from Familia a ' +
                'where lower(remove_acento(a.endereco.nomeLogradouro)) like remove_acento(:logradouro) ' +
                'group by a.endereco.nomeLogradouro ' +
                'order by count(*) desc ';
        String logradouroInicia = logradouro.toLowerCase() + '%'
        String logradouroContem = '%' + logradouro.toLowerCase() + '%'

        //Procura logradouros INICIANDO com o texto digitado
        //Usar um LinkedHashSet garante que os resultados nao se repitam
        Set logradouros = new LinkedHashSet(Familia.executeQuery(hql, [logradouro: logradouroInicia]))

        //se existem menos de 10, procura logradouros CONTENDO o texto digitado
        if (logradouros.size() < MAX_AUTOCOMPLETE_LOGRADOUROS) {
            Iterator<String> logradourosContem = Familia.executeQuery(hql, [logradouro: logradouroContem]).iterator()
            while (logradouros.size() < MAX_AUTOCOMPLETE_LOGRADOUROS && logradourosContem.hasNext())
                logradouros << logradourosContem.next()
        }

        return new ArrayList(logradouros)

    }

    @Transactional(readOnly = true)
    public boolean testaAcessoDominio(Familia familia) {
        log.debug("Testando acesso  aa familia "+familia.id)
        //Restringir acesso apenas ao servicoSistema que criou a familia
        if (familia.servicoSistemaSeguranca && segurancaService.getServicoLogado() &&
                familia.servicoSistemaSeguranca.id != segurancaService.getServicoLogado().id)
            return false
        return true;
    }

    public Set<String> getNotificacoes(Long idFamilia, Locale locale) {
        if (!idFamilia)
            return []
        Set<String> result = []
        Familia familia = Familia.get(idFamilia);

        //testa se a familia eh acompanhada por algum tecnico
        if (familia.tecnicoReferencia)
            result << messageSource.getMessage("notificacao.familia.acompanhada", [familia.tecnicoReferencia.username].toArray(), locale);

        //testa idades voltadas ao SCFV
        familia.getMembrosOrdemPadrao(true).each { Cidadao cidadao ->
            if (cidadao.idade && cidadao.idade < 7)
                result << messageSource.getMessage("notificacao.familia.SCFV.0a6", null, locale);
            if (cidadao.idade && cidadao.idade >= 60)
                result << messageSource.getMessage("notificacao.familia.SCFV.idosos", null, locale);
        }

        List<PedidoCertidaoProcessoDTO> pedidosCertidaoPendentes = pedidoCertidaoProcessoService.pedidosCertidaoPendentes(familia.id)
        if (pedidosCertidaoPendentes)
            result << messageSource.getMessage("notificacao.familia.pedidosCertidao", null, locale);

        return result
    }

    @Transactional(readOnly = true)
    public ReportDTO emitePlanoAcompanhamento(Familia familia) {
        ReportDTO result = new ReportDTO();
        result.nomeArquivo = "PlanoAcompanhamentoCad${familia.cad}.docx"

// 1) Load doc file and set Velocity template engine and cache it to the registry
        InputStream stream = this.class.getResourceAsStream(TEMPLATE_PLANO_ACOMPANHAMENTO)
//        InputStream stream = new ByteArrayInputStream(template2);
        result.report = XDocReportRegistry.getRegistry().loadReport(stream, TemplateEngineKind.Velocity);

// 2) Create Java model context
        result.context = result.report.createContext();
        result.fieldsMetadata = result.report.createFieldsMetadata();

        UsuarioSistema tecnico = familia.tecnicoReferencia;
        String tecnicoReferencia = "";
        if (tecnico)
            tecnicoReferencia = tecnico.nomeCompleto + (tecnico.matricula ? " ($tecnico.matricula)" : "")
        //define um mapa de pares chave/conteudo cujas CHAVES são buscadas como FIELDS no template do word e substiuídas
        // pelo conteúdo correspondente. Esse mapa é transferido na sequência para o CONTEXTO do mecanismo de geração do .doc
        [
                cras               : familia.servicoSistemaSeguranca?.nome,
                codigo_legado      : familia.cad,
                referencia_familiar: familia.getReferencia()?.nomeCompleto,
                nis_referencia     : familia.getReferencia()?.nis,
                tecnico_referencia : tecnicoReferencia,
                composicao_familiar: membrosToString(familia),
                endereco           : familia.endereco.obtemEnderecoCompleto(),
                ingresso           : familia.acompanhamentoFamiliar?.dataInicio?.format("dd/MM/yyyy"),
                encerramento       : familia.acompanhamentoFamiliar?.dataFim?.format("dd/MM/yyyy"),
                analise_tecnica    : familia.acompanhamentoFamiliar?.analiseTecnica,
                resultados         : familia.acompanhamentoFamiliar?.resultados,
                telefones          : familia.telefonesToString,
                vulnerabilidades   : marcadoresToString(familia.vulnerabilidadesHabilitadas, "\n", "- "),
                programas          : marcadoresToString(familia.programasHabilitados, "\n", "- "),
                acoes              : marcadoresToString(familia.acoesHabilitadas, "\n", "- "),
                monitoramentos     : monitoramentosToString(familia.monitoramentos)
        ].each { chave, conteudo ->
            result.context.put(chave, conteudo);
        }
        return result;
    }

    /**
     * Devolve uma lista de membros familiares, com o separador de campos e de linhas especificado
     */
    private String membrosToString(Familia familia, String separadorCampos = ", ", String separadorLinhas = "\n") {
        List<String> dadosCidadao = [];
        familia.getMembrosOrdemPadrao(true).each { cidadao ->
            if (cidadao.nomeCompleto)
                dadosCidadao << CollectionUtils.join(["- " + cidadao.nomeCompleto,
                                                      cidadao.parentescoReferencia,
                                                      cidadao.dataNascimento ? "nascimento: " + cidadao.dataNascimento.format("dd/MM/YYYY") : null
                ], separadorCampos);
        };
        return CollectionUtils.join(dadosCidadao, separadorLinhas);
    }

    private String marcadoresToString(Set<AssociacaoMarcador> associacaoMarcadores, String separador = ", ", String bulletList = "") {
        return CollectionUtils.join(associacaoMarcadores
                .sort { it.marcador.descricao?.toLowerCase() }
                .collect {
                    bulletList + it.marcador.descricao + (it.observacao ? ' - obs: '+it.observacao : '')
                }, separador)
    }

    private String monitoramentosToString(Set<Monitoramento> monitoramentos) {
        return CollectionUtils.join(monitoramentos
                .sort { it.dataCriacao }
                .collect {
            it.memo + "\n-> " + it.situacao
        }, "\n-----------------\n")
    }

    @Transactional
    public Familia gravaTelefones(Familia familia)
    {
        familia.telefones.each {
            it.save()
        }
        return familia.save()
    }

    @Transactional(readOnly = true)
    public Familia obtemFamilia(Long id, boolean carregaMembros = false, boolean carregaTelefones = false,
                                boolean carregaMarcadores = false, boolean carregaMonitoramentos = false) {
        Map fetchMap = [:];
        if (carregaMembros)
            fetchMap << [membros: 'join'];
        if (carregaTelefones)
            fetchMap << [telefones: 'join'];
        if (carregaMarcadores) {
            fetchMap << [programas: 'join'];
            fetchMap << [acoes: 'join'];
            fetchMap << [outrosMarcadores: 'join'];
            fetchMap << [vulnerabilidades: 'join'];
        }
        if (carregaMonitoramentos)
            fetchMap << [monitoramentos: 'join'];
        if (fetchMap)
            fetchMap = [fetch: fetchMap];
        return Familia.findById(id, fetchMap);
    }

    @Transactional(readOnly = true)
    public Familia novaFamilia(Familia familia = new Familia()) {
        familia.servicoSistemaSeguranca = segurancaService.servicoLogado;
        familia.criador = segurancaService.usuarioLogado;
        familia.ultimoAlterador = segurancaService.usuarioLogado;
        familia.situacaoFamilia = SituacaoFamilia.CADASTRADA;
        return familia;
    }

    @Transactional(readOnly = true)
    public List<ReportDTO> emiteFormularioCadastro(Familia familia) {
        List<ReportDTO> reports = []
        reports << cadastroFamiliar.folhaCadastroFamilia(familia);
        familia.getMembrosOrdemPadrao(true).each {
            reports << cadastroFamiliar.folhaCadastroCidadao(it)
        }
        return reports
    }

    public String errosParaJson(Familia familia) {
        ValidationTagLib g = new ValidationTagLib()

        ErrosFamilia errosFamilia = new ErrosFamilia();
        //Primeiro preenche os erros globais da familia
        familia.errors.globalErrors.each { ObjectError error ->
            errosFamilia.errosGlobais.add(g.message(error: error))
        }

        //Depois preenche os erros para cada campo da familia (ignora dos membros)
        familia.errors.fieldErrors.findAll{! it.field.startsWith('membros[')}.each { FieldError error ->
            log.debug(error.objectName);
            MensagensErro m = errosFamilia.mapaCampos.get(error.field)
            if (! m)
                m = new MensagensErro();
            m.add(g.message(error: error, args: [error.field]))
            errosFamilia.mapaCampos.put(error.field, m)
        }

        //Depois preenche os erros para cada membro
        familia.membros.findAll{ it.hasErrors() }.each { Cidadao membro ->
            ErrosCidadao errosCidadao = new ErrosCidadao();
            membro.errors.globalErrors.each { ObjectError error ->
                errosCidadao.errosGlobais.add(g.message(error: error))
            }
            membro.errors.fieldErrors.each { FieldError error ->
                MensagensErro m = errosCidadao.mapaCampos.get(error.field)
                if (! m)
                    m = new MensagensErro();
                m.add(g.message(error: error, args: [error.field]))
                errosCidadao.mapaCampos.put(error.field, m)
            }
            errosFamilia.mapaMembros.put(membro.ord, errosCidadao)
        }

        //Depois preenche os erros para cada telefone
        familia.telefones.findAll{ it.hasErrors() }.each { Telefone telefone ->
            telefone.errors.globalErrors.each { ObjectError error ->
                errosFamilia.errosGlobais.add(g.message(error: error) + " em telefones")
            }
            telefone.errors.fieldErrors.each { FieldError error ->
                errosFamilia.errosGlobais.add(g.message(error: error) + " em telefones")
            }
        }

        return JsonOutput.toJson(errosFamilia)
    }

    // * Organizando os metodos privados específicos pra emissao do formulario de cadastro familiar
    public FamiliasSemCad familiasSemCad = new FamiliasSemCad();
    public class FamiliasSemCad {
        //lista as colunas da planilha de Banco de Dados dos CRAS de Belo Horizonte, que foram previamente formatadas como texto e, assim, dispensam a ncessidade de terem conteudos entre aspas
        private static final List FORMATOS_TEXTO_PLANILHA = [1/*B*/,13/*N*/, 44/*AS*/, 45/*AT*/];

        private Collection<Familia> geraCelulasParaPlanilha(Collection<Familia> familias) {

            int colunaInicialPlanilha = 0;

            familias.each { Familia familia ->
                log.debug(familiasSemCad.converteBool(familia.mapaDetalhes['aguaTratada'], "Rede pública encanada", "Outro"));

                //lista base das células contendo as infos do cadastro, que podem ser copiadas para uma planilha
                List<String> copyPasteFamilia = [];

                //Nº do Cadastro;
                copyPasteFamilia << '';

                //acrescenta uma coluna a mais para o CRAS Havai-Ventosa
                if (customizacoesServiceProxy.contem(CustomizacoesService.Codigos.BELO_HORIZONTE_HAVAI_VENTOSA)) {
                    //Unidade;
                    copyPasteFamilia << '';
                    //neste caso, considerar que a planilha começa com uma coluna a mais do que o normal (na verdade, é a segunda coluna)
                    colunaInicialPlanilha++;
                }

                //Inicia a montagem da planilha com os elementos comuns aa familia
                copyPasteFamilia.addAll([
                        //Data do Cadastro;Nome da referência;
                        familia.dateCreated.format('dd/MM/YYYY'), familia.referencia?.nomeCompleto,
                        //NIS da referência;Código Familiar no CADÚNICO;Logradouro;Nome do logradouro;
                        familia.referencia?.nis, '', familia.endereco?.tipoLogradouro, familia.endereco?.nomeLogradouro,
                        //Nº;Complemento;Bairro;CEP;Telefone;
                        familia.endereco?.numero, familia.endereco?.complemento, familia.endereco?.bairro, familia.endereco?.CEP, familia.telefonesToString,
                        //Moradia;Cômodos / Quartos;
                        familia.mapaDetalhes['propriedadeMoradia'], familiasSemCad.comodosEquartos(familia),
                        //Tipo de construção;Riscos;Barreira arquitetônica;
                        familia.mapaDetalhes['tipoConstrucao'], familia.mapaDetalhes['riscoConstrutivo'], '',
                        //Energia elétrica;Destino de Esgoto;
                        familia.mapaDetalhes['eletricidade'], familiasSemCad.converteBool(familia.mapaDetalhes['redeEsgoto'], 'Rede Pública', 'Outro'),
                        //Instalação Sanitária;
                        familiasSemCad.converteBool(familia.mapaDetalhes['banheiro'], "Própria", "Ausente"),
                        //Abastecimento de água;
                        familiasSemCad.converteBool(familia.mapaDetalhes['aguaTratada'], "Rede pública encanada", "Outro"),
                        //Destino do lixo;
                        familiasSemCad.converteBool(familia.mapaDetalhes['coletaLixo'], "Coleta domiciliar", "Outro"),
                        //Nº de integrantes;Nº PEA;Renda familiar Total;
                        familia.getMembrosOrdemPadrao(true).size() + "", "", familiasSemCad.calculaRenda(familia),
                        //Possui cadastro no CADÚNICO;B.F.;R$;Descumprimento BF;Mês Desc. BF;familiasSemCad.publicoPrioritario(familia, "3"), "",
                        "", familiasSemCad.publicoPrioritario(familia, "1"), "", familiasSemCad.publicoPrioritario(familia, "2"), "",
                        //familiasSemCad.publicoPrioritario(familia, "3"), "",Trabalho Infantil;PETI;Outros benefícios?;R$ Outros Benefícios;Notificação de Ocorrência;Encaminhado para PSE;
                        familiasSemCad.publicoPrioritario(familia, "3"), "", "", "", "", "", "", "",
                        //Deficiente na família?;Em serviço de Acolhimento?;Família em Acompanhamento;Data de inserção no Acompanhamento;Data de encerramento do Acompanhamento;
                        familiasSemCad.contemMembro(familia, "deficiencia"), familiasSemCad.contemMembro(familia, "institucionalizado"), "", "", "",
                        //Renda per capta;
                        getRendaPerCapita(familia)
                ]);
                //Gera, para cada membro, as planilhas definitivas (a primeira ate a data de nascimento e a segunda depois da data de nascimento)
                familia.getMembrosOrdemPadrao(true).each { Cidadao cidadao ->
                    List<String> copyPaste1Lst = copyPasteFamilia.clone()
                    if (!cidadao.referencia)
                        copyPaste1Lst[colunaInicialPlanilha+1] = ''; //apaga a data de criacao para os membros que nao a referencia familiar
                    //Grau de parentesco;Nome do integrante;
                    copyPaste1Lst << cidadao.parentescoReferencia;
                    copyPaste1Lst << cidadao.nomeCompleto;
                    List<String> copyPaste2Lst = [
                            //Sexo;Escolaridade;Ocupação;Tipo de deficiência;
                            cidadao.sexo?.descricao, cidadao.escolaridade?.descricao, cidadao.mapaDetalhes['situacaoTrabalho'], cidadao.mapaDetalhes['tiposDeficiencia'],
                            //Raça;Estado civil;Centro de Saúde;Instituição de Ensino;Atividade do CRAS
                            cidadao.mapaDetalhes['corRaca'], cidadao.mapaDetalhes['estadoCivil'], familia.mapaDetalhes['centroSaude'], cidadao.mapaDetalhes['escola'], ''
                    ];

                    int index = 0;
                    String copyPaste1Str = copyPaste1Lst.collect {
//                        if (FORMATOS_TEXTO_PLANILHA.contains((index++) - colunaInicialPlanilha/*desprezar as colunas indicadas anteriormente*/))
                            return StringEscapeUtils.escapeJavaScript(it ? (""+it) : "");
//                            return StringEscapeUtils.escapeJavaScript(""+it) ?: "";
//                        else
//                            return it ? '="' + it.toString() + '"' : "";
                    }.join("\\t");
                    index += 7/*pula campos da idade*/;
                    String copyPaste2Str = copyPaste2Lst.collect {
//                        if (FORMATOS_TEXTO_PLANILHA.contains((index++) - colunaInicialPlanilha/*desprezar as colunas indicadas anteriormente*/))
                            return StringEscapeUtils.escapeJavaScript(it ? (""+it) : "");
//                        else
//                            return it ? '="' + it.toString() + '"' : "";
                    }.join("\\t");

                    //Adiciona uma propriedade dinamica no objeto cidada a ser utilizada na geração da gsp
                    cidadao.metaClass.copyPaste1 = copyPaste1Str;
                    cidadao.metaClass.copyPaste2 = copyPaste2Str;
                }
            }
        }

        private String converteBool(CampoDetalhe campoDetalhe, String seSim, String seNao) {
            if (campoDetalhe?.notEmpty())
                return campoDetalhe?.asBoolean() ? seSim : seNao
            else
                return '';
        }

        private String calculaRenda(Familia familia) {
            Integer total = 0;
            familia.getMembrosOrdemPadrao(true).each { Cidadao cidadao ->
                if (cidadao.mapaDetalhes['rendaMensal']?.notEmpty() && cidadao.mapaDetalhes['rendaMensal']?.toString()?.isNumber())
                    total += Integer.parseInt(cidadao.mapaDetalhes['rendaMensal'].toString());
            }
            return total + "";
        }

        private String comodosEquartos(Familia familia) {
            if (! familia.mapaDetalhes['numeroComodos'] && ! familia.mapaDetalhes['numeroQuartos'])
                return ""
            else
                return familia.mapaDetalhes['numeroComodos']?.toString() + " / " + familia.mapaDetalhes['numeroQuartos']?.toString()
        }

        private String publicoPrioritario(Familia familia, String codigo) {
            CampoDetalheMultiLookup publico = familia.mapaDetalhes['publicoPrioritario']
            return publico?.contemCodigo(codigo) ? "Sim" : null
        }

        private String contemMembro(Familia familia, String campo) {
            return familia.getMembrosOrdemPadrao(true).find { it.mapaDetalhes[campo]?.asBoolean() } ? "Sim" : null
        }

        /**
         * Lista as familias sem codigo legado. Gera também um mapa de células para serem copiadas e coladas em uma planilha excel
         */
        public List<Familia> list(String nomeOuCad, Date dataCriacao, UsuarioSistema criador) {

            boolean buscaPorCad = nomeOuCad && ! StringUtils.PATTERN_TEM_LETRAS.matcher(nomeOuCad);
            Set<Familia> familias = [];
            if (buscaPorCad) {
                if (segurancaService.identificacaoCodigoLegado)
                    familias = [Familia.findByCodigoLegadoAndServicoSistemaSeguranca(nomeOuCad, segurancaService.servicoLogado)]
                else
                    familias = [Familia.findByIdAndServicoSistemaSeguranca(nomeOuCad, segurancaService.servicoLogado)];
            } else {//Pesquisa sem filtros ou com filtro por nome

                int max = 50;

                String filtroNome = nomeOuCad
                def filtrosHql = [:]

                String hqlList = "select f from Familia f inner join fetch f.membros a ";

                String hqlWhere = ' where f.servicoSistemaSeguranca = :servicoSistema ';
                filtrosHql << [servicoSistema: segurancaService.getServicoLogado()];

                if (! (filtroNome || dataCriacao) ) {
                    hqlWhere += ' and f.codigoLegado is null ';
                }

                if (filtroNome) {
//                    hqlWhere += ' and a.nomeCompleto like :nome ';
                    hqlWhere += " and lower(remove_acento(a.nomeCompleto)) like remove_acento(:nome)"
                    filtrosHql.put('nome', "%${filtroNome?.toLowerCase()}%");
/*
                    String[] nomes = filtroNome?.split(" ");
                    nomes?.eachWithIndex { nome, i ->
                        String label = 'nome'+i
                        hqlWhere += " and lower(remove_acento(a.nomeCompleto)) like remove_acento(:"+label+")"
                        filtrosHql.put(label, '%'+nome?.toLowerCase()+'%')
                    }
*/
                }

                if (criador) {
                    hqlWhere += ' and f.criador = :criador ';
                    filtrosHql.put('criador', criador);
                }

                if (dataCriacao) {
                    hqlWhere += ' and f.dateCreated >= :dataInicial and f.dateCreated < :dataFinal ';
                    filtrosHql.put('dataInicial', dataCriacao );
                    filtrosHql.put('dataFinal', dataCriacao + 1);
                }

                String hqlOrder = ' order by f.id';


//                int count = Cidadao.executeQuery(hqlCount + hqlWhere, filtrosHql)[0]
                familias = Familia.executeQuery(hqlList + hqlWhere + hqlOrder, filtrosHql, [max: max]);
                if (filtroNome)
                    familias.each {
                        it.refresh();
                    }
            }
            familiasSemCad.geraCelulasParaPlanilha(familias)
            return familias.toList();
        }
    }

    // * Organizando os metodos privados específicos pra emissao do formulario de cadastro familiar
    public CadastroFamiliar cadastroFamiliar = new CadastroFamiliar();
    public class CadastroFamiliar {

        protected InputStream pipeReport(ReportDTO report) {
            InputStreamFromOutputStream<Void> pipeStream;
            pipeStream = new InputStreamFromOutputStream<Void>() {

                @Override
                public Void produce(final OutputStream dataSink) throws Exception {
                    report.report.process(report.context, dataSink);
                }
            };

            return pipeStream;
        }

        protected ReportDTO folhaCadastroFamilia(Familia familia) {

    // 1) Load doc file and set Velocity template engine and cache it to the registry
            Resource resource;
            if (segurancaService.servicoLogado.token == ServicoSistema.Tokens.CRJ)
                resource = apoiaSuasService.obtemArquivo(TEMPLATE_CADASTRO_FAMILIAR_CRJ)
            else
                resource = apoiaSuasService.obtemArquivo(TEMPLATE_CADASTRO_FAMILIAR)
            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(resource.getInputStream(), TemplateEngineKind.Velocity);

    // 2) Create Java model context
            IContext context = report.createContext();
            FieldsMetadata metadata = new FieldsMetadata();
            report.setFieldsMetadata(metadata);

            UsuarioSistema tecnico = familia.tecnicoReferencia;
            String tecnicoReferencia = "";

            alimentaContextoReport(context, familia);

            if (tecnico)
                tecnicoReferencia = tecnico.nomeCompleto + (tecnico.matricula ? " ($tecnico.matricula)" : "");

            //define um mapa de pares chave/conteudo cujas CHAVES são buscadas como FIELDS no template do word e substiuídas
            // pelo conteúdo correspondente. Esse mapa é transferido na sequência para o CONTEXTO do mecanismo de geração do .doc
            [
                    codigoLegado            : cad(familia),
                    referenciaFamiliar      : familia.getReferencia()?.nomeCompleto,
                    enderecoBasico          : familia.endereco.obtemEnderecoBasico(),
                    bairro                  : familia.endereco?.bairro,
                    municipioUF             : StringUtils.concatena(" - ", familia.endereco?.municipio, familia.endereco?.UF),
                    CEP                     : familia.endereco?.CEP,
                    bolsaFamilia            : boolToStr(familia.bolsaFamilia),
                    exBolsaFamilia          : boolToStr(familia.exBolsaFamilia),
                    bpc                     : boolToStr(familia.bpc),
                    despesaTotal            : roundDoubleToStr(getDespesaTotal(familia)),
                    rendaTotal              : roundDoubleToStr(getRendaTotal(familia)),
                    rendaPerCapita          : roundDoubleToStr(getRendaPerCapita(familia)),
                    telefones               : familia.telefonesToString,
                    //ignora quando o criador for o administrador
                    responsavelCadastro     : familia.criador?.id == segurancaService.admin?.id ? '' : StringUtils.concatena(' / ', familia.criador.nomeCompleto, familia.criador.matricula),
                    dataCadastro            : familia.dateCreated?.format('dd/MM/yyyy'),
                    equipamento        : familia.servicoSistemaSeguranca?.nome
                    //adiciona todos os detalhes automaticamente ao mapa da familia
            ].each { chave, conteudo ->
                context.put(chave, conteudo);
            }

            //mesmo processo para cada membro familiar
            List membros = []
            metadata.addFieldAsList("membros");
            familia.getMembrosOrdemPadrao(true).each { Cidadao cidadao ->
                Map membro = [:];
    //            metadata.addFieldAsList("membros.nomeCompleto");
                membro.nomeCompleto = cidadao.nomeCompleto ?: '';
                membro.parentescoReferencia = cidadao.referencia ? "Ref. Familiar" : cidadao.parentescoReferencia ?: '';
                membro.dataNascimento = getDataNacimento(cidadao)  ?: '';
                membro.vulnerabilidades = vulnerabilidadesMembro(cidadao) ?: '';
                membros << (membro << cidadao.mapaDetalhes) ;
            }
            context.put("membros", membros);

            return new ReportDTO(report: report, context: context);
        }

        private String cad(Familia familia) {
            return familia.cad == Familia.NOVO_CAD ? '' : familia.cad
        }
/**
         * arredonda e converte para string. se nulo, converte para uma string vazia ''
         */
        private String roundDoubleToStr(Double aDouble) {
            return aDouble == null ? '' : new Long(Math.round(aDouble)).toString();
        }

        private String vulnerabilidadesMembro(Cidadao cidadao) {
            List vulnerabilidades = []
            vulnerabilidades << (cidadao.mapaDetalhes['deficiencia']?.asBoolean() ? 'deficiência' : null);
            vulnerabilidades << (cidadao.mapaDetalhes['doencaGrave']?.asBoolean() ? 'doença grave' : null);
            vulnerabilidades << (cidadao.mapaDetalhes['migrante']?.asBoolean() ? 'migrante' : null);
            vulnerabilidades << (cidadao.analfabeto ? 'analfabeto' : null);
            vulnerabilidades << (cidadao.mapaDetalhes['institucionalizado']?.asBoolean() ? 'acolh. instit.' : null);
            vulnerabilidades << (cidadao.mapaDetalhes['sistemaPrisional']?.asBoolean() ? 'recluso/egresso ' : null);
            vulnerabilidades << (cidadao.mapaDetalhes['medidaSocioEducativa']?.asBoolean() ? 'm.s.e.' : null);
            vulnerabilidades << (cidadao.mapaDetalhes['situacaoRua']?.asBoolean() ? 'sit. rua' : null);
            CampoDetalheMultiLookup violacoes = cidadao.mapaDetalhes['violacao'];
            vulnerabilidades << (violacoes?.contemCodigo("1") ? 'violência mulher' : null);
            vulnerabilidades << (violacoes?.contemCodigo("2") ? 'trabalho infantil' : null);
            vulnerabilidades << (violacoes?.contemCodigo("3") ? 'abuso criança' : null);
            vulnerabilidades << (violacoes?.contemCodigo("4") ? 'homofobia' : null);
            vulnerabilidades << (violacoes?.contemCodigo("5") ? 'racismo' : null);
            vulnerabilidades << (violacoes?.contemCodigo("6") ? 'violência idoso/deficiente' : null);
            return vulnerabilidades.findAll{it != null}.join(", ");
        }

        protected ReportDTO folhaCadastroCidadao(Cidadao cidadao) {

            // 1) Load Docx file by filling Velocity template engine and cache it to the registry
            Resource resource;
            if (segurancaService.servicoLogado.token == ServicoSistema.Tokens.CRJ)
                resource = apoiaSuasService.obtemArquivo(TEMPLATE_CADASTRO_FAMILIAR_MEMBRO_CRJ)
            else
                resource = apoiaSuasService.obtemArquivo(TEMPLATE_CADASTRO_FAMILIAR_MEMBRO)
            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(resource.getInputStream(), TemplateEngineKind.Velocity);

            // 2) Create fields metadata to manage lazy loop (#forech velocity) for table row.
    //        FieldsMetadata metadata = new FieldsMetadata();
    //        metadata.addFieldAsList("compromissos.horario");
    //        report.setFieldsMetadata(metadata);

            // 3) Create context Java model
            IContext context = report.createContext();
            try {
                alimentaContextoReport(context, cidadao);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            log.debug(context);

            //Por fim, personaliza alguns campos ou sobrescreve os campos definidos anteriormente
            [
                    codigoLegado            : cad(cidadao.familia),
                    nomeCompleto            : cidadao.nomeCompleto,
                    filiacao                : getFiliacao(cidadao),
                    dataNascimento          : getDataNacimento(cidadao),
                    identidade              : getIdentidade(cidadao),
                    tituloEleitor           : getTituloEleitor(cidadao),
                    certidao                : getCertidao(cidadao),
                    estudando               : getEstudando(cidadao),
                    analfabeto              : boolToStr(cidadao.analfabeto),
                    escolaridade            : cidadao.escolaridade?.descricao,
                    doencaGrave             : getDoencaGrave(cidadao),
                    naturalidadeUF          : StringUtils.concatena(" - ", cidadao.naturalidade, cidadao.UFNaturalidade),
                    orientacaoSexualOuOutra : getOrientacaoSexual(cidadao),
                    sexo                    : cidadao?.sexo?.descricao,
                    equipamento             : cidadao.familia.servicoSistemaSeguranca?.nome
            ].each { chave, conteudo ->
                context.put(chave, conteudo);
            }
            return new ReportDTO(report: report, context: context);
        }

        private String boolToStr(Boolean b) {
             return b == true ? SimNao.SIM.descricao : b == false ? SimNao.NAO.descricao : '';
        }

        private String getDoencaGrave(Cidadao cidadao) {
            if (cidadao.mapaDetalhes['doencaGrave'].asBoolean())
                return cidadao.mapaDetalhes['nomeDoenca'] ?: cidadao.mapaDetalhes['doencaGrave']
            else
                return cidadao.mapaDetalhes['doencaGrave']
        }

        private String getOrientacaoSexual(Cidadao cidadao) {
            if (((CampoDetalheLookup)cidadao.mapaDetalhes['orientacaoSexual']).outros())
                return cidadao.mapaDetalhes['outraOrientacao']
            else
                cidadao.mapaDetalhes['orientacaoSexual']?.toString()
        }

        private String getCertidao(Cidadao cidadao) {
            return StringUtils.concatena(', ', cidadao.mapaDetalhes['tipoCertidao'],
                    label("livro ",cidadao.mapaDetalhes['livroCertidao']),
                    label("folha ",cidadao.mapaDetalhes['folhaCertidao']),
                    label("termo ",cidadao.mapaDetalhes['termoCertidao']),
                    cidadao.mapaDetalhes['cartorioCertidao'],
                    cidadao.mapaDetalhes['municipioCertidao'],
                    cidadao.mapaDetalhes['ufCertidao']);
        }

        private String getEstudando(Cidadao cidadao) {
            if (cidadao.mapaDetalhes['estudando'].asBoolean() && cidadao.mapaDetalhes['eja'].asBoolean())
                return SimNao.SIM.descricao + " (EJA)"
            else
                return cidadao.mapaDetalhes['estudando'].toString();
        }

        private String label(String label, Object conteudo) {
            String result = conteudo?.toString()?.trim();
            if (result)
                result = label + result;
            return result;
        }

        private String getTituloEleitor(Cidadao cidadao) {
            return StringUtils.concatena(" ", cidadao.mapaDetalhes['numeroTituloEleitor'],
                    label('zona ', cidadao.mapaDetalhes['zonaTituloEleitor']),
                    label('seção ', cidadao.mapaDetalhes['secaoTituloEleitor']));
        }

        private String getIdentidade(Cidadao cidadao) {
            if (! cidadao.identidade)
                return null;
            String emissor = StringUtils.concatena(' ',cidadao.mapaDetalhes['emissorIdentidade'], cidadao.mapaDetalhes['ufEmissorIdentidade']);
            return StringUtils.concatena(' / ', cidadao.identidade, emissor);
        }

        private String getDataNacimento(Cidadao cidadao) {
            if (cidadao.dataNascimento)
                return cidadao.dataNascimento.format('dd/MM/yyyy')
            else if (cidadao.dataNascimentoAproximada)
                return cidadao.dataNascimentoAproximada.format('yyyy')+" (aproximada)"
            else
                return null;
        }

        protected void alimentaContextoReport(IContext context, DetalhesJSON dominio) {
            //Primeiro alimenta o mapa de campos para o relatorio automaticamente com todos os detalhes do membro
            dominio.mapaDetalhes.each { chave, conteudo ->
                context.put(chave, conteudo)
            };
            //Depois alimenta automaticamente todos os campos do dominio (Cidadao/Familia)
//            GrailsDomainClass dc = dominio.domainClass;
            GrailsDomainClass dc = new DefaultGrailsDomainClass(dominio.class)
            dc.persistentProperties.each { def p ->
                context.put(p.name, dominio.getAt(p.name)?.toString());
            }
        }

        private String getFiliacao(Cidadao cidadao) {
            List pais = []
            if (! cidadao.mapaDetalhes.maeDescohecida.asBoolean() && cidadao.nomeMae )
                pais << cidadao.nomeMae
            if (! cidadao.mapaDetalhes.paiDesconhecido.asBoolean() && cidadao.nomePai )
                pais << cidadao.nomePai
            return pais.join(", ");
        }

    }

    /**
     * Calcula a renda total da família, somando a renda de cada membro ativo, e considerando "sem renda" como zero.
     * Obs: Caso não haja nenhuma informação registrada para nenhum dos membros, retorna null e não zero.
     */
    @Transactional(readOnly = true)
    public Double getRendaTotal(Familia familia) {
        Double total = 0;
        boolean semInformacao = true;
        familia.getMembrosOrdemPadrao(true).each { Cidadao cidadao ->
            if (cidadao.mapaDetalhes['semRenda']?.asBoolean()) {
                semInformacao = false;
            } else if (cidadao.mapaDetalhes['rendaMensal']?.notEmpty() && cidadao.mapaDetalhes['rendaMensal'].toString().isNumber() ) {
                semInformacao = false;
                total += Double.parseDouble(cidadao.mapaDetalhes['rendaMensal'].toString());
            }
        }
        return semInformacao ? null : total;
    }

    /**
     * Calcula a despesa total da família, somando as despesas detalhadas.
     * Obs: Caso não haja nenhuma informação registrada de despesa, retorna null e não zero.
     */
    @Transactional(readOnly = true)
    public Double getDespesaTotal(Familia familia) {
        Double total = 0;
        boolean semInformacao = true;
        List despesas = ['despesaAluguel','despesaAgua','despesaGas','despesaEnergia','despesaTransporte','despesaMedicamentos',
                           'despesaSupermercado','despesaOutras'];
        despesas.each {
            if (familia.mapaDetalhes[it]?.notEmpty()) {
                semInformacao = false;
                total += Double.parseDouble(familia.mapaDetalhes[it].toString())
            }
        }
        return semInformacao ? null : total;
    }

     /**
     * Calcula a renda per capita da família.
     * Obs: Caso não haja nenhuma informação registrada de renda, retorna null e não zero.
     */
    @Transactional(readOnly = true)
    public Double getRendaPerCapita(Familia familia) {
        Double rendaTotal = getRendaTotal(familia);
        if (rendaTotal == null)
           return null;
        int numeroMembros = familia.getMembrosOrdemPadrao(true).size();
        if (numeroMembros == 0) //evita erro de divisão por zero, partindo do pressuposto que toda familia tem pelo menos um membro
            numeroMembros = 1;
        return rendaTotal / numeroMembros;
    }

    @Transactional(readOnly = true)
    public List<Telefone> obtemTelefones(Long idFamilia) {
        if (idFamilia)
            return Familia.get(idFamilia).telefones.sort { a,b-> b.dateCreated<=>a.dateCreated } //ordem inversa
        else
            return [];
    }


}