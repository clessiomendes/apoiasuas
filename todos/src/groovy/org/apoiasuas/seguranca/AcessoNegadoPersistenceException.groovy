package org.apoiasuas.seguranca

/**
 * Created by admin on 14/05/2016.
 */
class AcessoNegadoPersistenceException extends RuntimeException {

    public AcessoNegadoPersistenceException(String login, String labelEntidade, Object descricaoEntidade) {
        super("Acesso negado de ${login} a ${labelEntidade} '${descricaoEntidade}'")
    }

}
