package org.apoiasuas

import org.apoiasuas.fileStorage.FileStorageDTO
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
    AbrangenciaTerritorial compartilhadoCom
    //Transiente:
    String fileName
    Boolean compartilhar
    FileStorageDTO.FileActions fileAction

    static searchable = {                           // <-- elasticsearch plugin
        only = ["descricao", "instrucoes", "url", FullTextSearchUtils.ID_SERVICO_SISTEMA, FullTextSearchUtils.ID_COMPARTILHADO_COM]
        descricao alias: FullTextSearchUtils.MEU_TITULO, index:'analyzed', boost:10
        instrucoes alias: FullTextSearchUtils.MEUS_DETALHES, index:'analyzed', boost:5
        url alias: FullTextSearchUtils.MEUS_DETALHES, index:'analyzed', boost:3
    }

    static transients = ['fileName', 'compartilhar', 'fileAction', FullTextSearchUtils.ID_SERVICO_SISTEMA, FullTextSearchUtils.ID_COMPARTILHADO_COM]

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_link']
        servicoSistemaSeguranca fetch: 'join' //por questoes de seguranca, sempre que um link eh obtido do banco de dados, o servicoSistema precisara ser consultado
    }

    static constraints = {
        tipo(nullable: false);
        descricao(nullable: false);
        descricao(unique: ['servicoSistemaSeguranca'], maxSize: 255);
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

    /**
     * Se o link NAO FOR compartilhado, restringir ao ServicoSistema que o criou
     */
    public Long getIdServicoSistema() {
        return compartilhadoCom ? null : servicoSistemaSeguranca?.id
    }

    /**
     * Se o link FOR compartilhado, restringir pela AbrangenciaTerritorial escolhida
     */
    public Long getIdComparilhadoCom() {
        return compartilhadoCom ? compartilhadoCom.id : null
    }

    public setCompartilhadoCom(AbrangenciaTerritorial abrangenciaTerritorial) {
        compartilhar = abrangenciaTerritorial != null
        compartilhadoCom = abrangenciaTerritorial
    }

}
