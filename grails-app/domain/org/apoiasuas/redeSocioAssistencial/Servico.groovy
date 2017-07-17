package org.apoiasuas.redeSocioAssistencial

import org.apoiasuas.cidadao.Endereco
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
    Endereco endereco
    AbrangenciaTerritorial abrangenciaTerritorial

    String descricaoCortada //transiente

//    UsuarioSistema criador, ultimoAlterador;
//    Date dateCreated, lastUpdated;

    static searchable = {                           // <-- elasticsearch plugin
        only = ["apelido","nomeFormal","descricao","site"]
        apelido alias:FullTextSearchUtils.MEU_TITULO, index:'analyzed', boost:10
        nomeFormal alias:FullTextSearchUtils.MEU_TITULO, index:'analyzed', boost:10
        descricao alias:FullTextSearchUtils.MEUS_DETALHES, index:'analyzed', boost:5
        site alias:FullTextSearchUtils.MEUS_DETALHES, index:'analyzed', boost:5
    }

    @Override
    public String toString() {
        return apelido
    }

    static embedded = ['endereco']

    static transients = ['descricaoCortada']

    static constraints = {
        abrangenciaTerritorial(nullable: false)
        apelido(nullable: false, maxSize: 80)
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_servico']
        podeEncaminhar(defaultValue: AmbienteExecucao.SqlProprietaria.getBoolean(true))
        encaminhamentoPadrao length: 1000000
        descricao length: 1000000
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
