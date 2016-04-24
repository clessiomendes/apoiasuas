package org.apoiasuas.formulario

import org.apoiasuas.anotacoesDominio.InfoDominioUtils
import org.apoiasuas.anotacoesDominio.InfoPropriedadeDominio
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.Endereco
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.util.StringUtils

import java.lang.reflect.Field
import java.text.SimpleDateFormat

class CampoFormulario {

    public static String CODIGO_DATA_PREENCHIMENTO = "data_preenchimento"
    public static String CODIGO_NOME_EQUIPAMENTO = "nome_equipamento"
    public static String CODIGO_ENDERECO_EQUIPAMENTO = "endereco_equipamento"
    public static String CODIGO_TELEFONE_EQUIPAMENTO = "telefone_equipamento"
    public static String CODIGO_RESPONSAVEL_PREENCHIMENTO = "responsavel_preenchimento"

    static enum Tipo { TEXTO, INTEIRO, DATA, TELEFONE, SELECAO
        boolean isData() { return this == DATA }
        boolean isTexto() { return this == TEXTO }
    }

    enum Origem { FAMILIA(Familia.class), ENDERECO(Endereco.class), CIDADAO(Cidadao.class), AVULSO(null)
        Class classeDominio
        Origem(Class classeDominio) { this.classeDominio = classeDominio }
        boolean isAvulso() { return this == AVULSO }
        boolean isCidadao() { return this == CIDADAO }
        boolean isFamilia() { return this == FAMILIA }
        boolean isEndereco() { return this == ENDERECO }
    }

//Propriedades persistentes
    Origem origem  //Codigo definido em InfoClaseDominio
    String codigo //Codigo (deve coincidir com o codigo de InfoPropriedadeDominio, quando atrelado ao cadastro de usuario)
    String descricaoPersonalizada
    String grupo
    Integer ordem
    boolean obrigatorio = false
    boolean listaLogradourosCidadaos = false
    int multiplasLinhas

    //Campos usados somente para origem do tipo AVULSO (pois nao ha como obte-los da anotacao do campo de BD)
    Tipo tipoPersonalizado
    int tamanhoPersonalizado
    boolean exibirParaPreenchimento = true

//Propriedades transientes
    Field propriedadeDominioField = null
    InfoPropriedadeDominio propriedadeDominioInfo = null
    private Object valorAvulso

    static transients = ['propriedadeDominioField','propriedadeDominioInfo', 'valorArmazenado']

    static belongsTo = [formulario: Formulario]

    /**
     * Validação de obrigatoriedade a ser aplicada nas propriedades descricao, tipo e tamanho personalizado de campos avulsos
     */
    def static obrigatoriosAvulso = { Object valor, CampoFormulario instancia ->
        if (instancia.origem?.avulso && ! valor)
            return ['campo.nao.pode.ser.nulo', "{0}: Valor não pode ser nulo"]
    }

    def static validaCodigoBancoDados = { String codigo, CampoFormulario instancia ->
        if ( (instancia.origem?.cidadao && ! InfoDominioUtils.infoPropriedadePeloCodigo(Cidadao.class, codigo)) ||
                (instancia.origem?.familia && ! InfoDominioUtils.infoPropriedadePeloCodigo(Familia.class, codigo)) ||
                (instancia.origem?.endereco && ! InfoDominioUtils.infoPropriedadePeloCodigo(Endereco.class, codigo)))
                return "Código ${codigo} não encontrado nos campos anotados da classe ${origem}"
    }

    static constraints = {
        codigo(nullable: false, unique: ['formulario','origem'], validator: { String codigo, CampoFormulario instancia ->
            //Verifica se o codigo escolhido realmente corresponde a um codigo previsto na classe persistente
            if ( (instancia.origem?.cidadao && ! InfoDominioUtils.infoPropriedadePeloCodigo(Cidadao.class, codigo)) ||
                    (instancia.origem?.familia && ! InfoDominioUtils.infoPropriedadePeloCodigo(Familia.class, codigo)) ||
                    (instancia.origem?.endereco && ! InfoDominioUtils.infoPropriedadePeloCodigo(Endereco.class, codigo)))
                return "Código ${codigo} não encontrado nos campos anotados da classe ${instancia.origem}"
        })
        origem(nullable: false)
        multiplasLinhas(validator: { Integer multiplasLinhas, CampoFormulario instancia ->
            if (multiplasLinhas && ! instancia.tipo?.texto)
                return "Somente campos TEXTO podem ter múltiplas linhas (${multiplasLinhas})"
        })
        ordem(nullable: true)
        grupo(nullable: true)
        obrigatorio(nullable: false)
        formulario(nullable: false)
        descricaoPersonalizada(nullable: true, validator: { Object valor, CampoFormulario instancia ->
            //valor nao pode ser nulo para campos avulsos
            if (instancia.origem?.avulso && ! valor)
                return ['campo.nao.pode.ser.nulo', "{0}: Valor não pode ser nulo"]
        })
        tipoPersonalizado(nullable: true, validator: { Object valor, CampoFormulario instancia ->
            //valor nao pode ser nulo para campos avulsos
            if (instancia.origem?.avulso && ! valor)
                return ['campo.nao.pode.ser.nulo', "{0}: Valor não pode ser nulo"]
        })
        tamanhoPersonalizado(nullable: true, validator: { Object valor, CampoFormulario instancia ->
            //valor nao pode ser nulo para campos avulsos do tipo TEXTO
            if (instancia.origem?.avulso && instancia.tipo?.texto && ! valor)
                return ['campo.nao.pode.ser.nulo', "{0}: Valor não pode ser nulo para tipo TEXTO"]
        })
        exibirParaPreenchimento(nullable: true)
    }

    static mapping = {
        obrigatorio defaultValue: false
        id generator: 'native', params: [sequence: 'sq_campo_formulario']
    }

    void setValorArmazenado(Object valor) {
        if (tipo == Tipo.DATA)
            if (valor instanceof String)
                valor = valor ? new SimpleDateFormat("dd/MM/yyyy").parse(valor) : null
        switch (origem) {
            case Origem.AVULSO: valorAvulso = valor; break
            case Origem.CIDADAO: formulario.cidadao?."${nomeCampoPersistente}" = valor; break
            case Origem.FAMILIA: formulario.cidadao?.familia?."${nomeCampoPersistente}" = valor; break
            case Origem.ENDERECO: formulario.cidadao?.familia?.endereco?."${nomeCampoPersistente}" = valor; break
            default: throw new RuntimeException("Origem não tratada: ${origem}")
        }
        log.debug getValorArmazenado()
    }

    /**
     * Acessa a instância (transiente) guardada internamente e retorna o conteúdo do campo correspondente
     */
    Object getValorArmazenado() {
        switch (origem) {
            case Origem.AVULSO: return valorAvulso //formulario.camposAvulsos?."${codigo}"
            case Origem.CIDADAO: return formulario.cidadao?."${nomeCampoPersistente}"
            case Origem.FAMILIA: return formulario.cidadao?.familia?."${nomeCampoPersistente}"
            case Origem.ENDERECO: return formulario.cidadao?.familia?.endereco?."${nomeCampoPersistente}"
            default: throw new RuntimeException("Origem não tratada: ${origem}")
        }
    }

//  atalho para informacoes do campo (obtidas à partir da Annotation ou do Field)
    String getDescricao() { return descricaoPersonalizada ?: propriedadeDominioInfo?.descricao() }
    String getNomeCampoPersistente() { return propriedadeDominioField?.name }
    Boolean isAtualizavel() { return origem?.avulso ? false : propriedadeDominioInfo?.atualizavel() }
    Tipo getTipo() { return origem?.avulso ? tipoPersonalizado : propriedadeDominioInfo?.tipo() }
    Integer getTamanho() { return origem?.avulso ? tamanhoPersonalizado : propriedadeDominioInfo?.tamanho() }

    /**
     * Caminho para acesso a um determinado campo em um grafo cidadao->familia->endereco
     * E usado para dar nome aos inputs da tela e, com isso, alimentar automaticamente o grafo de objetos gerados
     * pelo Grails na submissao (com autobinding de cada campo das classes Cidadao, Familie e Endereco)
     * Obs: Campos avulsos sao designados com "avulso." e, posteriormente, alimentam um mapa no formulario. Como
     * para eles nao existe campo persistente, adotamos o codigo em seu lugar.
     */
    public String getCaminhoCampo() {
        switch (origem) {
            case Origem.AVULSO: return 'avulso.' + codigo
            case Origem.CIDADAO: return StringUtils.firstLowerCase(Cidadao.class.simpleName) + '.' + nomeCampoPersistente
            case Origem.FAMILIA: return StringUtils.firstLowerCase(Familia.class.simpleName) + '.' + nomeCampoPersistente
            case Origem.ENDERECO: return StringUtils.firstLowerCase(Endereco.class.simpleName) + '.' + nomeCampoPersistente
            default: throw new RuntimeException("Origem não tratada: ${origem}")
        }
    }

    public String toString() {
        return codigo ? "${origem}.${codigo}" : "campo ${id}"
    }

    /**
     * Carrega informacoes transientes em propriedadeDominioField e propriedadeDominioInfo
     */
    def afterLoad() {
        origem?.classeDominio?.getDeclaredFields().each {
            InfoPropriedadeDominio info = it.getAnnotation(InfoPropriedadeDominio.class)
            if (info?.codigo() == codigo) {
                propriedadeDominioField = it
                propriedadeDominioInfo = info
                return
            }
        }
    }

}
