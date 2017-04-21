package org.apoiasuas

import grails.transaction.Transactional
import groovy.sql.Sql
import org.apoiasuas.util.AmbienteExecucao
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.dialect.PostgreSQL81Dialect
import org.hibernate.tool.hbm2ddl.DatabaseMetadata
import org.hibernate.transform.AliasToEntityMapResultTransformer
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder

@Transactional(readOnly = true)
class ApoiaSuasService {

    def grailsApplication
//    SessionFactory sessionFactory

    //FIXME sintaxe dependente do Postgree* (levar para AmbienteExecucao)
    static final String MISSING_SEQUENCES_SQL =
            "SELECT table_name\n" +
                    "  FROM information_schema.columns\n" +
                    " WHERE table_schema='public'\n" +
                    "   and column_name='id'   \n" +
                    "   and not table_name = 'ambiente'   \n" +
                    "   and not table_name = 'servicoSistema'   \n" +
                    //padrao de nome de sequencias criadas automaticamente para campos do tipo "serial" no postgresql:
                    "   and table_name || '_id_seq' not IN ( SELECT a.sequence_name FROM information_schema.sequences a )\n" +
                    //padrao de nome de sequencias criadas explicitamente para o projeto (compatibilidade):
                    "   and 'sq_' || table_name not IN ( SELECT a.sequence_name FROM information_schema.sequences a )"

    @Transactional(readOnly = true)
    public String[] getAtualizacoesPendentes(javax.sql.DataSource dataSource, String sessionFactoryName) {
//        Configuration conf = sessionFactory.configuration
        def sessionFactoryBean = grailsApplication.mainContext.getBean("&"+sessionFactoryName)
        SessionFactory sessionFactory = sessionFactoryBean.sessionFactory
        Configuration conf = sessionFactoryBean.configuration
        //FIXME as classes Postgree* não podem estar acopladas (levar para AmbienteExecucao)
        DatabaseMetadata metadata = new DatabaseMetadata(new Sql(dataSource).dataSource.connection, AmbienteExecucao.CURRENT2.getDialect());
        String[] result = conf.generateSchemaUpdateScriptList(new PostgreSQL81Dialect(), metadata).collect { it.script };

        sessionFactory.currentSession.createSQLQuery(MISSING_SEQUENCES_SQL).with{
            resultTransformer = AliasToEntityMapResultTransformer.INSTANCE
            list().each { Map row ->
                result = result + ("Faltando sequencia para a tabela " + row.values().toArray()[0])
            }
        }

        return result
    }

    @Transactional(readOnly = true)
    long ocupacaoBD() {
//        def result = groovySql.firstRow("select 0").get(0)
//        def result = groovySql.firstRow("select pg_database_size('bcck9gsbpzsnf7y')").getAt(0)
//        return (result instanceof Number) ? result.longValue() : null;
        return 0
//        select pg_size_pretty(pg_database_size('bcck9gsbpzsnf7y'));
    }
}
