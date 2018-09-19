package org.apoiasuas.pedidocertidao

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.formulario.ReportDTO
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.CollectionUtils
import org.apoiasuas.util.SimNao
import org.apoiasuas.util.StringUtils

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class PedidoCertidaoController extends AncestralController {

    def pedidoCertidaoService;

    def edit(PedidoCertidao pedido) {
        if (! pedido)
            return notFound()
        render view: 'edit', model: getModelEdicao(pedido)
    }

    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: ["Pedido de certidão não existe", params.id])
        return redirect(action: "list");
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def create(Long idFamilia) {
        PedidoCertidao pedido = new PedidoCertidao(params)
        if (idFamilia)
            pedido.familia = Familia.get(idFamilia);
        render view: 'create', model: getModelEdicao(pedido)
    }

/*
    private Map<String, Object> getModelExibicao(PedidoCertidao pedido) {
        [pedidoInstance: pedido,
             operadores: getTecnicosOrdenadosController(false, pedido.operadorResponsavel ? [pedido.operadorResponsavel] : [])]
    }
*/

    private LinkedHashMap<String, Object> getModelEdicao(PedidoCertidao pedido) {
        [pedidoInstance: pedido, membrosFamiliaresJsonDTO: pedido?.familia ? membrosFamiliaComum(pedido.familia) : [],
             operadores: getTecnicosOrdenadosController(false, pedido.operadorResponsavel ? [pedido.operadorResponsavel] : [])]
    }

    /**
     * Fatoracao do codigo comum a todas as actions que gravam o pedido (save, save and download...)
     * Retorna falso em caso de erros de validacao
     */
    private boolean saveComum(PedidoCertidao pedido, Boolean familiaSemCadastro) {
        if (! pedido)
            return notFound()

        boolean modoCriacao = pedido.id == null

        if (modoCriacao) {
            pedido.servicoSistemaSeguranca = segurancaService.getServicoLogado();
            pedido.situacao = PedidoCertidao.Situacao.NOVO_PEDIDO;
        }

        //Validações:
        boolean validado = pedido.validate();
        validado = validado & validaVersao(pedido);
        if (modoCriacao && ! pedido.familia?.id && familiaSemCadastro != true) {
            validado = false;
            pedido.errors.reject("", "Escolha uma família ou marque [família sem cadastro]");
        }

        //Grava
        if (validado) {
            pedidoCertidaoService.grava(pedido);
            return true;
        } else {
            return false;
        }
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def save(PedidoCertidao pedido, Boolean familiaSemCadastro) {
        boolean modoCriacao = pedido.id == null

        //exibe o formulario novamente em caso de problemas na validacao
        if (! saveComum(pedido, familiaSemCadastro))
            return render((modoCriacao ? [view: "create"] : [template: "form"]) + [model: getModelEdicao(pedido)]);

        flash.message = "Pedido gravado com sucesso";
        if (modoCriacao)
            render view: 'edit', model: getModelEdicao(pedido)
        else
            render template: 'form', model: getModelEdicao(pedido)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveAndDownloadDeclaracao(PedidoCertidao pedido, Boolean familiaSemCadastro) {
        return saveAndDonwload(pedido, familiaSemCadastro, true);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveAndDownloadPedido(PedidoCertidao pedido, Boolean familiaSemCadastro) {
        return saveAndDonwload(pedido, familiaSemCadastro, false);
    }

    private saveAndDonwload(PedidoCertidao pedido, Boolean familiaSemCadastro, boolean somenteDeclaracao) {
        boolean modoCriacao = pedido.id == null

        //exibe o formulario novamente em caso de problemas na validacao
        if (! saveComum(pedido, familiaSemCadastro))
            return render((modoCriacao ? [view: "create"] : [template: "form"]) + [model: getModelEdicao(pedido)]);

        //Guarda na sessao asinformacoes necessarias para a geracao do arquivo a ser baixado (que sera baixado por um
        //javascript que rodara automaticamente na proxima pagina)
        if (somenteDeclaracao)
            setReportsParaBaixar(session, [pedidoCertidaoService.imprimirDeclaracaoPobreza(pedido)])
        else
            setReportsParaBaixar(session, [pedidoCertidaoService.imprimirPedidoCartorio(pedido)])

        flash.message = "Pedido gravado com sucesso";
        if (modoCriacao)
            render view: 'edit', model: getModelEdicao(pedido)
        else
            render template: 'form', model: getModelEdicao(pedido)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def acaoPedido(PedidoCertidao pedido, String acao) {
        PedidoCertidao.Situacao enumAcao = PedidoCertidao.Situacao.valueOf(acao);
        if (! pedido)
            return notFound();

        if (enumAcao == PedidoCertidao.Situacao.DESFAZER)
            pedidoCertidaoService.desfazer(pedido)
        else
            pedidoCertidaoService.novaSituacao(pedido, enumAcao);
        flash.message = PedidoCertidao.Situacao.valueOf(acao).descricao;
        return render(template: 'historico', model: getModelEdicao(pedido))
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def inserirComentario(PedidoCertidao pedido, String descricao) {
        if (! pedido)
            return notFound();

        pedidoCertidaoService.inserirComentario(pedido, descricao);
        flash.message = "Comentário registrado";
        return render(template: 'historico', model: getModelEdicao(pedido))
    }

    def list(FiltroPedidoCertidaoCommand filtro) {
        params.max = params.max ?: 5
        Map filtrosUsados = params.findAll { it.value }

        PagedResultList pedidos = pedidoCertidaoService.procurar(params, filtro);

//        params.max = 200
//        ArrayList<PedidoCertidao> pedidosList = PedidoCertidao.findAll().sort { it.id }
//        ArrayList<PedidoCertidao> pedidosList = PedidoCertidao.findAllByServicoSistemaSeguranca(getServicoCorrente(), params).sort { it.id }
        render(view: 'list', model:[pedidosList: pedidos, pedidosCount: pedidos.getTotalCount(), filtro: filtrosUsados,
               operadores: getOperadoresOrdenadosController(false)]);
    }

    def getMembrosFamiliaJson(Familia familia) {
//        Familia familia = Familia.get(idFamilia);
        if (familia)
            render membrosFamiliaComum(familia);
        else
            render(status: 500, text: "Familia não encontrada") as JSON
    }

    private JSON membrosFamiliaComum(Familia familia) {
        return familia.getMembrosOrdemAlfabetica(true).collect { filtraCampos(it) } as JSON
    }

    /**
     * Transforma cada cidadao em um Mapa de campos
     * @param cidadao
     * @return
     */
    public static Map filtraCampos(Cidadao cidadao) {
        return [
                id: cidadao.id,
                nome: cidadao.nomeCompleto,
                nascimento: cidadao.dataNascimento?.format("dd/MM/yyyy"),
                nomeMae: cidadao.nomeMae,
                nomePai: cidadao.nomePai,
                identidade: cidadao.identidade,
                cpf: cidadao.cpf,
                nacionalidade: cidadao.mapaDetalhes?.nacionalidade?.toString(),
                profissao: cidadao.mapaDetalhes?.ocupacao?.toString(),
                estadoCivil: cidadao.mapaDetalhes?.estadoCivil?.toString(),
                endereco: CollectionUtils.join([cidadao.familia.endereco.obtemEnderecoBasico(), cidadao.familia.endereco.bairro], ", "),
                municipio: cidadao.familia.endereco.municipio,
                uf: cidadao.familia.endereco.UF,
        ]
    }

    public static Map situacoesPersonalizadas() {
        Map result = [:];
        return [(PedidoCertidao.Situacao.NOVO_PEDIDO): "Pedido não Enviado",
                (PedidoCertidao.Situacao.PEDIDO_ENVIADO): "Aguardando Certidão do Cartório",
                (PedidoCertidao.Situacao.CERTIDAO_RECEBIDA): "Certidão Disponível para Entrega",
                (PedidoCertidao.Situacao.CERTIDAO_ENTREGUE): PedidoCertidao.Situacao.CERTIDAO_ENTREGUE.descricao,
                (PedidoCertidao.Situacao.PEDIDO_CANCELADO): PedidoCertidao.Situacao.PEDIDO_CANCELADO.descricao,
        ]

    }

}

@grails.validation.Validateable
class FiltroPedidoCertidaoCommand implements Serializable {

    String nomeOuCad
    String cartorioOuMinicipio
    PedidoCertidao.Situacao situacao
    UsuarioSistema responsavel
    Boolean cartorioIndefinido
}
