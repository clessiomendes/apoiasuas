package org.apoiasuas.cidadao

import grails.transaction.NotTransactional
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apoiasuas.marcador.Acao
import org.apoiasuas.marcador.AssociacaoMarcador
import org.apoiasuas.marcador.Marcador
import org.apoiasuas.marcador.OutroMarcador
import org.apoiasuas.marcador.Vulnerabilidade
import org.apoiasuas.marcador.Programa
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.Papel
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.seguranca.UsuarioSistemaPapel
import org.apoiasuas.util.ambienteExecucao.AmbienteExecucao
import org.apoiasuas.util.ApoiaSuasException
import org.springframework.transaction.annotation.Propagation

@Transactional(readOnly = true)
class MarcadorService {

    public static final int MAX_FAMILIAS_REFERENCIADAS = 200
    def segurancaService;
    def groovySql;
    def sessionFactory;

    /**
     * Usado no caso de uso de Gestao Tecnica, para diferenciar o filtro de seleção de tecnico de referencia. Para
     * programas, usa-se a referencia familiar (FAMILIA) na filtragem. Em vulnerabilidades, acoes e outros marcadores,
     * usa-se o tecnico que criou o marcador (MARCADOR)
     */
    public static enum DestinoFiltroTecnico { FAMILIA, MARCADOR }


    /**
     * Busca os técnicos no sistema de segurança mas inclui eventuais tecnicos desabilitados e nao tecnicos presentes
     * nos marcadores da familia ou como referencia familiar. Usado para montar as telas de edição de marcadores e de
     * familia, nos casos em que já exisitir uma familia cuja referencia, ou um marcador cujo técnico, foram desabilitados.
     */
    public List<UsuarioSistema> getTecnicosIncluiMarcadores(Familia familia) {
        List<UsuarioSistema> tecnicosSelecionados = familia.todosOsMarcadores.collect { it.tecnico };
        Papel papelTecnico = Papel.findByAuthority(DefinicaoPapeis.STR_TECNICO);
        ArrayList<UsuarioSistema> result = segurancaService.getOperadoresOrdenados(true, [familia.tecnicoReferencia]).findAll { operador ->
            //sempre mostrar os operadores que ja foram selecionados em algum marcador
            (operador in tecnicosSelecionados
            ||
            //sempre mostrar o operador selecionado como referencia familia
            operador.id == familia.tecnicoReferencia?.id
            ||
            //ou se o operador tiver o papel de tecnico
            UsuarioSistemaPapel.countByUsuarioSistemaAndPapel(operador, papelTecnico) > 0)
        }
        return result;
    }

    /**
     * Método genérico usado para gravar alterações nas coleções de programas, ações, etc
     */
    @Transactional
    public void gravaMarcadoresFamilia(MarcadoresCommand marcadoresCommand, Set<AssociacaoMarcador> marcadores, Familia familia,
                                 Class<Marcador> classeMarcador, Class<AssociacaoMarcador> classeAssociacaoMarcador/*, List<String> novosMarcadores*/) {

        Set<AssociacaoMarcador> novosMarcadores = []

        marcadoresCommand?.marcadoresDisponiveis?.each { MarcadorCommand cmd ->
            log.debug(cmd);
            //Verifica se já existe uma associacao para este marcador e a recupera
            AssociacaoMarcador associacaoMarcador = marcadores.find { it.marcador.id.toString() == cmd.id }
            //Se ainda não existir a associacao, cria
            if (cmd.habilitado && ! associacaoMarcador) {
                associacaoMarcador = classeAssociacaoMarcador.getConstructor().newInstance();
                novosMarcadores << associacaoMarcador;
                associacaoMarcador.familia = familia;
                //noinspection GrUnresolvedAccess
                associacaoMarcador.marcador = classeMarcador.get(cmd.id)
                associacaoMarcador.data = new Date();
            }
            //atualiza as informacoes na associacao
            if (associacaoMarcador) {
                associacaoMarcador.habilitado = cmd.habilitado ? true : false //se nulo, false
                associacaoMarcador.observacao = cmd.observacao
                associacaoMarcador.tecnico = UsuarioSistema.get(cmd.tecnico);
                //noinspection GrUnresolvedAccess
                associacaoMarcador.save();
            }
        }

        //Somente após percorrer toda a lista de marcadores atualmente associados à familia, devemos manipula-la a fim
        //de adicionar os novos marcadores criados
        novosMarcadores.each {
            marcadores << it
        }
    }

    /**
     * Verifica eventual duplicidade de marcador
     */
    public void validaNovoMarcador(Marcador novoMarcador) {
        Class classeMarcador = novoMarcador.class;
        boolean duplicada = false;
        //verifica se essa descricao ja existe nos marcadores padrao (disponiveis para todos os servicoSistema)
        //noinspection GrUnresolvedAccess
        duplicada = duplicada || classeMarcador.findByDescricaoIlikeAndServicoSistemaSegurancaIsNull(novoMarcador.descricao);
        //noinspection GrUnresolvedAccess
        duplicada = false || duplicada || classeMarcador.findByDescricaoIlikeAndServicoSistemaSeguranca(novoMarcador.descricao, novoMarcador.servicoSistemaSeguranca);
        if (duplicada)
            throw new ApoiaSuasException("Erro. Descrição repetida para ${classeMarcador.simpleName} : "+novoMarcador.descricao);
    }

    /**
     * Ver getMarcadoresDisponiveis()
     */
    private List<Marcador> getMarcadoresDisponiveis(Class<Marcador> classeMarcador) {
        //noinspection GrUnresolvedAccess
        return classeMarcador.findAllByServicoSistemaSegurancaIsNullOrServicoSistemaSeguranca(segurancaService.servicoLogado)
                .findAll { it.habilitado }
                .sort { it.descricao?.toLowerCase() };
    }

    /**
     * Ver getMarcadoresDisponiveis()
     */
    public List<Marcador> getProgramasDisponiveis() {
        return getMarcadoresDisponiveis(Programa.class)
    }

    /**
     * Ver getMarcadoresDisponiveis()
     */
    public List<Marcador> getOutrosMarcadoresDisponiveis() {
        return getMarcadoresDisponiveis(OutroMarcador.class)
    }

    /**
     * Ver getMarcadoresDisponiveis()
     */
    public List<Marcador> getAcoesDisponiveis() {
        return getMarcadoresDisponiveis(Acao.class)
    }

    /**
     * Ver getMarcadoresDisponiveis()
     */
    public List<Marcador> getVulnerabilidadesDisponiveis() {
        return getMarcadoresDisponiveis(Vulnerabilidade.class)
    }

    @Transactional
    public Marcador gravaMarcador(Marcador marcador) {
        if (! marcador.id)
            validaNovoMarcador(marcador);
        //noinspection GrUnresolvedAccess
        return marcador.save();
    }

    /**
     * tabela em banco de dados para cada marcador específico
     */
    private String tabela(Class<Marcador> classeMarcador) {
        return sessionFactory.getClassMetadata(classeMarcador).tableName
/*
        if (classeMarcador instanceof Programa)
            return "programa"
        else if (classeMarcador instanceof Vulnerabilidade)
            return "vulnerabilidade"
        else if (classeMarcador instanceof Acao)
            return "acao"
        else if (classeMarcador instanceof OutroMarcador)
            return "outro_marcador";
        throw new ApoiaSuasException("definição de tabela não implementada para ${classeMarcador.name} em ${this.class.name}")
*/
    }

    private String id(Class<Marcador> classeMarcador) {
        return tabela(classeMarcador)+"_id";
    }

    private String assoc(Class<Marcador> classeMarcador) {
        return tabela(classeMarcador)+"_familia";
    }

    /**
     * Retorna quantidade de familias agrupadas por marcador
     * classeMarcador marcador especifico em que as quantidades serao agrupadas
     * idTecnico filtro opcional por tecnico
     */
    public Map<String, Long> qntFamiliasMarcadoresAgrupadas(Class<Marcador> classeMarcador, Long idTecnico, DestinoFiltroTecnico destinoFiltroTecnico) {
        def filtros = [:]
        String sql = "select marcador.${id(classeMarcador)} as idMarcador, count(DISTINCT f.id) as qntFamilias" +
                baseSqlFamiliasMarcadores(classeMarcador, filtros, idTecnico, destinoFiltroTecnico) +
                "\n group by marcador.${id(classeMarcador)}";
        log.debug("\n" + sql + "\n "+ filtros);
        List<GroovyRowResult> resultado = groovySql.rows(sql, filtros) ;
        Map<Programa, Long> result = [:];
        resultado.each {
            //noinspection GrUnresolvedAccess
            result.put(classeMarcador.get(it['idMarcador']), it['qntFamilias']);
        }
        return result.sort{
            it.key.descricao?.toLowerCase()
        };
    }

    /**
     * Retorna quantidade total de familias presentes em pelo menos um programa
     * idTecnico filtro opcional por tecnico
     */
    public Long totalFamiliasMarcadores(Class<Marcador> classeMarcador, Long idTecnico, DestinoFiltroTecnico destinoFiltroTecnico) {
        def filtros = [:]
        String sql = 'select count(DISTINCT f.id) as qntFamilias ' +
                baseSqlFamiliasMarcadores(classeMarcador, filtros, idTecnico, destinoFiltroTecnico);
        log.debug("\n" + sql + "\n "+ filtros);
        List<GroovyRowResult> resultado = groovySql.rows(sql, filtros) ;
        return resultado ? resultado[0]['qntFamilias'] : 0;
    }

    private String baseSqlFamiliasMarcadores(Class<Marcador> classeMarcador, Map filtros, Long idTecnico, DestinoFiltroTecnico destinoFiltroTecnico) {
        String result = "\n from ${assoc(classeMarcador)} marcador join familia f on f.id = marcador.familia_id" +
                "\n where f.servico_sistema_seguranca_id = :id_servico_seguranca";
        filtros << [id_servico_seguranca: segurancaService.servicoLogado.id];
        if (idTecnico) {
            if (destinoFiltroTecnico == DestinoFiltroTecnico.FAMILIA)
                result += "\n and f.tecnico_referencia_id = :id_tecnico";
            if (destinoFiltroTecnico == DestinoFiltroTecnico.MARCADOR)
                result += "\n and marcador.tecnico_id = :id_tecnico";
            filtros << [id_tecnico: idTecnico]
        }
        return result
    }

    /**
     * Retorna as referencias familiares presentes em pelo menos um programa
     * idPrograma filtro opcional por um programa especifico
     * idTecnico filtro opcional por um tecnico especifico
     */
    public List getReferenciasFamiliasMarcadores(Class<Marcador> classeMarcador, Long idMarcador, Long idTecnico,
                                                 DestinoFiltroTecnico destinoFiltroTecnico) {
        def filtros = [:]
        String sql = 'select distinct f.id as idFamilia '+baseSqlFamiliasMarcadores(classeMarcador, filtros, idTecnico, destinoFiltroTecnico);
        if (idMarcador) {
            sql += "\n and marcador.${id(classeMarcador)} = :idMarcador";
            filtros << [idMarcador: idMarcador]
        }
        List<Cidadao> result = []
        log.debug("\n" + sql + "\n "+ filtros);
        if (filtros.isEmpty())
            filtros = []
        groovySql.rows(sql, filtros, 0, MAX_FAMILIAS_REFERENCIADAS).each {
            Cidadao referencia = Familia.get(it['idFamilia']).getReferencia();
            if (referencia)
                result << referencia
        }

        //Usar para testar transacoes somente leitura
//        if (AmbienteExecucao.isDesenvolvimento())
//            result.each {
//                it.familia.endereco.numero = (new Integer(it.familia.endereco.numero) + 3).toString();
//            }

        return result.sort{ it.nomeCompleto?.toLowerCase() };
    }

}
