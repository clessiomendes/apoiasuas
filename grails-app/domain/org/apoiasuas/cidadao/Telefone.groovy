package org.apoiasuas.cidadao

import org.apoiasuas.seguranca.UsuarioSistema

class Telefone implements Serializable {

    String DDD
    String numero
//    boolean origemImportacaoAutomatica
//    UsuarioSistema criador, ultimoAlterador;
    Date dateCreated, lastUpdated, dataUltimaImportacao;
    String obs;
//    Boolean habilitado;

    static belongsTo = [familia: Familia]

    static mapping = {
        familia column:'familia', index:'Telefone_Familia_Idx'
        id generator: 'native', params: [sequence: 'sq_telefone']
    }

    static constraints = {
//        criador(nullable: false)
//        ultimoAlterador(nullable: false)
        DDD(blank: true, maxSize: 3);
        numero(blank: false, maxSize: 40);
        obs(blank: true, maxSize: 1000);
    }


    String toString() { return (DDD ? "("+DDD+")" : "") + numero }

    public Telefone() {
    }

    public Telefone(Familia familia) {
        this.familia = familia;
    }

}
