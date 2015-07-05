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

    protected void guardaUltimoCidadaoSelecionado(Cidadao cidadao) {
        session[ULTIMO_CIDADAO] = cidadao
        session[ULTIMA_FAMILIA] = cidadao.familia
    }

    protected void guardaUltimaFamiliaSelecionada(Familia familia) {
        session[ULTIMA_FAMILIA] = familia
        Cidadao ultimoCidadao = session[ULTIMO_CIDADAO]
        if (! ultimoCidadao || ultimoCidadao.familia.id != familia.id)
            session[ULTIMO_CIDADAO] = null
    }

    protected boolean verificaPermissao(String papel) {
        return SpringSecurityUtils.ifAnyGranted(papel)
    }

}
