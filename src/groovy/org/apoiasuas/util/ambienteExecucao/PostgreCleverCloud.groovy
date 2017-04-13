package org.apoiasuas.util.ambienteExecucao

import org.hibernate.dialect.Dialect
import org.hibernate.dialect.PostgreSQL81Dialect

class PostgreCleverCloud extends TipoAmbiente {
    public PostgreCleverCloud() {
        super("postgre","cleverCloud")
    }

    @Override
    Dialect getDialect() {
        return new PostgreSQL81Dialect();
    }
}
