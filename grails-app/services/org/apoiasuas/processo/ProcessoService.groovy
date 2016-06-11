package org.apoiasuas.processo

import grails.transaction.Transactional
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.SegurancaService
import org.apoiasuas.seguranca.UsuarioSistema
import org.camunda.bpm.engine.HistoryService
import org.camunda.bpm.engine.RepositoryService
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.TaskService
import org.camunda.bpm.engine.history.HistoricProcessInstance
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery
import org.camunda.bpm.engine.history.HistoricTaskInstance
import org.camunda.bpm.engine.history.HistoricTaskInstanceQuery
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl
import org.camunda.bpm.engine.impl.pvm.process.TransitionImpl
import org.camunda.bpm.engine.repository.ProcessDefinition
import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.task.Task
import org.camunda.bpm.engine.task.TaskQuery
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver

@Transactional(readOnly = true)
public class ProcessoService {

    RuntimeService runtimeService
    TaskService taskService
    RepositoryService repositoryService
    SegurancaService segurancaService
    HistoryService historyService
    def pedidoCertidaoProcessoService
    def oficioProcessoService

    @Transactional
    public void init() {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
        Resource[] resources = resolver.getResources("classpath*:/**/*.bpmn");
        for (Resource resource: resources){
            log.debug("Carregando "+resource.getFilename())
            //TODO: comparar mudanças antes de adicionar uma nova versao da definicao do processo
            repositoryService.createDeployment()
                    .enableDuplicateFiltering(true)
                    .addInputStream(resource.filename, resource.inputStream)
                    .deploy()
        }
        println(repositoryService.createProcessDefinitionQuery().list());
    }

    /**
     * A ser sobrescrito nas classes descendentes
     */
    protected String getProcessDefinitionStr() {
        return null
    }

    @Transactional
    protected ProcessInstance _novoProcesso(UsuarioSistema responsavelProximaTarefa, Map variables) {
        //Informações genéricas para toda definicao de processo
        variables = variables ?: [:]
        variables.put(ProcessoDTO.VARIABLE_ID_SERVICO_SISTEMA_SEGURANCA, segurancaService.servicoLogado.id.toString())

        ProcessInstance result = runtimeService.startProcessInstanceByKey(getProcessDefinitionStr(), variables)
        taskService.createTaskQuery().processInstanceId(result.id).list().each {
            taskService.setAssignee(it.id, responsavelProximaTarefa?.id?.toString())
        }
        return result
    }
/*
    protected HistoricTaskInstanceQuery _taskQuery(Long idServicoSistema, String definicaoProcesso, Long idUsuarioSistema, Boolean active) {
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
        if (active != null && active == true)
            query = query.active()
        if (definicaoProcesso)
            query = query.processDefinitionKey(definicaoProcesso)
        if (idServicoSistema)
            query = query.processVariableValueEquals(ProcessoDTO.VARIABLE_ID_SERVICO_SISTEMA_SEGURANCA, idServicoSistema.toString())
        if (idUsuarioSistema)
            query = query.taskAssignee(idUsuarioSistema.toString())
        return query

    }
*/

/*
    public List<TarefaDTO> getTarefasPendentes(Long idServicoSistema, String definicaoProcesso, Long idUsuarioSistema) {
        HistoricTaskInstanceQuery query = _taskQuery(idServicoSistema, definicaoProcesso, idUsuarioSistema, true)
        List<TarefaDTO> result = []
        query.listPage(0, ProcessoDTO.MAX_PAGINACAO).each { Task task ->  //FIXME: implementar paginacao na pesquisa de tarefas
            TarefaDTO tarefa = traduzTarefa(task)
            tarefa.processo = getProcesso(task.processInstanceId, false)
            result.add(tarefa)
        }
        return result
    }
*/

/*
    public List<Task> getTasks(ProcessDefinition processDefinition, ServicoSistema servicoSistema, UsuarioSistema usuarioSistema = null) {
        //Todas as tarefas em execuao
        List<Task> tasks = taskService.createTaskQuery().active().list()
    }
*/
    /**
     * Lista definicoes de processo disponiveis (apenas a ultima versao)
     */
    public List<ProcessDefinition> getDefinicoesProcessos() {
        List<DefinicaoProcessoDTO> result = []
        repositoryService.createProcessDefinitionQuery()./*active().*/latestVersion().orderByProcessDefinitionName().asc().list().each {
            result.add(traduzDefinicaoProcesso(it))
        }
    }

    private DefinicaoProcessoDTO traduzDefinicaoProcesso(ProcessDefinition processDefinition) {
        DefinicaoProcessoDTO result = new DefinicaoProcessoDTO()
        result.chave = processDefinition.key
        result.descricao = processDefinition.name
        result.id = processDefinition.id
        result.suspenso = processDefinition.suspended
        return result
    }

    protected ProcessoDTO preencheProcessoDTOHistorico(ProcessoDTO processoDTO, HistoricProcessInstance processInstance) {
        processoDTO.id = processInstance.id
        processoDTO.inicio = processInstance.startTime
        if (processInstance.endTime)
            processoDTO.situacaoAtual = ProcessoDTO.SITUACAO_CONCLUIDA
        else {
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.id).list()
            processoDTO.situacaoAtual = tasks.collect { it.name }.join(", ")
        }
        processoDTO.definicaoProcesso = getDefinicaoProcessoPeloId(processInstance.processDefinitionId)

        String idServicoSistema = getHistoricVariable(processInstance.id, ProcessoDTO.VARIABLE_ID_SERVICO_SISTEMA_SEGURANCA)
        processoDTO.servicoSistemaSeguranca = idServicoSistema ? ServicoSistema.get(idServicoSistema.toLong()) : null
        return processoDTO
    }

    protected Object getHistoricVariable(String processInstanceId, String nomeVariavel) {
        def variavel = historyService.createHistoricVariableInstanceQuery().
                variableName(nomeVariavel).processInstanceId(processInstanceId).singleResult()
        return variavel ? variavel.value : null
    }
/*
    private TarefaDTO traduzTarefaPendente(Task task) {
        TarefaDTO tarefa = new TarefaDTO()
        tarefa.id = task.id
        tarefa.responsavel = task.assignee ? UsuarioSistema.get(task.assignee.toLong()) : null
        tarefa.proximosPassos = getOutgoingTransitionNames(task)
        tarefa.ultimaPendente = getOutgoingTransitionsIsLast(task)
        tarefa.descricao = task.name
        tarefa.inicio = task.createTime
        tarefa.situacao = TarefaDTO.SituacaoTarefa.PENDENTE
        return tarefa
    }
*/
    private TarefaDTO traduzTarefa(HistoricTaskInstance historicTaskInstance) {
        TarefaDTO tarefa = new TarefaDTO()
        tarefa.id = historicTaskInstance.id
        tarefa.descricao = historicTaskInstance.name
        tarefa.responsavel = historicTaskInstance.assignee ? UsuarioSistema.get(historicTaskInstance.assignee.toLong()) : null
        tarefa.inicio = historicTaskInstance.startTime
        tarefa.fim = historicTaskInstance.endTime
        if (! historicTaskInstance.endTime) {
            tarefa.situacao = TarefaDTO.SituacaoTarefa.PENDENTE
            tarefa.proximosPassos = getOutgoingTransitionNames(historicTaskInstance)
            tarefa.ultimaPendente = getOutgoingTransitionsIsLast(historicTaskInstance)
        } else {
            if (historicTaskInstance.deleteReason?.toLowerCase()?.startsWith("completed"))
                tarefa.situacao = TarefaDTO.SituacaoTarefa.CONCLUIDA
            else
                tarefa.situacao = TarefaDTO.SituacaoTarefa.CANCELADA
        }

        return tarefa
    }

    public DefinicaoProcessoDTO getDefinicaoProcessoPeloId(String idDefinicaoProcesso) {
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(idDefinicaoProcesso)
        if (! processDefinition)
            return null

        DefinicaoProcessoDTO result = new DefinicaoProcessoDTO()
        result.id = idDefinicaoProcesso
        result.chave = processDefinition.key
        result.descricao = processDefinition.name
        result.suspenso = processDefinition.suspended
        return result
    }
/**
     * Tenta buscar o processo tanto na base de processos em andamento quanto na de processos concluidos
     * @param processId
     * @param preencheColecoes Se verdadeiro, popula a lista de tarefas.
     */
    public ProcessoDTO getProcesso(String processId, boolean preencheColecoes) {
        ProcessoService servicoEspecifico

//        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult()
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult()
        if (! processInstance)
            return null

        DefinicaoProcessoDTO definicaoProcessoDTO = getDefinicaoProcessoPeloId(processInstance.processDefinitionId)

        ProcessoDTO result
        switch (definicaoProcessoDTO.chave) {
            case DefinicaoProcessoDTO.CHAVE_PEDIDO_CERTIDAO:
                result =  new PedidoCertidaoProcessoDTO();
                servicoEspecifico = pedidoCertidaoProcessoService
                break;
            case DefinicaoProcessoDTO.CHAVE_OFICIO:
                result = new OficioProcessoDTO();
                servicoEspecifico = oficioProcessoService
                break;
            default:
                log.warn("Especialização da classe ProcessoDTO não encontrada para processos do tipo ${definicaoProcessoDTO.descricao}")
                servicoEspecifico = this
                result = new ProcessoDTO();
        }

        if (preencheColecoes) {
            //tarefas já concluídas
            historyService.createHistoricTaskInstanceQuery().processInstanceId(processId).list().each { HistoricTaskInstance historicTaskInstance ->
                result.addTarefa(traduzTarefa(historicTaskInstance))
            }
/*
            //tarefas pendentes
            if (! processInstance.endTime)
                taskService.createTaskQuery().processInstanceId(processInstance.id).orderByProcessInstanceId().asc().list().each { Task task ->
                    result.addTarefa(traduzTarefaPendente(task))
                }
*/
        }

        servicoEspecifico.preencheProcessoDTOHistorico(result, processInstance)

        //Ordena tarefas pela data de inicio
        result.tarefas = result.tarefas.sort { it.inicio }

        if (result.tarefas)
            result.inicio = result.tarefas[0].inicio

        return result
    }

    /**
     * Pesquisa a estrutura interna de definicao dos processos (classe ProcessDefinitionEntity)
     * Só considera uma transicao (flow) de saida unica. Exibe a propriedade "name" da transicao.
     * Importante: Nao sao usadas as interfaces publicas da framework!
     *
     */
    private List<String> getOutgoingTransitionNames(HistoricTaskInstance task) {
        try {
            task.processDefinitionId
            List<String> result = []
//            ExecutionEntity exe = (ExecutionEntity)runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
//        ScopeImpl a = (ScopeImpl)repositoryService.getProcessDefinition(exe.processDefinitionId)
            ProcessDefinitionEntity a = repositoryService.getProcessDefinition(task.processDefinitionId)

            a.activities.each { ActivityImpl activity ->
                if (activity.id == task.taskDefinitionKey) {
                    if (activity.outgoingTransitions?.size() == 1) {
                        TransitionImpl t = activity.outgoingTransitions[0]
                        boolean ultimaTarefa = t.getDestination().outgoingTransitions.size() == 0
                        result.add(t.properties.name)
                    }
                }
            }
            return result
        } catch (Exception e) {
            log.error("Nao foi possivel determinar a transicao de saida para a tarefa "+task.taskDefinitionKey)
            e.printStackTrace()
            return []
        }
    }

    /**
     * Verifica a definicao para saber se esta eh a ultima tarefa pendente
     * Ver getOutgoingTransitionNames(Task task)
     */
    private Boolean getOutgoingTransitionsIsLast(HistoricTaskInstance task) {
        try {
            Boolean result = true
//            ExecutionEntity exe = (ExecutionEntity)runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
//        ScopeImpl a = (ScopeImpl)repositoryService.getProcessDefinition(exe.processDefinitionId)
            ProcessDefinitionEntity a = repositoryService.getProcessDefinition(task.processDefinitionId)

            a.activities.each { ActivityImpl activity ->
                if (activity.id == task.taskDefinitionKey) {
                    if (activity.outgoingTransitions?.size() == 1) {
                        TransitionImpl t = activity.outgoingTransitions[0]
                        if (t.getDestination().outgoingTransitions.size() > 0)
                            result = result && false
                    }
                }
            }
            return result
        } catch (Exception e) {
            log.error("Nao foi possivel determinar a transicao de saida para a tarefa "+task.taskDefinitionKey)
            e.printStackTrace()
            return []
        }
    }

//    @Transactional
    /**
     * Conclui uma tarefa especifica, atribui uma possivel proxima tarefa ao novo responsavel e retorna o id do processo afetado
     */
    @Transactional
    public String concluiTarefa(Long idTarefa, Long idProximoResponsavel) {
        Task tarefaConcluida = taskService.createTaskQuery().taskId(idTarefa.toString()).singleResult()
        if (! tarefaConcluida)
            throw new RuntimeException("Tarefa $idTarefa nao encontrada durante gravacao")

        taskService.complete(idTarefa.toString())
        //Busca a proxima tarefa gerada (pendente e sem responsavel) para o processo
        Task proximaTarefa = taskService.createTaskQuery().processInstanceId(tarefaConcluida.processInstanceId).taskUnassigned().singleResult()
        if (proximaTarefa) {
            //Define o responsavel
            taskService.setAssignee(proximaTarefa.id, idProximoResponsavel.toString())
        }

        return tarefaConcluida.processInstanceId
    }

//    @Transactional
    /**
     * Volta o processo até uma tarefa que estava concluida anteriormente
     */
    @Transactional
    public String reabreTarefa(Long idTarefa) {
//        Task tarefaConcluida = taskService.createTaskQuery().taskId(idTarefa.toString()).singleResult()
        HistoricTaskInstance tarefaConcluida = historyService.createHistoricTaskInstanceQuery().taskId(idTarefa.toString()).singleResult()
        if (! tarefaConcluida)
            throw new RuntimeException("Tarefa $idTarefa nao encontrada durante gravacao")

        Task tarefaPendente = taskService.createTaskQuery().processInstanceId(tarefaConcluida.processInstanceId).singleResult()

        runtimeService.createProcessInstanceModification(tarefaConcluida.processInstanceId)
                .cancelAllForActivity(tarefaPendente.taskDefinitionKey)
                .startBeforeActivity(tarefaConcluida.taskDefinitionKey)
                .execute()

        Task novaTarefaPendente = taskService.createTaskQuery().processInstanceId(tarefaConcluida.processInstanceId).taskUnassigned().singleResult()
        taskService.setAssignee(novaTarefaPendente.id, tarefaConcluida.assignee)

/*
        taskService.complete(idTarefa.toString())
        //Busca a proxima tarefa gerada (pendente e sem responsavel) para o processo
        Task proximaTarefa = taskService.createTaskQuery().processInstanceId(tarefaConcluida.processInstanceId).taskUnassigned().singleResult()
        if (proximaTarefa) {
            //Define o responsavel
            taskService.setAssignee(proximaTarefa.id, idProximoResponsavel.toString())
        }
*/
        return tarefaConcluida.processInstanceId
    }

    @Transactional
    public void cancelaProcesso(String idProcesso) {
        if (runtimeService.createProcessInstanceQuery().processInstanceId(idProcesso).singleResult())
            runtimeService.deleteProcessInstance(idProcesso, "")
        if (historyService.createHistoricProcessInstanceQuery().processInstanceId(idProcesso).singleResult())
            historyService.deleteHistoricProcessInstance(idProcesso)
    }

    protected HistoricProcessInstanceQuery getQuery(Map filtros) {
        //Filtros compulsorios: tipo de processo, servicoSistema
        HistoricProcessInstanceQuery result = historyService.createHistoricProcessInstanceQuery()
                .variableValueEquals(ProcessoDTO.VARIABLE_ID_SERVICO_SISTEMA_SEGURANCA, segurancaService.servicoLogado.id.toString());
        if (getProcessDefinitionStr())
            result = result.processDefinitionKey(getProcessDefinitionStr())

        //Filtros opcionais:
        if (filtros.dataInicio)
            result = result.startedAfter(filtros.dataInicio)
        if (filtros.dataFim)
            result = result.startedBefore(filtros.dataFim+1)
        if (filtros.pendentes == false)
            result = result.finished()
        else if (filtros.pendentes == true)
            result = result.unfinished()

        return result
    }

}
