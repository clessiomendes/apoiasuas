package org.apoiasuas

import grails.async.Promise
import grails.transaction.NotTransactional
import grails.transaction.Transactional
import org.apoiasuas.formulario.Formulario
import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial
import org.apoiasuas.redeSocioAssistencial.Servico
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.util.FullTextSearchUtils
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.json.JsonXContent
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.plugin.mapper.attachments.MapperAttachmentsPlugin
import org.elasticsearch.search.highlight.HighlightBuilder
import org.elasticsearch.search.sort.SortBuilder
import org.elasticsearch.search.sort.SortOrder
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
    def searchableClassMappingConfigurator


    public void index() {
//        Promise p = task {

/*
documentacao: https://static.javadoc.io/org.elasticsearch/elasticsearch/2.3.0/org/elasticsearch/client/Client.html

tentando setar os novos plugins em ClientNodeFactoryBean.getObject():
        // Avoiding this:
//        node = nb.node()
        node =  new org.elasticsearch.node.Node(InternalSettingsPreparer.prepareEnvironment(nb.settings().build(), null), Version.CURRENT,
                Collections.singleton(Class.forName("org.elasticsearch.plugin.mapper.attachments.MapperAttachmentsPlugin")));
        node.start()
        def client = node.client()

*/



            log.debug("remapeando")
//            searchableClassMappingConfigurator.configureAndInstallMappings();
//            elasticSearchService.unindex();
        try {
            elasticSearchAdminService.deleteIndex("org.apoiasuas.menu")
        } catch (E) {
        }
            elasticSearchAdminService.createIndex("org.apoiasuas.menu")
            Map mapping = [menu:[properties:[
//                    nome:[index:"analyzed",boost:"10",type:"string",include_in_all:"true",term_vector:"with_positions_offsets"]
                    meu_titulo:[index:"analyzed",boost:"10",type:"string",include_in_all:"true",term_vector:"with_positions_offsets"],
                    meus_detalhes:[index:"analyzed",boost:"5",type:"string",include_in_all:"true",term_vector:"with_positions_offsets"],
                    url:[index:"not_analyzed",type:"string",include_in_all:"false"]
            ]]]
            elasticSearchAdminService.createMapping("org.apoiasuas.menu","menu", mapping)

//            elasticSearchClient.prepareIndex()
//                    .setIndex(scm.indexingIndex)
//                    .setType(scm.elasticTypeName)
//                    .setId(key.id)
//                    .setSource(json)

            IndexRequest indexRequest = new IndexRequest("org.apoiasuas.menu","menu", "9999");
            XContentBuilder json = jsonBuilder()
                    .startObject()
                    .field("url", "emissaoFormulario/escolherFormulario")
                    .field("meu_titulo", "Emissão de formulários")
                    .field("meus_detalhes", "Emite formulários de clessio gratuidade para segunda via de identidade, fotos 3x4, declaração de pobreza, etc.")
                    .endObject();

            indexRequest.source(json);
            IndexResponse response = elasticSearchClient.index(indexRequest).actionGet();
//            log.debug("Indexing source ${json.string()}")
//            elasticSearchService.index();
//        }
//        p.onError { Throwable err ->
//            log.error("Erro indexando busca textual", err)
//        }
//        p.onComplete { result ->
//            log.info("Indexação de busca textual terminada")
//        }
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
/*
        Closure sort = sortBuilder {
            "_score" {
                order SortOrder.DESC
            }
        }
*/

        Closure highlighter = sintaxeHighlighter {
            requireFieldMatch = false
            field FullTextSearchUtils.MEUS_DETALHES, MAX_SIZE_DETALHES, 1
//            field '*'
            preTags '<b>'
            postTags '</b>'
        }

        List<Long> idsMaes = segurancaService.getAbrangenciasTerritoriaisAcessiveis().collect {it.id}
        log.debug("Hierarquia da abrangencia territorial do servico logado: ${idsMaes.join(',')}")

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

// @formatter:off
        QueryBuilder query = QueryBuilders.boolQuery()
            .must(QueryBuilders.simpleQueryStringQuery(palavraChave))
            .filter(QueryBuilders.boolQuery()
                .should/*or*/(QueryBuilders.termQuery(FullTextSearchUtils.ID_SERVICO_SISTEMA, segurancaService.getServicoLogado().id )) //OU esta preenchido com o servico logado
                .should/*or*/(QueryBuilders.boolQuery() //OU uma das condicoes abaixo
                    .must/*and*/(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(FullTextSearchUtils.ID_SERVICO_SISTEMA))) //o campo idServicoSistema esta nulo
                    .must/*and*/(QueryBuilders.boolQuery() //E uma das condicoes abaixo
                        .should/*or*/(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(FullTextSearchUtils.ID_COMPARTILHADO_COM))) //OU o campo comparilhadoCom esta nulo
                        .should/*or*/(QueryBuilders.termsQuery(FullTextSearchUtils.ID_COMPARTILHADO_COM, idsMaes)) //OU a abrangencia territorial escolhida esta dentro da hierarquia do servico logado
                    )
                )
            )
// @formatter:on

        log.debug("Parametros da busca  centralizada: "+query.toString())

        //NÃO PRECISA ORDENAR EXPLICITAMENTE - por padrão a resposta vem ordenada pelo _score descendente
        Map elasticSearchResults = elasticSearchService.search(query, null,
                [size: MAX_RESULTADOS, highlight: highlighter, analyzer: ["asciifolding"]
//                , sort: "_score", from:0, size:30, types:["org.apoiasuas.Link"]
                ]);

/*
        Map elasticSearchResults = elasticSearchService.search(
                [size: MAX_RESULTADOS, highlight: highlighter, sort: "_score"
//from:0, size:30, types:["org.apoiasuas.Link"]
])
        {
//            bool {
//                must {
//                    query_string(query: palavraChave)
//                }
//                must {
//                    query_string(query: palavraChave)
//                }
//            }
        }
*/

        Resultado result = new Resultado(total: elasticSearchResults.total)
        for (int i = 0; i < elasticSearchResults.searchResults.size; i++) {
            String fragmentosDetalhes
            final def fragments = elasticSearchResults.highlight[i][FullTextSearchUtils.MEUS_DETALHES]?.fragments
            if (fragments)
                fragmentosDetalhes = "..."+fragments[0].toString()+"..."
//            log.debug("score " + elasticSearchResults.sort.entrySet()[i].value + " - " + elasticSearchResults.searchResults[i].toString())
            result.addResult(elasticSearchResults.searchResults[i], fragmentosDetalhes)
        }

/*
        elasticSearchResults.sort.each { key, value ->
            if (Servico.get(key))
            log.debug( Servico.get(key) );
        }
*/

        return result
    }

    private Closure sintaxeHighlighter(@DelegatesTo(HighlightBuilder) Closure closure) {
        return closure
    }

    private Closure sortBuilder(@DelegatesTo(SortBuilder) Closure closure) {
        return closure
    }

    @NotTransactional
    public String formataDetalhe(String s) {
        if (s) {
            s = s.replaceAll("(\\r|\\n|\\t)", " ")
            return s.substring(0, Math.min(MAX_SIZE_DETALHES+6/*...asd...*/, s.size()) )
        }
    }

    private void indexTeste() {
            log.debug("remapeando")
            elasticSearchAdminService.deleteIndex("org.apoiasuas.testeArquivos")

            elasticSearchAdminService.createIndex("org.apoiasuas.testeArquivos")
            Map mapping = [testeArquivos:[properties:[
//                    nome:[index:"analyzed",boost:"10",type:"string",include_in_all:"true",term_vector:"with_positions_offsets"]
                            meu_titulo:[index:"analyzed",boost:"10",type:"string",include_in_all:"true",term_vector:"with_positions_offsets"],
                            meus_detalhes:[index:"analyzed",boost:"5",type:"string",include_in_all:"true",term_vector:"with_positions_offsets"]
            ]]]
            elasticSearchAdminService.createMapping("org.apoiasuas.testeArquivos","testeArquivos", mapping)

//            elasticSearchClient.prepareIndex()
//                    .setIndex(scm.indexingIndex)
//                    .setType(scm.elasticTypeName)
//                    .setId(key.id)
//                    .setSource(json)

            IndexRequest indexRequest = new IndexRequest("org.apoiasuas.testeArquivos","testeArquivos", "9999");
            XContentBuilder json = jsonBuilder()
                    .startObject()
                    .field("url", "emissaoFormulario/escolherFormulario")
                    .field("meu_titulo", "Emissão de formulários")
                    .field("meus_detalhes", "Emite formulários de gratuidade para segunda via de identidade, fotos 3x4, declaração de pobreza, etc.")
                    .endObject();

            indexRequest.source(json);
            IndexResponse response = elasticSearchClient.index(indexRequest).actionGet();
//            log.debug("Indexing source ${json.string()}")
            elasticSearchService.index();
    }


}
