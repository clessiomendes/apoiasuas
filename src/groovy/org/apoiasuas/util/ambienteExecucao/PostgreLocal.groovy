package org.apoiasuas.util.ambienteExecucao

import org.hibernate.dialect.Dialect
import org.hibernate.dialect.PostgreSQL81Dialect

public class PostgreLocal extends TipoAmbiente {
    public PostgreLocal() {
        super("postgre","local")
    }

    @Override
    Dialect getDialect() {
        return new PostgreSQL81Dialect();
    }

}
