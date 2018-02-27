package org.apoiasuas.cidadao

import org.apache.commons.collections.FactoryUtils
import org.apache.commons.collections.list.LazyList
import org.apoiasuas.cidadao.detalhe.CampoDetalhe
import org.apoiasuas.lookup.DetalhesJSON
import org.apoiasuas.marcador.AcaoFamilia
import org.apoiasuas.anotacoesDominio.InfoClasseDominio
import org.apoiasuas.anotacoesDominio.InfoPropriedadeDominio

import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.marcador.AssociacaoMarcador
import org.apoiasuas.marcador.OutroMarcadorFamilia
import org.apoiasuas.marcador.VulnerabilidadeFamilia
import org.apoiasuas.marcador.ProgramaFamilia
import org.apoiasuas.redeSocioAssistencial.RecursosServico
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.DominioProtegidoServico
import org.apoiasuas.util.AmbienteExecucao
import org.apoiasuas.util.ApoiaSuasDateUtils
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.CollectionUtils
import org.apoiasuas.DetalheService;

@InfoClasseDominio(codigo=CampoFormulario.Origem.FAMILIA)
class Familia implements Serializable, DominioProtegidoServico, DetalhesJSON {

    //Necessario acesso ao servico para definir se o ServicoSistema logado faz uso ou nao de codigo legado
    def segurancaService;

    @InfoPropriedadeDominio(codigo='cad', descricao = 'Cad', tamanho = 10, atualizavel = false)
    String cad //transiente

    String codigoLegado  //importado
    Set<Telefone> telefones = []

    SituacaoFamilia situacaoFamilia
    UsuarioSistema tecnicoReferencia
    UsuarioSistema criador, ultimoAlterador;
    Date dateCreated, lastUpdated, dataUltimaImportacao;
    Endereco endereco = new Endereco(); //importado
    Set<ProgramaFamilia> programas = []
    Set<AcaoFamilia> acoes = []
    Set<VulnerabilidadeFamilia> vulnerabilidades = []
    Set<OutroMarcadorFamilia> outrosMarcadores = []
    Set<Cidadao> membros = []

    AcompanhamentoFamiliar acompanhamentoFamiliar //deveria ser hasOne, mas essa funcionalidade não está estável no Grails/Hibernate
    String detalhes;
    Boolean bolsaFamilia;
    Boolean exBolsaFamilia;
    Boolean bpc;

//CAMPOS TRANSIENTES
    @InfoPropriedadeDominio(codigo='telefone', descricao = 'Telefone', tipo = CampoFormulario.Tipo.TELEFONE, tamanho = 10)
    String telefone //campo transiente (usado para conter telefones escolhidos/digitados pelo operador em casos de uso como o de preenchimento de formulario
    String nomeReferencia
    Map<String, CampoDetalhe> mapaDetalhes = [:]
    static transients = ['telefone', 'programasHabilitados', 'vulnerabilidadesHabilitadas', 'acoesHabilitadas',
                         'outrosMarcadoresHabilitados', 'todosOsMarcadores', 'cad', 'nomeReferencia', 'mapaDetalhes']

    ServicoSistema servicoSistemaSeguranca

//    static hasOne = [acompanhamentoFamiliar: AcompanhamentoFamiliar]

    static hasMany = [membros: Cidadao, telefones: Telefone, monitoramentos: Monitoramento,
                      programas: ProgramaFamilia, acoes: AcaoFamilia,
                      outrosMarcadores: OutroMarcadorFamilia,
                      vulnerabilidades: VulnerabilidadeFamilia]

    static embedded = ['endereco']
/*
    Familia() {
        log.debug("create Familia()");
    }
*/
    static constraints = {
        id(bindable: true)
        codigoLegado(nullable: true, maxSize: 60, unique: 'servicoSistemaSeguranca') //Cria um índice composto e único contendo os campos familia(id) e nomeCompleto
        telefone(bindable: true) //permite que uma propriedade transiente seja alimentada automaticamente pelo construtor
        situacaoFamilia(nullable: false)
        criador(nullable: false)
        ultimoAlterador(nullable: false)
        servicoSistemaSeguranca(nullable: false)
        endereco(nullable: false, cascade: true)
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_familia']
        servicoSistemaSeguranca fetch: 'join' //por questoes de seguranca, sempre que uma familia eh obtida do banco de dados, o servicoSistema precisara ser consultado
        detalhes length: 1000000
    }

    public String getTelefonesToString() {
        return telefones?.sort{it.dateCreated}?.join(", ")
    }

    public Cidadao getReferencia() {
        return membros?.findAll{ it.referencia && it.habilitado }?.min{ it.dataNascimento }
    }

    public String montaDescricao() {
        return CollectionUtils.join([
                toString(),
                getReferencia() ? "referência " + getReferencia().toString() : null
        ], ", ")
    }

    public String toString() {
        return "cad " + getCad();
    }

    /**
     * Exibe o identificador principal desta familia (o codigo legado ou o id)
     */
    public String getCad() {
        if (cad)
            return cad
        try {
            if (segurancaService.acessoRecursoServico(RecursosServico.IDENTIFICACAO_PELO_CODIGO_LEGADO))
                return codigoLegado ?: "S/C"
            else
                return id ?: "S/C";
        } catch (Exception e) {
            //ignora eventual erro de acesso ao servico "segurancaService" e exibe id ou codigoLegado
            log.error(e);
            if (AmbienteExecucao.isDesenvolvimento())
                throw e;
            return codigoLegado ?: id?.toString();
        }

    }

    public setCad(String cad) {
        this.cad = cad;
    }

    static Familia novaInstacia() {
        Familia result = new Familia()
        result.endereco = new Endereco()
        return result
    }

    /*
     * Usado para popular automaticamente novos cidadaos quando necessário (por exemplo, em uma tela de mestre-detalhe)
     */
    def getExpandableMembrosList() {
        return LazyList.decorate(membros,FactoryUtils.instantiateFactory(Cidadao.class))
    }

    public List<Cidadao> getMembrosOrdemAlfabetica(boolean habilitados = true) {
        return membros.findAll{ it.habilitado == habilitados }.sort{ it.nomeCompleto?.toLowerCase() }
    }

    /**
     * Devolve todos os membros em uma ordem padrão: 1º a referencia, depois do mais velho para o mais novo.
     * obs: a lista retornada não mantem a ligação com a sessão de persistência
     * @param habilitados filtra somente os membros habilitados (default true)
     * @return
     */
    public List<Cidadao> getMembrosOrdemPadrao(Boolean habilitados = null) {
        return membros?.findAll{ (habilitados != null) ? it.habilitado == habilitados : true }?.sort{ a,b ->
            if (a.referencia && ! b.referencia)
                return -1;
            if (b.referencia && ! a.referencia)
                return 1;
            Date aDataNascimento = a.dataNascimento ?: a.dataNascimentoAproximada
            Date bDataNascimento = b.dataNascimento ?: b.dataNascimentoAproximada
            if (aDataNascimento && ! bDataNascimento)
                return -1;
            if (bDataNascimento && ! aDataNascimento)
                return 1;
            if ((! bDataNascimento && ! aDataNascimento) || aDataNascimento.equals(bDataNascimento)) {
                if (a.nomeCompleto && b.nomeCompleto)
                    return a.nomeCompleto.compareTo(b.nomeCompleto)
                else
                    return 0;
            }
            return aDataNascimento.compareTo(bDataNascimento)
        };
    }

    public boolean alteradoAposImportacao() {
        return dataUltimaImportacao == null || ! ApoiaSuasDateUtils.momentosProximos(dataUltimaImportacao, lastUpdated)
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
        return vulnerabilidades.findAll{ it.habilitado && it.marcador.habilitado }.sort { it.marcador?.descricao?.toLowerCase() }
    }
    public Set<AcaoFamilia> getAcoesHabilitadas() {
        //retorna apenas as habilitadas e em ordem alfabetica
        return acoes.findAll{ it.habilitado }.sort { it.marcador?.descricao?.toLowerCase() }
    }
    public Set<ProgramaFamilia> getProgramasHabilitados() {
        //retorna apenas as habilitadas e em ordem alfabetica
        return programas.findAll{ it.habilitado && it.marcador.habilitado }.sort { it.marcador?.descricao?.toLowerCase() }
    }
    public Set<OutroMarcadorFamilia> getOutrosMarcadoresHabilitados() {
        //retorna apenas as habilitadas e em ordem alfabetica
        return outrosMarcadores.findAll{ it.habilitado }.sort { it.marcador?.descricao?.toLowerCase() }
    }
    public Set<AssociacaoMarcador> getTodosOsMarcadores() {
        return (programas ?: [])
                + (acoes ?: [])
                + (vulnerabilidades ?: [])
                + (outrosMarcadores ?: []);
    }

    @Override
    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
        DetalheService.parseDetalhes(this, detalhes)
    }

}
