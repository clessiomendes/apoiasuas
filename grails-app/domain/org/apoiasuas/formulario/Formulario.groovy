package org.apoiasuas.formulario

import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.seguranca.UsuarioSistema

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
    //utilizado eventualmente para designar relatorio com tratamento específico no sistema
    byte[] template

    //Campos transientees
    Cidadao cidadao
    UsuarioSistema usuarioSistema //Campo transiente para armazenar um usuarioResponsavel (caso ele exista no formulario)
    boolean atualizarPersistencia
    FormularioEmitido formularioEmitido
    static transients = ['formularioEmitido', 'cidadao', 'usuarioSistema', 'dataPreenchimento', 'nomeEquipamento', 'enderecoEquipamento', 'telefoneEquipamento', /*'nomeResponsavelPreenchimento', 'camposAvulsos',*/ 'atualizarPersistencia']

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

    void setNomeEquipamento(String nomeEquipamento) {
        campos.find{ it.codigo == CampoFormulario.CODIGO_NOME_EQUIPAMENTO }?.valorArmazenado = nomeEquipamento
    }

    Date getNomeEquipamento() {
        return campos.find{ it.codigo == CampoFormulario.CODIGO_NOME_EQUIPAMENTO }?.valorArmazenado
    }

    void setEnderecoEquipamento(String enderecoEquipamento) {
        campos.find{ it.codigo == CampoFormulario.CODIGO_ENDERECO_EQUIPAMENTO }?.valorArmazenado = enderecoEquipamento
    }

    Date getEnderecoEquipamento() {
        return campos.find{ it.codigo == CampoFormulario.CODIGO_ENDERECO_EQUIPAMENTO }?.valorArmazenado
    }

    void setTelefoneEquipamento(String telefoneEquipamento) {
        campos.find{ it.codigo == CampoFormulario.CODIGO_TELEFONE_EQUIPAMENTO }?.valorArmazenado = telefoneEquipamento
    }

    Date getTelefoneEquipamento() {
        return campos.find{ it.codigo == CampoFormulario.CODIGO_TELEFONE_EQUIPAMENTO }?.valorArmazenado
    }

//    String getNomeResponsavelPreenchimento() {
//        return campos.find{ it.codigo == CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO }?.valorArmazenado
//    }

//    void setNomeResponsavelPreenchimento(String nome) {
//        campos.find{ it.codigo == CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO }?.valorArmazenado = nome
//    }

    public CampoFormulario getCampoAvulso(String codigo) {
        CampoFormulario result = campos.find{ it.codigo == codigo && it.origem?.avulso }
        if (! result)
            throw new RuntimeException("Campo avulso ${codigo} não encontrado")
        return result
    }

    public Object getConteudoCampo(String codigo) {
        CampoFormulario result = campos.find{ it.codigo == codigo }
        if (! result)
            throw new RuntimeException("Campo ${codigo} não encontrado")
        return result.valorArmazenado
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
    ArrayList<CampoFormulario> getCamposOrdenados(boolean somenteCamposParaPreenchimento) {
        ArrayList<CampoFormulario> temp = campos?.sort { [it.ordem ?: 9999 /*nulos no final*/, it.id] }
        return somenteCamposParaPreenchimento ? temp.findAll { it.exibirParaPreenchimento } : temp
    }

    ArrayList<ArrayList<CampoFormulario>> getCamposAgrupados(boolean somenteCamposParaPreenchimento) {

        String ultimoGrupo = ""
        ArrayList result = []
        ArrayList<CampoFormulario> listaTemp = []
        getCamposOrdenados(somenteCamposParaPreenchimento).each { CampoFormulario campo ->
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
    }

}
