package org.apoiasuas.processo

import org.apoiasuas.seguranca.UsuarioSistema

/**
 * DTO usado para representar tarefas da engine BPM Camunda, tanto para tarefas concluidas quanto para pendentes
 */
class TarefaDTO {
    public enum SituacaoTarefa {PENDENTE, CONCLUIDA, CANCELADA}

    String id
    ProcessoDTO processo
    UsuarioSistema responsavel //Task.assignee
//    Boolean ativa
    Date inicio
    Date fim
    SituacaoTarefa situacao
    List<String> proximosPassos = []
    String descricao //Task.name
    Boolean ultimaPendente
}
