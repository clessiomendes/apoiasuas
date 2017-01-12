package org.apoiasuas.cidadao

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.marcador.Acao
import org.apoiasuas.marcador.Vulnerabilidade
import org.apoiasuas.processo.PedidoCertidaoProcessoDTO
import org.apoiasuas.processo.PedidoCertidaoProcessoService
import org.apoiasuas.programa.Programa
import org.apoiasuas.seguranca.DefinicaoPapeis

import javax.servlet.http.HttpSession

@Secured([DefinicaoPapeis.STR_USUARIO])
class FamiliaController extends AncestralController {

    public static final String HIDDEN_NOVAS_ACOES = "hiddenNovasAcoes"
    public static final String HIDDEN_NOVAS_VULNERABILIDADES = "hiddenNovasVulnerabilidades"
    public static final String HIDDEN_NOVOS_PROGRAMAS = "hiddenNovosProgramas"
    def beforeInterceptor = [action: this.&interceptaSeguranca, entity:Familia.class, only: ['show','edit', 'delete', 'update', 'save']]
    private static final String SESSION_ULTIMA_FAMILIA = "SESSION_ULTIMA_FAMILIA"
    private static final String SESSION_NOTIFICACAO_FAMILIA = "SESSION_NOTIFICACAO_FAMILIA"
    private static final String SESSION_NOTIFICACAO_FAMILIA_NUMERO_EXIBICOES = "SESSION_NOTIFICACAO_FAMILIA_NUMERO_EXIBICOES"
    MarcadorService marcadorService;
    PedidoCertidaoProcessoService pedidoCertidaoProcessoService;


    def index(Integer max) {
        redirect(controller: 'cidadao', action: 'procurarCidadao')
//        render view: 'list', model: [familiaInstanceList: Familia.list(params), familiaInstanceCount: Familia.count()]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def show(Familia familiaInstance) {

        //FIXME: apenas para testes
        marcadorService.init();

        if (! familiaInstance)
            return notFound()

        //Garante que as coleções sejam exibidas em uma ordem especifica
        familiaInstance.membros = familiaInstance.membros.sort { it.id }
        familiaInstance.programas = familiaInstance.programas.sort { it.programa.nome }
        familiaInstance.acoes = familiaInstance.acoes.sort { it.acao.descricao }

        List<PedidoCertidaoProcessoDTO> pedidosCertidaoPendentes = pedidoCertidaoProcessoService.pedidosCertidaoPendentes(familiaInstance.id)

        guardaUltimaFamiliaSelecionada(familiaInstance)
        render view: 'show', model: [familiaInstance: familiaInstance, pedidosCertidaoPendentes: pedidosCertidaoPendentes]
    }

/*
    def create() {
        respond new Familia(params)
    }
*/

    def save(Familia familiaInstance, ProgramasCommand programasCommand, AcoesCommand acoesCommand, VulnerabilidadesCommand vulnerabilidadesCommand) {
        if (! familiaInstance)
            return notFound()

        boolean modoCriacao = familiaInstance.id == null;
        List<String> novasAcoes = request.getParameterValues(HIDDEN_NOVAS_ACOES);
        List<String> novasVulnerabilidades = request.getParameterValues(HIDDEN_NOVAS_VULNERABILIDADES);
        List<String> novosProgramas = request.getParameterValues(HIDDEN_NOVOS_PROGRAMAS);

        //Grava
        if (! familiaService.grava(familiaInstance, programasCommand, novosProgramas,
                acoesCommand, novasAcoes, vulnerabilidadesCommand, novasVulnerabilidades)) {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: getEditCreateModel(familiaInstance))
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'familia.label', default: 'Família'), familiaInstance.id])
        return show(familiaInstance)
    }

    private Map getEditCreateModel(Familia familiaInstance) {
        List<Programa> programasDisponiveis = marcadoresDisponiveis(familiaInstance.programas, marcadorService.getProgramasDisponiveis() )
        List<Acao> acoesDisponiveis = marcadoresDisponiveis(familiaInstance.acoes, marcadorService.getAcoesDisponiveis() )
        List<Vulnerabilidade> vulnerabilidadesDisponiveis = marcadoresDisponiveis(familiaInstance.vulnerabilidades, marcadorService.getVulnerabilidadesDisponiveis() )
        return [familiaInstance: familiaInstance, operadores: getOperadoresOrdenadosController(true),
                programasDisponiveis: programasDisponiveis, acoesDisponiveis: acoesDisponiveis, vulnerabilidadesDisponiveis: vulnerabilidadesDisponiveis]
    }

    /**
     * Marca dentre os programas/acoes/etc disponiveis, aqueles que estão atualmente associados à família
     */
    private List<Marcador> marcadoresDisponiveis(Set<AssociacaoMarcador> marcadoresSelecionados, List<Marcador> marcadoresDisponiveis) {
        marcadoresDisponiveis.each { Marcador marcadorDisponivel ->
            marcadorDisponivel.selected = marcadoresSelecionados.find { it.marcador == marcadorDisponivel }
        }
        marcadoresDisponiveis.sort { Marcador p1, Marcador p2 ->
            if (p1.selected && ! p2.selected)
                return -1;
            if (p2.selected && ! p1.selected)
                return 1;
            return p1.descricao.compareToIgnoreCase(p2.descricao)
        }
        return marcadoresDisponiveis
    }

    def edit(Familia familiaInstance) {
        render view: 'edit', model: getEditCreateModel(familiaInstance)
    }

/*
    def delete(Familia familiaInstance) {
        if (! familiaInstance)
            return notFound()

        familiaService.apaga(familiaInstance)
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'Familia.label', default: 'Família'), familiaInstance.id])
        redirect action:"index"

        if (familiaInstance == null) {
            notFound()
            return
        }
    }
*/

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'Familia.label', default: 'Família'), params.id])
        return redirect(controller: 'cidadao', action: 'procurarCidadao')
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def obtemLogradouros(String term) {
        if (term)
            render familiaService.procurarLogradouros(term) as JSON
        else {
            response.status = 500
            return render ([errorMessage: "parametro vazio"] as JSON)
        }
    }

    public static Long getNumeroExibicoesNotificacao(HttpSession session) {
        return session[SESSION_NOTIFICACAO_FAMILIA_NUMERO_EXIBICOES]
    }

    public static String getNotificacao(HttpSession session) {
        return session[SESSION_NOTIFICACAO_FAMILIA]
    }

    public static Familia getUltimaFamilia(HttpSession session) {
        Long numeroExibicoes = getNumeroExibicoesNotificacao(session) ?: 0L;
        numeroExibicoes++
        session[SESSION_NOTIFICACAO_FAMILIA_NUMERO_EXIBICOES] = numeroExibicoes;
        return session[SESSION_ULTIMA_FAMILIA]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def limparNotificacoes() {
        session[SESSION_NOTIFICACAO_FAMILIA] = null;
        render 200;
    }

}

class ProgramasCommand implements MarcadoresCommand {
    List<MarcadorCommand> programasDisponiveis = [].withLazyDefault { new MarcadorCommand() }
    List<MarcadorCommand> getMarcadoresDisponiveis() { programasDisponiveis }
}

class AcoesCommand implements MarcadoresCommand {
    List<MarcadorCommand> acoesDisponiveis = [].withLazyDefault { new MarcadorCommand() }
    List<MarcadorCommand> getMarcadoresDisponiveis() { acoesDisponiveis }
}

class VulnerabilidadesCommand implements MarcadoresCommand {
    List<MarcadorCommand> vulnerabilidadesDisponiveis = [].withLazyDefault { new MarcadorCommand() }
    List<MarcadorCommand> getMarcadoresDisponiveis() { vulnerabilidadesDisponiveis }
}

class MarcadorCommand {
    String id
    String selected
//    String historico
}
