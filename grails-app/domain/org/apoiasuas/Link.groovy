package org.apoiasuas

class Link {

    String url
    String descricao
    String instrucoes

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_link']
    }

    static constraints = {
        url(nullable: false, maxSize: 255)
        descricao(nullable: false, maxSize: 255)
        instrucoes(nullable: true, maxSize: 255)
    }

    public String getUrlCompleta() {
        if (! url)
            return url
        return url.toLowerCase().startsWith("http") ? url : "http://"+url
    }

}
