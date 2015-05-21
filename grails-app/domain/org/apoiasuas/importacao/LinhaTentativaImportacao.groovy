package org.apoiasuas.importacao

class LinhaTentativaImportacao  implements Serializable {

    //TODO: Criar índice para LinhaTentativaImportacao por TentativaImportacao
    static belongsTo = [tentativaImportacao :TentativaImportacao ]
    Long ordem
    String JSON

    static constraints = {
        ordem(nullable: false);
        JSON(maxSize: 100000);
    }

    static mapping = {
        tentativaImportacao column:'tentativaImportacao', index:'Linha_Tent_Import_Idx'
        id generator: 'native', params: [sequence: 'sq_linha_tentativa_importacao']
    }

    String toString() { "Linha " + (ordem + " ") ?: "" + JSON.toString() ?: "" }

}
