package org.apoiasuas.marcador

import org.apoiasuas.cidadao.Familia
import org.apoiasuas.seguranca.UsuarioSistema

/**
 * Many to Many entre acoes e familias
 */
class AcaoFamilia implements AssociacaoMarcador {
    Acao acao
    Familia familia
    Date data;
    UsuarioSistema tecnico;
    Boolean habilitado;
    String observacao;

    static belongsTo = [familia: Familia]
    static transients = ['marcador']

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_acao_familia'];
        version defaultValue: "0";
    }

    static constraints = {
        acao(nullable: false) //por enquanto, só pode existir uma ligação entre as entidades
        familia(nullable: false)
        tecnico(nullable: false)
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