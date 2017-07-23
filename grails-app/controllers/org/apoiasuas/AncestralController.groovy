package org.apoiasuas

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.CidadaoController
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.cidadao.FamiliaController
import org.apoiasuas.cidadao.FamiliaService
import org.apoiasuas.formulario.ReportDTO
import org.apoiasuas.processo.PedidoCertidaoProcessoController
import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial
import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorialService
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.AcessoNegadoPersistenceException
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.SegurancaService
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.ApoiaSuasException
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import org.springframework.web.servlet.support.RequestContextUtils

import javax.servlet.http.HttpSession

/**
 * Created by admin on 20/04/2015.
 */
class AncestralController {

    def segurancaService
    def abrangenciaTerritorialService
    def familiaService
    public static String ENTITY_CLASS_ENTRY = "entity"
    public static String JSTREE_HIDDEN_ABRANGENCIA_TERRITORIAL = "JSTREE_HIDDEN_ABRANGENCIA_TERRITORIAL"
    private static final String ULTIMO_REPORT_DTO = "ULTIMO_FORMULARIO_REPORT_DTO"

    protected void guardaUltimoCidadaoSelecionado(Cidadao cidadao) {
        if (cidadao && cidadao.id) {
            CidadaoController.setUltimoCidadao(session, cidadao)
            if (cidadao.familia && cidadao.familia.id)
//                forward(controller: "familia", action: "setUltimaFamilia", params: [familia: cidadao.familia])
                setUltimaFamilia(cidadao.familia)
            else
                throw new RuntimeException("Impossivel determinar familia do cidadao sendo armazenado na sessao. Id cidadao = ${cidadao.id}")
        } else {
            CidadaoController.setUltimoCidadao(session, null)
        }
    }

    protected void guardaUltimaFamiliaSelecionada(Familia familia) {
        if (familia && familia.id) {
//            forward(controller: "familia", action: "setUltimaFamilia", params: [familia: familia])
            setUltimaFamilia(familia)
            //Elimina o ultimo cidadao selecionado da sessao se houver mudança da familia selecionada
            Cidadao ultimoCidadao = CidadaoController.getUltimoCidadao(session)
            if (! ultimoCidadao || ultimoCidadao.familia.id != familia.id)
                CidadaoController.setUltimoCidadao(session, null)
        } else {
            setUltimaFamilia(null)
//            forward(controller: "familia", action: "setUltimaFamilia", params: [familia: null])
            CidadaoController.setUltimoCidadao(session, null)
        }
    }

    private void setUltimaFamilia(Familia familia) {
        if (! familia) {
            session[FamiliaController.SESSION_ULTIMA_FAMILIA] = null;
            session[FamiliaController.SESSION_NOTIFICACAO_FAMILIA] = null;
            session[FamiliaController.SESSION_NOTIFICACAO_FAMILIA_NUMERO_EXIBICOES] = 0L;
        } else if (session[FamiliaController.SESSION_ULTIMA_FAMILIA]?.id != familia.id) {
            session[FamiliaController.SESSION_ULTIMA_FAMILIA] = familia;
            Set notificacoes = familiaService.getNotificacoes(familia.id, RequestContextUtils.getLocale(request));
            String textoNotificacoes = "";
            notificacoes.each { strNotificacao ->
                if (strNotificacao == g.message(code: "notificacao.familia.pedidosCertidao"))
                    strNotificacao = g.link(controller:"pedidoCertidaoProcesso", action:"list", title: "Ver pedidos pendentes",
                            params: [situacao: PedidoCertidaoProcessoController.SITUACAO_PENDENTE, codigoLegado: familia.cad]){
                        g.message(code: "notificacao.familia.pedidosCertidao")
                    }

                if (! textoNotificacoes)
                    textoNotificacoes += strNotificacao
                else
                    textoNotificacoes += "<br> "+strNotificacao
            }
            log.debug(textoNotificacoes);
            session[FamiliaController.SESSION_NOTIFICACAO_FAMILIA] = textoNotificacoes;
            session[FamiliaController.SESSION_NOTIFICACAO_FAMILIA_NUMERO_EXIBICOES] = 0L
        }
    }

    protected boolean verificaPermissao(String papel) {
        return SpringSecurityUtils.ifAnyGranted(papel)
    }

    protected ServicoSistema getServicoCorrente() {
        return segurancaService.getServicoLogado();
    }

    protected UsuarioSistema getUsuarioLogado() {
        return segurancaService.getUsuarioLogado();
    }

    /**
     * Tenta acessar o objeto a ser utilizado na action e, caso o acesso seja negado, captura a excessao e exibe uma
     * mensagem de erro para o usuário. obs: A excessao é levantada nos eventos de persistência em ApoiaSuasPersistenceListener
     *
     * (defined with private scope, so it's not considered an action)
     */
    protected interceptaSeguranca(/*Class domainClass*/) {
        try {
            if (params?.getIdentifier()) {
            //Desabilitando o interceptaSeguranca por questoes de performance
//                Class domainClass = getProperty(GrailsControllerClass.BEFORE_INTERCEPTOR)[ENTITY_CLASS_ENTRY]
//                domainClass?.get(params.getIdentifier())
            }
        } catch  (AcessoNegadoPersistenceException e) {
            flash.message = e.getMessage()
            redirect(uri: request.getHeader('referer') )
            return false
        }
    }

    protected ArrayList<UsuarioSistema> getOperadoresOrdenadosController(boolean somenteHabilitados, Collection<UsuarioSistema> sempreMostrar = []) {
        return segurancaService.getOperadoresOrdenados(somenteHabilitados, sempreMostrar)
    }

    protected ArrayList<UsuarioSistema> getTecnicosOrdenadosController(boolean somenteHabilitados, Collection<UsuarioSistema> sempreMostrar = []) {
        return segurancaService.getTecnicosOrdenados(somenteHabilitados, sempreMostrar)
    }

    /**
     * Muda a localizacao onde as views (gsps) serao buscadas
     */
/*
    def afterInterceptor = { model, ModelAndView modelAndView ->
        String[] pathNodes = modelAndView.viewName.split("/")
        pathNodes[pathNodes.length-2] = "processo"
        modelAndView.viewName = pathNodes.join("/")
    }
*/

    /**
     * Converte um conjunto de valores passados como String no request em uma lista de instancias de AbrangenciaTerritorial
     * @param requestParameter Se nao mencionado, procura por JSTREE_HIDDEN_ABRANGENCIA_TERRITORIAL como default
     */
    protected AbrangenciaTerritorial atribuiAbrangenciaTerritorial(String requestParameter = null) {
        def territorioAtuacaoRequest = request.getParameter(requestParameter ? requestParameter : JSTREE_HIDDEN_ABRANGENCIA_TERRITORIAL)
        if ( territorioAtuacaoRequest) {
            List<String> selecionados = territorioAtuacaoRequest.split(",")
            if (selecionados.size() == 0)
                return null
            else if (selecionados.size() == 1) {
                Long id = selecionados[0].substring(AbrangenciaTerritorialService.ID_TERRITORIOS_ATUACAO.size()).toLong()
                return abrangenciaTerritorialService.getAbrangenciaTerritorial(id);
            } else {
                throw ApoiaSuasException("Apenas um territorio pode ser selecionado. Submetidos ${selecionados.size()}")
            }
        }
        return null;
    }

    protected String getAbrangenciasTerritoriaisEdicao(AbrangenciaTerritorial abrangenciaTerritorial) {
        return abrangenciaTerritorialService.JSONAbrangenciasTerritoriaisEdicao(null, abrangenciaTerritorial ? [abrangenciaTerritorial] : [])
    }

    protected String getAbrangenciasTerritoriaisExibicao(AbrangenciaTerritorial abrangenciaTerritorial) {
        abrangenciaTerritorialService.JSONAbrangenciasTerritoriaisExibicao(abrangenciaTerritorial)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def baixarArquivo() {
        ReportDTO reportDTO = getReportParaBaixar(session)
        try {
            response.contentType = 'application/octet-stream'
            if (reportDTO) {
                response.setHeader 'Content-disposition', "attachment; filename=\"$reportDTO.nomeArquivo\""
                reportDTO.report.process(reportDTO.context, response.outputStream);
            } else {
                response.setHeader 'Content-disposition', "signal; filename=\"erro-favor-cancelar\""
            }
            response.outputStream.flush()
        } finally {
            setReportParaBaixar(session, null)
        }
    }

    public static ReportDTO getReportParaBaixar(HttpSession session) {
        return session[ULTIMO_REPORT_DTO]
    }

    public static void setReportParaBaixar(HttpSession session, ReportDTO reportParaBaixar) {
        session[ULTIMO_REPORT_DTO] = reportParaBaixar
    }

    protected boolean validaVersao(Object instancia) {
        log.debug("conferindo gravacao concorrente de "+instancia.class.simpleName)
        if (params && params.version && instancia && instancia['version'] && new Long(params.version) != instancia['version']) {
            instancia.errors.rejectValue("", "", "Este registro foi alterado simultaneamente por outra pessoa. Favor abri-lo novamente antes de gravar.");
            instancia['version'] = new Long(params.version);
            return false;
        }
        if (params && instancia['version'] && ! params.version)
            log.warn("Atenção! A gravação de "+instancia.class.simpleName+" em "+request.requestURL+
                    " não está verificando possíveis erros de gravação concorrente que sobrescrevem informações" +
                    " sem o conhecimento do operador (optimistic lock).");
        return true;
    }

}
