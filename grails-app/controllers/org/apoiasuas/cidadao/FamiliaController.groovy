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
import org.apoiasuas.redeSocioAssistencial.RecursosServico
import org.apoiasuas.util.ApoiaSuasException
import org.apoiasuas.util.StringUtils
import org.hibernate.Hibernate

import javax.servlet.http.HttpSession

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class FamiliaController extends AncestralController {

    def beforeInterceptor = [action: this.&interceptaSeguranca, entity:Familia.class, only: ['show','edit', 'delete', 'update', 'save']]
    private static final String SESSION_ULTIMA_FAMILIA = "SESSION_ULTIMA_FAMILIA"
    private static final String SESSION_NOTIFICACAO_FAMILIA = "SESSION_NOTIFICACAO_FAMILIA"
    private static final String SESSION_NOTIFICACAO_FAMILIA_NUMERO_EXIBICOES = "SESSION_NOTIFICACAO_FAMILIA_NUMERO_EXIBICOES"
    private static final int TELEFONES_POR_FAMILIA = 5; //numero máximo de possíveis telefones para uma dada familia

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
/*
    @Override
    protected interceptaSeguranca() {
        try {
            if (params?.getIdentifier()) {
                familiaService.obtemFamilia(params.getIdentifier(), true)
            }
        } catch  (AcessoNegadoPersistenceException e) {
            flash.message = e.getMessage()
            redirect(uri: request.getHeader('referer') )
            return false
        }
    }
*/
    def index(Integer max) {
        redirect(controller: 'cidadao', action: 'procurarCidadao')
//        render view: 'list', model: [familiaInstanceList: Familia.list(params), familiaInstanceCount: Familia.count()]
    }

    def show(Familia familiaInstance) {
        if (! familiaInstance)
            return notFound()

        familiaInstance = familiaService.obtemFamilia(familiaInstance.id, true, true, true, true);
//        familiaInstance = familiaService.obtemFamilia(familiaInstance.id, true, true, true, false/*carregado na sequencia por ajax*/)

        List<PedidoCertidaoProcessoDTO> pedidosCertidaoPendentes = pedidoCertidaoProcessoService.pedidosCertidaoPendentes(familiaInstance.id)

        guardaUltimaFamiliaSelecionada(familiaInstance)
        render view: 'show', model: [familiaInstance: familiaInstance, pedidosCertidaoPendentes: pedidosCertidaoPendentes]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def create() {
        if (! segurancaService.acessoRecursoServico(RecursosServico.INCLUSAO_FAMILIA))
            throw new ApoiaSuasException("O recurso de inclusão de família não está habilitado para este serviço")

        //passa para o modelo da view DUAS novas instâncias: uma para a família e outra para a referência familiar
        Cidadao referenciaFamiliar = new Cidadao();
        referenciaFamiliar.referencia = true;
        referenciaFamiliar.parentescoReferencia = cidadaoService.PARENTESCO_REFERENCIA;
        Familia novaFamilia = new Familia();
        preparaTelefonesParaEdicao(novaFamilia);
        render view: "create", model: getEditCreateModel(novaFamilia)+[cidadaoInstance: referenciaFamiliar];
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def save(Familia familiaInstance, ProgramasCommand programasCommand, AcoesCommand acoesCommand,
             VulnerabilidadesCommand vulnerabilidadesCommand, OutrosMarcadoresCommand outrosMarcadoresCommand) {
        if (! familiaInstance)
            return notFound()
        boolean modoCriacao = familiaInstance.id == null;

        //A gravação de uma nova família embute também a gravação de um primeiro membro como referencia familiar
        Cidadao novaReferenciaFamiliar = null;
        if (modoCriacao) {
            //inicializando a referencia familiar
            if (! params.cidadao)
                throw new ApoiaSuasException("Faltando informações da referência familiar na criação de uma nova família.")
            novaReferenciaFamiliar = new Cidadao(params.cidadao);
            novaReferenciaFamiliar.familia = familiaInstance;
            novaReferenciaFamiliar.servicoSistemaSeguranca = segurancaService.servicoLogado;
            novaReferenciaFamiliar.criador = segurancaService.usuarioLogado;
            novaReferenciaFamiliar.habilitado = true;
            novaReferenciaFamiliar.ultimoAlterador = segurancaService.usuarioLogado;

            //inicializando a familia
            familiaInstance.criador = usuarioLogado;
            familiaInstance.ultimoAlterador = usuarioLogado;
            familiaInstance.situacaoFamilia = SituacaoFamilia.CADASTRADA;
            familiaInstance.servicoSistemaSeguranca = servicoCorrente;
        }

        //Validacao: se estiver gravando também uma referencia familiar, é preciso validá-la também
        boolean validado = familiaInstance.validate() && telefonesFromRequest(familiaInstance) ;
        if (novaReferenciaFamiliar)
            validado = validado && novaReferenciaFamiliar.validate();
        if (validado) {
            familiaInstance = modoCriacao ? familiaService.gravaNovo(familiaInstance, novaReferenciaFamiliar)
                    : familiaService.grava(familiaInstance, programasCommand, acoesCommand,
                        vulnerabilidadesCommand, outrosMarcadoresCommand);

            flash.message = message(code: 'default.updated.message', args: [message(code: 'familia.label', default: 'Família'), familiaInstance.id])
            return show(familiaInstance)
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: getEditCreateModel(familiaInstance)+[cidadaoInstance: novaReferenciaFamiliar]);
        }
    }

    private Map getEditCreateModel(Familia familiaInstance) {
        List<Programa> programasDisponiveis = marcadoresDisponiveis(familiaInstance.programas, marcadorService.getProgramasDisponiveis() )
        List<Acao> acoesDisponiveis = marcadoresDisponiveis(familiaInstance.acoes, marcadorService.getAcoesDisponiveis() )
        List<Vulnerabilidade> vulnerabilidadesDisponiveis = marcadoresDisponiveis(familiaInstance.vulnerabilidades, marcadorService.getVulnerabilidadesDisponiveis() )
        List<OutroMarcador> outrosMarcadoresDisponiveis = marcadoresDisponiveis(familiaInstance.outrosMarcadores, marcadorService.getOutrosMarcadoresDisponiveis() )
        return [familiaInstance: familiaInstance,
                operadores: marcadorService.getTecnicosIncluiMarcadores(familiaInstance),
                outrosMarcadoresDisponiveis: outrosMarcadoresDisponiveis, programasDisponiveis: programasDisponiveis,
                acoesDisponiveis: acoesDisponiveis, vulnerabilidadesDisponiveis: vulnerabilidadesDisponiveis]
    }

    private Map getEditCreateModelMonitoramento(Monitoramento monitoramentoInstance) {
        return [monitoramentoInstance: monitoramentoInstance, operadores: getTecnicosOrdenadosController(true, monitoramentoInstance?.responsavel)];
    }

    /**
     * Marca dentre os programas/acoes/etc disponiveis, aqueles que estão atualmente associados à família
     */
    private List<Marcador> marcadoresDisponiveis(Set<AssociacaoMarcador> marcadoresSelecionados, List<Marcador> marcadoresDisponiveis) {
        marcadoresDisponiveis.each { Marcador marcadorDisponivel ->
            marcadorDisponivel.selecionado = false;//assume como não selecionado inicialmente
            marcadorDisponivel.tecnico = segurancaService.usuarioLogado //utilizar usuario logado como opção default
            marcadoresSelecionados.each { marcadorSelecionado ->
                if (marcadorSelecionado.marcador == marcadorDisponivel) {
                    marcadorDisponivel.selecionado = marcadorSelecionado.habilitado;
                    marcadorDisponivel.observacao = marcadorSelecionado.observacao;
                    marcadorDisponivel.tecnico = marcadorSelecionado.tecnico;
                }
            }
//            marcadorDisponivel.selected = marcadoresSelecionados.find { it.marcador == marcadorDisponivel }
        }
        marcadoresDisponiveis.sort { Marcador p1, Marcador p2 ->
            if (p1.selecionado && ! p2.selecionado)
                return -1;
            if (p2.selecionado && ! p1.selecionado)
                return 1;
            return p1.descricao.compareToIgnoreCase(p2.descricao)
        }
        return marcadoresDisponiveis
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def edit(Familia familiaInstance) {
        def model = getEditCreateModel(familiaInstance);
        Hibernate.initialize(familiaInstance.membros);
        preparaTelefonesParaEdicao(familiaInstance);
        render view: 'edit', model: model
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

    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'Familia.label', default: 'Família'), params.id])
        return redirect(controller: 'cidadao', action: 'procurarCidadao')
    }

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

    def limparNotificacoes() {
        session[SESSION_NOTIFICACAO_FAMILIA] = null;
        render 200;
    }

    def listMonitoramento(Long id) {
        List monitoramentos = familiaService.obtemFamilia(id, false, false, false, true).monitoramentos.sort();
        render view: 'monitoramento/_listMonitoramento', model: [monitoramentoInstanceList: monitoramentos]
    }

    protected Monitoramento buscaMonitoramento(Long id) {
        Monitoramento result = Monitoramento.get(id);
        if (result)
            return result;
        render(status: 500, text: "Erro: Monitoramento com id $id não encontrado");
        return null;
    }


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

    @Secured([DefinicaoPapeis.STR_SUPER_USER])
    def deleteMonitoramento(Monitoramento monitoramento) {
        if (! monitoramento)
            return buscaMonitoramento(-1);

        monitoramentoService.apagaMonitoramento(monitoramento)
        flash.message = "Monitoramento removido"
        return render(contentType:'text/json', text: ['success': true] as JSON);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def suspendeMonitoramento(Monitoramento monitoramento) {
        if (! monitoramento)
            return buscaMonitoramento(-1);

        monitoramento.suspenso = true;
        monitoramentoService.gravaMonitoramento(monitoramento)
        flash.message = "Monitoramento suspenso"
        return render(contentType:'text/json', text: ['success': true] as JSON);
    }

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
    def saveAcompanhamento(Familia familiaInstance, ProgramasCommand programasCommand, AcoesCommand acoesCommand,
                           VulnerabilidadesCommand vulnerabilidadesCommand, OutrosMarcadoresCommand outrosMarcadoresCommand) {
//        familiaInstance.acompanhamentoFamiliar.familia = familiaInstance;
        if (! familiaInstance)
            return notFound()

        //Valida antes de gravar
        if (familiaInstance.validate() && familiaInstance.acompanhamentoFamiliar.validate()) {
            flash.message = "As informações da família e do acompanhamento foram atualizados"
            familiaInstance = familiaService.grava(familiaInstance, programasCommand,
                    acoesCommand, vulnerabilidadesCommand, outrosMarcadoresCommand)
        }

        //sempre retorna para a tela de edicao, quer a gravacao tenha sido feita com sucesso ou tenha erros de validacao
        render view: "acompanhamento/editAcompanhamento", model: modeloSelecionarAcompanhamento + getEditCreateModel(familiaInstance);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveAndDownloadAcompanhamento(Familia familiaInstance, ProgramasCommand programasCommand, AcoesCommand acoesCommand,
                                      VulnerabilidadesCommand vulnerabilidadesCommand, OutrosMarcadoresCommand outrosMarcadoresCommand) {
        saveAcompanhamento(familiaInstance, programasCommand, acoesCommand, vulnerabilidadesCommand, outrosMarcadoresCommand);
        //Guarda na sessao asinformacoes necessarias para a geracao do arquivo a ser baixado (que sera baixado por um
        //javascript que rodara automaticamente na proxima pagina)
        setReportParaBaixar(session, familiaService.emitePlanoAcompanhamento(familiaInstance))
    }

    def selecionarAcompanhamento() {
        Map modelo = FamiliaController.modeloSelecionarAcompanhamento + [cidadaoInstanceList: [], cidadaoInstanceCount: 0, filtro: [:]];
        if (FamiliaController.getUltimaFamilia(session))
            modelo << [defaultNomePesquisa: FamiliaController.getUltimaFamilia(session).cad];
        render(view: "/cidadao/procurarCidadao", model: modelo )
    }

    def selecionarAcompanhamentoExecuta(FiltroCidadaoCommand filtro) {
        //Preenchimento de numeros no primeiro campo de busca indica pesquisa por cad
        boolean buscaPorCad = filtro.nomeOuCad && ! StringUtils.PATTERN_TEM_LETRAS.matcher(filtro.nomeOuCad)
        params.max = params.max ?: 20
        PagedResultList cidadaos = cidadaoService.procurarCidadao(params, filtro)
        Map filtrosUsados = params.findAll { it.value }

        if (buscaPorCad && cidadaos?.resultList?.size() > 0) {
            Cidadao cidadao = cidadaos?.resultList[0]
            redirect(controller: "familia", action: "editAcompanhamentoFamilia", id: cidadao.familia.id)
        } else {
            Map modelo = FamiliaController.modeloSelecionarAcompanhamento + [cidadaoInstanceList: cidadaos, cidadaoInstanceCount: cidadaos.getTotalCount(), filtro: filtrosUsados];
            render(view:"/cidadao/procurarCidadao", model: modelo)
        }
    }

    def listProgramasDisponiveis(String id) {
        List<Marcador> marcadoresDisponiveis = marcadoresDisponiveis(Familia.get(id).programas, marcadorService.getProgramasDisponiveis() )
        return render(view:"marcador/_divMarcadoresDisponiveis", model: [marcadoresDisponiveis: marcadoresDisponiveis,
                                                                         label: 'programasDisponiveis',
                                                                         nomeDiv: 'divEditPrograma',
                                                                         classeMarcador: 'marcadores-programa'])
    }

    def listAcoesDisponiveis(String id) {
        List<Marcador> marcadoresDisponiveis = marcadoresDisponiveis(Familia.get(id).acoes, marcadorService.getAcoesDisponiveis() )
        return render(view:"marcador/_divMarcadoresDisponiveis", model: [marcadoresDisponiveis: marcadoresDisponiveis,
                                                                         label: 'acoesDisponiveis',
                                                                         nomeDiv: 'divEditAcoes',
                                                                         classeMarcador: 'marcadores-acao'])
    }

    def listVulnerabilidadesDisponiveis(String id) {
        List<Marcador> marcadoresDisponiveis = marcadoresDisponiveis(Familia.get(id).vulnerabilidades, marcadorService.getVulnerabilidadesDisponiveis() )
        return render(view:"marcador/_divMarcadoresDisponiveis", model: [marcadoresDisponiveis: marcadoresDisponiveis,
                                                                         label: 'vulnerabilidadesDisponiveis',
                                                                         nomeDiv: 'divEditVulnerabilidade',
                                                                         classeMarcador: 'marcadores-vulnerabilidade'])
    }

    def listProgramasAcoes(String id) {
        List<Marcador> marcadoresDisponiveis = marcadoresDisponiveis(Familia.get(id).acoes, marcadorService.getAcoesDisponiveis() )
        return render(view:"marcador/_divMarcadoresDisponiveis", model: [marcadoresDisponiveis: marcadoresDisponiveis,
                                                                         label: 'acoesDisponiveis',
                                                                         nomeDiv: 'divEditAcao',
                                                                         classeMarcador: 'marcadores-acao'])
    }

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
            //força o marcador a ser gravado exclusivamente para o servico logado
            marcadorInstance.servicoSistemaSeguranca = segurancaService.servicoLogado;
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

    public def showPrograma(ProgramaFamilia marcador) {
        if (marcador)
            render(view: "marcador/showMarcador", model: [marcador: marcador])
        else
            render(status: 500, text: "programa não encontrado");
    }

    public def showVulnerabilidade(VulnerabilidadeFamilia marcador) {
        if (marcador)
            render(view: "marcador/showMarcador", model: [marcador: marcador])
        else
            render(status: 500, text: "vulnerabilidade não encontrada");
    }

    public def showAcao(AcaoFamilia marcador) {
        if (marcador)
            render(view: "marcador/showMarcador", model: [marcador: marcador])
        else
            render(status: 500, text: "ação não encontrada");
    }

    public def showOutroMarcador(OutroMarcadorFamilia marcador) {
        if (marcador)
            render(view: "marcador/showMarcador", model: [marcador: marcador])
        else
            render(status: 500, text: "sinalização não encontrada");
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def editTelefones(Long idFamilia) {
        Familia familia = Familia.get(idFamilia);
        if (! familia)
            throw new RuntimeException("Impossível acessar familia. id "+idFamilia);

        preparaTelefonesParaEdicao(familia)

        render view: 'telefone/editTelefones', model: [familiaInstance: familia];
    }

    private void preparaTelefonesParaEdicao(Familia familia) {
        Hibernate.initialize(familia.telefones);
        familia.discard();

//        familia.telefones = new LinkedHashSet<>(familia.telefones);
        for (int i = familia.telefones.size(); i < TELEFONES_POR_FAMILIA - 1; i++)
            familia.telefones.add(new Telefone(familia));
        familia.telefones.add(new Telefone(familia)); //Sempre adicionar um em branco
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def getTelefones(Long id) {
        Familia familia = Familia.get(id);
        String result = "";
        familia.telefones?.sort{it.dateCreated}?.eachWithIndex { Telefone telefone, int i ->
            if (i > 0)
                result += ", ";
            result += telefone.toString();
            if (telefone.obs)
                result += g.helpTooltip { telefone.obs }
        }
        return render(text: result);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveTelefones(Long idFamilia) {
        Familia familia = Familia.get(idFamilia);
        if (! familia)
            throw new RuntimeException("Impossível acessar familia. id "+idFamilia);

        boolean validado = telefonesFromRequest(familia)

        if (validado) {
            familiaService.gravaTelefones(familia);
            flash.message = "Telefones gravados com sucesso"
            //retornando mensagem de sucesso sem exibir nada na tela (a janela modal sera simplemente fechada)
            return render(contentType:'text/json', text: ['success': true] as JSON);
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(status: 500 /*apontar que houve erro*/,
                    view: 'telefone/editTelefones', model: [familiaInstance: familia]);
        }
    }

    /**
     * Busca os telefones fornecidos na tela e transmitidos via request e os converte em uma lista de dominios Telefone
     * associada à familia passada como parametro. Retorna verdadeiro caso todos os telefones passem pela validacao
     * do dominio (e falso caso pelo menos um deles não tenha passado. Neste caso, cada objeto telefone carrega os seus
     * erros de validacao)
     */
    private boolean telefonesFromRequest(Familia familia) {
        boolean result = true;
        request.getParameterValues("numero").eachWithIndex { fool, i ->
            String idTelefone = request.getParameterValues("idTelefone")[i];
            String ddd = request.getParameterValues("ddd")[i];
            String numero = request.getParameterValues("numero")[i];
            String obs = request.getParameterValues("obs")[i];
            if (request.getParameterValues("remover")[i]?.toBoolean()) {
                Telefone telefone = Telefone.get(idTelefone);
                familia.telefones.remove(telefone);
                telefone.delete();
            } else if (numero || ddd || obs || idTelefone) {
                Telefone telefone = idTelefone ? Telefone.get(idTelefone) : new Telefone(familia)
                telefone.DDD = ddd;
                telefone.numero = numero;
                telefone.obs = obs;
                familia.telefones.add(telefone);
                result = result && telefone.validate();
            } else if (!numero && !ddd && !obs && idTelefone) {
                Telefone telefone = Telefone.get(idTelefone);
                familia.telefones.remove(telefone);
                telefone.delete();
            }
        }
        return result
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
