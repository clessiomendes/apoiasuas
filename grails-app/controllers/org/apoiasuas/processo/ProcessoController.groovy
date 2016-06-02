package org.apoiasuas.processo

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.UsuarioSistema
import org.camunda.bpm.engine.RepositoryService
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService
import org.camunda.bpm.engine.repository.ProcessDefinition
import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.task.Task

import java.text.ParseException
import java.text.SimpleDateFormat

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class ProcessoController extends AncestralController {

    public static final String SITUACAO_PENDENTE = "PENDENTE"
    public static final String SITUACAO_CONCLUIDO = "CONCLUIDO"
    RuntimeService runtimeService
    TaskService taskService
    RepositoryService repositoryService
    def processoService

    def init() {
        processoService.init()
        return list()
    }

    def list() {
        Long idUsuarioSistema = params.usuarioSistema ? params.usuarioSistema.toString().toLong() : null;
        String idDefinicaoProcesso = params.definicicaoProcesso ? params.definicicaoProcesso : null;

        List<TarefaDTO> tarefas = processoService.getTarefasPendentes(getServicoCorrente().id, idDefinicaoProcesso, idUsuarioSistema)

        render(view: "/processo/list", model: [tarefas: tarefas, definicoesProcessoDisponiveis: processoService.getDefinicoesProcessos(), ususariosDisponiveis: segurancaService.getOperadoresOrdenados()])
    }

    def mostraProcesso(ProcessoDTO processoDTO) {
        if (! processoDTO.id)
            return notFound()

        //popula a instancia com mais informacoes dos que as passadas no request
        processoDTO = processoService.getProcesso(processoDTO.id, true)
        if (! processoDTO)
            return notFound()

        render(view: "/processo/show", model: [processo: processoDTO, ususariosDisponiveis: segurancaService.getOperadoresOrdenados(), templateProcessoEspecifico: getTemplateProcessoEspecifico()])
    }

    /**
     * Metodo a ser especializado nas classes descendentes para definir eventuais templates customizados para exibição dos campos do processo
     */
    protected String getTemplateProcessoEspecifico() {
        return null
    }

    def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'processo.label', default: 'Processo'), params.id])
        return redirect(action: "list")
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
        return redirect(action: "list")
    }

}
