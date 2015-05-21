package org.apoiasuas

import grails.plugin.springsecurity.SpringSecurityUtils
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.seguranca.DefinicaoPapeis

/**
 * Created by admin on 20/04/2015.
 */
class AncestralController {

    def grailsApplication

    public static final String ULTIMO_CIDADAO = "ID_ULTIMO_CIDADAO"
    public static final String ULTIMA_FAMILIA = "ID_ULTIMA_FAMILIA"

    protected void guardaUltimoSelecionado(Cidadao cidadao, Familia familia) {
        session.setAttribute(ULTIMA_FAMILIA, familia)
        session.setAttribute(ULTIMO_CIDADAO, cidadao)
    }

    protected boolean verificaPermissao(String papel) {
        return SpringSecurityUtils.ifAnyGranted(papel)
    }

}
