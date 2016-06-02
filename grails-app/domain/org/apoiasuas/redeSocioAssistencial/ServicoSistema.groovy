package org.apoiasuas.redeSocioAssistencial

import org.apoiasuas.cidadao.Endereco

/**
 * Created by admin on 02/05/2016.
 */
class ServicoSistema {
    String nome
    String telefone
    String site
    Endereco endereco
    AbrangenciaTerritorial abrangenciaTerritorial
    Boolean habilitado

//    AbrangenciaTerritorial abrangenciaTerritorial

    static transients = ['urlSite']
    static embedded = ['endereco']
    static mapping = {
        id generator: 'native', params: [sequence: 'sq_servico_sistema']
        nome length: 80
        telefone length: 30
        site length: 80
    }
    static constraints = {
        endereco(nullable: true)
        abrangenciaTerritorial(nullable: true)
    }

    public String getUrlSite() {
        if (! site)
            return site
        return site.toLowerCase().startsWith("http") ? site : "http://"+site
    }

}

