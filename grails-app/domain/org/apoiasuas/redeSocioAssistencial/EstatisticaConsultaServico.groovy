package org.apoiasuas.redeSocioAssistencial

import org.apoiasuas.seguranca.UsuarioSistema

class EstatisticaConsultaServico {

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
        id generator: 'native', params: [sequence: 'sq_estatistica_consulta_servico']
        servicoSistemaSeguranca index: 'idx_estatistica_cs_ss'
//        usuarioSistema index: 'idx_estatistica_cs_us'
//        servico index: 'idx_estatistica_cs_s'
        mes index: 'idx_estatistica_cs_m'
    }
}
