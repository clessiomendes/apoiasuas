package org.apoiasuas.lookup
/**
 * Created by clessio on 18/02/2018.
 */
public enum Sexo {
    MASCULINO("Masculino"),
    FEMININO("Feminino");

    String descricao;

    Sexo(String descricao) {
        this.descricao = descricao;
    }

    static public Map asMap() {
        return Sexo.values().collectEntries { [(it): it.descricao] }
    }
}
