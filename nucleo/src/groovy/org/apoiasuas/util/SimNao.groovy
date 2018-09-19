package org.apoiasuas.util

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