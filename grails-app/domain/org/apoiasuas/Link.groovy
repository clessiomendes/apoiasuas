package org.apoiasuas

import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.util.FullTextSearchUtils

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
//    AbrangenciaTerritorial compartilhadoCom

    static searchable = {                           // <-- elasticsearch plugin
        only = ["descricao","instrucoes","url",FullTextSearchUtils.ID_SERVICO_SISTEMA/*, 'fileContent'*/]
        descricao alias: FullTextSearchUtils.MEU_TITULO, index:'analyzed', boost:10
        instrucoes alias: FullTextSearchUtils.MEUS_DETALHES, index:'analyzed', boost:5
        url alias: FullTextSearchUtils.MEUS_DETALHES, index:'analyzed', boost:3
    }

    static transients = ['fileName', FullTextSearchUtils.ID_SERVICO_SISTEMA/*, 'fileContent'*/]

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_link']
    }

    static constraints = {
        tipo(nullable: false);
        descricao(nullable: false, maxSize: 255);
        instrucoes(nullable: true, maxSize: 255);
        servicoSistemaSeguranca(nullable: false);
    }

    public Long getIdServicoSistema() {
        return servicoSistemaSeguranca?.id
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
