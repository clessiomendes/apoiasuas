package org.apoiasuas.cidadao

import org.apache.commons.collections.FactoryUtils
import org.apache.commons.collections.list.LazyList
import org.apoiasuas.marcador.AcaoFamilia
import org.apoiasuas.anotacoesDominio.InfoClasseDominio
import org.apoiasuas.anotacoesDominio.InfoPropriedadeDominio

import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.marcador.AssociacaoMarcador
import org.apoiasuas.marcador.OutroMarcador
import org.apoiasuas.marcador.OutroMarcadorFamilia
import org.apoiasuas.marcador.VulnerabilidadeFamilia
import org.apoiasuas.marcador.ProgramaFamilia
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
    Set<VulnerabilidadeFamilia> vulnerabilidades
    Set<OutroMarcadorFamilia> outrosMarcadores
    Set<Cidadao> membros
    AcompanhamentoFamiliar acompanhamentoFamiliar

    @InfoPropriedadeDominio(codigo='telefone', descricao = 'Telefone', tipo = CampoFormulario.Tipo.TELEFONE, tamanho = 10)
    String telefone //campo transiente (usado para conter telefones escolhidos/digitados pelo operador em casos de uso como o de preenchimento de formulario
    static transients = ['telefone', 'programasHabilitados', 'vulnerabilidadesHabilitadas', 'acoesHabilitadas',
                         'outrosMarcadoresHabilitados', 'todosOsMarcadores']

    ServicoSistema servicoSistemaSeguranca

    static hasOne = [acompanhamentoFamiliar: AcompanhamentoFamiliar]

    static hasMany = [membros: Cidadao, telefones: Telefone, monitoramentos: Monitoramento,
                      programas: ProgramaFamilia, acoes: AcaoFamilia,
                      outrosMarcadores: OutroMarcadorFamilia,
                      vulnerabilidades: VulnerabilidadeFamilia]

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
        return membros?.findAll{ it.referencia && it.habilitado }?.min{ it.dataNascimento }
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

    public List<Cidadao> getMembrosOrdemAlfabetica() {
        getMembrosHabilitados()?.sort{ it.nomeCompleto?.toLowerCase() }
    }

    public boolean alteradoAposImportacao() {
        return dataUltimaImportacao == null || ! DateUtils.momentosProximos(dataUltimaImportacao, lastUpdated)
    }

    public String vulnerabilidadesToString(String separador = ", ") {
        return CollectionUtils.join(vulnerabilidadesHabilitadas.collect { it.vulnerabilidade.descricao }, separador )
    }
    public String programasToString(String separador = ", ") {
        return CollectionUtils.join(programasHabilitados.collect { it.programa.descricao }, separador )
    }
    public String acoesToString(String separador = ", ") {
        return CollectionUtils.join(acoesHabilitadas.collect { it.acao.descricao }, separador )
    }
    public String outrosMarcadoresToString(String separador = ", ") {
        return CollectionUtils.join(outrosMarcadoresHabilitados.collect { it.outroMarcador.descricao }, separador )
    }

    public Set<VulnerabilidadeFamilia> getVulnerabilidadesHabilitadas() {
        //retorna apenas as habilitadas e em ordem alfabetica
        return vulnerabilidades.findAll{ it.habilitado }.sort { it.marcador?.descricao?.toLowerCase() }
    }
    public Set<AcaoFamilia> getAcoesHabilitadas() {
        //retorna apenas as habilitadas e em ordem alfabetica
        return acoes.findAll{ it.habilitado }.sort { it.marcador?.descricao?.toLowerCase() }
    }
    public Set<ProgramaFamilia> getProgramasHabilitados() {
        //retorna apenas as habilitadas e em ordem alfabetica
        return programas.findAll{ it.habilitado }.sort { it.marcador?.descricao?.toLowerCase() }
    }
    public Set<OutroMarcadorFamilia> getOutrosMarcadoresHabilitados() {
        //retorna apenas as habilitadas e em ordem alfabetica
        return outrosMarcadores.findAll{ it.habilitado }.sort { it.marcador?.descricao?.toLowerCase() }
    }
    public Set<AssociacaoMarcador> getTodosOsMarcadores() {
        return programas + acoes + vulnerabilidades + outrosMarcadores;
    }
    public List<Cidadao> getMembrosHabilitados(boolean habilitados = true) {
        return membros.sort{ it.id }.findAll{ it.habilitado == habilitados }
    }
    public Set<Cidadao> getMembros() {
        LinkedHashSet<Cidadao> result = new LinkedHashSet<Cidadao>(membros.sort{ a,b ->
            if (a.referencia && ! b.referencia)
                return -1;
            if (b.referencia && ! a.referencia)
                return 1;
            if (a.dataNascimento && ! b.dataNascimento)
                return -1;
            if (b.dataNascimento && ! a.dataNascimento)
                return 1;
            if (a.dataNascimento.equals(b.dataNascimento))
                return a.id.compareTo(b.id)
            else
                return a.dataNascimento.compareTo(b.dataNascimento)
        });
    }
}
