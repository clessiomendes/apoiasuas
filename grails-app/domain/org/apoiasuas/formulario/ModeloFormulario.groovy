package org.apoiasuas.formulario

class ModeloFormulario {

    public static final String DESCRICAO_PADRAO = "modelo padrão"
    private static final int MEGABYTES = 1024 * 1024
    private static final int TEMPLATE_SIZE = 10 * MEGABYTES

    boolean padrao
    String descricao
    byte[] arquivo

    static constraints = {
        arquivo(nullable: true, size: 0..TEMPLATE_SIZE)
    }

    static mapping = {
        padrao defaultValue: false
        id generator: 'native', params: [sequence: 'sq_modelo_formulario']
    }

    static belongsTo = [formulario: Formulario]

}
