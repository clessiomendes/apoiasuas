package org.apoiasuas

import grails.transaction.Transactional
import groovy.sql.Sql
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.dialect.PostgreSQL81Dialect
import org.hibernate.tool.hbm2ddl.DatabaseMetadata
import org.hibernate.transform.AliasToEntityMapResultTransformer

@Transactional
class ApoiaSuasService {

    def grailsApplication
    def groovySql
    def dataSource
    SessionFactory sessionFactory

    //FIXME sintaxe dependente do Postgree* (levar para AmbienteExecucao)
    static final String MISSING_SEQUENCES_SQL =
            "SELECT table_name\n" +
                    "  FROM information_schema.columns\n" +
                    " WHERE table_schema='public'\n" +
                    "   and column_name='id'   \n" +
                    "   and not table_name = 'configuracao'   \n" +
                    "   and 'sq_' || table_name not IN ( SELECT a.sequence_name FROM information_schema.sequences a )"

    @Transactional(readOnly = true)
    String[] getAtualizacoesPendentes() {
        Configuration conf = grailsApplication.mainContext.getBean("&sessionFactory").configuration
        //FIXME as classes Postgree* não podem estar acopladas (levar para AmbienteExecucao)
        DatabaseMetadata metadata = new DatabaseMetadata(new Sql(dataSource).dataSource.connection, new PostgreSQL81Dialect());
        String[] result = conf.generateSchemaUpdateScript(new PostgreSQL81Dialect(), metadata)

        sessionFactory.currentSession.createSQLQuery(MISSING_SEQUENCES_SQL).with{
            resultTransformer = AliasToEntityMapResultTransformer.INSTANCE
            list().each { Map row ->
                result = result + ("Faltando sequencia para a tabela " + row.values().toArray()[0])
            }
        }

        return result
    }

    @Transactional
    long ocupacaoBD() {
//        def result = groovySql.firstRow("select 0").get(0)
//        def result = groovySql.firstRow("select pg_database_size('bcck9gsbpzsnf7y')").getAt(0)
//        return (result instanceof Number) ? result.longValue() : null;
        return 0
//        select pg_size_pretty(pg_database_size('bcck9gsbpzsnf7y'));
    }
}
