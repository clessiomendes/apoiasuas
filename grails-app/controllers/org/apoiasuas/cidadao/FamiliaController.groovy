package org.apoiasuas.cidadao

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.marcador.Acao
import org.apoiasuas.marcador.Vulnerabilidade
import org.apoiasuas.processo.PedidoCertidaoProcessoDTO
import org.apoiasuas.processo.PedidoCertidaoProcessoService
import org.apoiasuas.marcador.Programa
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.util.StringUtils

import javax.servlet.http.HttpSession

class FamiliaController extends AncestralController {

    public static final String HIDDEN_NOVAS_ACOES = "hiddenNovasAcoes"
    public static final String HIDDEN_NOVAS_VULNERABILIDADES = "hiddenNovasVulnerabilidades"
    public static final String HIDDEN_NOVOS_PROGRAMAS = "hiddenNovosProgramas"
    def beforeInterceptor = [action: this.&interceptaSeguranca, entity:Familia.class, only: ['show','edit', 'delete', 'update', 'save']]
    private static final String SESSION_ULTIMA_FAMILIA = "SESSION_ULTIMA_FAMILIA"
    private static final String SESSION_NOTIFICACAO_FAMILIA = "SESSION_NOTIFICACAO_FAMILIA"
    private static final String SESSION_NOTIFICACAO_FAMILIA_NUMERO_EXIBICOES = "SESSION_NOTIFICACAO_FAMILIA_NUMERO_EXIBICOES"
    //destinos de navegação usados na seleção de famílias para o caso de uso de acompanhamento familiar
    public static final Map modeloSelecionarAcompanhamento = [
            controllerButtonProcurar: "familia",
            actionButtonProcurar: "selecionarAcompanhamentoExecuta",
            controllerLinkFamilia: "familia",
            actionLinkFamilia: "editAcompanhamentoFamilia",
            controllerLinkCidadao: "familia",
            actionLinkCidadao: "editAcompanhamentoCidadao"
    ]

    MarcadorService marcadorService;
    PedidoCertidaoProcessoService pedidoCertidaoProcessoService;
    CidadaoService cidadaoService;

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
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

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def save(Familia familiaInstance, ProgramasCommand programasCommand, AcoesCommand acoesCommand, VulnerabilidadesCommand vulnerabilidadesCommand) {
        if (! familiaInstance)
            return notFound()

        boolean modoCriacao = familiaInstance.id == null;
        List<String> novasAcoes = request.getParameterValues(HIDDEN_NOVAS_ACOES);
        List<String> novasVulnerabilidades = request.getParameterValues(HIDDEN_NOVAS_VULNERABILIDADES);
        List<String> novosProgramas = request.getParameterValues(HIDDEN_NOVOS_PROGRAMAS);

        //Grava
        if (familiaInstance.validate()) {
            familiaService.grava(familiaInstance, programasCommand, novosProgramas,
                    acoesCommand, novasAcoes, vulnerabilidadesCommand, novasVulnerabilidades)
        } else {
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

    private Map getEditCreateModelMonitoramento(Monitoramento monitoramentoInstance) {
        return [monitoramentoInstance: monitoramentoInstance, operadores: getOperadoresOrdenadosController(true)];
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

    @Secured([DefinicaoPapeis.STR_USUARIO])
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

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    public static Long getNumeroExibicoesNotificacao(HttpSession session) {
        return session[SESSION_NOTIFICACAO_FAMILIA_NUMERO_EXIBICOES]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    public static String getNotificacao(HttpSession session) {
        return session[SESSION_NOTIFICACAO_FAMILIA]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
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

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def listMonitoramento(Long id) {
        List monitoramentos = Familia.get(id).monitoramentos.sort{ it.dataCriacao }
        render view: 'listMonitoramento', model: [monitoramentoInstanceList: monitoramentos]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def showMonitoramento(Long id) {
        render view: 'showMonitoramento', model: [monitoramentoInstance: Monitoramento.get(id)];
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def editMonitoramento(Long id) {
        render view: 'editMonitoramento', model: getEditCreateModelMonitoramento(Monitoramento.get(id));
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def createMonitoramento(Long idFamilia) {
        Monitoramento novoMonitoramento = new Monitoramento();
        Familia familia = Familia.get(idFamilia);
        if (! familia)
            throw new RuntimeException("Impossível acessar familia. id "+idFamilia);
        if (familia.tecnicoReferencia)
            novoMonitoramento.responsavel = familia.tecnicoReferencia;
        novoMonitoramento.familia = familia;
        novoMonitoramento.dataCriacao = new Date();
        render view: 'createMonitoramento', model: getEditCreateModelMonitoramento(novoMonitoramento);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveMonitoramento(Monitoramento monitoramentoInstance) {
        if (! monitoramentoInstance)
            return notFound()

        boolean modoCriacao = monitoramentoInstance.id == null;
        if (monitoramentoInstance.validate()) {
            familiaService.gravaMonitoramento(monitoramentoInstance);
            flash.message = "monitoramento gravado com sucesso"
            //retornando mensagem de sucesso sem exibir nada na tela (a janela modal sera simplemente fechada)
            return render(contentType:'text/json', text: ['success': true] as JSON);
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(status: 503 /*apontar que houve erro*/,
                    view: modoCriacao ? "createMonitoramento" : "editMonitoramento",
                    model: getEditCreateModelMonitoramento(monitoramentoInstance));
        }
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def deleteMonitoramento(Monitoramento monitoramento) {
        if (! monitoramento)
            return notFound()

//        throw new RuntimeException("Erro aqui");
        familiaService.apagaMonitoramento(monitoramento)
//        flash.message = message(code: 'default.deleted.message', args: [message(code: 'servico.label', default: 'Servico'), servicoInstance.apelido])
        return render(contentType:'text/json', text: ['success': true] as JSON);
    }

/*
    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def emitirPlanoAcompanhamento(Familia familiaInstance) {
        ReportDTO reportDTO = familiaService.emitePlanoAcompanhamento(familiaInstance);

        //Montando a saída (somente após passar pelo processamento completo na camada de serviço)
        response.contentType = 'application/octet-stream'
        response.setHeader 'Content-disposition', "attachment; filename=\"${reportDTO.nomeArquivo}\"";
        reportDTO.report.process(reportDTO.context, response.outputStream);
        response.outputStream.flush()
    }
*/

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def editAcompanhamentoFamilia(Familia familiaInstance) {
        guardaUltimaFamiliaSelecionada(familiaInstance)
        render view: "editAcompanhamento", model: modeloSelecionarAcompanhamento +
                getEditCreateModel(familiaInstance)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def editAcompanhamentoCidadao(Cidadao cidadaoInstance) {
        return redirect(action: "editAcompanhamentoFamilia", id: cidadaoInstance.familia.id);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def gravaAcompanhamento(Familia familiaInstance, ProgramasCommand programasCommand, AcoesCommand acoesCommand, VulnerabilidadesCommand vulnerabilidadesCommand) {
        if (! familiaInstance)
            return notFound()

        List<String> novasAcoes = request.getParameterValues(HIDDEN_NOVAS_ACOES);
        List<String> novasVulnerabilidades = request.getParameterValues(HIDDEN_NOVAS_VULNERABILIDADES);
        List<String> novosProgramas = request.getParameterValues(HIDDEN_NOVOS_PROGRAMAS);

        //Grava
        if (familiaInstance.validate()) {
            flash.message = "As informações da família e do acompanhamento foram atualizados"
            familiaInstance = familiaService.grava(familiaInstance, programasCommand, novosProgramas,
                    acoesCommand, novasAcoes, vulnerabilidadesCommand, novasVulnerabilidades)
        }

        //Guarda na sessao asinformacoes necessarias para a geracao do arquivo a ser baixado (que sera baixado por um
        //javascript que rodara automaticamente na proxima pagina)
        setReportParaBaixar(session, familiaService.emitePlanoAcompanhamento(familiaInstance))
        return editAcompanhamentoFamilia(familiaInstance)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def selecionarAcompanhamento() {
        Map modelo = FamiliaController.modeloSelecionarAcompanhamento + [cidadaoInstanceList: [], cidadaoInstanceCount: 0, filtro: [:]];
        if (FamiliaController.getUltimaFamilia(session))
            modelo << [defaultNomePesquisa: FamiliaController.getUltimaFamilia(session).codigoLegado];
        render(view: "/cidadao/procurarCidadao", model: modelo )
    }


    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def selecionarAcompanhamentoExecuta(FiltroCidadaoCommand filtro) {
        //Preenchimento de numeros no primeiro campo de busca indica pesquisa por codigo legado
        boolean buscaPorCodigoLegado = filtro.nomeOuCodigoLegado && ! StringUtils.PATTERN_TEM_LETRAS.matcher(filtro.nomeOuCodigoLegado)
        params.max = params.max ?: 20
        PagedResultList cidadaos = cidadaoService.procurarCidadao(params, filtro)
        Map filtrosUsados = params.findAll { it.value }

        if (buscaPorCodigoLegado && cidadaos?.resultList?.size() > 0) {
            Cidadao cidadao = cidadaos?.resultList[0]
            redirect(controller: "familia", action: "editAcompanhamentoFamilia", id: cidadao.familia.id)
        } else {
            Map modelo = FamiliaController.modeloSelecionarAcompanhamento + [cidadaoInstanceList: cidadaos, cidadaoInstanceCount: cidadaos.getTotalCount(), filtro: filtrosUsados];
            render(view:"/cidadao/procurarCidadao", model: modelo)
        }
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
