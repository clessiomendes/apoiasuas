package org.apoiasuas

import org.apoiasuas.marcador.Programa

/**
 * Enum contendo os programas pre-definidos do sistema
 */
enum ProgramasPreDefinidos {

    BOLSA_FAMILIA("PBF", "Bolsa Família"), BPC("BPC", "Benefício de Prestação Continuada"),
    ACOMPANHAMENTO_PAIF("AcPAIF", "Acompanhamento PAIF"), MAIOR_CUIDADO("PMC", "Programa Maior Cuidado"),
    FAMILIA_CIDADA("FC", "Projeto Família Cidadã")

    String sigla
    String nome

    Programa instanciaPersistida

    ProgramasPreDefinidos(String sigla, String nome) {
        this.sigla = sigla
        this.nome = nome
    }
}