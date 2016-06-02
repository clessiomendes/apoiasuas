package org.apoiasuas.processo

/**
 * DTO usado para representar definicoes de processo da engine BPM Camunda
 */
class DefinicaoProcessoDTO {

    static final String CHAVE_PEDIDO_CERTIDAO = "pedidoCertidao"
    static final String CHAVE_OFICIO = "oficio"

    String id
    String chave //ProcessDefinition.key
    String descricao //ProcessDefinition.name
    Boolean suspenso //ProcessDefinition.suspended
}
