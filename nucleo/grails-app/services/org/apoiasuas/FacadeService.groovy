package org.apoiasuas

import grails.transaction.Transactional
import grails.util.Holders
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.util.Modulos

@Transactional
class FacadeService {

    public List pedidosCertidaoPendentes(Familia familia) {
        if (Modulos.testaDependencia(Modulos.PEDIDO_CERTIDAO, false)) {
             def pedidoCertidaoService = Holders.grailsApplication.mainContext.getBean('pedidoCertidaoService');
            return pedidoCertidaoService.getPedidosPendentes(familia);
        } else
            return []
    }

}
