package org.apoiasuas.pedidocertidao

import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.Modulos

class HistoricoPedidoCertidao {

    String descricao
    Date dataHora
    UsuarioSistema operador
    PedidoCertidao.Situacao acao

    static belongsTo = [pedido: PedidoCertidao]
    static mapping = {
        table schema: Modulos.PEDIDO_CERTIDAO;
        id generator: 'native', params: [sequence: Modulos.PEDIDO_CERTIDAO+'.sq_historico_pedidocertidao']
    }

    static constraints = {
        acao(nullable: true);
        descricao(nullable: false, maxSize: 10000);
        dataHora(nullable: false);
        operador(nullable: false);
        pedido(nullable: false);
    }

}
