package org.apoiasuas.atalhos

class Link {

    String url
    String descricao
    String instrucoes

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_link']
    }

    static constraints = {
    }

    public String getUrlCompleta() {
        if (! url)
            return url
        return url.toLowerCase().startsWith("http") ? url : "http://"+url
    }

}
