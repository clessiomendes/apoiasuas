package org.apoiasuas.relatorio

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.programa.Programa
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.UsuarioSistema
import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.Period

@Secured([DefinicaoPapeis.USUARIO_LEITURA])
class EmissaoRelatorioController extends AncestralController {

    def segurancaService
    def relatorioService

    def definirListagem() {
        render view: 'definirListagem', model: [programasDisponiveis: Programa.all.sort { it.nome }, operadores: getOperadores()]
    }

    private LinkedHashMap<Long, String> getOperadores() {
        Map<Long, String> operadoresOrdenados = [:]
        operadoresOrdenados.put(UsuarioSistema.SELECAO_ALGUM_TECNICO, "-algum técnico-");
        operadoresOrdenados.put(UsuarioSistema.SELECAO_NENHUM_TECNICO, "-nenhum técnico-");
        segurancaService.getOperadoresOrdenados().each {
            operadoresOrdenados.put(it.id, it.username)
        }
        return operadoresOrdenados
    }

    def downloadListagem(DefinicaoListagemCommand definicao) {
        if(! definicao.validate())
            return render(view: 'definirListagem', model: [definicaoListagem: definicao, programasDisponiveis: Programa.all.sort { it.nome }, operadores: getOperadores()])

        log.debug("Listar membros? ${definicao.membros}")
        log.debug("Tecnico de referencia: ${definicao.tecnicoReferencia}");
        org.joda.time.LocalDate dataNascimentoInicial, dataNascimentoFinal;
        if (definicao.idadeFinal)
            dataNascimentoInicial = new org.joda.time.LocalDate().minusYears(definicao.idadeFinal+1)
        if (definicao.idadeInicial)
            dataNascimentoFinal = new org.joda.time.LocalDate().minusYears(definicao.idadeInicial)
        log.debug("Data de nascimento entre ${dataNascimentoInicial} e ${dataNascimentoFinal}")
        //Converte e filtra apenas os programas selecionados
        List<Programa> programasSelecionados = definicao.programasdisponiveis.collect { cmd ->
            if (cmd.selected)
                Programa.get(cmd.id)
        }.grep() //limpa nulos
        log.debug("Programas: ${programasSelecionados}");

        response.contentType = 'application/octet-stream'
        response.setHeader 'Content-disposition', "attachment; filename=\"listagem-apoiasuas.csv\""

        relatorioService.geraListagem(response.outputStream, dataNascimentoInicial, dataNascimentoFinal, definicao.membros, definicao.tecnicoReferencia, programasSelecionados)
    }
}

class DefinicaoListagemCommand implements Serializable {
    String membros
    Long tecnicoReferencia
    Integer idadeInicial
    Integer idadeFinal
    List<ProgramaCommand> programasdisponiveis = [].withLazyDefault { new ProgramaCommand() }
    static constraints = {
        idadeInicial(nullable: true, range: 0..200)
        idadeFinal(nullable: true, range: 0..200)
        membros(nullable: true)
        tecnicoReferencia(nullable: true)
    }
}

class ProgramaCommand {
    String id
    String selected
}
