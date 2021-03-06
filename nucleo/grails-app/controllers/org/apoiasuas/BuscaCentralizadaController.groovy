package org.apoiasuas

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.formulario.Formulario
import org.apoiasuas.redeSocioAssistencial.Servico
import org.apoiasuas.redeSocioAssistencial.ServicoController
import org.apoiasuas.seguranca.DefinicaoPapeis

class BuscaCentralizadaController extends AncestralController {

    public static final String EXEMPLO = "ex:"
    def fullTextSearchService
    def linkService
    def servicoService
    def formularioService

    static defaultAction = "list"

    @Secured([DefinicaoPapeis.STR_SUPER_USER])
    def index() {
        fullTextSearchService.index()
        render "Atualizando índices em backgroud"
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def list(FiltroBuscaCommand filtroBuscaCommand) {
        String palavraChave = filtroBuscaCommand.palavraChave
        if (! palavraChave)
            return render(view: "list")

        if (palavraChave.toLowerCase().startsWith(EXEMPLO))
            palavraChave = palavraChave.substring(EXEMPLO.length())

        FullTextSearchService.Resultado resultado = fullTextSearchService.search(palavraChave)

        List <FullTextSearchService.ObjetoEncontrado> temp = []
        resultado.objetosEncontrados.each { FullTextSearchService.ObjetoEncontrado objetoEncontrado ->
            def objeto = objetoEncontrado.objeto //CUIDADO! O objeto trazido da pesquisa vem semi-preenchido
            boolean ignorar = false
            if (objeto instanceof Servico) {
                Servico servicoInstance = servicoService.getServico(objeto.id)
                if (! servicoInstance.habilitado) {
                    ignorar = true;
                    resultado.total--;
                }
                objetoEncontrado.tipo = "serviço, benefício ou programa da rede intersetorial e socioassistencial"
                if (servicoInstance.imagemFileStorage)
                    objetoEncontrado.imagem = "<img src='${g.createLink(controller: 'servico', action: 'imagem', params: [imagemFileStorage: servicoInstance.imagemFileStorage])}'" +
                            "title='${objetoEncontrado.tipo}'/>"
                else
                    objetoEncontrado.imagem = asset.image(src: "usecases/rede-socio-assistencial.png", title: objetoEncontrado.tipo);
                objetoEncontrado.url = link([controller: "servico", action: "show", id: servicoInstance.id]){ servicoInstance.apelido }
                objetoEncontrado.detalhes = fullTextSearchService.formataDetalhe(objetoEncontrado.detalhes ?: servicoInstance.descricao)
            } else if (objeto instanceof Link) {
                Link linkInstance = linkService.getLink(objeto.id) //necessario preencher o objeto inteiro aa partir do banco de dados
                objetoEncontrado.detalhes = fullTextSearchService.formataDetalhe(objetoEncontrado.detalhes ?: linkInstance.instrucoes)
                if (linkInstance.tipo.file) {
                    objetoEncontrado.tipo = "arquivo para download"
                    objetoEncontrado.imagem = asset.image(src: "usecases/file.png", title: objetoEncontrado.tipo);
                    objetoEncontrado.url = link([controller: "link", action: "downloadFile", id: linkInstance.id]){ linkInstance.descricao }
                } else if (linkInstance.tipo.url) {
                    objetoEncontrado.tipo = "link externo"
                    objetoEncontrado.imagem = asset.image(src: "usecases/link.png", title: objetoEncontrado.tipo);
                    String urlCorrigida = linkInstance.url.toLowerCase().startsWith("http") ? linkInstance.url : "http://"+linkInstance.url
                    objetoEncontrado.url = link([target: "new", url: urlCorrigida]) { linkInstance.descricao }
                }
            } else if (objeto instanceof Formulario) {
                Formulario formularioInstance = formularioService.getFormulario(objeto.id)
                objetoEncontrado.tipo = "formulário online"
                objetoEncontrado.imagem = asset.image(src: "usecases/formulario.png", title: objetoEncontrado.tipo);
                objetoEncontrado.url = link([controller: "emissaoFormulario", action: "escolherFormulario",
                                             params: [idFormulario: formularioInstance.id]]) { formularioInstance.nome }
                objetoEncontrado.detalhes = fullTextSearchService.formataDetalhe(objetoEncontrado.detalhes ?: formularioInstance.descricao)
            } else if (objeto instanceof Map) {  //menu
                Map menu = objeto
                objetoEncontrado.tipo = "opção do sistema"
                objetoEncontrado.imagem = asset.image(src: "menu.png", title: objetoEncontrado.tipo);
                objetoEncontrado.url = link([url: menu.url]) { menu.meu_titulo }
                objetoEncontrado.detalhes = fullTextSearchService.formataDetalhe(objetoEncontrado.detalhes ?: menu.meus_detalhes)
            }
            if (! ignorar)
                temp.add(objetoEncontrado) //se nao houve nenhuma excessao de acesso, mantem o objeto na lista de resultado
        }
        resultado.objetosEncontrados = temp
        render(view: "list", model: [resultadoDTO: resultado, filtro: filtroBuscaCommand]);
    }

}

@grails.validation.Validateable
class FiltroBuscaCommand implements Serializable {
    String palavraChave
    String[] tipos

}
