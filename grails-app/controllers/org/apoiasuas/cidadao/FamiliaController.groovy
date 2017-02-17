package org.apoiasuas.cidadao

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.marcador.Acao
import org.apoiasuas.marcador.AcaoFamilia
import org.apoiasuas.marcador.AssociacaoMarcador
import org.apoiasuas.marcador.Marcador
import org.apoiasuas.marcador.OutroMarcador
import org.apoiasuas.marcador.OutroMarcadorFamilia
import org.apoiasuas.marcador.ProgramaFamilia
import org.apoiasuas.marcador.Vulnerabilidade
import org.apoiasuas.marcador.VulnerabilidadeFamilia
import org.apoiasuas.processo.PedidoCertidaoProcessoDTO
import org.apoiasuas.marcador.Programa
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.util.StringUtils

import javax.servlet.http.HttpSession

class FamiliaController extends AncestralController {

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
/*
    public static final String HIDDEN_NOVAS_ACOES = "hiddenNovasAcoes"
    public static final String HIDDEN_NOVAS_VULNERABILIDADES = "hiddenNovasVulnerabilidades"
    public static final String HIDDEN_NOVOS_PROGRAMAS = "hiddenNovosProgramas"
*/

    def marcadorService;
    def pedidoCertidaoProcessoService;
    def cidadaoService;
    def monitoramentoService;

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def index(Integer max) {
        redirect(controller: 'cidadao', action: 'procurarCidadao')
//        render view: 'list', model: [familiaInstanceList: Familia.list(params), familiaInstanceCount: Familia.count()]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def show(Familia familiaInstance) {

        if (! familiaInstance)
            return notFound()

        //Garante que as coleções sejam exibidas em uma ordem especifica
        familiaInstance.membros = familiaInstance.membros.sort { it.id }
/*
        familiaInstance.programas = familiaInstance.programas.sort { it.programa.nome }
        familiaInstance.acoes = familiaInstance.acoes.sort { it.acao.descricao }
        familiaInstance.vulnerabilidades = familiaInstance.vulnerabilidades.sort { it.vulnerabilidade.descricao }
*/

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
    def save(Familia familiaInstance, ProgramasCommand programasCommand, AcoesCommand acoesCommand,
             VulnerabilidadesCommand vulnerabilidadesCommand, OutrosMarcadoresCommand outrosMarcadoresCommand) {
        if (! familiaInstance)
            return notFound()

        boolean modoCriacao = familiaInstance.id == null;
        log.debug(outrosMarcadoresCommand);

        //Grava
        if (familiaInstance.validate()) {
            familiaInstance = familiaService.grava(familiaInstance, programasCommand, acoesCommand,
                    vulnerabilidadesCommand, outrosMarcadoresCommand)
            flash.message = message(code: 'default.updated.message', args: [message(code: 'familia.label', default: 'Família'), familiaInstance.id])
            return show(familiaInstance)
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: getEditCreateModel(familiaInstance))
        }

    }

    private Map getEditCreateModel(Familia familiaInstance) {
        List<Programa> programasDisponiveis = marcadoresDisponiveis(familiaInstance.programas, marcadorService.getProgramasDisponiveis() )
        List<Acao> acoesDisponiveis = marcadoresDisponiveis(familiaInstance.acoes, marcadorService.getAcoesDisponiveis() )
        List<Vulnerabilidade> vulnerabilidadesDisponiveis = marcadoresDisponiveis(familiaInstance.vulnerabilidades, marcadorService.getVulnerabilidadesDisponiveis() )
        List<Vulnerabilidade> outrosMarcadoresDisponiveis = marcadoresDisponiveis(familiaInstance.outrosMarcadores, marcadorService.getOutrosMarcadoresDisponiveis() )
        return [familiaInstance: familiaInstance, operadores: getOperadoresOrdenadosController(true), outrosMarcadoresDisponiveis: outrosMarcadoresDisponiveis,
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
            marcadorDisponivel.habilitado = false;//assume como não selecionado inicialmente
            marcadorDisponivel.tecnico = segurancaService.usuarioLogado //utilizar usuario logado como opção default
            marcadoresSelecionados.each { marcadorSelecionado ->
                if (marcadorSelecionado.marcador == marcadorDisponivel) {
                    marcadorDisponivel.habilitado = marcadorSelecionado.habilitado;
                    marcadorDisponivel.observacao = marcadorSelecionado.observacao;
                    marcadorDisponivel.tecnico = marcadorSelecionado.tecnico;
                }
            }
//            marcadorDisponivel.selected = marcadoresSelecionados.find { it.marcador == marcadorDisponivel }
        }
        marcadoresDisponiveis.sort { Marcador p1, Marcador p2 ->
            if (p1.habilitado && ! p2.habilitado)
                return -1;
            if (p2.habilitado && ! p1.habilitado)
                return 1;
            return p1.descricao.compareToIgnoreCase(p2.descricao)
        }
        return marcadoresDisponiveis
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def edit(Familia familiaInstance) {
        render view: 'edit', model: getEditCreateModel(familiaInstance)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def editMarcadoresApenas(Familia familiaInstance) {
        render view: 'marcador/editMarcadoresApenas', model: getEditCreateModel(familiaInstance)
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
        List monitoramentos = Familia.get(id).monitoramentos.sort();
        render view: 'monitoramento/listMonitoramento', model: [monitoramentoInstanceList: monitoramentos]
    }

    protected Monitoramento buscaMonitoramento(Long id) {
        Monitoramento result = Monitoramento.get(id);
        if (result)
            return result;
        render(status: 500, text: "Erro: Monitoramento com id $id não encontrado");
        return null;
    }


    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def showMonitoramento(Long id) {
        Monitoramento monitoramento = buscaMonitoramento(id);
        if (monitoramento)
            render view: 'monitoramento/showMonitoramento', model: [monitoramentoInstance: monitoramento]

    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def editMonitoramento(Long id) {
        Monitoramento monitoramento = buscaMonitoramento(id);
        if (monitoramento)
            render view: 'monitoramento/editMonitoramento', model: getEditCreateModelMonitoramento(monitoramento);
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
        render view: 'monitoramento/createMonitoramento', model: getEditCreateModelMonitoramento(novoMonitoramento);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveMonitoramento(Monitoramento monitoramentoInstance) {
        if (! monitoramentoInstance)
            return buscaMonitoramento(-1);

        boolean modoCriacao = monitoramentoInstance.id == null;
        if (monitoramentoInstance.validate()) {
            monitoramentoService.gravaMonitoramento(monitoramentoInstance);
            flash.message = "Monitoramento gravado com sucesso"
            //retornando mensagem de sucesso sem exibir nada na tela (a janela modal sera simplemente fechada)
            return render(contentType:'text/json', text: ['success': true] as JSON);
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(status: 500 /*apontar que houve erro*/,
                    view: modoCriacao ? "monitoramento/createMonitoramento" : "monitoramento/editMonitoramento",
                    model: getEditCreateModelMonitoramento(monitoramentoInstance));
        }
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def efetivaMonitoramento(Monitoramento monitoramento) {
        if (! monitoramento)
            return buscaMonitoramento(-1);

        if (monitoramento.validate()) {
            monitoramentoService.efetivaMonitoramento(monitoramento)
            flash.message = "Ação monitorada efetivada"
            //retornando mensagem de sucesso sem exibir nada na tela (a janela modal sera simplemente fechada)
            return render(contentType:'text/json', text: ['success': true] as JSON);
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(status: 500 /*apontar que houve erro*/,
                    view: "monitoramento/showMonitoramento",
                    model: [monitoramentoInstance: monitoramento]);
        }
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def deleteMonitoramento(Monitoramento monitoramento) {
        if (! monitoramento)
            return buscaMonitoramento(-1);

        monitoramentoService.apagaMonitoramento(monitoramento)
        flash.message = "Monitoramento removido"
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
        render view: "acompanhamento/editAcompanhamento", model: modeloSelecionarAcompanhamento +
                getEditCreateModel(familiaInstance)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def editAcompanhamentoCidadao(Cidadao cidadaoInstance) {
        return redirect(action: "editAcompanhamentoFamilia", id: cidadaoInstance.familia.id);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveAcompanhamento(Familia familiaInstance, ProgramasCommand programasCommand, AcoesCommand acoesCommand, VulnerabilidadesCommand vulnerabilidadesCommand, OutrosMarcadoresCommand outrosMarcadoresCommand) {
        familiaInstance.acompanhamentoFamiliar.familia = familiaInstance;
        if (! familiaInstance)
            return notFound()

        //Valida antes de gravar
        if (familiaInstance.validate() && familiaInstance.acompanhamentoFamiliar.validate()) {
            flash.message = "As informações da família e do acompanhamento foram atualizados"
            familiaInstance = familiaService.grava(familiaInstance, programasCommand,
                    acoesCommand, vulnerabilidadesCommand, outrosMarcadoresCommand)
            //Guarda na sessao asinformacoes necessarias para a geracao do arquivo a ser baixado (que sera baixado por um
            //javascript que rodara automaticamente na proxima pagina)
            setReportParaBaixar(session, familiaService.emitePlanoAcompanhamento(familiaInstance))
        }

        //sempre retorna para a tela de edicao, quer a gravacao tenha sido feita com sucesso ou tenha erros de validacao
        render view: "acompanhamento/editAcompanhamento", model: modeloSelecionarAcompanhamento + getEditCreateModel(familiaInstance);
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

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def listProgramasDisponiveis(String id) {
        List<Marcador> marcadoresDisponiveis = marcadoresDisponiveis(Familia.get(id).programas, marcadorService.getProgramasDisponiveis() )
        return render(view:"marcador/_divMarcadoresDisponiveis", model: [marcadoresDisponiveis: marcadoresDisponiveis,
                                                                         label: 'programasDisponiveis',
                                                                         nomeDiv: 'divEditPrograma',
                                                                         classeMarcador: 'marcadores-programa'])
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def listAcoesDisponiveis(String id) {
        List<Marcador> marcadoresDisponiveis = marcadoresDisponiveis(Familia.get(id).acoes, marcadorService.getAcoesDisponiveis() )
        return render(view:"marcador/_divMarcadoresDisponiveis", model: [marcadoresDisponiveis: marcadoresDisponiveis,
                                                                         label: 'acoesDisponiveis',
                                                                         nomeDiv: 'divEditAcoes',
                                                                         classeMarcador: 'marcadores-acao'])
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def listVulnerabilidadesDisponiveis(String id) {
        List<Marcador> marcadoresDisponiveis = marcadoresDisponiveis(Familia.get(id).vulnerabilidades, marcadorService.getVulnerabilidadesDisponiveis() )
        return render(view:"marcador/_divMarcadoresDisponiveis", model: [marcadoresDisponiveis: marcadoresDisponiveis,
                                                                         label: 'vulnerabilidadesDisponiveis',
                                                                         nomeDiv: 'divEditVulnerabilidade',
                                                                         classeMarcador: 'marcadores-vulnerabilidade'])
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def listProgramasAcoes(String id) {
        List<Marcador> marcadoresDisponiveis = marcadoresDisponiveis(Familia.get(id).acoes, marcadorService.getAcoesDisponiveis() )
        return render(view:"marcador/_divMarcadoresDisponiveis", model: [marcadoresDisponiveis: marcadoresDisponiveis,
                                                                         label: 'acoesDisponiveis',
                                                                         nomeDiv: 'divEditAcao',
                                                                         classeMarcador: 'marcadores-acao'])
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def listOutrosMarcadoresDisponiveis(String id) {
        List<Marcador> marcadoresDisponiveis = marcadoresDisponiveis(Familia.get(id).outrosMarcadores, marcadorService.getOutrosMarcadoresDisponiveis() )
        return render(view:"marcador/_divMarcadoresDisponiveis", model: [marcadoresDisponiveis: marcadoresDisponiveis,
                                                                         label: 'outrosMarcadoresDisponiveis',
                                                                         nomeDiv: 'divEditOutroMarcador',
                                                                         classeMarcador: 'marcadores-outro-marcador'])
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def createPrograma(Long idFamiliaDestino) {
        return createMarcador(idFamiliaDestino, new Programa(), marcadorService.programasDisponiveis, "#fieldsetProgramas", "savePrograma", "Programa", 200/*sem erro*/);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def createAcao(Long idFamiliaDestino) {
        return createMarcador(idFamiliaDestino, new Acao(), marcadorService.acoesDisponiveis, "#fieldsetAcoes", "saveAcao", "Ação", 200/*sem erro*/);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def createVulnerabilidade(Long idFamiliaDestino) {
        return createMarcador(idFamiliaDestino, new Vulnerabilidade(), marcadorService.vulnerabilidadesDisponiveis, "#fieldsetVulnerabilidades", "saveVulnerabilidade", "Vulnerabilidade", 200/*sem erro*/);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def createOutroMarcador(Long idFamiliaDestino) {
        return createMarcador(idFamiliaDestino, new OutroMarcador(), marcadorService.outrosMarcadoresDisponiveis, "#fieldsetOutrosMarcadores", "saveOutroMarcador", "Sinalização", 200/*sem erro*/);
    }

    private def createMarcador(Long idFamiliaDestino, Marcador marcador, List<Marcador> marcadoresDisponiveis,
                               String fieldsetMarcador, String actionSaveMarcador, String tipoMarcador, Long errorCode) {
        Familia familia = Familia.get(idFamiliaDestino);
        if (! familia)
            throw new RuntimeException("Impossível acessar familia. id "+idFamiliaDestino);

        render status: errorCode, view: 'marcador/createMarcador', model: [familiaInstance: familia,
                marcadorInstance: marcador, marcadoresDisponiveis: marcadoresDisponiveis,
                fieldsetMarcador: fieldsetMarcador, actionSaveMarcador: actionSaveMarcador,
                tipoMarcador: tipoMarcador, operadores: getOperadoresOrdenadosController(true)];
        ;
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def savePrograma(Programa programaInstance, ProgramasCommand programasCommand, Long idFamilia) {
        Familia familia = Familia.get(idFamilia);
        if (! saveMarcador(programaInstance, programasCommand, familia, familia.programas, Programa.class, ProgramaFamilia.class))
        //exibe o formulario novamente em caso de problemas na validacao
            return createMarcador(idFamilia, programaInstance, marcadorService.programasDisponiveis, "#fieldsetProgramas", "savePrograma", "Programa", 500/*erro*/);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveOutroMarcador(OutroMarcador outroMarcadorInstance, OutrosMarcadoresCommand outrosMarcadoresCommand, Long idFamilia) {
        Familia familia = Familia.get(idFamilia);
        if (! saveMarcador(outroMarcadorInstance, outrosMarcadoresCommand, familia, familia.outrosMarcadores, OutroMarcador.class, OutroMarcadorFamilia.class))
        //exibe o formulario novamente em caso de problemas na validacao
            return createMarcador(idFamilia, outroMarcadorInstance, marcadorService.outrosMarcadoresDisponiveis, "#fieldsetOutrosMarcadores", "saveOutroMarcador", "Sinalização", 500/*erro*/);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveAcao(Acao acaoInstance, AcoesCommand acoesCommand, Long idFamilia) {
        Familia familia = Familia.get(idFamilia);
        if (! saveMarcador(acaoInstance, acoesCommand, familia, familia.acoes, Acao.class, AcaoFamilia.class))
        //exibe o formulario novamente em caso de problemas na validacao
            return createMarcador(idFamilia, acaoInstance, marcadorService.acoesDisponiveis, "#fieldsetAcoes", "saveAcao", "Ação", 500/*erro*/);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveVulnerabilidade(Vulnerabilidade vulnerabilidadeInstance, VulnerabilidadesCommand vulnerabilidadesCommand, Long idFamilia) {
        Familia familia = Familia.get(idFamilia);
        if (! saveMarcador(vulnerabilidadeInstance, vulnerabilidadesCommand, familia, familia.vulnerabilidades, Vulnerabilidade.class, VulnerabilidadeFamilia.class))
        //exibe o formulario novamente em caso de problemas na validacao
            return createMarcador(idFamilia, vulnerabilidadeInstance, marcadorService.vulnerabilidadesDisponiveis, "#fieldsetVulnerabilidade", "saveVulnerabilidade", "Vulnerabilidade", 500/*erro*/);
    }

    /**
     * Grava um novo marcador
     * Alternativamente, grava alteracoes nos marcadores associados a uma família se eles forem passados no request. Esta funcionalidade
     * é necessária no cdu de edição de famíla, pois existe a possibilidade de se criar um novo marcador aa partir da tela de edição e,
     * para que não sejam perdidas eventuais informações fornecidas pelo usuário, optou-se por ravar os marcadores correspondentes ao tipo
     * de marcador que se deseja incluir.
     */
    private def saveMarcador(Marcador marcadorInstance, MarcadoresCommand marcadoresCommand, Familia familia, Set<AssociacaoMarcador> marcadoresFamilia,
                             Class<Marcador> classeMarcador, Class<AssociacaoMarcador> classeAssociacaoMarcador) {
        //noinspection GrUnresolvedAccess
        if (marcadorInstance.validate()) {
            marcadorService.gravaMarcador(marcadorInstance);
            if (marcadoresCommand && marcadoresCommand.marcadoresDisponiveis && familia) {
                marcadorService.gravaMarcadoresFamilia(marcadoresCommand, marcadoresFamilia, familia, classeMarcador, classeAssociacaoMarcador);
            }
            flash.message = "marcador gravado com sucesso"
            //retornando mensagem de sucesso sem exibir nada na tela (a janela modal sera simplemente fechada)
            render(contentType:'text/json', text: ['success': true] as JSON);
            return true;
        } else {
            return false;
        }
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    public def showPrograma(ProgramaFamilia marcador) {
        if (marcador)
            render(view: "marcador/showMarcador", model: [marcador: marcador])
        else
            render(status: 500, text: "programa não encontrado");
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    public def showVulnerabilidade(VulnerabilidadeFamilia marcador) {
        if (marcador)
            render(view: "marcador/showMarcador", model: [marcador: marcador])
        else
            render(status: 500, text: "vulnerabilidade não encontrada");
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    public def showAcao(AcaoFamilia marcador) {
        if (marcador)
            render(view: "marcador/showMarcador", model: [marcador: marcador])
        else
            render(status: 500, text: "ação não encontrada");
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    public def showOutroMarcador(OutroMarcadorFamilia marcador) {
        if (marcador)
            render(view: "marcador/showMarcador", model: [marcador: marcador])
        else
            render(status: 500, text: "sinalização não encontrada");
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

class OutrosMarcadoresCommand implements MarcadoresCommand {
    List<MarcadorCommand> outrosMarcadoresDisponiveis = [].withLazyDefault { new MarcadorCommand() }
    List<MarcadorCommand> getMarcadoresDisponiveis() { outrosMarcadoresDisponiveis }
}

class MarcadorCommand {
    String id
    Boolean habilitado
    String observacao
    String tecnico
}
