package org.apoiasuas.processo

import org.apoiasuas.cidadao.Familia
import org.apoiasuas.redeSocioAssistencial.RecursosServico
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

        pedidoCertidaoProcessoDTO.observacoesInternas = getHistoricVariable(processInstance.id, PedidoCertidaoProcessoDTO.VARIABLE_OBSERVACOES_INTERNAS)

        return pedidoCertidaoProcessoDTO
    }

    //Erro nullpointer do framework caso se defina @Transactional explicitamente nos metodos desta classe (que foi especializada de outra classe que
    // implementa um serviço). Bug do grails, nao consegui identificar o porque ???
    public ProcessInstance novoProcesso(UsuarioSistema responsavelProximaTarefa, Long idFamilia,
                                        Long idOperadorResponsavel, String dadosCertidao,
                                        Long idFormularioEmitido, String cartorio,
                                        String numeroAR, String observacoesInternas) {

        //Verifica se se trata de uma reemissao de um formulario de pedido de certidao ja emitido anteriormente e,
        //neste caso, apaga o processo gerado anteriormente
        if (idFormularioEmitido) {
            HistoricProcessInstance processoDuplicado = getQuery(idFormularioEmitido: idFormularioEmitido).singleResult()
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
        variables.put(PedidoCertidaoProcessoDTO.VARIABLE_OBSERVACOES_INTERNAS, observacoesInternas)

        return super._novoProcesso(responsavelProximaTarefa, variables);
    }

    //Erro nullpointer do framework caso se defina @Transactional explicitamente nos metodos desta classe (que foi especializada de outra classe que
    // implementa um serviço). Bug do grails, nao consegui identificar o porque ???
    public List<PedidoCertidaoProcessoDTO> getProcessos(Map params) {
        HistoricProcessInstanceQuery query = getQuery(params)
        List<HistoricProcessInstance> processInstances = query.orderByProcessInstanceStartTime().asc().listPage(0, ProcessoDTO.MAX_PAGINACAO)

        List<PedidoCertidaoProcessoDTO> result = []
        processInstances.each { HistoricProcessInstance processInstance ->
            PedidoCertidaoProcessoDTO processo = getProcesso(processInstance.id, false)
            result.add(processo)
        }

        return result
    }

    //Erro nullpointer do framework caso se defina @Transactional explicitamente nos metodos desta classe (que foi especializada de outra classe que
    // implementa um serviço). Bug do grails, nao consegui identificar o porque ???
    public void grava(String idProcesso, String numeroAR, String observacoesInternas) {
        runtimeService.setVariable(idProcesso, PedidoCertidaoProcessoDTO.VARIABLE_NUMERO_AR, numeroAR)
        runtimeService.setVariable(idProcesso, PedidoCertidaoProcessoDTO.VARIABLE_OBSERVACOES_INTERNAS, observacoesInternas)
    }

    //Erro nullpointer do framework caso se defina @Transactional explicitamente nos metodos desta classe (que foi especializada de outra classe que
    // implementa um serviço). Bug do grails, nao consegui identificar o porque ???
    public List<PedidoCertidaoProcessoDTO> pedidosCertidaoPendentes(long idFamilia) {
        if ((! idFamilia) || (! segurancaService.acessoRecursoServico(RecursosServico.PEDIDOS_CERTIDAO)))
            return []

        List<ProcessInstance> processInstances = runtimeService
                .createProcessInstanceQuery()
                .processDefinitionKey(getProcessDefinitionStr())
                .variableValueEquals(PedidoCertidaoProcessoDTO.VARIABLE_ID_FAMILIA, idFamilia.toString())
                .variableValueEquals(ProcessoDTO.VARIABLE_ID_SERVICO_SISTEMA_SEGURANCA, segurancaService.getServicoLogado().id.toString())
                .list();

        List<PedidoCertidaoProcessoDTO> result = []
        processInstances.each {
            result.add(getProcesso(it.id, true))
        }

        return result
    }

    //Erro nullpointer do framework caso se defina @Transactional explicitamente nos metodos desta classe (que foi especializada de outra classe que
    // implementa um serviço). Bug do grails, nao consegui identificar o porque ???
    public String getIdProcessoPeloFormularioEmitido(Long idFormularioEmitido) {
        List<HistoricProcessInstance> processos = getQuery(idFormularioEmitido: idFormularioEmitido).list()
        if (processos.size() == 0)
            return null
        else if (processos.size() > 1)
            log.fatal("Erro. Mais de um processo gerado para o mesmo idFormulario ${idFormularioEmitido} / servicoSistema ${segurancaService.servicoLogado.id}")
        return processos[0].id
    }

    @Override
    protected HistoricProcessInstanceQuery getQuery(Map filtros) {
        HistoricProcessInstanceQuery result = super.getQuery(filtros)

        //Filtros opcionais:
        if (filtros.idFormularioEmitido)
            result = result.variableValueEquals(PedidoCertidaoProcessoDTO.VARIABLE_ID_FORMULARIO_EMTIDO, filtros.idFormularioEmitido.toString())
        if (filtros.idFamilia)
            result = result.variableValueEquals(PedidoCertidaoProcessoDTO.VARIABLE_ID_FAMILIA, filtros.idFamilia.toString())
        else if (filtros.cad) {
            Familia familia = cidadaoService.obtemFamiliaPeloCad(filtros.cad, false)
            result = result.variableValueEquals(PedidoCertidaoProcessoDTO.VARIABLE_ID_FAMILIA, familia ? familia.id.toString() : "-1"/*nao listara nenhum processo*/)
        }
        if (filtros.dadosCertidao)
            result = result.variableValueLike(PedidoCertidaoProcessoDTO.VARIABLE_DADOS_CERTIDAO, "%"+filtros.dadosCertidao.toUpperCase()+"%")
        if (filtros.idUsuarioSistema)
            result = result.variableValueEquals(PedidoCertidaoProcessoDTO.VARIABLE_ID_OPERADOR_RESPONSAVEL, filtros.idUsuarioSistema.toString())
        if (filtros.numeroAR)
            result = result.variableValueEquals(PedidoCertidaoProcessoDTO.VARIABLE_NUMERO_AR, "%"+filtros.numeroAR.toString().toUpperCase()+"%")
        if (filtros.cartorio)
            result = result.variableValueLike(PedidoCertidaoProcessoDTO.VARIABLE_CARTORIO, "%"+filtros.cartorio.toUpperCase()+"%")

        return result
    }
}
