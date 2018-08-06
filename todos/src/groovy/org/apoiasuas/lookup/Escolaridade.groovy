package org.apoiasuas.lookup;

import java.util.*;

/**
 * Created by clessio on 18/02/2018.
 */
public enum Escolaridade {
    INFANTIL("Educação Infantil/Creche"),
    FUNDAMENTAL_INC("Fundamental Incompleto"),
    FUNDAMENTAL_COMP("Fundamental Completo"),
    MEDIO_INC("Médio Incompleto"),
    MEDIO_COMP("Médio Completo"),
    SUPERIOR_INC("Superior Incompleto"),
    SUPERIOR_COMP("Superior Completo");

    String descricao;

    Escolaridade(String descricao) {
        this.descricao = descricao;
    }

    static public Map asMap() {
        return Escolaridade.values().collectEntries { [(it): it.descricao] }
    }
}
