package org.apoiasuas.marcador

import org.apoiasuas.cidadao.AssociacaoMarcador
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.cidadao.Marcador
import org.apoiasuas.marcador.Programa

/**
 * Many to Many entre programas e familias
 */
class ProgramaFamilia implements AssociacaoMarcador {
    Programa programa
    Familia familia

    static belongsTo = [familia: Familia]
    static transients = ['marcador']

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_programa_familia']
    }
    static constraints = {
        programa(nullable: false, unique: ['familia']) //por enquanto, só pode existir uma ligação entre as entidades
        familia(nullable: false)
    }

    @Override
    Marcador getMarcador() {
        return programa;
    }

    @Override
    void setMarcador(Marcador marcador) {
        programa = marcador;
    }
}