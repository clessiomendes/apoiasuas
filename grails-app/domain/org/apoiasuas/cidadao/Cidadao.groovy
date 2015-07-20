package org.apoiasuas.cidadao

import org.apoiasuas.anotacoesDominio.InfoClasseDominio
import org.apoiasuas.anotacoesDominio.InfoPropriedadeDominio

import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.AmbienteExecucao
import org.apoiasuas.util.DateUtils
import org.docx4j.docProps.core.dc.terms.Period
import org.grails.databinding.BindingFormat
import org.joda.time.Period
import org.joda.time.DateTime

@InfoClasseDominio(codigo=CampoFormulario.Origem.CIDADAO)
class Cidadao implements Serializable {

    //TODO: internacionalizar todas as descricoes de campo das annotations InfoPropriedadeDominio
    @InfoPropriedadeDominio(codigo='nome_completo', descricao = 'Nome completo', tamanho = 60)
    String nomeCompleto //importado

    @InfoPropriedadeDominio(codigo='parentesco_referencia', descricao = 'Parentesco (referência)', tamanho = 20)
    String parentescoReferencia //importado

    @BindingFormat('dd/MM/yyyy')
    @InfoPropriedadeDominio(codigo='data_nascimento', descricao = 'Data de nascimento', tipo = CampoFormulario.Tipo.DATA)
    Date dataNascimento //importado

    @InfoPropriedadeDominio(codigo='nis', descricao = 'NIS', tamanho = 20)
    String nis //importado

    @InfoPropriedadeDominio(codigo='nome_mae', descricao = 'Nome da mãe', tamanho = 60)
    String nomeMae

    @InfoPropriedadeDominio(codigo='nome_pai', descricao = 'Nome do pai', tamanho = 60)
    String nomePai

    @InfoPropriedadeDominio(codigo='identidade', descricao = 'Nº Identidade', tamanho = 20)
    String identidade

    @InfoPropriedadeDominio(codigo='numero_ctps', descricao = 'Nº CTPS', tamanho = 20)
    String numeroCTPS

    @InfoPropriedadeDominio(codigo='serie_ctps', descricao = 'Série CTPS', tamanho = 20)
    String serieCTPS

    @InfoPropriedadeDominio(codigo='cpf', descricao = 'CPF', tamanho = 20)
    String cpf

    @InfoPropriedadeDominio(codigo='estado_civil', descricao = 'Estado Civil', tamanho = 20)
    String estadoCivil

    @InfoPropriedadeDominio(codigo='naturalidade', descricao = 'Naturalidade', tamanho = 60)
    String naturalidade

    @InfoPropriedadeDominio(codigo='UF_naturalidade', descricao = 'UF da naturalidade', tamanho = 2)
    String UFNaturalidade

    boolean referencia //importado
    UsuarioSistema criador, ultimoAlterador;
    Date dateCreated, lastUpdated, dataUltimaImportacao;
//    boolean origemImportacaoAutomatica //importado
//    Parentesco parentescoReferencia


    static belongsTo = [familia: Familia] //importado

    static constraints = {
        id(bindable: true)
        referencia(nullable: false)
        criador(nullable: false)
        ultimoAlterador(nullable: false)
        familia unique: 'nomeCompleto' //Cria um índice composto e único contendo os campos familia(id) e nomeCompleto
    }

    static mapping = {
        referencia(defaultValue: AmbienteExecucao.getBoolean(false))
        id generator: 'native', params: [sequence: 'sq_cidadao']
//        origemImportacaoAutomatica(defaultValue: AmbienteExecucao.getFalse())
//        familia column:'familia', index:'Cidadao_Familia_Idx'
//        nomeCompleto column:'nomeCompleto', index:'Cidadao_Nome_Idx'
    }

    static Cidadao novaInstancia() {
        return new Cidadao()
    }

    String toString() { return nomeCompleto ?: "sem nome (criado em "+dateCreated+")" }

    public boolean alteradoAposImportacao() {
        return dataUltimaImportacao == null || ! DateUtils.momentosProximos(dataUltimaImportacao, lastUpdated)
    }

    public Integer getIdade() {
        return dataNascimento ? new Period(new DateTime(dataNascimento), new DateTime()).years : null
    }
}
