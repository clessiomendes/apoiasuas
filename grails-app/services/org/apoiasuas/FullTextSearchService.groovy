package org.apoiasuas

import grails.async.Promise
import grails.transaction.NotTransactional
import grails.transaction.Transactional
import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.util.FullTextSearchUtils
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.json.JsonXContent
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.highlight.HighlightBuilder
import org.grails.plugins.elasticsearch.ElasticSearchBootStrapHelper
import org.grails.plugins.elasticsearch.util.GXContentBuilder

import static grails.async.Promises.*
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder

@Transactional(readOnly = true)
class FullTextSearchService {

    public static final int MAX_SIZE_DETALHES = 200
    public static final int MAX_RESULTADOS = 30

    def elasticSearchService
    def elasticSearchAdminService
    def segurancaService
    def elasticSearchClient

    public void index() {
        Promise p = task {
            elasticSearchService.index()
            elasticSearchAdminService.createIndex("org.apoiasuas.menu")
            Map mapping = [menu:[properties:[
//                    nome:[index:"analyzed",boost:"10",type:"string",include_in_all:"true",term_vector:"with_positions_offsets"]
//                    meu_titulo:[index:"analyzed",boost:"10",type:"string",include_in_all:"true",term_vector:"with_positions_offsets"],
//                    meus_detalhes:[index:"analyzed",boost:"5",type:"string",include_in_all:"true",term_vector:"with_positions_offsets"],
                    url:[index:"not_analyzed",type:"string",include_in_all:"false"]
            ]]]
            elasticSearchAdminService.createMapping("org.apoiasuas.menu","menu", mapping)
            /*
            elasticSearchClient.prepareIndex()
                    .setIndex(scm.indexingIndex)
                    .setType(scm.elasticTypeName)
                    .setId(key.id)
                    .setSource(json)
            */
            IndexRequest indexRequest = new IndexRequest("org.apoiasuas.menu","menu", "9999");
            XContentBuilder json = jsonBuilder()
                    .startObject()
                    .field("url", "emissaoFormulario/escolherFamilia")
                    .field("meu_titulo", "Emissão de formulários")
                    .field("meus_detalhes", "Emite formulários de gratuidade para segunda via de identidade, fotos 3x4, declaração de pobreza, etc.")
                    .endObject();

            indexRequest.source(json);
            IndexResponse response = elasticSearchClient.index(indexRequest).actionGet();
//            LOG.debug("Indexing $key.clazz (index: $scm.indexingIndex , type: $scm.elasticTypeName) of id $key.id and source ${json.string()}")
            log.debug("Indexing source ${json.string()}")


        }
        p.onError { Throwable err ->
            log.error("Erro indexando busca textual", err)
        }
        p.onComplete { result ->
            log.info("Indexação de busca textual terminada")
        }
    }

    public class Resultado {
        int total
        List<ObjetoEncontrado> objetosEncontrados = []
        public void addResult(Object objeto, String fragmento) {
            objetosEncontrados << new ObjetoEncontrado(objeto: objeto, detalhes: fragmento)
        }
    }

    public class ObjetoEncontrado {
        Object objeto
        String url
        String tipo
        String imagem
        String detalhes
    }

    public Resultado search(String palavraChave) {
        Closure highlighter = sintaxeHighlighter {
            requireFieldMatch = false
            field FullTextSearchUtils.MEUS_DETALHES, MAX_SIZE_DETALHES, 1
//            field '*'
            preTags '<b>'
            postTags '</b>'
        }

        List<Long> idsMaes = segurancaService.getAbrangenciasTerritoriaisAcessiveis().collect {it.id}
        log.debug("Hierarquia da abrangencia territorial do servico logado: ${idsMaes.join(',')}")

// @formatter:off
        /**
         * Constroi uma pesquisa que verifica:
         *          Se servicoSistema == servicoLogado exibe
         *          Se servicoSistema NULO ou NA => {
         *              Se abrangenciaTerritorial IN maes => exibe
         *              Se abrangenciaTerritorial NULO ou NA => exibe
         *              (Se abrangenciaTerritorial NOT IN maes => NAO EXIBE)
         *          }
         *          (Se servicoSistema != servicoLogado => NAO EXIBE)
         */
        QueryBuilder query = QueryBuilders.boolQuery()
            .must(QueryBuilders.simpleQueryStringQuery(palavraChave))
            .filter(QueryBuilders.boolQuery()
                .should/*or*/(QueryBuilders.termQuery(FullTextSearchUtils.ID_SERVICO_SISTEMA, segurancaService.getServicoLogado().id )) //OU esta preenchido com o servico logado
                .should/*or*/(QueryBuilders.boolQuery() //OU uma das condicoes abaixo
                    .must/*and*/(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(FullTextSearchUtils.ID_SERVICO_SISTEMA))) //o campo idServicoSistema esta nulo
//                    .mustNot(QueryBuilders.existsQuery(FullTextSearchUtils.ID_SERVICO_SISTEMA)) //o campo idServicoSistema esta nulo
                    .must/*and*/(QueryBuilders.boolQuery() //E uma das condicoes abaixo
                        .should/*or*/(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(FullTextSearchUtils.ID_COMPARTILHADO_COM))) //OU o campo comparilhadoCom esta nulo
                        .should/*or*/(QueryBuilders.termsQuery(FullTextSearchUtils.ID_COMPARTILHADO_COM, idsMaes)) //OU a abrangencia territorial escolhida esta dentro da hierarquia do servico logado
                    )
                )
            )
// @formatter:on


/*
        Closure closureQuery = sintaxeQuery {
            bool {
                must {
                    query_string(query: params.query)
                }
                if (params.firstname) {
                    must {
                        term(firstname: params.firstname)
                    }
                }
            }
        }
*/

//        def lalala = elasticSearchService.search(searchType:'dfs_query_and_fetch', closureQuery)
        log.debug("Parametros da busca centralizada: "+query.toString())

        Map elasticSearchResults = elasticSearchService.search(query, null,
//        Map elasticSearchResults = elasticSearchService.search(palavraChave,
                [size: MAX_RESULTADOS, highlight: highlighter /*from:0, size:30, types:["org.apoiasuas.Link"]*/]);
        Resultado result = new Resultado(total: elasticSearchResults.total)
        for (int i = 0; i < elasticSearchResults.searchResults.size; i++) {
            String fragmentosDetalhes
            final def fragments = elasticSearchResults.highlight[i][FullTextSearchUtils.MEUS_DETALHES]?.fragments
            if (fragments)
                fragmentosDetalhes = "..."+fragments[0].toString()+"..."
            result.addResult(elasticSearchResults.searchResults[i], fragmentosDetalhes)
        }
        return result
    }

    private Closure sintaxeHighlighter(@DelegatesTo(HighlightBuilder) Closure closure) {
        return closure
    }

    @NotTransactional
    public String formataDetalhe(String s) {
        if (s) {
            s = s.replaceAll("(\\r|\\n|\\t)", " ")
            return s.substring(0, Math.min(MAX_SIZE_DETALHES+6/*...asd...*/, s.size()) )
        }
    }


}
