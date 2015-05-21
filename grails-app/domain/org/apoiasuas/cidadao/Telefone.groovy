package org.apoiasuas.cidadao

import org.apoiasuas.seguranca.UsuarioSistema

class Telefone implements Serializable {

    String DDD
    String numero
//    TipoTelefone tipoTelefone
//    boolean origemImportacaoAutomatica
    UsuarioSistema criador, ultimoAlterador;
    Date dateCreated, lastUpdated, dataUltimaImportacao;

    static belongsTo = [familia: Familia]

    static mapping = {
        familia column:'familia', index:'Telefone_Familia_Idx'
        id generator: 'native', params: [sequence: 'sq_telefone']
//        origemImportacaoAutomatica(defaultValue:AmbienteExecucao.getFalse())
    }

    static constraints = {
        criador(nullable: false)
        ultimoAlterador(nullable: false)
    }


    String toString() { return (DDD ? "("+DDD+")" : "") + numero }

}
