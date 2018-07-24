package org.apoiasuas.redeSocioAssistencial

import org.apoiasuas.cidadao.Endereco
import org.apoiasuas.fileStorage.FileStorageDTO
//import org.apoiasuas.fileStorage.FileStorageIndex
import org.apoiasuas.util.AmbienteExecucao
import org.apoiasuas.util.FullTextSearchUtils

class Servico {

    String apelido
    String descricao
    String telefones
    String site
    Boolean habilitado

    Boolean podeEncaminhar
    String nomeFormal
    String encaminhamentoPadrao
//    Endereco endereco
    AbrangenciaTerritorial abrangenciaTerritorial
    String imagemFileStorage

    String publico
    String documentos
    String enderecos
    String fluxo
    String contatosInternos
    Date ultimaVerificacao, dateCreated, lastUpdated

//    Set<FileStorageIndex> anexos = []
//    static hasMany = [anexos: FileStorageIndex]

    //Transiente:
    FileStorageDTO.FileActions fileAction
    String descricaoCortada

    static searchable = {                           // <-- elasticsearch plugin
        only = ["apelido","nomeFormal","descricao","publico"]
        apelido alias:FullTextSearchUtils.MEU_TITULO, index:'analyzed', boost:10
        nomeFormal alias:FullTextSearchUtils.MEU_TITULO, index:'analyzed', boost:10
        descricao alias:FullTextSearchUtils.MEUS_DETALHES, index:'analyzed', boost:5
        publico alias:FullTextSearchUtils.MEUS_DETALHES, index:'analyzed', boost:3
    }

    @Override
    public String toString() {
        return apelido
    }

//    static embedded = ['endereco']

    static transients = ['descricaoCortada','fileAction']

    static constraints = {
        abrangenciaTerritorial(nullable: false)
        apelido(nullable: false, maxSize: 80)
        ultimaVerificacao(nullable: true)
        imagemFileStorage(nullable: true)
        fileAction(bindable: true);
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_servico']
        podeEncaminhar(defaultValue: AmbienteExecucao.SqlProprietaria.getBoolean(true))
        encaminhamentoPadrao length: 1000000
        descricao length: 1000000
        publico length: 1000000
        documentos length: 1000000
//        horarios length: 1000000
        fluxo length: 1000000
        telefones length: 1000000
        contatosInternos length: 1000000
        enderecos length: 1000000
    }

    public String getUrlSite() {
        if (! site)
            return site
        return site.toLowerCase().startsWith("http") ? site : "http://"+site
    }

    public setVersion(Long version) {
        this.version = version;
    }

}
