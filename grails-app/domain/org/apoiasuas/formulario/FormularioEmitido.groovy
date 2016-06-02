package org.apoiasuas.formulario

import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.UsuarioSistema

class FormularioEmitido {

    String descricao
    String tipo
    PreDefinidos formularioPreDefinido
    Cidadao cidadao
    Familia familia
    Date dataPreenchimento
    UsuarioSistema operadorLogado
    ServicoSistema servicoSistemaSeguranca

    static hasMany = [campos: CampoFormularioEmitido]

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_formulario_emitido']
    }
    static constraints = {
        descricao(nullable: false)
        dataPreenchimento(nullable: true)
        operadorLogado(nullable: false)
        tipo(nullable: true)
        formularioPreDefinido(nullable: true)
        cidadao(nullable: true)
        familia(nullable: true)
        servicoSistemaSeguranca(nullable: false)
    }

    public List getCamposOrdenados() {
        return campos.sort { it.id }
    }

}
