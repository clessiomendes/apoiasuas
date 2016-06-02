package org.apoiasuas.importacao

import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.AmbienteExecucao

class DefinicoesImportacaoFamilias  implements Serializable {

    Integer linhaDoCabecalho
    Integer abaDaPlanilha

    String colunaCodigoFamilia
    String colunaDataCadastroFamilia

    String colunaTipoLogradouro
    String colunaNomeLogradouro
    String colunaNumero
    String colunaComplemento
    String colunaBairro
    String colunaCEP
    String colunaMunicipio
    String colunaUF
    String colunaTelefones


    String colunaNomeCidadao
    String colunaNomeReferencia //se sobrepõe a colunaNomeCidadao APENAS NA PRIMEIRA LINHA
    String colunaNIS
    String colunaNISReferencia //se sobrepõe a colunaNIS APENAS NA PRIMEIRA LINHA
    String colunaParentesco
    String colunaDataNascimento

    String colunaBPC
    String colunaPBF

    UsuarioSistema ultimoAlterador;
    Date lastUpdated;

    ServicoSistema servicoSistemaSeguranca;

    static constraints = {
        ultimoAlterador(nullable: false)
        lastUpdated(nullable: false)
        servicoSistemaSeguranca(nullable: false)
    }

    static mapping = {
        linhaDoCabecalho(defaultValue:AmbienteExecucao.getLiteralInteiro(1))
        abaDaPlanilha(defaultValue:AmbienteExecucao.getLiteralInteiro(1))
        id generator: 'native', params: [sequence: 'sq_definicoes_importacao_familias']
    }

    String toString() { "Definicao de " + lastUpdated + (" por " + ultimoAlterador?.username) ?: "" }

}
