package org.apoiasuas.processo

import org.apoiasuas.cidadao.Familia
import org.apoiasuas.seguranca.UsuarioSistema
import org.camunda.bpm.engine.history.HistoricProcessInstance
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery
import org.camunda.bpm.engine.runtime.ProcessInstance

/**
 * Created by admin on 21/05/2016.
 */
class PedidoCertidaoProcessoService extends ProcessoService {

    def cidadaoService

    @Override
    public String getProcessDefinitionStr() {
        return  "pedidoCertidao"
    }

    @Override
    protected ProcessoDTO preencheProcessoDTOHistorico(ProcessoDTO processoDTO, HistoricProcessInstance processInstance) {
        PedidoCertidaoProcessoDTO pedidoCertidaoProcessoDTO = processoDTO

        super.preencheProcessoDTOHistorico(pedidoCertidaoProcessoDTO, processInstance)

        String idFamilia = getHistoricVariable(processInstance.id, PedidoCertidaoProcessoDTO.VARIABLE_ID_FAMILIA)
        pedidoCertidaoProcessoDTO.familia = idFamilia ? Familia.get(idFamilia.toLong()) : null

        String idOperadorResponsavel = getHistoricVariable(processInstance.id, PedidoCertidaoProcessoDTO.VARIABLE_ID_OPERADOR_RESPONSAVEL)
        pedidoCertidaoProcessoDTO.operadorResponsavel = idOperadorResponsavel ? UsuarioSistema.get(idOperadorResponsavel.toLong()) : null

        pedidoCertidaoProcessoDTO.dadosCertidao = getHistoricVariable(processInstance.id, PedidoCertidaoProcessoDTO.VARIABLE_DADOS_CERTIDAO)

        pedidoCertidaoProcessoDTO.cartorio = getHistoricVariable(processInstance.id, PedidoCertidaoProcessoDTO.VARIABLE_CARTORIO)

        pedidoCertidaoProcessoDTO.idFormularioEmitido = getHistoricVariable(processInstance.id, PedidoCertidaoProcessoDTO.VARIABLE_ID_FORMULARIO_EMTIDO)

        pedidoCertidaoProcessoDTO.numeroAR = getHistoricVariable(processInstance.id, PedidoCertidaoProcessoDTO.VARIABLE_NUMERO_AR)

        return pedidoCertidaoProcessoDTO
    }

    public ProcessInstance novoProcesso(UsuarioSistema responsavelProximaTarefa, Long idFamilia,
                                        Long idOperadorResponsavel, String dadosCertidao,
                                        Long idFormularioEmitido, String cartorio, String numeroAR) {

        //Verifica se se trata de uma reemissao de um formulario de pedido de certidao ja emitido anteriormente e,
        //neste caso, apaga o processo gerado anteriormente
        if (idFormularioEmitido) {
            ProcessInstance processoDuplicado = runtimeService.createProcessInstanceQuery().variableValueEquals(PedidoCertidaoProcessoDTO.VARIABLE_ID_FORMULARIO_EMTIDO, idFormularioEmitido.toString()).singleResult()
            if (processoDuplicado) {
                runtimeService.deleteProcessInstance(processoDuplicado.id, "Pedido de certidão reemitido")
                historyService.deleteHistoricProcessInstance(processoDuplicado.id)
            }
        }

        //Informações específicas desta definicao de processo
        Map variables = [:];
        variables.put(PedidoCertidaoProcessoDTO.VARIABLE_ID_FAMILIA, idFamilia?.toString())
        variables.put(PedidoCertidaoProcessoDTO.VARIABLE_ID_OPERADOR_RESPONSAVEL, idOperadorResponsavel?.toString())
        variables.put(PedidoCertidaoProcessoDTO.VARIABLE_DADOS_CERTIDAO, dadosCertidao?.toUpperCase())
        variables.put(PedidoCertidaoProcessoDTO.VARIABLE_CARTORIO, cartorio?.toUpperCase())
        variables.put(PedidoCertidaoProcessoDTO.VARIABLE_ID_FORMULARIO_EMTIDO, idFormularioEmitido.toString())
        variables.put(PedidoCertidaoProcessoDTO.VARIABLE_NUMERO_AR, numeroAR)

        return super._novoProcesso(responsavelProximaTarefa, variables);
    }

    public List<PedidoCertidaoProcessoDTO> getProcessos(String codigoLegado, String dadosCertidao,
                          Long idUsuarioSistema, Boolean pendentes, String numeroAR, String cartorio, Date dataInicio, Date dataFim) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(getProcessDefinitionStr())
        if (codigoLegado) {
            Familia familia = cidadaoService.obtemFamilia(codigoLegado, false)
            query = query.variableValueEquals(PedidoCertidaoProcessoDTO.VARIABLE_ID_FAMILIA, familia ? familia.id.toString() : "-1"/*nao listara nenhum processo*/)
        }
        if (dadosCertidao)
            query = query.variableValueLike(PedidoCertidaoProcessoDTO.VARIABLE_DADOS_CERTIDAO, "%"+dadosCertidao.toUpperCase()+"%")
        if (idUsuarioSistema)
            query = query.variableValueEquals(PedidoCertidaoProcessoDTO.VARIABLE_ID_OPERADOR_RESPONSAVEL, idUsuarioSistema.toString())
        if (pendentes == false)
            query = query.finished()
        else if (pendentes == true)
            query = query.unfinished()
        if (numeroAR)
            query = query.variableValueEquals(PedidoCertidaoProcessoDTO.VARIABLE_NUMERO_AR, numeroAR)
        if (cartorio)
            query = query.variableValueLike(PedidoCertidaoProcessoDTO.VARIABLE_CARTORIO, "%"+cartorio.toUpperCase()+"%")
        if (dataInicio)
            query = query.startedAfter(dataInicio)
        if (dataFim)
            query = query.startedBefore(dataFim+1)
        List<HistoricProcessInstance> processInstances = query.orderByProcessInstanceStartTime().asc().listPage(0, ProcessoDTO.MAX_PAGINACAO)

        List<PedidoCertidaoProcessoDTO> result = []
        processInstances.each { HistoricProcessInstance processInstance ->
            PedidoCertidaoProcessoDTO processo = getProcesso(processInstance.id, false)
            result.add(processo)
        }

        return result
    }

    public void gravaAR(String idProcesso, String numeroAR) {
        runtimeService.setVariable(idProcesso, PedidoCertidaoProcessoDTO.VARIABLE_NUMERO_AR, numeroAR)
    }
}
