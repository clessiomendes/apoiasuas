package org.apoiasuas.marcador

import org.apoiasuas.cidadao.AssociacaoMarcador
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.cidadao.Marcador

/**
 * Many to Many entre acoes e familias
 */
class VulnerabilidadeFamilia implements AssociacaoMarcador {
    Vulnerabilidade vulnerabilidade
    Familia familia

    static belongsTo = [familia: Familia]
    static transients = ['marcador']

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_vulnerabilidade_familia'];
        version defaultValue: "0";
    }

    static constraints = {
        vulnerabilidade(nullable: false) //por enquanto, só pode existir uma ligação entre as entidades
        familia(nullable: false)
    }

    @Override
    Marcador getMarcador() {
        return vulnerabilidade;
    }

    @Override
    void setMarcador(Marcador marcador) {
        vulnerabilidade = marcador;
    }
}