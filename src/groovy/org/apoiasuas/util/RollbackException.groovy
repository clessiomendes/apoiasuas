package org.apoiasuas.util

/**
 * Classe de excecao a ser usada para reverter transacoes de BD. containedObject contem eventuais informacoes, a serem
 * apresentadas para o operador do sistema, que motivaram o cancelamento da transacao.
 */
class RollbackException extends RuntimeException {
    private Object containedObject;

    RollbackException(Object containedObject) {
        this.containedObject = containedObject;
    }

    Object getContainedObject(){
        return containedObject;
    }
}
