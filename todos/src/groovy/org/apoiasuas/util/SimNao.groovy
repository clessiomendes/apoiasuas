package org.apoiasuas.util

/**
 * Created by clessio on 20/01/2018.
 */
public enum SimNao {

    SIM('sim'), NAO('não');
    String descricao;

    SimNao(String descricao) {
        this.descricao = descricao;
    }

    public static final Boolean sim(String valor) {
        return (valor && valor.toUpperCase() == SIM.toString() )
    }

}