package org.apoiasuas.importacao

import apoiasuas.ImportacaoJob
import grails.async.Promise
import grails.async.Promises
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.json.JsonSlurper
import org.apoiasuas.AncestralController
import org.apoiasuas.fileStorage.FileStorageDTO
import org.apoiasuas.fileStorage.FileStorageService
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.AmbienteExecucao
import org.apoiasuas.util.ApoiaSuasException
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.apoiasuas.seguranca.DefinicaoPapeis
import uk.co.desirableobjects.ajaxuploader.exception.FileUploadException

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

/**
 * Created by admin on 08/05/2016.
 */
@Secured([DefinicaoPapeis.STR_USUARIO])
class ImportacaoFamiliasController extends AncestralController {

    ImportarFamiliasService servicoImportarFamilias;
    FileStorageService fileStorageService;

    private static final String SESSION_ID_IMPORTACAO_EM_CURSO = "SESSION_ID_IMPORTACAO_EM_CURSO"
    private static final String SESSION_DATA_ULTIMA_IMPORTACAO = "SESSION_ULTIMA_IMPORTACAO"

    static defaultAction = "list"
    static responseFormats = ['json']

    /**
     * Chamar com: curl http://localhost:8080/apoiasuas/importacaoFamilias/restUpload -F user=exportabd -F pass=senha -F qqFile=@c:\temp\bd.xlsx
     */
    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def restUpload() {
        log.debug("restUpload acionado")

        try {

            final UsuarioSistema operador = autentica(request)
            if (! operador) {
                response.status = 403 //Forbidden
                return render ([errorMessage: "Usuario e senha invalidos ou parametros (user e pass) ausentes"] as JSON)
            } else {
                log.debug("Usuario ${operador.username} autorizado a importar familias")
            }
            InputStream inputStream = selectMultipartInputStream(request)
            final ServicoSistema servicoSistema = segurancaService.servicoLogado

            DefinicoesImportacaoFamilias definicoes = servicoImportarFamilias.getDefinicoes(servicoSistema);
            if (!definicoes.linhaDoCabecalho || !definicoes.abaDaPlanilha) {
                response.status = 500
                return render ([errorMessage: "Configuracoes nao definidas (linha do cabecalho ou aba da planilha)"] as JSON)
            }
            TentativaImportacao tentativaImportacao = servicoImportarFamilias.registraNovaImportacao(definicoes.linhaDoCabecalho, definicoes.abaDaPlanilha, operador, servicoSistema)
            try {
                tentativaImportacao = servicoImportarFamilias.preImportacao(inputStream, tentativaImportacao, definicoes.linhaDoCabecalho, definicoes.abaDaPlanilha, false/*assincrono*/)
            } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
                response.status = 500
                return render ([errorMessage: "Formato invalido ou arquivo de importacao nao enviado"] as JSON)
            }
            log.info("pre importacao encerrada")
            if (!tentativaImportacao?.id) {
                response.status = 500
                return render ([errorMessage: "Erro na pre-importacao"] as JSON)
            }

            Map camposPreenchidos = servicoImportarFamilias.getDefinicoesImportacaoFamilia(servicoSistema)

            log.info("concluindo importacao")
            servicoImportarFamilias.concluiImportacao(camposPreenchidos, tentativaImportacao.id, operador, servicoSistema)

            def result = []
            result << "Importação concluída com sucesso (id ${tentativaImportacao.id}). Veja detalhes na tela de importações."
            render result as JSON

        } catch (FileUploadException e) {
            log.error("Failed to upload file.", e)
            render(["Erro no recebimento do arquivo: ${e.message}", org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e)] as JSON)
        } catch (Throwable t) {
            log.error("Erro na importação.", t)
            render (["Erro na importação: ${t.message}", org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(t)] as JSON)
        }

    }

    /**
     * Chamar com: curl http://localhost:8080/apoiasuas/importacaoFamilias/restUpload -F user=exportabd -F pass=senha -F qqFile=@c:\temp\out.txt
     */
    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def restUploadBatch() {
        log.debug("restUploadBatch acionado")

        try {

            final UsuarioSistema operador = autentica(request)
            if (! operador) {
                response.status = 403 //Forbidden
                return render ([errorMessage: "Usuario e senha invalidos ou parametros (user e pass) ausentes"] as JSON)
            } else {
                log.debug("Usuario ${operador.username} autorizado a importar familias")
            }

            //Upload de arquivo
            if (request instanceof MultipartHttpServletRequest) {
                MultipartFile multipartFile = ((MultipartHttpServletRequest)request).getFile(FileStorageDTO.INPUT_FILE)
                //FIXME: configurar o servidor de aplicacao para vetar arquivos grandes antes que eles cheguem ao servidor e causem grandes danos. Cuidado para nao vetar os tamanhos de arquivos de importacao de bancos de dados
                if (multipartFile.size > FileStorageDTO.MAX_FILE_SIZE)
                    throw new ApoiaSuasException("Tamanho do arquivo (${multipartFile.size}) maior do que o permitido (${FileStorageDTO.MAX_FILE_SIZE})")

                FileStorageDTO file = new FileStorageDTO(ImportarFamiliasService.NOME_PLANILHA_IMPORTACAO, multipartFile.bytes)
                fileStorageService.add(ImportarFamiliasService.BUCKET, file)
            } else {
                throw new ApoiaSuasException("Tipo inesperado de request para upload de arquivos: "+request.getClass().name)
            }




//            DefinicoesImportacaoFamilias definicoes = servicoImportarFamilias.getDefinicoes();
//            if (!definicoes.linhaDoCabecalho || !definicoes.abaDaPlanilha) {
//                response.status = 500
//                return render ([errorMessage: "Configuracoes nao definidas (linha do cabecalho ou aba da planilha)"] as JSON)
//            }
//            TentativaImportacao tentativaImportacao = servicoImportarFamilias.registraNovaImportacao(definicoes.linhaDoCabecalho, definicoes.abaDaPlanilha, operador, segurancaService.servicoLogado)
//            try {
//                tentativaImportacao = servicoImportarFamilias.preImportacao(inputStream, tentativaImportacao, definicoes.linhaDoCabecalho, definicoes.abaDaPlanilha, false/*assincrono*/)
//            } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
//                response.status = 500
//                return render ([errorMessage: "Formato invalido ou arquivo de importacao nao enviado"] as JSON)
//            }
//            log.info("pre importacao encerrada")
//            if (!tentativaImportacao?.id) {
//                response.status = 500
//                return render ([errorMessage: "Erro na pre-importacao"] as JSON)
//            }
//
//            Map camposPreenchidos = servicoImportarFamilias.getDefinicoesImportacaoFamilia()
//
//            log.info("concluindo importacao")
//            servicoImportarFamilias.concluiImportacao(camposPreenchidos, tentativaImportacao.id, operador)

            def result = []
            result << "Arquivo enviado e aguardando processamento."
            render result as JSON

        } catch (Throwable e) {
            log.error("Failed to upload file.", e)
            render(["Erro no recebimento do arquivo: ${e.message}", org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e)] as JSON)
        }
    }

    def list(Integer max) {
        //Atualiza o parametro do request "max"
        //http://groovy.codehaus.org/Operators#Operators-ElvisOperator(?:)
        params.max = max ?: 20
        params.order = 'desc'
        params.sort = 'id'

        //Gerando resposta à partir de uma listagem de usuarioSistema filtrada por params
        //Parametro model define quaisquer outros objetos que se deseja passar MAS EXCLUSIVAMENTE PARA RESPOSTAS HTML
        //http://grails.org/doc/2.3.x/ref/Controllers/respond.html
        def tentativasImportacao = TentativaImportacao.findAllByServicoSistemaSeguranca(segurancaService.getServicoLogado(), params);
        render(view: 'list', model:[tentativasImportacao: tentativasImportacao, tentativaImportacaoInstanceCount: tentativasImportacao.size()])
    }

    def show(TentativaImportacao importacao) {
        ResumoImportacaoDTO resumoImportacaoDTO = null
        //FIXME: Nao guardar informacoes sobre o processamento em uma estrutura JSON (possivel fonte de problemas caso a versao da classe ResumoImportacaoDTO venha a mudar)
        if (importacao?.informacoesDoProcessamento?.startsWith('{"'))
            resumoImportacaoDTO = new ResumoImportacaoDTO( new JsonSlurper().parseText(importacao.informacoesDoProcessamento))
        render view: 'show', model: [resumoImportacaoDTO: resumoImportacaoDTO, dtoTentatviaImportacao: importacao]
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
        try {

            //O componente ajax envia um arquivo diretamente (enctype="application/octet-stream") e não um form contendo um arquivo (enctype="multipart/form-data")
            InputStream inputStream = request.getInputStream();

            //idImportacao e enviado para o proximo request a fim de que se possa dar continuidade ao processo de importacao dos dados
            //à partir do que já foi persistido
            int linhaDoCabecalho = params.int("linhaDoCabecalho")
            int abaDaPlanilha = params.int("abaDaPlanilha")

            TentativaImportacao tentativaImportacao = servicoImportarFamilias.registraNovaImportacao(linhaDoCabecalho, abaDaPlanilha, segurancaService.usuarioLogado, segurancaService.servicoLogado)

            servicoImportarFamilias.preImportacao(inputStream, tentativaImportacao, linhaDoCabecalho, abaDaPlanilha, true/*assincrono*/)

            if (!tentativaImportacao?.id) {
                return render(text: [success: false] as JSON, contentType: 'text/json')
            }

            setIdImportacaoEmCurso(tentativaImportacao.id)
            return render(text: [success: true] as JSON, contentType: 'text/json')

        } catch (FileUploadException e) {
            log.error("Failed to upload file.", e)
            return render(text: [success: false] as JSON, contentType: 'text/json')
        }

    }

    /**
     * Obtem o arquivo de um dos inputs, PARTE de um form enviado via HTTP post
     * @param request
     * @return
     */
    private InputStream selectMultipartInputStream(HttpServletRequest request) {
        if (request instanceof MultipartHttpServletRequest) {
            return request.getFile("qqFile").inputStream
//            MultipartFile uploadedFile = ((MultipartHttpServletRequest) request).getFile('qqfile')
//            return uploadedFile.inputStream
        } else {
            throw new ApoiaSuasException("Tipo inesperado de request para upload de arquivos: "+request.getClass().name)
        }
    }

    private UsuarioSistema autentica(HttpServletRequest request) {
/*
        if (request instanceof MultipartHttpServletRequest) {
            String login = ((MultipartHttpServletRequest) request).getParameter('user')
            String pass = ((MultipartHttpServletRequest) request).getParameter('pass')
            return segurancaService.autentica(login, pass, DefinicaoPapeis.STR_WEB_SERVICE )
        }
*/
        String login = request.getParameter('user')
        String pass = request.getParameter('pass')
        return segurancaService.autentica(login, pass, DefinicaoPapeis.STR_WEB_SERVICE )
    }

    /**
     * Primeiro passo da importacao via browser. Seguido por create2()
     * @return
     */
    def create() {
        //Limpa a sessão
        session.removeAttribute(SESSION_ID_IMPORTACAO_EM_CURSO)
        DefinicoesImportacaoFamilias definicoes = servicoImportarFamilias.getDefinicoes(segurancaService.servicoLogado);
        request.linhaDoCabecalho = definicoes.linhaDoCabecalho
        request.abaDaPlanilha = definicoes.abaDaPlanilha
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
        if (! getIdImportacaoEmCurso()) {
            redirect(action: 'list')
            return false
        }

        List<ColunaImportadaCommand> colunasImportadas = servicoImportarFamilias.obtemColunasImportadas(getIdImportacaoEmCurso())
        if (! colunasImportadas) {
            //Algum erro na preImportacao pode fazer com que nao haja nenhuma coluna importada. Redirecionar para listagem com mensagem de erro
            flash.error = 'Erro na importação. Clique em "detalhes" para ver o motivo.'
            redirect(action: 'list')
            return false
        }

        wrapperCabecalhos.colunasImportadas =  colunasImportadas
        wrapperCabecalhos.camposBDDisponiveis = servicoImportarFamilias.obtemCamposBDDisponiveis(segurancaService.servicoLogado)
        return [wrapperCabecalhos: wrapperCabecalhos]
        //Adiciona um objeto de nome "wrapperCabecalhos" no escopo da página correspondente a esta action
    }

    /**
     * Chamado pelo botao "Processar" para 1-atualizar as configuracoes no banco e 2-concluir o processamento da planilha
     */
    def concluirImportacao(WrapperCabecalhosCommand wrapperCabecalhos) {
        ServicoSistema servicoSistema = segurancaService.servicoLogado;
        UsuarioSistema usuarioSistema = segurancaService.usuarioLogado;

        if (! getIdImportacaoEmCurso()) {
            redirect(action: 'list')
            return false
        }
        log.debug(["gravando definicoes de idImportacao ": getIdImportacaoEmCurso()])


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
        servicoImportarFamilias.atualizaDefinicoesImportacaoFamilia(camposPreenchidos, servicoSistema);

        if (wrapperCabecalhos.hasErrors()) {
            wrapperCabecalhos.camposBDDisponiveis = servicoImportarFamilias.obtemCamposBDDisponiveis(servicoSistema)
            render view: "create2", model: [wrapperCabecalhos: wrapperCabecalhos]
        } else {
            if (AmbienteExecucao.desenvolvimento) {
                servicoImportarFamilias.concluiImportacao(camposPreenchidos, getIdImportacaoEmCurso(), usuarioSistema, servicoSistema)
            }
            else //Rodar assincronamente
                Promise p = Promises.task {
                //Efetivar a importação dos dados da planilha, que foram gravados previamente em uma tabela temporaria no BD
                    servicoImportarFamilias.concluiImportacao(camposPreenchidos, getIdImportacaoEmCurso(), usuarioSistema, servicoSistema)
                }
            redirect action: 'show', id: getIdImportacaoEmCurso()
        }
    }

    public static DataUltimaImportacaoDTO getDataUltimaImportacao(HttpSession session) {
        return session[SESSION_DATA_ULTIMA_IMPORTACAO]
    }

    public static void setDataUltimaImportacao(HttpSession session, DataUltimaImportacaoDTO dataUltimaImportacao) {
        session[SESSION_DATA_ULTIMA_IMPORTACAO] = dataUltimaImportacao
    }

    private Long getIdImportacaoEmCurso() {
        return session[SESSION_ID_IMPORTACAO_EM_CURSO]
    }

    private void setIdImportacaoEmCurso(Long idImportacao) {
        session[SESSION_ID_IMPORTACAO_EM_CURSO] = idImportacao
    }

    def fire() {
//        ImportacaoJob.schedule("0 35 16 ? * *")
        ImportacaoJob.triggerNow();
        System.sleep(1000)//1s
        forward(action: 'list')
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
