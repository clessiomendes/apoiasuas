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
        if (cidadao && cidadao.id) {
            session[ULTIMO_CIDADAO] = cidadao
            if (cidadao.familia && cidadao.familia.id)
                session[ULTIMA_FAMILIA] = cidadao.familia
            else
                throw new RuntimeException("Impossivel determinar familia do cidadao sendo armazenado na sessao. Id cidadao = ${cidadao.id}")
        } else {
            session[ULTIMO_CIDADAO] = null
        }
    }

    protected void guardaUltimaFamiliaSelecionada(Familia familia) {
        if (familia && familia.id) {
            session[ULTIMA_FAMILIA] = familia
            //Elimina o ultimo cidadao selecionado da sessao se houver mudança da familia selecionada
            Cidadao ultimoCidadao = session[ULTIMO_CIDADAO]
            if (! ultimoCidadao || ultimoCidadao.familia.id != familia.id)
                session[ULTIMO_CIDADAO] = null
        } else {
            session[ULTIMA_FAMILIA] = null
            session[ULTIMO_CIDADAO] = null
        }
    }

    protected boolean verificaPermissao(String papel) {
        return SpringSecurityUtils.ifAnyGranted(papel)
    }

}
