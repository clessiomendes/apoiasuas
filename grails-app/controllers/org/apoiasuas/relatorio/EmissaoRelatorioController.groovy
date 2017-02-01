package org.apoiasuas.relatorio

import grails.plugin.springsecurity.annotation.Secured
import groovy.sql.GroovyRowResult
import org.apoiasuas.AncestralController
import org.apoiasuas.cidadao.AcoesCommand
import org.apoiasuas.cidadao.Marcador
import org.apoiasuas.cidadao.MarcadorService
import org.apoiasuas.cidadao.MarcadoresCommand
import org.apoiasuas.cidadao.ProgramasCommand
import org.apoiasuas.cidadao.VulnerabilidadesCommand
import org.apoiasuas.marcador.Acao
import org.apoiasuas.marcador.Programa
import org.apoiasuas.marcador.Vulnerabilidade
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.UsuarioSistema
import org.springframework.security.access.annotation.Secured

class EmissaoRelatorioController extends AncestralController {

    RelatorioService relatorioService;
    MarcadorService marcadorService;

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def definirListagem() {
        render view: 'definirListagem', model: getModel()
    }

    private Map<String, Object> getModel(DefinicaoListagemCommand definicao = null) {
        Map result = [
                programasDisponiveis       : marcadorService.programasDisponiveis,
                vulnerabilidadesDisponiveis: marcadorService.vulnerabilidadesDisponiveis,
                acoesDisponiveis           : marcadorService.acoesDisponiveis,
                operadores                 : getOperadores()
        ];
        if (definicao)
            result << [definicaoListagem: definicao]
        return result;
    }

    private LinkedHashMap<Long, String> getOperadores() {
        Map<Long, String> operadoresOrdenados = [:]
        operadoresOrdenados.put(UsuarioSistema.SELECAO_ALGUM_TECNICO, "-algum técnico-");
        operadoresOrdenados.put(UsuarioSistema.SELECAO_NENHUM_TECNICO, "-nenhum técnico-");
        getOperadoresOrdenadosController(false).each {
            operadoresOrdenados.put(it.id, it.username)
        }
        return operadoresOrdenados
    }

    /**
     * Exibe a listagem na tela, em uma tabela HTML
     */
    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def exibeListagem(DefinicaoListagemCommand definicao, ProgramasCommand programasCommand,
                      VulnerabilidadesCommand vulnerabilidadesCommand, AcoesCommand acoesCommand) {
        List<GroovyRowResult> registrosEncontrados = listagem(definicao, programasCommand, vulnerabilidadesCommand, acoesCommand);
        response.contentType = 'text/html; charset=UTF-8'
        relatorioService.geraListagemFinal(response.outputStream, false, registrosEncontrados);
    }

    /**
     * Baixa a listagem como um arquivo CSV, que pode ser aberto em excel
     */
    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def downloadListagem(DefinicaoListagemCommand definicao, ProgramasCommand programasCommand,
                         VulnerabilidadesCommand vulnerabilidadesCommand, AcoesCommand acoesCommand) {
//        definicao.planilhaParaDownload = true
//        return listagem(definicao, programasCommand, vulnerabilidadesCommand, acoesCommand)
        List<GroovyRowResult> registrosEncontrados = listagem(definicao, programasCommand, vulnerabilidadesCommand, acoesCommand);
        response.contentType = 'application/octet-stream'
        response.setHeader 'Content-disposition', "attachment; filename=\"listagem-apoiasuas.csv\""
        relatorioService.geraListagemFinal(response.outputStream, true, registrosEncontrados);
    }

    private List<Marcador> filtraSelecionados(MarcadoresCommand marcadores, Class<Marcador> classeMarcador) {
        return marcadores.marcadoresDisponiveis.collect { marcador ->
            if (marcador.selected)
                classeMarcador.get(marcador.id)
        }.grep() //limpa nulos
    }

    private List<GroovyRowResult> listagem(DefinicaoListagemCommand definicao, ProgramasCommand programasCommand,
                     VulnerabilidadesCommand vulnerabilidadesCommand, AcoesCommand acoesCommand) {
        if(! definicao.validate())
            return render(view: 'definirListagem', model: getModel(definicao))

        log.debug("Listar membros? ${definicao.membros}")
        log.debug("Tecnico de referencia: ${definicao.tecnicoReferencia}");
        org.joda.time.LocalDate dataNascimentoInicial, dataNascimentoFinal;
        if (definicao.idadeFinal)
            dataNascimentoInicial = new org.joda.time.LocalDate().minusYears(definicao.idadeFinal+1)
        if (definicao.idadeInicial)
            dataNascimentoFinal = new org.joda.time.LocalDate().minusYears(definicao.idadeInicial)
        log.debug("Data de nascimento entre ${dataNascimentoInicial} e ${dataNascimentoFinal}")

        //Converte e filtra apenas os marcadores selecionados
        List<Programa> programasSelecionados = filtraSelecionados(programasCommand, Programa.class);
        List<Vulnerabilidade> vulnerabilidadesSelecionadas = filtraSelecionados(vulnerabilidadesCommand, Vulnerabilidade.class);
        List<Acao> acoesSelecionadas = filtraSelecionados(acoesCommand, Acao.class);

        return relatorioService.processaConsulta(dataNascimentoInicial, dataNascimentoFinal,
                definicao.membros, definicao.tecnicoReferencia, programasSelecionados, vulnerabilidadesSelecionadas, acoesSelecionadas);
    }
}

class DefinicaoListagemCommand implements Serializable {
//    boolean planilhaParaDownload
    String membros
    Long tecnicoReferencia
    Integer idadeInicial
    Integer idadeFinal
    static constraints = {
        idadeInicial(nullable: true, range: 0..200)
        idadeFinal(nullable: true, range: 0..200)
        membros(nullable: true)
        tecnicoReferencia(nullable: true)
    }
}
