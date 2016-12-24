package org.apoiasuas.acao

import org.apoiasuas.acao.Acao
import org.apoiasuas.cidadao.Cidadao

/**
 * Many to Many entre acoes e cidadaos
 */
class AcaoCidadao {
    Acao acao
    Cidadao cidadao

    static belongsTo = [cidadao: Cidadao]

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_acao_cidadao']
    }

    static constraints = {
        acao(nullable: false) //por enquanto, só pode existir uma ligação entre as entidades
        cidadao(nullable: false)
    }
}