package org.apoiasuas

import org.apoiasuas.redeSocioAssistencial.ServicoSistema

class Link {

    public static enum Tipo { URL, FILE
        boolean isUrl() { return this == URL }
        boolean isFile() { return this == FILE }
    }

    String url
    String fileLabel
    String descricao
    String instrucoes
    Tipo tipo
    ServicoSistema servicoSistemaSeguranca
    //Transiente:
    String fileName

    static searchable = {                           // <-- elasticsearch plugin
        only = ["descricao","instrucoes","url"]
        descricao alias:"meu_titulo", index:'analyzed', boost:10
        instrucoes alias:"meus_detalhes", index:'analyzed', boost:5
        url alias:"meus_detalhes", index:'analyzed', boost:3
    }

    static transients = ['fileName']

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_link']
    }

    static constraints = {
        //Referencia: http://stackoverflow.com/a/11447609/1916198
/*
        url(maxSize: 255, validator: {val, obj ->
            if (obj.tipo?.isUrl() && !val)
                return ['url.required']
        }, nullable: true)
        fileLabel(maxSize: 255, validator: {val, obj ->
            if (obj.tipo?.isFile() && !val)
                return ['fileLabel.required']
        }, nullable: true);
*/
        tipo(nullable: false);
        descricao(nullable: false, maxSize: 255);
        instrucoes(nullable: true, maxSize: 255);
        servicoSistemaSeguranca(nullable: false);
    }

    public String getUrlCompleta() {
        if (! url)
            return url
        return url.toLowerCase().startsWith("http") ? url : "http://"+url
    }

    @Override
    public String toString() {
        return descricao
    }

}
