import org.apoiasuas.redeSocioAssistencial.RecursosServico
import org.apoiasuas.seguranca.ASMenuBuilder
import org.apoiasuas.seguranca.ItemMenuDTO

class PedidoCertidaoBootStrap {

    ASMenuBuilder menuBuilder

    def init = { servletContext ->
        System.out.println("meu bootstrap pedidocertidao");

        menuBuilder.novaOpcaoMenu(new ItemMenuDTO(ordem: 800L, descricao: "Pedidos de Certidão",
                recursoServico: RecursosServico.PEDIDOS_CERTIDAO_2_0,
                hint: "Consultar a situação de pedidos de certidão emitidos anteriormente (ou registrar manualmente um pedido feito fora do sistema)",
                imagem: "usecases/pedidos-certidao-w.png", classeCss: "marrom", link: [controller: "pedidoCertidao", action: "list"]));

        menuBuilder.novaOpcaoMenu(new ItemMenuDTO(ordem: 2000L, descricao: "Pedidos de Certidão (antigos)",
                recursoServico: RecursosServico.PEDIDOS_CERTIDAO_2_0,
                hint: "Consultar a situação de pedidos de certidão emitidos anteriormente (ou registrar manualmente um pedido feito fora do sistema)",
                imagem: "usecases/pedidos-certidao-w.png", classeCss: "marrom", link: [controller: "pedidoCertidaoProcesso", action: "preList"]));


    }
}