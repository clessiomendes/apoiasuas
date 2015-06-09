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
    String site

    Boolean podeEncaminhar
    String nomeFormal
    String encaminhamentoPadrao
    Endereco endereco

    String descricaoCortada //transiente

//    UsuarioSistema criador, ultimoAlterador;
//    Date dateCreated, lastUpdated;

    static embedded = ['endereco']

    static transients = ['descricaoCortada']

    static constraints = {
        apelido(nullable: false, maxSize: 80)
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_servico']
        podeEncaminhar(defaultValue: AmbienteExecucao.getBoolean(true))
        encaminhamentoPadrao length: 100000
        descricao length: 1000000
    }

    public String getUrlSite() {
        if (! site)
            return site
        return site.toLowerCase().startsWith("http") ? site : "http://"+site
    }

}
