package org.apoiasuas.importacao

import grails.async.Promise
import grails.async.Promises
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.apoiasuas.seguranca.DefinicaoPapeis
import uk.co.desirableobjects.ajaxuploader.exception.FileUploadException

import javax.servlet.http.HttpServletRequest

@Secured([DefinicaoPapeis.USUARIO])
class ImportacaoFamiliasController {

    def importarFamiliasService

    static defaultAction = "list"

    def list(Integer max) {
        //Atualiza o parametro do request "max"
        //http://groovy.codehaus.org/Operators#Operators-ElvisOperator(?:)
        params.max = max ?: 20
        params.order = 'desc'
        params.sort = 'id'

        //Gerando resposta à partir de uma listagem de usuarioSistema filtrada por params
        //Parametro model define quaisquer outros objetos que se deseja passar MAS EXCLUSIVAMENTE PARA RESPOSTAS HTML
        //http://grails.org/doc/2.3.x/ref/Controllers/respond.html
        render view: 'list', model:[tentativasImportacao: TentativaImportacao.list(params), tentativaImportacaoInstanceCount: TentativaImportacao.count()]
    }

    def show(TentativaImportacao importacao) {
        ResumoImportacaoDTO resumoImportacaoDTO = null
        //FIXME: Nao guardar informacoes sobre o processamento em uma estrutura JSON (possivel fonte de problemas caso a versao da classe ResumoImportacaoDTO venha a mudar)
        if (importacao?.informacoesDoProcessamento?.startsWith('{"'))
            resumoImportacaoDTO = JSON.parse(importacao.informacoesDoProcessamento)
        render view: 'show', model: [resumoImportacaoDTO: resumoImportacaoDTO, dtoTentatviaImportacao: importacao]
    }


    def create() {
        //Limpa a sessão
        session.removeAttribute("idImportacao")
        DefinicoesImportacaoFamilias definicoes = importarFamiliasService.getDefinicoes();
        request.linhaDoCabecalho = definicoes.linhaDoCabecalho
        request.abaDaPlanilha = definicoes.abaDaPlanilha
    }

    def progressoUpload() {
        log.debug("arquivo subindo... " + params.loaded + " de " + params.total )
        return render(text: [success: true] as JSON, contentType: 'text/json')
    }

    /**
     * Chamado pelo botão de upload através de uma requisição AJAX. Faz um pre-processamento da planilha guardando suas
     * linhas no banco de dados sem nenhum parsing (utilizando formatacao conteudoLinhaPlanilha)
     */
    def upload() {
        int tempoSessao
        try {

            InputStream inputStream = selectInputStream(request)
            //idImportacao e enviado para o proximo request a fim de que se possa dar continuidade ao processo de importacao dos dados
            //à partir do que já foi persistido
            int linhaDoCabecalho = params.int("linhaDoCabecalho")
            int abaDaPlanilha = params.int("abaDaPlanilha")

            TentativaImportacao tentativaImportacao = importarFamiliasService.registraNovaImportacao(linhaDoCabecalho, abaDaPlanilha)

//            Promise p = Promises.task() {
                importarFamiliasService.preImportacao(inputStream, tentativaImportacao, linhaDoCabecalho, abaDaPlanilha)
//            }

            if (!tentativaImportacao?.id) {
                return render(text: [success: false] as JSON, contentType: 'text/json')
            }

            session.idImportacao = tentativaImportacao.id
            return render(text: [success: true] as JSON, contentType: 'text/json')

        } catch (FileUploadException e) {
            log.error("Failed to upload file.", e)
            return render(text: [success: false] as JSON, contentType: 'text/json')
        }

    }

    private InputStream selectInputStream(HttpServletRequest request) {
        if (request instanceof MultipartHttpServletRequest) {
            MultipartFile uploadedFile = ((MultipartHttpServletRequest) request).getFile('qqfile')
            return uploadedFile.inputStream
        }
        return request.inputStream
    }

    /**
     * Uma vez feito o upload, um javascript automaticamente recarrega a pagina usando esta action, que ira mostrar as
     * colunas obtidas da planilha para que o usuario faca a correlacao com os campos do BD antes de efetivar a importacao.
     * Obs:
     * 1) Usamos o termo COLUNA para nos referir ao titulo das colunas como eles aparecem na primeira linha da planilha.
     * 2) Usamos o termo CAMPO para nos referir a cada campo da classe de dominio importacao.DefinicoesImportacaoFamilias
     */
    def create2(/*WrapperCabecalhosCommand wrapperCabecalhos - este command object virá sempre vazio mas poderá ser preenchido e passado para a gsp*/) {
        WrapperCabecalhosCommand wrapperCabecalhos = new WrapperCabecalhosCommand()
        if (! session.idImportacao) {
            redirect(action: 'list')
            return false
        }
        long idImportacao = session.idImportacao


        List<ColunaImportadaCommand> colunasImportadas = importarFamiliasService.obtemColunasImportadas(idImportacao)
        if (! colunasImportadas) {
            //Algum erro na preImportacao pode fazer com que nao haja nenhuma coluna importada. Redirecionar para listagem com mensagem de erro
            flash.error = 'Erro na importação. Clique em "detalhes" para ver o motivo.'
            redirect(action: 'list')
            return false
        }

        wrapperCabecalhos.colunasImportadas =  colunasImportadas
        wrapperCabecalhos.camposBDDisponiveis = importarFamiliasService.obtemCamposBDDisponiveis()
        return [wrapperCabecalhos: wrapperCabecalhos]
        //Adiciona um objeto de nome "wrapperCabecalhos" no escopo da página correspondente a esta action
    }
/*
    def reProcessar() {
        long ultimaImportacao = importarFamiliasService.ultimaImportacao()
        log.debug(ultimaImportacao)
        if (ultimaImportacao)
            session.idImportacao = ultimaImportacao
        redirect action: "preProcessar"
    }
*/

    /**
     * Chamado pelo botao "Processar" para 1-atualizar as configuracoes no banco e 2-concluir o processamento da planilha
     */
    def concluirImportacao(WrapperCabecalhosCommand wrapperCabecalhos) {
        if (! session.idImportacao) {
            redirect(action: 'list')
            return false
        }
        long idImportacao = session.idImportacao
        log.debug(["gravando definicoes de idImportacao ": idImportacao])


        /* Antes de chamar os servicos, temos que interpretar as opcoes feitas pelo usuario em caixas de selecao e
         * traduzi-las no mapa camposPreenchidos. Chave:"campo no BD", Valor:"coluna na planilha" */
        Map camposPreenchidos = [:]
        Set camposDuplicados = []

        //Percorre todos os selects cujos nomes sao padronizados como selectColunaImportada0..99
        wrapperCabecalhos.colunasImportadas.eachWithIndex { ColunaImportadaCommand colunaImportada, int i ->
//        for (int i = 0; i < params.hiddenTotalColunas.toInteger(); i++ ) {

            //COLUNA DA PLNAILHA correspondente ao select que estamos processando
            String valorColunaImportada = colunaImportada.nome
            log.debug([valorColunaImportada: valorColunaImportada])

            //Valor selecionado no select é o nome do CAMPO NO BD
            String valorCampoBD = colunaImportada.campoBDSelecionado
            log.debug([valorCampoBD: valorCampoBD])

            //Se o usuario tiver preenchido este select, adicionamos o par em um mapa de campos preenchidos
            if (valorCampoBD) {
                if (camposPreenchidos.containsKey(valorCampoBD)) {
                    camposDuplicados << valorCampoBD
                } else {
                    camposPreenchidos.put(valorCampoBD, valorColunaImportada)
                }
            }
        }

        //Se forem detectadas duplicidades de escolha de campos...
        if (camposDuplicados) {
            wrapperCabecalhos.colunasImportadas.eachWithIndex { ColunaImportadaCommand colunaImportada, int i ->
                if (camposDuplicados.contains(colunaImportada.campoBDSelecionado)) {
                    //...destacar em vermelho cada COLUNA IMPORTADA cujo campo BD selecionado estiver em duplicidade
                    //obs: não adicionar mensagem de erro específico para este campo, se não ele será renderizado múltiplas vezes na tela
                    wrapperCabecalhos.errors.rejectValue("colunasImportadas[" + i + "]", "")
                }
            }
        }

        //Adicionando mensagens de erro globais para os campos em duplicidade detectados, que serão renderizadas no topo da tela
        camposDuplicados.each {
            wrapperCabecalhos.errors.reject("campo.db.duplicado", [it].toArray(), "")
        }

        //Gravar as novas definicoes (obs1: transacao separada da importacao em si porque, caso haja algum erro, as
        //definicoes que acabaram de ser feitas pelo usuario não serão perdidas)
        //obs2: as escolhas do usuario serao gravadas independentes de eventuais erros de validaca (duplicidade)
        importarFamiliasService.atualizaDefinicoesImportacaoFamilia(camposPreenchidos);

        if (wrapperCabecalhos.hasErrors()) {
            wrapperCabecalhos.camposBDDisponiveis = importarFamiliasService.obtemCamposBDDisponiveis()
            render view: "create2", model: [wrapperCabecalhos: wrapperCabecalhos]
        } else {
            //Rodar assincronamente
            Promise p = Promises.task {
                //Efetivar a importação dos dados da planilha, que foram gravados previamente em uma tabela temporaria no BD
                importarFamiliasService.concluiImportacao(camposPreenchidos, idImportacao)
            }
            redirect action: 'show', id: idImportacao
        }
    }
}

class ColunaImportadaCommand implements Serializable {
    String nome
    String campoBDSelecionado
    String conteudoExemplo

    ColunaImportadaCommand() { }

    ColunaImportadaCommand(String nome, String campoBDSelecionado, String conteudoExemplo) {
        this.nome = nome
        this.campoBDSelecionado = campoBDSelecionado
        this.conteudoExemplo = conteudoExemplo
    }
}

class WrapperCabecalhosCommand implements Serializable {
    List<String> camposBDDisponiveis = [].withLazyDefault { return "" }
    List<ColunaImportadaCommand> colunasImportadas = [].withLazyDefault { return new ColunaImportadaCommand() }
}
