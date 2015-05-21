package org.apoiasuas.servico

import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.Endereco
import org.apoiasuas.cidadao.Telefone
import org.apoiasuas.seguranca.UsuarioSistema

class Servico {

    String apelido
    String nomeFormal
    Endereco endereco

    String encaminhamentoPadrao

//    UsuarioSistema criador, ultimoAlterador;
//    Date dateCreated, lastUpdated;

    //TODO: telefones por servico, com pessoa de contato
//    static hasMany = [telefones: TelefoneServico]

    static embedded = ['endereco'/*, 'despesas'*/]

    static constraints = {
        apelido(nullable: false)
        nomeFormal(nullable: false)
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_servico']
        encaminhamentoPadrao length: 100000
    }
}
