package org.apoiasuas.servico

import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.Endereco
import org.apoiasuas.cidadao.Telefone
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.AmbienteExecucao

class Servico {

    String apelido
    String descricao
    String telefones

    Boolean podeEncaminhar
    String nomeFormal
    Endereco endereco
    String encaminhamentoPadrao

//    UsuarioSistema criador, ultimoAlterador;
//    Date dateCreated, lastUpdated;

    //TODO: telefones por servico, com pessoa de contato
//    static hasMany = [telefones: TelefoneServico]

    static embedded = ['endereco'/*, 'despesas'*/]

    static constraints = {
        apelido(nullable: false, maxSize: 80)
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_servico']
        podeEncaminhar(defaultValue: AmbienteExecucao.getBoolean(true))
        encaminhamentoPadrao length: 100000
        descricao length: 100000
    }
}
