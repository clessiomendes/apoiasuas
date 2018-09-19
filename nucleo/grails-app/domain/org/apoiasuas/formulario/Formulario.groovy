package org.apoiasuas.formulario

import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.FullTextSearchUtils

import java.text.ParseException

class Formulario implements Serializable {
    public static final String EXTENSAO_ARQUIVO = ".docx"

    String nome
    String descricao
    String tipo //para agrupar as opções de formulário apresentadas ao operador
    PreDefinidos formularioPreDefinido
    Set<CampoFormulario> campos = []
    Set<ModeloFormulario> modelos = []

    //Campos transientees
    byte[] template
    Cidadao cidadao
    Familia familia
    UsuarioSistema usuarioSistema //Campo transiente para armazenar um usuarioResponsavel (caso ele exista no formulario)
    boolean atualizarPersistencia
    FormularioEmitido formularioEmitido
    boolean anexarFichaNoEncaminhamento //Campo específico para formularios de encaminhamento
    static transients = ['formularioEmitido', 'cidadao', 'familia', 'usuarioSistema', 'dataPreenchimento',
                         'nomeEquipamento', 'enderecoEquipamento', 'telefoneEquipamento',
                         'emailEquipamento', 'cidadeEquipamento', 'ufEquipamento',
//                         'arquivosModelos',
                         /*'nomeResponsavelPreenchimento', 'camposAvulsos',*/ 'atualizarPersistencia',
                        'camposOrdenados', 'campoAvulso', 'conteudoCampo', 'modeloPadrao', 'anexarFichaNoEncaminhamento'
    ]

//FIXME: busca por formularios desabilitada porque, duranta a incializacao do elasticsearch, caso um formulario tenha sido indexado com um enum removido em Predefinidos, a aplicacao nao consegue inicializar
//mensagem de erro: Error initializing the application: Unknown name value [PASSE_LIVRE_PCD] for enum class [org.apoiasuas.formulario.PreDefinidos]
/*
    static searchable = {                           // <-- elasticsearch plugin
        only = ["nome","descricao"]
        nome alias:FullTextSearchUtils.MEU_TITULO, index:'analyzed', boost:50
        descricao alias:FullTextSearchUtils.MEUS_DETALHES, index:'analyzed', boost:5
//        arquivosModelos alias:FullTextSearchUtils.MEUS_DETALHES, index:'analyzed', boost:1, type: "attachment"
    }
*/

//    public List<byte[]> getArquivosModelos() {
//        return modelos*.arquivo
//    }

    static hasMany = [campos: CampoFormulario, modelos: ModeloFormulario]

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_formulario']
    }

    static constraints = {
        nome(nullable: false, unique: true)
        descricao(nullable: true)
        formularioPreDefinido(nullable: true, unique: true)
    }

    /**
     * Atribui a cada campo avulso previsto no formulario, o correspondente valor obtido do request
     * @param params O "recorte" do request correspondente aos conteudos avulsos
     * @return Retorna uma lista de erros de conversão (ou vazia, se não houve nenhum erro)
     */
    public void setCamposAvulsos(Map params) {
        //Filtra, de todos os campos, apenas aqueles do tipo AVULSO e itera sobre eles
        campos.findAll { it.origem?.avulso }*.each { campo ->
                campo.valorArmazenado = params.get(campo.codigo)
        }
    }

    public void setDataPreenchimento(Date date) {
        campos.find{ it.codigo == CampoFormulario.CODIGO_DATA_PREENCHIMENTO }?.valorArmazenado = date
    }

    public Date getDataPreenchimento() {
        return campos.find{ it.codigo == CampoFormulario.CODIGO_DATA_PREENCHIMENTO }?.valorArmazenado
    }

    public void setNomeEquipamento(String nomeEquipamento) {
        campos.find{ it.codigo == CampoFormulario.CODIGO_NOME_EQUIPAMENTO }?.valorArmazenado = nomeEquipamento
    }

    public Date getNomeEquipamento() {
        return campos.find{ it.codigo == CampoFormulario.CODIGO_NOME_EQUIPAMENTO }?.valorArmazenado
    }

    public void setEnderecoEquipamento(String enderecoEquipamento) {
        campos.find{ it.codigo == CampoFormulario.CODIGO_ENDERECO_EQUIPAMENTO }?.valorArmazenado = enderecoEquipamento
    }

    public Date getEnderecoEquipamento() {
        return campos.find{ it.codigo == CampoFormulario.CODIGO_ENDERECO_EQUIPAMENTO }?.valorArmazenado
    }

    public void setTelefoneEquipamento(String telefoneEquipamento) {
        campos.find{ it.codigo == CampoFormulario.CODIGO_TELEFONE_EQUIPAMENTO }?.valorArmazenado = telefoneEquipamento
    }

    public Date getTelefoneEquipamento() {
        return campos.find{ it.codigo == CampoFormulario.CODIGO_TELEFONE_EQUIPAMENTO }?.valorArmazenado
    }

    public void setEmailEquipamento(String emailEquipamento) {
        campos.find{ it.codigo == CampoFormulario.CODIGO_EMAIL_EQUIPAMENTO }?.valorArmazenado = emailEquipamento
    }

    public Date getEmailEquipamento() {
        return campos.find{ it.codigo == CampoFormulario.CODIGO_EMAIL_EQUIPAMENTO }?.valorArmazenado
    }

    public void setUfEquipamento(String ufEquipamento) {
        campos.find{ it.codigo == CampoFormulario.CODIGO_UF_EQUIPAMENTO }?.valorArmazenado = ufEquipamento
    }

    public Date getUfEquipamento() {
        return campos.find{ it.codigo == CampoFormulario.CODIGO_UF_EQUIPAMENTO }?.valorArmazenado
    }

    public void setCidadeEquipamento(String cidadeEquipamento) {
        campos.find{ it.codigo == CampoFormulario.CODIGO_CIDADE_EQUIPAMENTO }?.valorArmazenado = cidadeEquipamento
    }

    public Date getCidadeEquipamento() {
        return campos.find{ it.codigo == CampoFormulario.CODIGO_CIDADE_EQUIPAMENTO }?.valorArmazenado
    }

//    String getNomeResponsavelPreenchimento() {
//        return campos.find{ it.codigo == CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO }?.valorArmazenado
//    }

//    void setNomeResponsavelPreenchimento(String nome) {
//        campos.find{ it.codigo == CampoFormulario.CODIGO_RESPONSAVEL_PREENCHIMENTO }?.valorArmazenado = nome
//    }

    public CampoFormulario getCampo(String codigo) {
        CampoFormulario result = campos.find{ it.codigo == codigo }
        if (! result)
            throw new RuntimeException("Campo ${codigo} não encontrado")
        return result
    }

    public Object getConteudoCampo(String codigo) {
        CampoFormulario result = campos.find{ it.codigo == codigo }
        if (! result)
            throw new RuntimeException("Campo ${codigo} não encontrado")
        return result.valorArmazenado
    }

    public String geraNomeArquivo() {
        return nome ? nome.replaceAll("\\W+", "_") + EXTENSAO_ARQUIVO : nome //substitui caracteres que nao sejam alfanumericos
    }

    public String toString() {
        String result = nome + ', ' + descricao + '['
        campos?.each {
            result += it.ordem + ', ' + it.codigo
        }
        return result + ']'
    }

    /**
     * Ordena primeiro pelo campo "ordem" e depois pela sequencia de insercao
     */
    public ArrayList<CampoFormulario> getCamposOrdenados(boolean somenteCamposParaPreenchimento) {
        ArrayList<CampoFormulario> temp = campos?.sort { [it.ordem ?: 9999 /*nulos no final*/, it.id] }
        return somenteCamposParaPreenchimento ? temp.findAll { it.exibirParaPreenchimento } : temp
    }

    public ArrayList<ArrayList<CampoFormulario>> getCamposAgrupados(boolean somenteCamposParaPreenchimento) {

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

    public ModeloFormulario getModeloPadrao() {
        return modelos?.find { it.padrao }
    }

    /**
     * Caso haja um cidadao definido, retorna a familia ligada ao cidadao, se nao, retorna a familia autonoma
     */
    public Familia getFamilia() {
        return cidadao?.familia ?: familia;
    }

}
