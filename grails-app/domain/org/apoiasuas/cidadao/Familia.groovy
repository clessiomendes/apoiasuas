package org.apoiasuas.cidadao

import org.apache.commons.collections.FactoryUtils
import org.apache.commons.collections.list.LazyList
import org.apache.xpath.operations.Bool
import org.apoiasuas.acao.AcaoFamilia
import org.apoiasuas.anotacoesDominio.InfoClasseDominio
import org.apoiasuas.anotacoesDominio.InfoPropriedadeDominio

import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.programa.Programa
import org.apoiasuas.programa.ProgramaFamilia
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.util.DateUtils
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.CollectionUtils;

@InfoClasseDominio(codigo=CampoFormulario.Origem.FAMILIA)
class Familia implements Serializable {

    @InfoPropriedadeDominio(codigo='codigo_legado', descricao = 'Cad', tamanho = 10, atualizavel = false)
    String codigoLegado  //importado
    Set<Telefone> telefones

//	Integer numeroComodos
//	Integer numeroQuartos
//	Boolean coletaLixo
//	String codigoFamiliarCadUnico
//	DestinoEsgoto destinoEsgoto
//	FornecimentoAgua fornecimentoAgua
//	FornecimentoEnergia fornecimentoEnergia
//	PropriedadeDomicilio propriedadeDomicilio
//	RiscoDomicilio riscoDomicilio
    SituacaoFamilia situacaoFamilia
//    Boolean familiaAcompanhada
    UsuarioSistema tecnicoReferencia
    UsuarioSistema criador, ultimoAlterador;
    Date dateCreated, lastUpdated, dataUltimaImportacao;
    Endereco endereco //importado
    Set<ProgramaFamilia> programas
    Set<AcaoFamilia> acoes

    @InfoPropriedadeDominio(codigo='telefone', descricao = 'Telefone', tipo = CampoFormulario.Tipo.TELEFONE, tamanho = 10)
    String telefone //campo transiente (usado para conter telefones escolhidos/digitados pelo operador em casos de uso como o de preenchimento de formulario
    static transients = ['telefone']

    ServicoSistema servicoSistemaSeguranca

    static hasMany = [membros: Cidadao, telefones: Telefone, programas: ProgramaFamilia, acoes: AcaoFamilia]

    static embedded = ['endereco']

    static constraints = {
        id(bindable: true)
        telefone(bindable: true) //permite que uma propriedade transiente seja alimentada autmaticamente pelo construtor
        situacaoFamilia(nullable: false)
        criador(nullable: false)
        ultimoAlterador(nullable: false)
//        codigoLegado(unique: true)
        servicoSistemaSeguranca(nullable: false)
    }

    String getTelefonesToString() {
        return telefones?.join(", ")
    }

    Cidadao getReferencia() {
        Cidadao result = null
        membros?.each {
            if (it?.referencia)
                result = it
        }
        return result
    }

    String toString() {

        return CollectionUtils.join([
                codigoLegado ? "Cad " + codigoLegado : null,
                getReferencia() ? "Referência " + getReferencia().toString() : null], ", ")
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_familia']
//        endLogradouro formula: 'endereco_nome_logradouro'
//        codigoLegado column:'codigoLegado', index:'Familia_Cod_Legado_Idx'
    }

    static Familia novaInstacia() {
        Familia result = new Familia()
        result.endereco = new Endereco()
//      TODO result.despesas = new Despesas()
        return result
    }

    /*
     * Usado para popular automaticamente novos cidadaos quando necessário (por exemplo, em uma tela de mestre-detalhe)
     */
    def getExpandableMembrosList() {
        return LazyList.decorate(membros,FactoryUtils.instantiateFactory(Cidadao.class))
    }

    List<Cidadao> getMembrosOrdemAlfabetica() {
        membros?.sort{ it.nomeCompleto?.toLowerCase() }
    }

    public boolean alteradoAposImportacao() {
        return dataUltimaImportacao == null || ! DateUtils.momentosProximos(dataUltimaImportacao, lastUpdated)
    }

/*
    String mostraTecnicoReferencia() {
        if (!familiaAcompanhada)
            return null
        return 'Técnico de referência: ' + (tecnicoReferencia?.username ?: 'indefinido')
    }
*/
}

class Despesas  implements Serializable {
    Float aluguel
    Float energiaEletrica
    Float agua
    Float supermercado
    Float remedios
    Float outros
}
//  CAMPOS COMPOSTOS     <<<===================
