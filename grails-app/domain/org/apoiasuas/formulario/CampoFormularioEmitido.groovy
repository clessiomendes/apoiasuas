package org.apoiasuas.formulario

class CampoFormularioEmitido {

    String descricao
    String grupo
    String conteudoImpresso

//    Origem origem  //Codigo definido em InfoClaseDominio
//    String codigo //Codigo (deve coincidir com o codigo de InfoPropriedadeDominio, quando atrelado ao cadastro de usuario)
//    int multiplasLinhas
//    Tipo tipoPersonalizado

    static belongsTo = [formulario: FormularioEmitido]

    static constraints = {
        descricao(nullable: false)
        grupo(nullable: true)
        conteudoImpresso(nullable: true)
    }

}
