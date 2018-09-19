package org.apoiasuas

import grails.util.Holders
import org.apoiasuas.util.Modulos

class FacadeTagLib {
    static defaultEncodeAs = [taglib: 'raw']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]
    static namespace = "facade"

    /**
     * Template para listagem dos pedidos de certidao (versao 2.0) pendentes, com links para acesso aos pedidos.
     * Caso o módulo não esteja instalado, apenas ignora a chamada.
     *
     * @attr pedidos REQUIRED lista de PedidoCertidao
     */
    def pedidosCertidoesPendentes = { Map attrs, body ->
        if (Holders.pluginManager.hasGrailsPlugin(Modulos.PEDIDO_CERTIDAO))
            out << g.render(plugin: Modulos.PEDIDO_CERTIDAO, template: "/pedidoCertidao/familia/pendentes",
                    model: [pedidos: attrs.pedidos]);
        else
            log.warn("ignorando plugin não instalado: "+attrs.plugin);
        return null;
    }

}
