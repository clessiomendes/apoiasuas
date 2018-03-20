package org.apoiasuas.formulario

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.formulario.definicao.FormularioBase
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.CidadaoService
import org.apoiasuas.cidadao.Endereco
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.cidadao.FiltroCidadaoCommand
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.redeSocioAssistencial.RecursosServico
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.ApoiaSuasException
import org.apoiasuas.util.StringUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import java.text.ParseException
import java.text.SimpleDateFormat

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
/**
 * CDU de Emissão de Formulários
 * documentação (diagrama) em https://www.draw.io/#G13oiXAgbzyw6ZWHq1Rsv15raTeNZ0hKkw
 */
class EmissaoFormularioController extends AncestralController {

    static defaultAction = "escolherFormulario"
//    static scope = "prototype" //garante uma nova instancia deste controller para cada request

    CidadaoService cidadaoService
    def pedidoCertidaoProcessoService

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def _old_escolherFormulario(Long idCidadao) {
        if (idCidadao)
            guardaUltimoCidadaoSelecionado(Cidadao.get(idCidadao))
        List<Formulario> formulariosDisponiveis = [];
        if (params.idFormulario)
            formulariosDisponiveis << servico(null).getFormulario(params.idFormulario.toLong())
        else
            formulariosDisponiveis = servico(null).getFormulariosDisponiveis();
        Map<String, List<Formulario>> tiposFormulario = formulariosDisponiveis.sort {
            [it.id, it.tipo]
        }.groupBy { it.tipo ?: "Outros" }
        render view: 'escolherFormulario', model: [formulariosDisponiveis: tiposFormulario]
    }


    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def escolherFormulario() {
        List<Formulario> formulariosDisponiveis = [];
        if (params.idFormulario)
            formulariosDisponiveis << servico(null).getFormulario(params.idFormulario.toLong())
        else
            formulariosDisponiveis = servico(null).getFormulariosDisponiveis();
        //Mapa ordenado, para preservar a ordem definida para os formulários durante a exibicao na tela
        SortedMap<String, List<Formulario>> tiposFormulario = new TreeMap<String, List<Formulario>>();
        //Agrupa os formularios por tipo e os insere em um mapa ordenado
        formulariosDisponiveis.sort { [it.formularioPreDefinido?.ordem, it.id] }.each {
            String tipo = it.tipo ?: "Outros";
            if (! tiposFormulario.containsKey(tipo))
                tiposFormulario.put(tipo, [])
            tiposFormulario[tipo].add(it);
        }
        render view: 'escolherFormulario', model: [formulariosDisponiveis: tiposFormulario]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def preencherFormulario(Long idFormulario, Long idServico /*preenchido apenas quando vindo do cdu de servico socio assistencial*/,
                            Long membroSelecionado, Long familiaSelecionada) {
//        try {
        Formulario formulario = servico(Formulario.get(idFormulario)).preparaPreenchimentoFormulario(idFormulario, membroSelecionado, familiaSelecionada)
        if (! formulario)
            return redirect(controller: 'inicio')
        guardaUltimaFamiliaSelecionada(formulario.familia)
        guardaUltimoCidadaoSelecionado(formulario.cidadao)
        render(view: 'preencherFormulario',
                model: [templateCamposCustomizados: getTemplateCamposCustomizados(formulario),
                        dtoFormulario: formulario,
                        idServico: idServico,
                        tecnicos: getTecnicosOrdenadosController(true) ])
//        } catch (Exception e) {
//            e.printStackTrace()
//            throw e
//        }

    }

    /**
     * Define eventuais templates customizados para exibição dos campos a serem preenchidos
     */
    private String getTemplateCamposCustomizados(Formulario formulario) {
        switch (formulario.formularioPreDefinido) {
            case PreDefinidos.ENCAMINHAMENTO: return "formularioEncaminhamento"
            default: return null
        }
    }

/*
    @Secured([DefinicaoPapeis.USUARIO])
    def imprimirFormularioGravando(Long idFormulario) {
        return imprimirFormulario(idFormulario, true)
    }
*/

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def _old_imprimirFormulario(Long idFormulario, Long idModelo) {
        Formulario formulario

        instanciamento_dos_objetos: { //Instancia e associa os objetos cidadao, familia, telefones, endereco (e formulario) à partir do preenchimento da tela (e nao do banco de dados)
            formulario = servico(Formulario.get(idFormulario)).getFormulario(idFormulario, true)

            String idUsuarioSistema = params.avulso?.get(CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO)
            if (idUsuarioSistema)
                formulario.usuarioSistema = UsuarioSistema.get(idUsuarioSistema.toLong());
            if (params.cidadao)
                formulario.cidadao = new Cidadao(params.cidadao);
            if (params.familia) {
                formulario.familia = new Familia(params.familia);
                //necessário buscar os telefones no banco de dados
                formulario.familia.telefones = familiaService.obtemTelefones(formulario.familia.id);
//                formulario.familia.telefones = cidadaoService.obtemTelefonesViaCidadao(formulario.cidadao.id)
                if (params.endereco)
                    formulario.familia.endereco = new Endereco(params.endereco)
            }

            formulario.formularioEmitido = FormularioEmitido.get(params.formularioEmitido.id)

            //Validacao dos campos obrigatorios e dos formatos
            if (! validarPreenchimento(formulario, params))
                return render(view: 'preencherFormulario', model: [templateCamposCustomizados: getTemplateCamposCustomizados(formulario),
                                                                   dtoFormulario: formulario, tecnicos: getTecnicosOrdenadosController(true) ]);

//            formulario.setCamposAvulsos(params.avulso)
        }

        geraFormularioPreenchidoEgrava: {
            ReportDTO reportDTO = servico(formulario).prepararImpressao(formulario, idModelo)
            if (verificaPermissao(DefinicaoPapeis.STR_USUARIO))
                servico(formulario).gravarAlteracoesAntigo(formulario)

            //Guarda na sessao asinformacoes necessarias para a geracao do arquivo a ser baixado (que sera baixado por um
            //javascript que rodara automaticamente na proxima pagina)
            setReportParaBaixar(session, reportDTO)
            if (formulario.formularioPreDefinido == PreDefinidos.CERTIDOES_E_PEDIDO && segurancaService.acessoRecursoServico(RecursosServico.PEDIDOS_CERTIDAO)) {
                String idProcesso = pedidoCertidaoProcessoService.getIdProcessoPeloFormularioEmitido(reportDTO.formularioEmitido.id)
                return redirect(controller: "pedidoCertidaoProcesso", action: "mostraProcesso", id:idProcesso)
            } else {
                redirect action: 'escolherFormulario'
            }
        }
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def imprimirFormulario(Long idFormulario, Long idModelo) {
        Formulario formulario

        instanciamento_dos_objetos: { //Instancia e associa os objetos cidadao, familia, telefones, endereco (e formulario) à partir do preenchimento da tela (e nao do banco de dados)
            formulario = servico(Formulario.get(idFormulario)).getFormulario(idFormulario, true)

            String idUsuarioSistema = params.avulso?.get(CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO)
            if (idUsuarioSistema)
                formulario.usuarioSistema = UsuarioSistema.get(idUsuarioSistema.toLong())
            if (params.cidadao)
                formulario.cidadao = new Cidadao(params.cidadao);
            if (params.familia) {
                formulario.familia = new Familia(params.familia);
                //necessário buscar os telefones no banco de dados
                formulario.familia.telefones = familiaService.obtemTelefones(formulario.familia.id);
//                formulario.familia.telefones = cidadaoService.obtemTelefonesViaCidadao(formulario.cidadao.id)
                if (params.endereco)
                    formulario.familia.endereco = new Endereco(params.endereco)
            }

            formulario.formularioEmitido = FormularioEmitido.get(params.formularioEmitido.id);

            //Validacao dos campos obrigatorios e dos formatos
            if (! validarPreenchimento(formulario, params))
                return render(view: 'preencherFormulario', model: [templateCamposCustomizados: getTemplateCamposCustomizados(formulario),
                                                                   dtoFormulario: formulario, tecnicos: getTecnicosOrdenadosController(true) ]);

//            formulario.setCamposAvulsos(params.avulso)
        }

        geraFormularioPreenchido: {
            ReportDTO reportDTO = servico(formulario).prepararImpressao(formulario, idModelo)
            //Guarda na sessao asinformacoes necessarias para a geracao do arquivo a ser baixado (que sera baixado por um
            //javascript que rodara automaticamente na proxima pagina)
            setReportParaBaixar(session, reportDTO)

            //Verifica se existem campos alterados que podem ser atualizados no cadastro
            //FIXME: abranger também situações em que não há cidadão selecionado, mas há família
            if (formulario.familia && verificaPermissao(DefinicaoPapeis.STR_USUARIO)) {
                List camposAfetados = servico(formulario).camposAlterados(formulario);
                if (camposAfetados) {
                    return render(view: "atualizarCadastro", model: [formulario: formulario, camposAfetados: camposAfetados,
                                  familiaSelecionada: Familia.get(formulario.familia?.id),
                                  cidadaoSelecionado: Cidadao.get(formulario.cidadao?.id)])
                }
            }

            return fimEmissao(formulario);
        }
    }

    /**
     * Decide qual será a tela para a qual o operador será encaminhado após concluir a emisão do formulario (e confirmar
     * a gravação de eventuais alterações no cadastro). Em geral, retorna para a tela de escolha de formulario. Mas casos
     * especificos, como do pedido de certidão, podem ter um destino diferente.
     */

    private def fimEmissao(Formulario formulario) {
        if (! formulario.formularioEmitido)
            throw new ApoiaSuasException("formulario.formularioEmitido não fornecido")
        if (formulario.formularioPreDefinido == PreDefinidos.CERTIDOES_E_PEDIDO && segurancaService.acessoRecursoServico(RecursosServico.PEDIDOS_CERTIDAO)) {
            String idProcesso = pedidoCertidaoProcessoService.getIdProcessoPeloFormularioEmitido(formulario.formularioEmitido.id)
            return redirect(controller: "pedidoCertidaoProcesso", action: "mostraProcesso", id:idProcesso)
        } else {
            redirect action: 'escolherFormulario'
        }
    }

    /**
     * Verifica se todos os campos foram preenchidos com valores validos ou obrigatorios
     */
    private boolean validarPreenchimento(Formulario formulario, GrailsParameterMap params) {
        boolean result = true;
        formulario.campos.each { CampoFormulario campo ->
            final conteudoFornecido = params.get(campo.caminhoCampo)?.toString();
            //Campo obrigatorio
            String descricaoCampo = campo.descricao + (campo.grupo ? " - " + campo.grupo : "");
            if (! conteudoFornecido && campo.obrigatorio) {
                campo.mensagemErro = "O campo '${descricaoCampo}' é obrigatório"
                result = false;
            }
            if (conteudoFornecido) {
                //Formato de data
                if (CampoFormulario.Tipo.DATA == campo.tipo) {
                    try {
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        df.setLenient(false);
                        df.parse(conteudoFornecido);
                        //re-armazenao valor preenchido na tela para uma eventual
                        campo.valorArmazenado = conteudoFornecido;
                    } catch (ParseException e) {
                        result = false;
                        campo.mensagemErro = "Data inválida '${conteudoFornecido}' em '${descricaoCampo}'. Formato esperado: dd/mm/yyyy"
                    }
                //Formato de número inteiro
                } else if (CampoFormulario.Tipo.INTEIRO == campo.tipo) {
                    try {
                        Integer.parseInt(conteudoFornecido)
                        //re-armazenao valor preenchido na tela para uma eventual
                        campo.valorArmazenado = conteudoFornecido;
                    } catch (ParseException e) {
                        result = false;
                        campo.mensagemErro = "Número inválido '${conteudoFornecido}' em '${descricaoCampo}'."
                    }
                //os outros tipos nao precisam de validacao
                } else {
                    //re-armazenao valor preenchido na tela para uma eventual
                    campo.valorArmazenado = conteudoFornecido;
                }
            }
        }
        return result;
    }

    /**
     * Infere, à partir do formulário sendo gerado, o serviço correspondente
     */
    private FormularioService servico(Formulario formulario) {
        Class<? extends FormularioBase> f = null
        if (formulario && formulario.formularioPreDefinido)
            f = formulario.formularioPreDefinido.definicaoFormulario.newInstance().classeServico()
        else
            f = FormularioBase.newInstance().classeServico();
        return grailsApplication.mainContext.getBean(StringUtils.firstLowerCase(f.simpleName))
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def escolherCidadao(String cad) {
        Familia familiaSelecionada = null
//        boolean familiaEncontrada = false
        if (cad) {
            familiaSelecionada = cidadaoService.obtemFamiliaPeloCad(cad, true)
//            if (familiaSelecionada)
//                familiaEncontrada = true
        }
        if (familiaSelecionada)
            render(template:'escolherCidadao', model:[dtoFamiliaSelecionada: familiaSelecionada/*, familiaEncontrada: familiaEncontrada*/])
        else
            render(template: 'familiaNaoEncontrada');
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
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
        request.message = "Família "+familia.cad+" atualizada"
        //Volta para a tela de selecao de familias
        forward action: "procurarCidadao"
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def procurarCidadao(FiltroCidadaoCommand filtro) {
        params.max = params.max ?: 10
        PagedResultList cidadaos = cidadaoService.procurarCidadao(params, filtro)
        Map filtrosUsados = params.findAll { it.value }
        render view: "/cidadaos/cidadao/procurarCidadao", model: [cidadaoInstanceList: cidadaos, cidadaoInstanceCount: cidadaos.getTotalCount(), filtro: filtrosUsados ]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def selecionarFamilia(Familia familiaInstance) {
        forward action: "listarMembros"
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def listarFormulariosEmitidosFamilia(Long idFamilia) {
        /* TODO Paginar
        params.max = Math.min(max ?: 10, 100)
        render view: "/cidadao/procurarCidadao", model: [cidadaoInstanceList: cidadaos, cidadaoInstanceCount: cidadaos.getTotalCount(), filtro: filtrosUsados ]
        */
        List<FormularioEmitido> formularios = FormularioEmitido.findAllByFamilia(Familia.get(idFamilia), [max: 20, sort: "id", order:"desc"])
        render view: "listarFormulariosEmitidos", model: [formularioEmitidoInstanceList: formularios]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def listarFormulariosEmitidosCidadao(Long idCidadao) {
        /* TODO Paginar
        params.max = Math.min(max ?: 10, 100)
        render view: "/cidadao/procurarCidadao", model: [cidadaoInstanceList: cidadaos, cidadaoInstanceCount: cidadaos.getTotalCount(), filtro: filtrosUsados ]
        */
        List<FormularioEmitido> formularios = FormularioEmitido.findAllByCidadao(Cidadao.get(idCidadao), [max: 20, sort: "id", order:"desc"])
        render view: "listarFormulariosEmitidos", model: [formularioEmitidoInstanceList: formularios]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def mostrarFormularioEmitido(FormularioEmitido formularioEmitidoInstance) {
        render view: "mostrarFormularioEmitido", model: [formularioEmitidoInstance: formularioEmitidoInstance ]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def gravarAlteracoes(FormularioCommand formularioCommand) {
        Formulario formulario = Formulario.get(formularioCommand.id);
        formulario.formularioEmitido = FormularioEmitido.get(formularioCommand.idFormularioEmitido);
        if (formularioCommand.idFamilia)
            formulario.familia = Familia.get(formularioCommand.idFamilia)
        else
            throw new ApoiaSuasException("Id do cidadão não encontrado entre os parâmetros")
        if (formularioCommand.idCidadao)
            formulario.cidadao = Cidadao.get(formularioCommand.idCidadao);

        servico(formulario).gravarAlteracoes(formulario, formularioCommand.campos);
        return fimEmissao(formulario);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def cancelarAlteracoes(FormularioCommand formularioCommand) {
        Formulario formulario = Formulario.get(formularioCommand.id);
        formulario.formularioEmitido = FormularioEmitido.get(formularioCommand.idFormularioEmitido);
        return fimEmissao(formulario);
    }

}

class FormularioCommand {
    String id
    String idCidadao
    String idFamilia
    String idFormularioEmitido
    List<CampoFormularioCommand> campos
}

class CampoFormularioCommand {
    String id
    String novoConteudo
}