package org.apoiasuas.cidadao

import org.apoiasuas.anotacoesDominio.InfoClasseDominio
import org.apoiasuas.anotacoesDominio.InfoPropriedadeDominio
import org.apoiasuas.cidadao.detalhe.CampoDetalhe
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.lookup.DetalhesJSON
import org.apoiasuas.lookup.Escolaridade
import org.apoiasuas.lookup.Sexo
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.DominioProtegidoServico
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.ambienteExecucao.AmbienteExecucao
import org.apoiasuas.ambienteExecucao.SqlProprietaria
import org.apoiasuas.util.ApoiaSuasDateUtils
import org.apoiasuas.DetalheService
import org.grails.databinding.BindingFormat
import org.joda.time.Period
import org.joda.time.DateTime

@InfoClasseDominio(codigo=CampoFormulario.Origem.CIDADAO)
class Cidadao implements Serializable, DominioProtegidoServico, DetalhesJSON {

    //TODO: internacionalizar todas as descricoes de campo das annotations InfoPropriedadeDominio
    @InfoPropriedadeDominio(codigo='nome_completo', descricao = 'Nome completo', tamanho = 60)
    String nomeCompleto //importado

    //FIXME: implementar busca por nomeCompleto OU nomeSocial. Usar nome social nos encaminhamentos (mantendo o nome oficial nas guias de documentação)
    String nomeSocial

    @InfoPropriedadeDominio(codigo='parentesco_referencia', descricao = 'Parentesco (referência)', tamanho = 20)
    String parentescoReferencia //importado

    @BindingFormat('dd/MM/yyyy')
    @InfoPropriedadeDominio(codigo='data_nascimento', descricao = 'Data de nascimento', tipo = CampoFormulario.Tipo.DATA)
    Date dataNascimento //importado

    Date dataNascimentoAproximada //importado

    @InfoPropriedadeDominio(codigo='nis', descricao = 'NIS', tamanho = 20)
    String nis //importado

    @InfoPropriedadeDominio(codigo='nome_mae', descricao = 'Nome da mãe', tamanho = 60)
    String nomeMae

    @InfoPropriedadeDominio(codigo='nome_pai', descricao = 'Nome do pai', tamanho = 60)
    String nomePai

    @InfoPropriedadeDominio(codigo='identidade', descricao = 'Nº Identidade', tamanho = 20)
    String identidade

    @InfoPropriedadeDominio(codigo='cpf', descricao = 'CPF', tamanho = 20)
    String cpf

    @InfoPropriedadeDominio(codigo='naturalidade', descricao = 'Naturalidade', tamanho = 60)
    String naturalidade

    @InfoPropriedadeDominio(codigo='UF_naturalidade', descricao = 'UF da naturalidade', tamanho = 2)
    String UFNaturalidade

    Boolean analfabeto
    Escolaridade escolaridade
    Sexo sexo

    boolean referencia //importado
    UsuarioSistema criador, ultimoAlterador;
    ServicoSistema servicoSistemaSeguranca

    Date dateCreated, lastUpdated, dataUltimaImportacao;
    boolean habilitado = true;
    String detalhes;
    Set<Auditoria> auditoria = [];

//CAMPOS TRANSIENTES
    Integer ord
    Integer idadeAproximada
    Map<String, CampoDetalhe> mapaDetalhes = [:]
    static transients = ['ord', 'mapaDetalhes', 'idadeAproximada', 'desabilitado'];

//CAMPOS DE DETALHES USADOS EM FORMULÁRIOS
    public static final String CODIGO_NACIONALIDADE = "nacionalidade"

    static belongsTo = [familia: Familia] //importado

    static hasMany = [auditoria: Auditoria]

    static constraints = {
        id(bindable: true) //permite que uma propriedade transiente seja alimentada automaticamente pelo construtor
        referencia(nullable: false)
        criador(nullable: false)
        ultimoAlterador(nullable: false)
        nomeCompleto(nullable: false, maxSize: 255, unique: 'familia') //Cria um índice composto e único contendo os campos familia(id) e nomeCompleto
        familia(nullable: false) //Cria um índice composto e único contendo os campos familia(id) e nomeCompleto
        servicoSistemaSeguranca(nullable: false)
        ord(bindable: true) //permite que uma propriedade transiente seja alimentada automaticamente pelo construtor
        idadeAproximada(bindable: true) //permite que uma propriedade transiente seja alimentada automaticamente pelo construtor
        desabilitado(bindable: true) //permite que uma propriedade transiente seja alimentada automaticamente pelo construtor
    }

    static mapping = {
        referencia(defaultValue: AmbienteExecucao.SQL_FACADE.getBoolean(false))
        id generator: 'native', params: [sequence: 'sq_cidadao']
//        origemImportacaoAutomatica(defaultValue: AmbienteExecucao.getFalse())
//        familia column:'familia', index:'Cidadao_Familia_Idx'
//        nomeCompleto column:'nomeCompleto', index:'Cidadao_Nome_Idx'
        servicoSistemaSeguranca fetch: 'join' //por questoes de seguranca, sempre que um cidadao eh obtido do banco de dados, o servicoSistema precisara ser consultado
        detalhes length: 1000000
    }

    static Cidadao novaInstancia() {
        return new Cidadao()
    }

    public String toString() { return nomeCompleto ?: "sem nome (criado em "+dateCreated+")" }

    public boolean alteradoAposImportacao() {
        return dataUltimaImportacao == null || ! ApoiaSuasDateUtils.momentosProximos(dataUltimaImportacao, lastUpdated)
    }

    public Integer getIdade() {
        return dataNascimento ? new Period(new DateTime(dataNascimento), new DateTime()).years : null
    }

    public Integer idadeOuAprox() {
        log.debug('idade aprox')
        Date base = dataNascimento ?: dataNascimentoAproximada;
        return base ? new Period(new DateTime(base), new DateTime()).years : null
    }

    @Override
    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
        DetalheService.parseDetalhes(this, detalhes)
    }

    def afterLoad() {
        //calcula o campo derivado "idadeAproximada"
        this.idadeAproximada = ApoiaSuasDateUtils.yearsBetween(this.dataNascimentoAproximada, new Date())
    }

    public void setDesabilitado(boolean desabilitado) {
        habilitado = ! desabilitado;
    }

    public boolean getDesabilitado() {
        return ! habilitado;
    }

}
