package org.apoiasuas

import org.apoiasuas.redeSocioAssistencial.ServicoSistema

class Link {

    String url
    String descricao
    String instrucoes
    ServicoSistema servicoSistemaSeguranca

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_link']
    }

    static constraints = {
        url(nullable: false, maxSize: 255)
        descricao(nullable: false, maxSize: 255)
        instrucoes(nullable: true, maxSize: 255)
        servicoSistemaSeguranca(nullable: false)
    }

    public String getUrlCompleta() {
        if (! url)
            return url
        return url.toLowerCase().startsWith("http") ? url : "http://"+url
    }

    public String toString() {
        return descricao
    }
}
