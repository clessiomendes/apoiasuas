package org.apoiasuas.importacao

import grails.converters.JSON
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import uk.co.desirableobjects.ajaxuploader.exception.FileUploadException

import javax.servlet.http.HttpServletRequest

/**
 * Tentativa FRUSTRADA (por enquanto) de usar webflow em nosso controler.
 * Problemas não resolvidos até agora:
 * 1) NullPointerException no encerramento do fluxo, parece estar vindo da bibiloteca de gerenciamento de transações do próprio Spring.
 * 2) Ajax. Não conseguimos manipular o componente ajax para que consiga interagir dentro da filosofia dos fluxos.
 * 3) A chamada aos serviços está levantando uma excecao sem grandes explicacoes (o bean não está nulo). Possivelmente tem a mesma origem da excessao das transacoes.
 */
class FluxoImportacaoFamiliasController {

//    static allowedMethods = [update: "PUT"]

    def importarFamiliasService

    def fluxoImportacaoFlow = {

        estadoInicial{
            action {
                if (! params.containsKey("idImportacao")) return error()
                long idImportacao = params.long("idImportacao")

                flow.idImportacao = idImportacao

                WrapperCabecalhosCommand wrapperCabecalhos = new WrapperCabecalhosCommand()
                wrapperCabecalhos.colunasImportadas = importarFamiliasService.obtemColunasImportadas(idImportacao)
                wrapperCabecalhos.camposBDDisponiveis = importarFamiliasService.obtemCamposBDDisponiveis()
                return [wrapperCabecalhos: wrapperCabecalhos]
            }
            on("success").to("estadoListaDeCabecalhos")
            on("error").to("estadoCancelado")
        }

        estadoListaDeCabecalhos()   {
            on("eventoProcessar").to("estadoCritica")
            on("eventoCancelar").to("estadoCancelado")
        }

        estadoCritica {
            on("eventoCancelar").to("estadoCancelado")
        }

        estadoCancelado()

    }

    private void bindCabecalhos(WrapperCabecalhosCommand wrapperCabecalhos, long idImportacao) {
        log.debug(["gravando definicoes de idImportacao ": idImportacao])

        /**
         * Antes de chamar os servicos, temos que interpretar as opcoes feitas pelo usuario em caixas de selecao e
         * traduzi-las no mapa camposPreenchidos. Chave:"campo no BD", Valor:"coluna na planilha"
         */
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

        //Gravar as novas definicoes (obs1: transacao separada da idImportacao em si porque, caso haja algum erro, as
        //definicoes que acabaram de ser feitas pelo usuario não serão perdidas)
        //obs2: as escolhas do usuario serao gravadas independentes de eventuais erros de validaca (duplicidade)
        importarFamiliasService.update(camposPreenchidos);
    }

}
