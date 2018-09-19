package org.apoiasuas.pedidocertidao

import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.DominioProtegidoServico
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.Modulos
import org.apoiasuas.util.SimNao

class PedidoCertidao implements DominioProtegidoServico {

    public static enum Situacao {
        DESFAZER("Última Ação Desfeita", "Retornar para a Situação Anterior", "undo.png", []),
        PEDIDO_CANCELADO("Pedido Cancelado", "Cancelar Pedido", "cancel.png",
                [DESFAZER]),
        CERTIDAO_ENTREGUE("Certidão Entregue", "Entregar Certidão", "checked.png",
                [DESFAZER]),
        CERTIDAO_RECEBIDA("Certidão Recebida","Receber Certidão", "import.png",
                [DESFAZER, CERTIDAO_ENTREGUE, PEDIDO_CANCELADO]),
        PEDIDO_ENVIADO("Pedido Enviado","Enviar Pedido", "export.png",
                [DESFAZER, CERTIDAO_RECEBIDA, CERTIDAO_ENTREGUE, PEDIDO_CANCELADO]),
        NOVO_PEDIDO("Nova Demanda","Receber Demanda", "database/novo.png",
                [PEDIDO_ENVIADO, CERTIDAO_RECEBIDA, CERTIDAO_ENTREGUE, PEDIDO_CANCELADO])

        public static List<Situacao> PENDENTES = [NOVO_PEDIDO, PEDIDO_ENVIADO, CERTIDAO_RECEBIDA];
        public static List<Situacao> ENCERRADOS = [PEDIDO_CANCELADO, CERTIDAO_ENTREGUE];

        String descricao;
        String acao;
        String icone;
        Situacao[] acoesPossiveis;
        public Situacao(String descricao, String acao, String icone, List<Situacao> acoesPossiveis) {
            this.descricao = descricao;
            this.acao = acao;
            this.icone = icone;
            this.acoesPossiveis = acoesPossiveis;
//            log.debug("Acoes possiveis para $descricao: "+acoesPossiveis.join(","));
        }
        public String toString() {
            return this.name();
        }
    }

    public static enum TipoCertidao {
        NASCIMENTO("nascimento"),
        CASAMENTO("casamento"),
        OBITO("óbito")

        String descricao
        public TipoCertidao(String descricao) {
            this.descricao = descricao
        }
    }

    ServicoSistema servicoSistemaSeguranca
    Familia familia
    Situacao situacao
    Set<HistoricoPedidoCertidao> historico = []
    UsuarioSistema operadorResponsavel
    Date dateCreated
    Date lastUpdated

    Cidadao cidadaoRegistro
    TipoCertidao tipoCertidao
    String nomeRegistro
    String nomeConjugeRegistro
    Date dataRegistro
    String livro
    String folha
    String termo
    String observacoesRegistro

    Cidadao cidadaoSolicitante
    String nomeSolicitante
    String identidadeSolicitante
    String cpfSolicitante
    String nacionalidadeSolicitante
    String profissaoSolicitante
    String maeSolicitante
    String paiSolicitante
    String estadoCivilSolicitante
    SimNao uniaoEstavelSolicitante
    String conviventeSolicitante
    String contatosSolicitante
    String enderecoSolicitante
    String municipioSolicitante
    String ufSolicitante

    String nomeCartorio
    String enderecoCartorio
    String bairroCartorio
    String municipioCartorio
    String ufCartorio
    String cepCartorio
    String contatosCartorio
    String observacoesCartorio

    static hasMany = [historico: HistoricoPedidoCertidao]

    static mapping = {
        table schema: Modulos.PEDIDO_CERTIDAO;
        id generator: 'native', params: [sequence: Modulos.PEDIDO_CERTIDAO+'.sq_pedidocertidao']
        servicoSistemaSeguranca fetch: 'join' //por questoes de seguranca, sempre que um link eh obtido do banco de dados, o servicoSistema precisara ser consultado
    }

    static constraints = {
        nomeRegistro(nullable: false, maxSize: 10000);
        nomeSolicitante(nullable: false, maxSize: 10000);
        nomeCartorio(nullable: true, maxSize: 10000);
        tipoCertidao(nullable: false);
//        servicoSistemaSeguranca(nullable: false);
//        situacao(nullable: false);
//        operadorResponsavel(nullable: false);
        contatosSolicitante(maxSize: 10000);
        contatosCartorio(maxSize: 10000);
        observacoesCartorio(maxSize: 10000);
        observacoesRegistro(maxSize: 10000);
    }

}
