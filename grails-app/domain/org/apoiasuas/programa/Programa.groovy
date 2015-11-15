package org.apoiasuas.programa

import org.apoiasuas.ProgramasPreDefinidos
import org.apoiasuas.cidadao.Familia

class Programa {

    String nome
    String sigla
    ProgramasPreDefinidos programaPreDefinido
    Boolean selected

    static transients = ['selected']

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_programa']
    }

    static constraints = {
        nome(nullable: false, maxSize: 255)
        sigla(nullable: true, maxSize: 10)
    }

    public String siglaENome() {
        return sigla ? (sigla + (nome ? " ($nome)" : "")) : nome
    }

    public String toString() {
        return nome
    }
}
