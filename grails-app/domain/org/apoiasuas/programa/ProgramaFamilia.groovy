package org.apoiasuas.programa

import org.apoiasuas.cidadao.Familia

/**
 * Many to Many entre programas e familias
 */
class ProgramaFamilia {
    Programa programa
    Familia familia

    static belongsTo = [familia: Familia]

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_programa_familia']
    }
    static constraints = {
        programa(nullable: false, unique: ['familia']) //por enquanto, só pode existir uma ligação entre as entidades
        familia(nullable: false)
    }
}