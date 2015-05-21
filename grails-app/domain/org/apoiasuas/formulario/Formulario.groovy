package org.apoiasuas.formulario

import org.apoiasuas.bootstrap.FormularioCertidoes
import org.apoiasuas.cidadao.Cidadao

class Formulario implements Serializable {
    public static final String EXTENSAO_ARQUIVO = ".docx"
//    public static final String CAMPO_DATA_PREENCHIMENTO = "formulario.dataPreenchimento"
//    public static final String CAMPO_NOME_TECNICO_PREENCHIMENTO = "formulario.nomeResponsavelPreenchimento"

    private static final int MEGABYTES = 1024 * 1024
    private static final int TEMPLATE_SIZE = 10 * MEGABYTES

    String nome
    String descricao
    String tipo //para agrupar as opções de formulário apresentadas ao operador
    PreDefinidos formularioPreDefinido
    //utilizado eventualmente para designar relatorios com tratamento específico no sistema
    byte[] template

    //Campos transientees
    Cidadao cidadao
//    Map<String, Object> camposAvulsos
//    Date dataPreenchimento
//    String nomeResponsavelPreenchimento
    boolean atualizarPersistencia
    FormularioEmitido formularioEmitido
    static transients = ['formularioEmitido', 'cidadao', 'dataPreenchimento', /*'nomeResponsavelPreenchimento', 'camposAvulsos',*/ 'atualizarPersistencia']

    static hasMany = [campos: CampoFormulario]

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_formulario']
    }

    /**
     * Atribui a cada campo avulso previsto no formulario, o correspondente valor obtido do request
     * @param params O "recorte" do request correspondente aos conteudos avulsos
     */
    void setCamposAvulsos(Map params) {
        //Filtra, de todos os campos, apenas aqueles do tipo AVULSO e itera sobre eles
        campos.findAll { it.origem?.avulso }*.each { campo ->
            campo.valorArmazenado = params.get(campo.codigo)
        }
    }

    void setDataPreenchimento(Date date) {
        campos.find{ it.codigo == CampoFormulario.CODIGO_DATA_PREENCHIMENTO }?.valorArmazenado = date
    }

    Date getDataPreenchimento() {
        return campos.find{ it.codigo == CampoFormulario.CODIGO_DATA_PREENCHIMENTO }?.valorArmazenado
    }

//    String getNomeResponsavelPreenchimento() {
//        return campos.find{ it.codigo == CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO }?.valorArmazenado
//    }

//    void setNomeResponsavelPreenchimento(String nome) {
//        campos.find{ it.codigo == CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO }?.valorArmazenado = nome
//    }

    CampoFormulario getCampoAvulso(String codigo) {
        CampoFormulario result = campos.find{ it.codigo == codigo && it.origem?.avulso }
        if (! result)
            throw new RuntimeException("Campo avulso ${codigo} não encontrado")
        return result
    }

    static constraints = {
        nome(nullable: false, unique: true)
        descricao(nullable: true)
        formularioPreDefinido(nullable: true, unique: true)
        template(nullable: true, size: 0..TEMPLATE_SIZE)
    }

    String geraNomeArquivo() {
        return nome ? nome.replaceAll("\\W+", "_") + EXTENSAO_ARQUIVO : nome //substitui caracteres que nao sejam alfanumericos
    }

    String toString() {
        String result = nome + ', ' + descricao + '['
        campos?.each {
            result += it.ordem + ', ' + it.codigo
        }
        return result + ']'
    }

    /**
     * Ordena primeiro pelo campo "ordem" e depois pela sequencia de insercao
     */
    ArrayList<CampoFormulario> getCamposOrdenados() {
        campos?.sort { [it.ordem ?: 9999 /*nulos no final*/, it.id] }
    }

    ArrayList<ArrayList<CampoFormulario>> getCamposAgrupados() {

        String ultimoGrupo = ""
        ArrayList result = []
        ArrayList<CampoFormulario> listaTemp = []
        camposOrdenados.each { CampoFormulario campo ->
            if (campo.grupo != ultimoGrupo) {
                if (listaTemp)
                    result.add(listaTemp)
                listaTemp = []
                ultimoGrupo = campo.grupo
            }
            listaTemp.add(campo)
        }
        result.add(listaTemp)
        log.debug(result)
        return result
//        Map gruposOrdenados = [:]
//        Map gruposDesordenados = campos?.groupBy { it.grupo }
//        gruposDesordenados.each { chave, List<CampoFormulario> valor ->
//            gruposOrdenados.put(chave, valor.sort{ [it.ordem ?: 9999 /*nulos no final*/, it.id] })
//        }
//        return gruposOrdenados
    }

}
