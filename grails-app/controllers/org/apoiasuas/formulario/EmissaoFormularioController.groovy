package org.apoiasuas.formulario

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.bootstrap.FormularioBase
import org.apoiasuas.bootstrap.FormularioCertidoes
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.Endereco
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.cidadao.FiltroCidadaoCommand
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.servico.Servico
import org.apoiasuas.util.StringUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import java.text.ParseException
import java.text.SimpleDateFormat

@Secured([DefinicaoPapeis.USUARIO_LEITURA])
class EmissaoFormularioController extends AncestralController {

    static defaultAction = "escolherFamilia"
//    static scope = "prototype" //garante uma nova instancia deste controller para cada request

    def cidadaoService
    def grailsApplication

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def escolherFamilia() {
        Map<String, List<Formulario>> tiposFormulario = service(null).getFormulariosDisponiveis().sort { [it.tipo, it.descricao] } .groupBy { it.tipo ?: "Outros" }
//        tiposFormulario.each { chave, valor ->
//            if (! chave)
//                chave = "Outros"
//        }
        render view: 'escolherFamilia', model: [formulariosDisponiveis: tiposFormulario]
    }

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def preencherFormulario(Long idFormulario, Long membroSelecionado, Long familiaSelecionada) {

        Formulario formulario = service(Formulario.get(idFormulario)).preparaPreenchimentoFormulario(idFormulario, membroSelecionado, familiaSelecionada)
        guardaUltimoSelecionado(formulario.cidadao, formulario.cidadao.familia)
        render(view: 'preencherFormulario', model: [templateCamposCustomizados: getTemplateCamposCustomizados(formulario), dtoFormulario: formulario, usuarios: service(formulario).getOpcoesResponsavelPreenchimento() ])
    }

    /**
     * Define eventuais templates customizados para exibição dos campos a serem preenchidos
     */
    String getTemplateCamposCustomizados(Formulario formulario) {
        switch (formulario.formularioPreDefinido) {
            case PreDefinidos.ENCAMINHAMENTO: return "formularioEncaminhamento"
            default: return null
        }
    }

    @Secured([DefinicaoPapeis.USUARIO])
    def imprimirFormularioGravando(Long idFormulario) {
        return imprimirFormulario(idFormulario, true)
    }

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def imprimirFormulario(Long idFormulario, Boolean gravaAlteracoes) {
        Formulario formulario

        instanciamento_dos_objetos: try { //Instancia e associa os objetos cidadao, familia, telefones, endereco (e formulario) à partir do preenchimento da tela (e nao do banco de dados)
            formulario = service(Formulario.get(idFormulario)).getFormularioComCampos(idFormulario)
            formulario.cidadao = new Cidadao(params.cidadao)
            formulario.cidadao.familia = new Familia(params.familia)
            formulario.cidadao.familia.telefones = cidadaoService.obtemTelefonesViaCidadao(formulario.cidadao.id) //carrega opções de seleção para telefone
            formulario.cidadao.familia.endereco = new Endereco(params.endereco)
            formulario.formularioEmitido = FormularioEmitido.get(params.formularioEmitido.id)
//            formulario.formularioEmitido.responsavelPreenchimento = UsuarioSistema.get(params.long('avulso.'+CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO))
            formulario.setCamposAvulsos(params.avulso)
        } catch (ParseException e) {
            //TODO: Identificar exatamente o(s) campo(s) com erro de conversão e informar ao operador com precisão (vai dar trabalho. teremos que abrir mão do bind automático do Grails)
            formulario.errors.reject(null, "Erro de conversão. Conteúdo fornecido inválido em algum dos campos")
            return render(view: 'preencherFormulario', model: [templateCamposCustomizados: getTemplateCamposCustomizados(formulario), dtoFormulario: formulario, usuarios: service(formulario).getOpcoesResponsavelPreenchimento() ])
        }

        boolean validacaoPreenchimento = service(formulario).validarPreenchimento(formulario)
        boolean validacaoDatas = validaFormatoDatas(formulario, params)
        if (! validacaoPreenchimento || ! validacaoDatas) //exibe o formulario novamente em caso de problemas na validacao
            return render(view: 'preencherFormulario', model: [templateCamposCustomizados: getTemplateCamposCustomizados(formulario), dtoFormulario: formulario, usuarios: service(formulario).getOpcoesResponsavelPreenchimento() ])

        geraFormularioPreenchidoEgrava: {
            ReportDTO reportDTO = service(formulario).prepararImpressao(formulario)
            if (gravaAlteracoes && verificaPermissao(DefinicaoPapeis.USUARIO))
                service(formulario).gravarAlteracoes(formulario)

            response.contentType = 'application/octet-stream'
            response.setHeader 'Content-disposition', "attachment; filename=\"$reportDTO.nomeArquivo\""
            reportDTO.report.process(reportDTO.context, response.outputStream);
            response.outputStream.flush()
        }

    }

    private boolean validaFormatoDatas(Formulario formulario, GrailsParameterMap params) {
        Set<String> mensagensErro = new HashSet()
        formulario.campos.each { campo ->
            final conteudoFornecido = params.get(campo.caminhoCampo)?.toString()
            if (campo.tipo?.data && conteudoFornecido) {
                try {
                    new SimpleDateFormat("dd/MM/yyyy").parse(conteudoFornecido)
                } catch (ParseException e) {
                    campo.errors.reject(null)
                    mensagensErro << "Erro interpretando informação ${conteudoFornecido}. Formato esperado: dd/mm/yyyy"
                }
            }
        }
        mensagensErro.each { formulario.errors.reject(null, it) }
        return mensagensErro.size() == 0
    }
/**
     * Infere, à partir do formulário sendo gerado, o serviço correspondente
     */
    FormularioService service(Formulario formulario) {
        Class<? extends FormularioBase> f = formulario ? formulario.formularioPreDefinido.definicaoFormulario.newInstance().classeServico() :
                FormularioBase.newInstance().classeServico();
        return grailsApplication.mainContext.getBean(StringUtils.firstLowerCase(f.simpleName))
/*
        switch (formulario?.formularioPreDefinido) {
            case [PreDefinidos.CERTIDOES, PreDefinidos.CERTIDOES_E_PEDIDO]: return grailsApplication.mainContext.getBean(StringUtils.firstLowerCase(FormularioCertidoesService.simpleName))
            case [PreDefinidos.IDENTIDADE, PreDefinidos.FOTOS, PreDefinidos.IDENTIDADE_FOTO]: return grailsApplication.mainContext.getBean(StringUtils.firstLowerCase(FormularioBeneficioEventualService.simpleName))
            default: return grailsApplication.mainContext.getBean(StringUtils.firstLowerCase(FormularioService.simpleName))
        }
*/
    }

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def familiaParaSelecao(String codigoLegado) {
        Familia familiaSelecionada = null
        boolean familiaEncontrada = false
        if (codigoLegado) {
            familiaSelecionada = cidadaoService.obtemFamiliaEMembros(codigoLegado)
            if (familiaSelecionada)
                familiaEncontrada = true
        }
        render(template:'escolherFamilia-Selecionar', model:[dtoFamiliaSelecionada: familiaSelecionada, familiaEncontrada: familiaEncontrada])
    }

    @Secured([DefinicaoPapeis.USUARIO])
    def atualizarAlteracoes() {
        //Busca dados completos da familia no BD
        Familia familia = Familia.get(params.id)
        //Atualiza com as informacoes alteradas na tela
        familia.properties = params

        String telefoneSelecionado = params.novoTelefone
        if ("novo".equalsIgnoreCase(telefoneSelecionado))
            telefoneSelecionado = params.novoTelefone

        //Grava alteracoes
        cidadaoService.atualizarFamiliaTelefoneCidadao(familia, telefoneSelecionado, params.novoTelefoneDDD)
        request.message = "Família "+familia.codigoLegado+" atualizada"
        //Volta para a tela de selecao de familias
        forward action: "procurarCidadao"
    }

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def procurarCidadao(FiltroCidadaoCommand filtro) {
        params.max = params.max ?: 10
        PagedResultList cidadaos = cidadaoService.procurarCidadao(params, filtro)
        Map filtrosUsados = params.findAll { it.value }
        render view: "/cidadao/procurarCidadao", model: [cidadaoInstanceList: cidadaos, cidadaoInstanceCount: cidadaos.getTotalCount(), filtro: filtrosUsados ]
    }

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def selecionarFamilia(Familia familiaInstance) {
        forward action: "listarMembros"
    }

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def listarFormulariosEmitidos(Integer max, Long idFamilia) {
        /* TODO Paginar
        params.max = Math.min(max ?: 10, 100)
        render view: "/cidadao/procurarCidadao", model: [cidadaoInstanceList: cidadaos, cidadaoInstanceCount: cidadaos.getTotalCount(), filtro: filtrosUsados ]
        */
        List<FormularioEmitido> formularios = FormularioEmitido.findAllByFamilia(Familia.get(idFamilia))
        render view: "listarFormulariosEmitidos", model: [formularioEmitidoInstanceList: formularios /*, formularioEmitidoInstanceCount: formularios.size(), filtro: filtrosUsados*/ ]
    }

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def mostrarFormularioEmitido(FormularioEmitido formularioEmitidoInstance) {
        render view: "mostrarFormularioEmitido", model: [formularioEmitidoInstance: formularioEmitidoInstance ]
    }


}
