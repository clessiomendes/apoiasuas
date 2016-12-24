package org.apoiasuas.acao

import org.apoiasuas.acao.Acao
import org.apoiasuas.cidadao.AssociacaoMarcador
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.cidadao.Marcador

/**
 * Many to Many entre acoes e familias
 */
class AcaoFamilia implements AssociacaoMarcador {
    Acao acao
    Familia familia

    static belongsTo = [familia: Familia]
    static transients = ['marcador']

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_acao_familia']
    }

    static constraints = {
        acao(nullable: false) //por enquanto, só pode existir uma ligação entre as entidades
        familia(nullable: false)
    }

    @Override
    Marcador getMarcador() {
        return acao;
    }

    @Override
    void setMarcador(Marcador marcador) {
        acao = marcador;
    }
}