package org.apoiasuas.processo

import grails.validation.Validateable
import org.apoiasuas.redeSocioAssistencial.ServicoSistema

/**
 * DTO usado para representar instancias de processo da engine BPM Camunda, tanto para processos concluidos quanto para em andamento
 */
@Validateable
class ProcessoDTO {

    public static final String SITUACAO_CONCLUIDA = "Concluído"
    public static final int MAX_PAGINACAO = 100

    String id
    DefinicaoProcessoDTO definicaoProcesso
    Date inicio
    Date fim
    String situacaoAtual

    public final static String VARIABLE_ID_SERVICO_SISTEMA_SEGURANCA = "idServicoSistemaSeguranca"
    ServicoSistema servicoSistemaSeguranca

    List<TarefaDTO> tarefas = []

    public List<TarefaDTO> getTarefasPendentes() {
        return tarefas.findAll { it.situacao == TarefaDTO.SituacaoTarefa.PENDENTE }
    }

    public List<TarefaDTO> getTarefasConcluidas() {
        return tarefas.findAll { it.situacao == TarefaDTO.SituacaoTarefa.CONCLUIDA }
    }

    public void addTarefa(TarefaDTO tarefa) {
        tarefa.processo = this
        tarefas.add(tarefa)
    }
    public String getDescricao() {
        return definicaoProcesso ? definicaoProcesso.descricao : null
    }
}
