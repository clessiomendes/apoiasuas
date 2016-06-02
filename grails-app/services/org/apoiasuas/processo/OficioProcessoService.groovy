package org.apoiasuas.processo

import org.apoiasuas.cidadao.Familia
import org.apoiasuas.seguranca.UsuarioSistema
import org.camunda.bpm.engine.history.HistoricProcessInstance
import org.camunda.bpm.engine.runtime.ProcessInstance

/**
 * Created by admin on 21/05/2016.
 */
class OficioProcessoService extends ProcessoService {

    @Override
    public String getProcessDefinitionStr() {
        return  "oficio"
    }

    @Override
    protected ProcessoDTO preencheProcessoDTOHistorico(ProcessoDTO processoDTO, HistoricProcessInstance processInstance) {
        OficioProcessoDTO oficioProcessoDTO = processoDTO

        super.preencheProcessoDTOHistorico(oficioProcessoDTO, processInstance)

        String idFamilia = getHistoricVariable(processInstance.id, PedidoCertidaoProcessoDTO.VARIABLE_ID_FAMILIA)
        oficioProcessoDTO.familia = idFamilia ? Familia.get(idFamilia.toLong()) : null

        String operadorAutor = getHistoricVariable(processInstance.id, PedidoCertidaoProcessoDTO.VARIABLE_ID_OPERADOR_RESPONSAVEL)
        oficioProcessoDTO.operadorAutor = operadorAutor ? UsuarioSistema.get(operadorAutor.toLong()) : null

        oficioProcessoDTO.destinatario = getHistoricVariable(processInstance.id, PedidoCertidaoProcessoDTO.VARIABLE_DADOS_CERTIDAO)

        oficioProcessoDTO.numeroOficio = getHistoricVariable(processInstance.id, PedidoCertidaoProcessoDTO.VARIABLE_NUMERO_AR)

        oficioProcessoDTO.titulo = getHistoricVariable(processInstance.id, PedidoCertidaoProcessoDTO.VARIABLE_NUMERO_AR)

        return oficioProcessoDTO
    }


    public ProcessInstance novoProcesso(UsuarioSistema responsavelProximaTarefa, Long idFamilia, Long idOperadorAutor, String destinatario, String titulo, String numeroOficio) {
        //Informações específicas desta definicao de processo
        Map variables = [:];
        variables.put(OficioProcessoDTO.VARIABLE_ID_FAMILIA, idFamilia?.toString())
        variables.put(OficioProcessoDTO.VARIABLE_ID_OPERADOR_AUTOR, idOperadorAutor?.toString())
        variables.put(OficioProcessoDTO.VARIABLE_DESTINATARIO, destinatario)
        variables.put(OficioProcessoDTO.VARIABLE_TITULO, titulo)
        variables.put(OficioProcessoDTO.VARIABLE_NUMERO_OFICIO, numeroOficio)

        return super._novoProcesso(responsavelProximaTarefa, variables);
    }

}
