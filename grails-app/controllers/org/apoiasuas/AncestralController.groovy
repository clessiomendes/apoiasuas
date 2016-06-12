package org.apoiasuas

import grails.plugin.springsecurity.SpringSecurityUtils
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.CidadaoController
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.cidadao.FamiliaController
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.AcessoNegadoPersistenceException
import org.apoiasuas.seguranca.SegurancaService
import org.apoiasuas.seguranca.UsuarioSistema
import org.codehaus.groovy.grails.commons.GrailsControllerClass

/**
 * Created by admin on 20/04/2015.
 */
class AncestralController {

    SegurancaService segurancaService
    public static String ENTITY_CLASS_ENTRY = "entity"

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


}
