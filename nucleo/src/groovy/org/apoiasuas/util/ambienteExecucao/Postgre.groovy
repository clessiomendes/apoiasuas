package org.apoiasuas.util.ambienteExecucao

import groovy.sql.Sql
import org.hibernate.cfg.Configuration
import org.hibernate.dialect.Dialect
import org.hibernate.dialect.PostgreSQL81Dialect
import org.hibernate.tool.hbm2ddl.DatabaseMetadata

import javax.sql.DataSource

public class Postgre extends TipoAmbiente {

    public Postgre(String ignorado, String runtime) {
        super("postgre", runtime)
    }

    @Override
    public Dialect getDialect() {
        return new PostgreSQL81Dialect();
    }

    @Override
    public String[] atualizacoesDDL(DataSource dataSource, Configuration conf) {
        DatabaseMetadata metadata = new DatabaseMetadata(new Sql(dataSource).dataSource.connection, getDialect());
        String[] result = conf.generateSchemaUpdateScriptList(getDialect(), metadata).collect {
            it.script
        };
        return result
    }


}