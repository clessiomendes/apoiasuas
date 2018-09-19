package org.apoiasuas

import grails.util.Holders
import org.apoiasuas.AncestralController
import org.apoiasuas.util.ApoiaSuasException
import org.apoiasuas.util.Modulos

class FacadeController extends AncestralController {

    def pedidoCertidao(long idFamilia) {
        Modulos.testaDependencia(Modulos.PEDIDO_CERTIDAO, true);
        return forward(plugin: Modulos.PEDIDO_CERTIDAO, controller: "pedidoCertidao", action: "create", params: [idFamilia: idFamilia])
    }

}
