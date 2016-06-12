package org.apoiasuas.processo

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.camunda.bpm.engine.RepositoryService
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class ProcessoController extends AncestralController {

    static defaultAction = "preList"
    public static final String SITUACAO_PENDENTE = "PENDENTE"
    public static final String SITUACAO_CONCLUIDO = "CONCLUIDO"
    RuntimeService runtimeService
    TaskService taskService
    RepositoryService repositoryService
    def processoService

    def init() {
        processoService.init()
        return preList()
    }

    def preList() {
        Long idUsuarioSistema = params.usuarioSistema ? params.usuarioSistema.toString().toLong() : null;
        String idDefinicaoProcesso = params.definicicaoProcesso ? params.definicicaoProcesso : null;

//        List<TarefaDTO> tarefas = processoService.getTarefasPendentes(getServicoCorrente().id, idDefinicaoProcesso, idUsuarioSistema)

        render(view: "/processo/list", model: [tarefas: [], definicoesProcessoDisponiveis: processoService.getDefinicoesProcessos(), ususariosDisponiveis: getOperadoresOrdenadosController(true)])
    }

    def mostraProcesso(ProcessoDTO processoDTO) {
        if (! processoDTO.id)
            return notFound()

        //popula a instancia com mais informacoes dos que as passadas no request
        processoDTO = processoService.getProcesso(processoDTO.id, true)
        if (! processoDTO)
            return notFound()

        render(view: "/processo/show", model: [processo: processoDTO, ususariosDisponiveis: getOperadoresOrdenadosController(true), templateProcessoEspecifico: getTemplateProcessoEspecifico()])
    }

    /**
     * Metodo a ser especializado nas classes descendentes para definir eventuais templates customizados para exibição dos campos do processo
     */
    protected String getTemplateProcessoEspecifico() {
        return null
    }

    def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'processo.label', default: 'Processo'), params.id])
        return redirect(action: "preList")
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def concluiTarefa(TarefaDTO tarefaDTO) {
        Long idProximoResponsavel = params.proximoResponsavel?.toLong()
        String idProcesso = processoService.concluiTarefa(tarefaDTO.id.toLong(), idProximoResponsavel)
        redirect(action: "mostraProcesso", id: idProcesso)
//        mostraProcesso(new ProcessoDTO(id: idProcesso))
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def reabreTarefa(TarefaDTO tarefaDTO) {
        String idProcesso = processoService.reabreTarefa(tarefaDTO.id.toLong())
        redirect(action: "mostraProcesso", id: idProcesso)
//        mostraProcesso(new ProcessoDTO(id: idProcesso))
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def cancelaProcesso(ProcessoDTO processoDTO) {
        processoService.cancelaProcesso(processoDTO.id)
        flash.message = "Processo ${processoDTO.id} cancelado"
        return redirect(action: "preList")
    }

}
