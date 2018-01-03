package org.apoiasuas

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.json.JsonSlurper
import org.apoiasuas.agenda.Compromisso
import org.apoiasuas.cidadao.CidadaoController
import org.apoiasuas.cidadao.FamiliaController
import org.apoiasuas.redeSocioAssistencial.AtendimentoParticularizado
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.ApoiaSuasDateUtils
import org.apoiasuas.util.StringUtils
import org.springframework.security.access.annotation.Secured

import java.awt.Color

//import fr.opensagres.xdocreport.samples.docxandvelocity.model.Developer;
//import fr.opensagres.xdocreport.samples.docxandvelocity.model.Project;
@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class AgendaController extends AncestralController {

    static defaultAction = "calendario";
    static private final String CONFIGURACOES = "CONFIGURACOES_AGENDA";
    def agendaService;
    def usuarioSistemaService;

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def calendario(Long idUsuarioSistema) {
//        Map configuracaoCalendario;
//        if (! session[CONFIGURACAO_CALENDARIO])
//            session[CONFIGURACAO_CALENDARIO] = (CONFIGURACAO_INICIAL + [defaultDate: ApoiaSuasDateUtils.dateTimeToStringIso8601(new Date())]) as JSON;
//                //Caso não haja configuração prévia guardada na sessão, usar parâmetros default e partir da data atual

        render view: "calendario", model: [operadores: getOperadoresOrdenadosController(true),
                configuracao: getConfiguracao(), idUsuarioSistema: idUsuarioSistema]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def createCompromisso(Long idUsuarioSistema, String inicio, String fim) {
        Compromisso novoCompromisso = new Compromisso();
        if (idUsuarioSistema)
            novoCompromisso.participantes.add(UsuarioSistema.get(idUsuarioSistema));
        novoCompromisso.inicio = ApoiaSuasDateUtils.stringToDateTimeIso8601(inicio);
        novoCompromisso.fim = ApoiaSuasDateUtils.stringToDateTimeIso8601(fim);// + 0.042;
        novoCompromisso.tipo = Compromisso.Tipo.OUTROS;
        novoCompromisso.habilitado = true;
        render view: "createCompromisso", model: getEditCreateModelCompromisso(novoCompromisso)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def createCompromissoAutomatico(Long idUsuarioSistema, String inicio, String fim) {
        if (! idUsuarioSistema || ! inicio || ! fim)
            render(status: 500, text: "responsável ou hora inicial ou hora final do atendimento não foi fornecido");
        UsuarioSistema tecnico = UsuarioSistema.get(idUsuarioSistema);
        if (! tecnico)
            render(status: 500, text: "técnico  não encontrado para o id $idUsuarioSistema");

        Compromisso novoCompromisso = agendaService.criaCompromissoAtendimento(tecnico,  ApoiaSuasDateUtils.stringToDateTimeIso8601(inicio),
                ApoiaSuasDateUtils.stringToDateTimeIso8601(fim));
        agendaService.sinalizaConflitos(novoCompromisso);

        render compromissoToEvent(novoCompromisso) as JSON;
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def obterCompromissos(Long idUsuarioSistema, String start, String end,
                          Boolean mostrarAtendimentos, Boolean mostrarOutrosCompromissos) {
        List<Compromisso> compromissos = agendaService.listarCompromissos(
                ApoiaSuasDateUtils.stringToDateTimeIso8601(start),
                ApoiaSuasDateUtils.stringToDateTimeIso8601(end),
                idUsuarioSistema, mostrarAtendimentos, mostrarOutrosCompromissos);

        //Verifica possíveis conflitos entre compromissos de um mesmo responsavel, marcando-os de vermelho
/*
        compromissos.each { compromisso1 ->
            compromissos.each { compromisso2 ->
                if (compromisso1 != compromisso2 && compromisso1.responsavel && compromisso2.responsavel
                        && compromisso1.responsavel.id == compromisso2.responsavel.id) {
                    if (!(compromisso2.fim <= compromisso1.inicio || compromisso1.fim <= compromisso2.inicio)) {
                        compromisso1.cor = "red";
                        compromisso2.cor = "red";
                    }
                }
            }
        }
*/

        render compromissos.collect { compromissoToEvent(it) } as JSON
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def updateCompromissoHorario(Long idCompromisso, String start, String end) {
        final Compromisso compromisso = agendaService.getCompromisso(idCompromisso);
        compromisso.inicio = ApoiaSuasDateUtils.stringToDateTimeIso8601(start);
        compromisso.fim = ApoiaSuasDateUtils.stringToDateTimeIso8601(end);
        agendaService.gravaCompromisso(compromisso);
        agendaService.sinalizaConflitos(compromisso);
        return render(compromissoToEvent(compromisso) as JSON);
//        render(['success': true] as JSON);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveCompromisso(Compromisso compromissoInstance) {
//        if (! compromissoInstance)
//            return buscaMonitoramento(-1);

        if (compromissoInstance == null) //registro excluido em outra sessao de usuario
            return render(status: 500 /*apontar que houve erro*/,
                    view: /*modoCriacao ? "" : */ "erroAlteracaoConcorrente",
                    model: [detalhesErro: "Este compromisso foi excluído em outro computador."]
            )
        else if (!validaVersao(compromissoInstance))  //registro alterado em outra sessao de usuario
            return render(status: 500 /*apontar que houve erro*/,
                    view: /*modoCriacao ? "" : */ "erroAlteracaoConcorrente",
                    model: [detalhesErro: "Este compromisso foi alterado em outro computador. Tente novamente."]
            );

        LinkedHashSet participantes = [];
        params.participantesAux?.each { String idParticipante ->
            if (! idParticipante?.isEmpty())
                participantes.add(usuarioSistemaService.getUsuarioSistema(new Long(idParticipante)))
        }
        compromissoInstance.participantes.clear();
        compromissoInstance.participantes.addAll(participantes);

        boolean modoCriacao = compromissoInstance.id == null;
        if (modoCriacao)
            compromissoInstance.servicoSistemaSeguranca = segurancaService.getServicoLogado()

        compromissoInstance.inicio = dataDoRequest(params['dataInicio'], params['horaInicio'])
        compromissoInstance.fim = dataDoRequest(params['dataFim'], params['horaFim'])

        boolean validado = compromissoInstance.validate()
        if (compromissoInstance.fim <= compromissoInstance.inicio) {
            validado = false;
            compromissoInstance.errors.rejectValue('inicio', "", "O inicio do compromisso deve vir antes do fim")
        }

        if (validado) {
            agendaService.gravaCompromisso(compromissoInstance);
            agendaService.sinalizaConflitos(compromissoInstance)
            flash.message = "Compromisso gravado com sucesso"
            return render(compromissoToEvent(compromissoInstance) as JSON);
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(status: 500 /*apontar que houve erro*/,
                    view: modoCriacao ? "createCompromisso" : "editCompromisso",
                    model: getEditCreateModelCompromisso(compromissoInstance));
        }
    }

    private Date dataDoRequest(String data, String hora) {
        if (data) {
            if (hora)
                return ApoiaSuasDateUtils.stringToDateTime(data + ' ' + hora)
            else
                return ApoiaSuasDateUtils.stringToDateTime(data + ' 00:00');
        } else
            return null;
    }

    private Map getEditCreateModelCompromisso(Compromisso compromissoInstance) {
        return [ compromissoInstance: compromissoInstance,
            operadores: getOperadoresOrdenadosController(true, compromissoInstance ? compromissoInstance.participantes : [] )
        ]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def imprimir(String start, String end) {
//        if (! idUsuarioSistema)
//            throw new ApoiaSuasException("Obrigatório escolher um operador para gerar sua agenda");
//        UsuarioSistema usuarioSistema = UsuarioSistema.get(idUsuarioSistema)
        Date inicio = ApoiaSuasDateUtils.stringToDateTimeIso8601(start)
        Date fim = ApoiaSuasDateUtils.stringToDateTimeIso8601(end)

        response.contentType = 'application/octet-stream'
        response.setHeader('Content-disposition', "attachment; filename=\"Agenda de ${inicio.format("dd-MM-yyyy")}.docx\"");
//        response.setHeader('Content-disposition', "attachment");
        agendaService.imprimirAgendaAtendimentos(response.outputStream, inicio, fim);
        response.outputStream.flush();
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def editCompromisso(Long idCompromisso) {
        Compromisso compromisso = agendaService.getCompromisso(idCompromisso);

        if (compromisso == null) //registro excluido em outra sessao de usuario
            return render(status: 500 /*apontar que houve erro*/,
                    view: /*modoCriacao ? "" : */ "erroAlteracaoConcorrente",
                    model: [detalhesErro: "Este compromisso/atendimento foi excluído em outro computador."]
            )

        if (compromisso.tipo.atendimento)
            render view: "editAtendimentoParticularizado", model: getEditCreateModelAtendimento(compromisso.atendimentoParticularizado);
        else
            render view: "editCompromisso", model: getEditCreateModelCompromisso(compromisso)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def deleteCompromisso(Compromisso compromisso) {
        if (agendaService.deleteCompromisso(compromisso.id))
            return render(contentType:'text/json', text: [id: compromisso.id] as JSON)
        else
            return render(status: 500, text: "Compromisso com ID ${compromisso.id} não encontrado")
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveAtendimento(AtendimentoParticularizado atendimentoInstance) {

//        Metodo acionado apenas para gravacao de edicao (nunca para novo atendimento)
//        boolean modoCriacao = atendimentoInstance.id == null;
//        if (modoCriacao)
//            atendimentoInstance.servicoSistemaSeguranca = segurancaService.getServicoLogado();

        if (atendimentoInstance == null) //registro excluido em outra sessao de usuario
            return render(status: 500 /*apontar que houve erro*/,
                    view: /*modoCriacao ? "" : */ "erroAlteracaoConcorrente",
                    model: [detalhesErro: "Este atendimento foi excluído em outro computador."]
            )
        else if (!validaVersao(atendimentoInstance))  //registro alterado em outra sessao de usuario
            return render(status: 500 /*apontar que houve erro*/,
                    view: /*modoCriacao ? "" : */ "erroAlteracaoConcorrente",
                    model: [detalhesErro: "Este atendimento foi alterado em outro computador. Tente novamente."]
            );

        atendimentoInstance.dataHora = dataDoRequest(params['data'], params['hora'])
//        if (params['idFamilia'])

        //          V A L I D A Ç Õ E S
        boolean validado = atendimentoInstance.validate()
        if (atendimentoInstance.compareceu != null && ! atendimentoInstance.nomeCidadao) {
            validado = false;
            atendimentoInstance.errors.rejectValue('nomeCidadao', "", "Obrigatório definir o nome do cidadão antes de confirmar o comparecimento")
        }
        if (atendimentoInstance.compareceu == null)
            if (atendimentoInstance.nomeCidadao && ! atendimentoInstance.telefoneContato && ! atendimentoInstance.semTelefone) {
                validado = false;
                atendimentoInstance.errors.rejectValue('telefoneContato', "", "Obrigatório informar um telefone ou escolher 'sem telefone'")
            }
        //Se preencher o nome, defe informar o cadastro ou marcar explicitamente "sem cadastro"
        if (atendimentoInstance.nomeCidadao)
            if ((!atendimentoInstance.familia) && (!atendimentoInstance.familiaSemCadastro)) {
                validado = false;
                atendimentoInstance.errors.rejectValue('familia', "", "Obrigatório buscar o cad ou informar 'família sem cadastro'")
            }
        //Se escolher uma familia ou marcou explicitamente "sem cadastro", deve informar um nome ou liberar o horario
        if (atendimentoInstance.familia || atendimentoInstance.familiaSemCadastro)
            if (! atendimentoInstance.nomeCidadao) {
                validado = false;
                atendimentoInstance.errors.rejectValue('nomeCidadao', "", "Obrigatório informar o nome ou liberar o horário")
            }
        //Nome contendo numeros nao sao permitidos
        if (atendimentoInstance.nomeCidadao && StringUtils.PATTERN_TEM_NUMEROS.matcher(atendimentoInstance.nomeCidadao)) {
            validado = false;
            atendimentoInstance.errors.rejectValue('nomeCidadao', "", "Nome inválido")
        }

        if (validado) {
            agendaService.gravaAtendimento(atendimentoInstance);
            agendaService.sinalizaConflitos(atendimentoInstance.compromisso)
            flash.message = "Atendimento gravado com sucesso"
            return render(compromissoToEvent(atendimentoInstance.compromisso) as JSON);
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(status: 500 /*apontar que houve erro*/,
                    view: /*modoCriacao ? "" : */"editAtendimentoParticularizado",
                    model: getEditCreateModelAtendimento(atendimentoInstance));
        }
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def compareceuAtendimento(AtendimentoParticularizado atendimentoInstance) {
        atendimentoInstance.compareceu = true;
        saveAtendimento(atendimentoInstance);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def naoCompareceuAtendimento(AtendimentoParticularizado atendimentoInstance) {
        atendimentoInstance.compareceu = false;
        saveAtendimento(atendimentoInstance);
    }

    private Map getEditCreateModelAtendimento(AtendimentoParticularizado atendimentoInstance) {
        return [ atentimentoParticularizadoInstance: atendimentoInstance,
                 cidadaoCandidato: CidadaoController.getUltimoCidadao(session),
                 familiaCandidata: FamiliaController.getUltimaFamilia(session),
                 operadores: getOperadoresOrdenadosController(true, [atendimentoInstance.tecnico])
        ]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def deleteAtendimento(AtendimentoParticularizado atendimento) {
        return deleteCompromisso(Compromisso.findByAtendimentoParticularizado(atendimento))
    }

    /**
     * Converte da classe Compromisso para um mapa contendo as propriedades de um Event do componente FullCalendar
     */
    private Map compromissoToEvent(Compromisso compromisso) {
        String participantes = "";
        if (compromisso.participantes) {
            participantes += "["+compromisso.participantes[0].username;
            if (compromisso.participantes.size() > 1)
                participantes += ", ...";
            participantes += "] "
        }
//        compromisso.participantes ? "["+compromisso.responsavel.username+"] " : "";
        String titulo = participantes + compromisso.descricao;
        if (compromisso.atendimentoParticularizado)
            titulo = participantes + compromisso.tooltip;
        return [id: compromisso.id,
                title: titulo,
                start: ApoiaSuasDateUtils.dateTimeToStringIso8601(compromisso.inicio),
                end: ApoiaSuasDateUtils.dateTimeToStringIso8601(compromisso.fim),
                className: compromisso.cor,
                //Personalizados:
                tipoAtendimento: compromisso.tipo.atendimento,
                tooltip: compromisso.tooltip,
                idsParticipantes: compromisso.participantes.collect { it.id },
                mensagem: compromisso.mensagem]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def configuracao() {
        LinkedHashMap<String, Integer> inicioSemana = [1: "segunda", 2: "terça", 3: "quarta", 4: "quinta",
                5:"sexta", 6:"sábado", 0: "domingo"];
        Map definicoesAtuais = [:];
        if (session[CONFIGURACOES])
            definicoesAtuais = new JsonSlurper().parseText(session[CONFIGURACOES])
        else
            definicoesAtuais = agendaService.getConfiguracao();
        return render(view: "configuracao", model: [configuracao: definicoesAtuais, inicioSemana: inicioSemana]);
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def saveConfiguracao(ConfiguracaoCommand configuracao) {
        agendaService.gravaConfiguracao(configuracao);
        session[CONFIGURACOES] = (configuracao as JSON).toString();
        return render(contentType:'text/json', text: ["Configurações gravadas com sucesso."] as JSON)
//        return render(status: 200 /*apontar sucesso*/);
    }

    private JSON getConfiguracao() {
        if (session[CONFIGURACOES])
            return new JsonSlurper().parseText(session[CONFIGURACOES]) as JSON
        else {
            Map result = agendaService.getConfiguracao();
            result.atendimentos = true;
            result.outrosCompromissos = true;
            session[CONFIGURACOES] = (result as JSON).toString();
            return result as JSON;
        }
    }

}

class ConfiguracaoCommand implements Serializable {
    String minTime
    Boolean atendimentos
    Boolean outrosCompromissos
    String maxTime
    Integer firstDay
    Boolean weekends
}
