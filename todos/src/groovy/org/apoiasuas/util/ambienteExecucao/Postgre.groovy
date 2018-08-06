package org.apoiasuas.util.ambienteExecucao

import org.hibernate.dialect.Dialect
import org.hibernate.dialect.PostgreSQL81Dialect

public class Postgre extends TipoAmbiente {

    public Postgre(String ignorado, String runtime) {
        super("postgre", runtime)
    }

    @Override
    Dialect getDialect() {
        return new PostgreSQL81Dialect();
    }
}