package org.apoiasuas.marcador

import org.apoiasuas.cidadao.Familia
import org.apoiasuas.seguranca.UsuarioSistema

/**
 * Many to Many entre acoes e familias
 */
class OutroMarcadorFamilia implements AssociacaoMarcador {
    OutroMarcador outroMarcador
    Familia familia
    Date data;
    UsuarioSistema tecnico;
    Boolean habilitado;
    String observacao;

    static belongsTo = [familia: Familia]
    static transients = ['marcador']

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_outro_marcador_familia'];
        version defaultValue: "0";
        outroMarcador fetch: 'join'
    }

    static constraints = {
        outroMarcador(nullable: false) //por enquanto, só pode existir uma ligação entre as entidades
        familia(nullable: false)
        tecnico(nullable: false)
    }

    @Override
    Marcador getMarcador() {
        return outroMarcador;
    }

    @Override
    void setMarcador(Marcador marcador) {
        outroMarcador = marcador;
    }
}