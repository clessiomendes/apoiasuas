package org.apoiasuas

import grails.plugin.springsecurity.SpringSecurityUtils
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.CidadaoController
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.cidadao.FamiliaController
import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial
import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorialService
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.AcessoNegadoPersistenceException
import org.apoiasuas.seguranca.SegurancaService
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.ApoiaSuasException
import org.codehaus.groovy.grails.commons.GrailsControllerClass

/**
 * Created by admin on 20/04/2015.
 */
class AncestralController {

    SegurancaService segurancaService
    AbrangenciaTerritorialService abrangenciaTerritorialService
    public static String ENTITY_CLASS_ENTRY = "entity"
    public static String JSTREE_HIDDEN_ABRANGENCIA_TERRITORIAL = "JSTREE_HIDDEN_ABRANGENCIA_TERRITORIAL"


    protected void guardaUltimoCidadaoSelecionado(Cidadao cidadao) {
        if (cidadao && cidadao.id) {
            CidadaoController.setUltimoCidadao(session, cidadao)
            if (cidadao.familia && cidadao.familia.id)
                FamiliaController.setUltimaFamilia(session, cidadao.familia)
            else
                throw new RuntimeException("Impossivel determinar familia do cidadao sendo armazenado na sessao. Id cidadao = ${cidadao.id}")
        } else {
            CidadaoController.setUltimoCidadao(session, null)
        }
    }

    protected void guardaUltimaFamiliaSelecionada(Familia familia) {
        if (familia && familia.id) {
            FamiliaController.setUltimaFamilia(session, familia)
            //Elimina o ultimo cidadao selecionado da sessao se houver mudança da familia selecionada
            Cidadao ultimoCidadao = CidadaoController.getUltimoCidadao(session)
            if (! ultimoCidadao || ultimoCidadao.familia.id != familia.id)
                CidadaoController.setUltimoCidadao(session, null)
        } else {
            FamiliaController.setUltimaFamilia(session, null)
            CidadaoController.setUltimoCidadao(session, null)
        }
    }

    protected boolean verificaPermissao(String papel) {
        return SpringSecurityUtils.ifAnyGranted(papel)
    }

    protected ServicoSistema getServicoCorrente() {
        return segurancaService.getServicoLogado();
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
                Class domainClass = getProperty(GrailsControllerClass.BEFORE_INTERCEPTOR)[ENTITY_CLASS_ENTRY]
                domainClass?.get(params.getIdentifier())
            }
        } catch  (AcessoNegadoPersistenceException e) {
            flash.message = e.getMessage()
            redirect(uri: request.getHeader('referer') )
            return false
        }
    }

    protected ArrayList<UsuarioSistema> getOperadoresOrdenadosController(boolean somenteHabilitados) {
            return segurancaService.getOperadoresOrdenados(somenteHabilitados)
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
        return abrangenciaTerritorialService.JSONAbrangenciasTerritoriaisEdicao(null, [abrangenciaTerritorial])
    }

    protected String getAbrangenciasTerritoriaisExibicao(AbrangenciaTerritorial abrangenciaTerritorial) {
        abrangenciaTerritorialService.JSONAbrangenciasTerritoriaisExibicao(abrangenciaTerritorial)
    }


}
