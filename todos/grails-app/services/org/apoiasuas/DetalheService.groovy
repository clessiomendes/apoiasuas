package org.apoiasuas

import grails.transaction.NotTransactional
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apoiasuas.cidadao.detalhe.CampoDetalhe
import org.apoiasuas.lookup.DetalhesJSON

class DetalheService {

    static transactional = false;
    def lookupService

    /**
     * Dado um texto no formato JSON (@param detalhes), faz o parse dos seus elementos e os armazena em dominio.mapaDetalhes
     */
    @NotTransactional
    public static void parseDetalhes(DetalhesJSON dominio, String detalhes) {
        dominio.mapaDetalhes = [:]
        if (detalhes)
            new JsonSlurper().parseText(detalhes).each {
                def temp = dominio.mapaDetalhes.put(it.key, CampoDetalhe.parseJSON(it.value))
            }
    }

    /**
     * Busca nos parametros do request (já previamente filtrados para os campos de detalhe) os conteudos (alem de tipos e tabelas lookup correspondentes)
     * gerando, como resultado, uma estrutura JSON com uma entrada para cada campo de detalhe
     */
    @NotTransactional
    public String paramsToJson(Map params) {
        Map result = [:]
        //Passa a primeira vez pelos parametros buscando os nomes dos campos (ignorando os parametros auxiliares dos campos)
        params.each { def param ->
            if (!((String)param.key).startsWith('_') && !((String)param.key).endsWith('_tipo')
                    && !((String)param.key).endsWith('_tabela') ) {
                CampoDetalhe campoDetalhe = CampoDetalhe.parseRequest(param.key, params, lookupService);
                result << [(param.key): campoDetalhe.toJsonMap() /*Importante! não usar mecanismo automatico de conversao de objeto -> JSON do groovy*/ ]
            }
        }

        //Passa uma segunda vez pelos parametros buscando parametros auxiliares de checkbox (porque checkbox não selecionado é ignorado no post)
        params.each { def param ->
            if (((String)param.key).startsWith('_')) {
                String nomeCampo = param.key.toString().substring(1); //retira o _ para chegar ao nome do campo
                //Se não houver entrada do campo ainda...
                if (! result.containsKey(nomeCampo)) {
                    CampoDetalhe campoDetalhe = CampoDetalhe.parseRequest(nomeCampo, params, lookupService);
                    result << [(nomeCampo): campoDetalhe.toJsonMap() /*Importante! não usar mecanismo automatico de conversao de objeto -> JSON do groovy*/ ]
                }
            }
        }

        return JsonOutput.toJson(result)
    }

/*
    @NotTransactional
    public void registraJSON() {
        JSON.registerObjectMarshaller(CampoDetalhe) { CampoDetalhe it ->
            log.debug("marshaller 1: ${it}");
            return it.toJsonMap();
        }
    }
*/
}

