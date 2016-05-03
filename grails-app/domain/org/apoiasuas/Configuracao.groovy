package org.apoiasuas

import org.apoiasuas.cidadao.Endereco
import org.apoiasuas.util.AmbienteExecucao

class Configuracao {
    Equipamento equipamento

    static embedded = ['equipamento']
    static mapping = {
        id generator: 'assigned'
    }
}

class Equipamento {
    String nome
    String telefone
    String site
    Endereco endereco
//    AbrangenciaTerritorial abrangenciaTerritorial

    static embedded = ['endereco']
    static transients = ['urlSite']

    static mapping = {
        nome length: 80
        telefone length: 30
        site length: 80
    }

    public String getUrlSite() {
        if (! site)
            return site
        return site.toLowerCase().startsWith("http") ? site : "http://"+site
    }

    static constraints = {
        endereco(nullable: true)
    }
}
