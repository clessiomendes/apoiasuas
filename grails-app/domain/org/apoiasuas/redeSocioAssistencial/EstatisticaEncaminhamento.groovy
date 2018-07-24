package org.apoiasuas.redeSocioAssistencial

import org.apoiasuas.seguranca.UsuarioSistema

class EstatisticaEncaminhamento {

    ServicoSistema servicoSistemaSeguranca
    Servico servico
    UsuarioSistema usuarioSistema
    Date mes
    Long quantidade

    static constraints = {
        servicoSistemaSeguranca(nullable: true)
        servico(nullable: false)
        usuarioSistema(nullable: false)
        mes(nullable: false)
        quantidade(nullable: false)
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_estatistica_encaminhamento']
        servicoSistemaSeguranca index: 'idx_estatistica_e_ss'
        mes index: 'idx_estatistica_e_m'
    }
}
