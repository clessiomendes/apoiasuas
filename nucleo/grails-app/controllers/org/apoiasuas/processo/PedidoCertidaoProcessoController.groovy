package org.apoiasuas.processo

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.camunda.bpm.engine.runtime.ProcessInstance

import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Created by admin on 28/05/2016.
 */
@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class PedidoCertidaoProcessoController extends ProcessoController {

    def pedidoCertidaoProcessoService
    static defaultAction = "preList"
    def cidadaoService

    /**
     * Exibe apenas os filtros, simulando um resultado vazio de processos "encontrados"
     */
    def preList() {
        render(view: "/processo/pedidoCertidao/list", model: [processos: [], ususariosDisponiveis: getOperadoresOrdenadosController(false)])
    }

    def list() {
        Long idUsuarioSistema = params.usuarioSistema ? params.usuarioSistema.toString().toLong() : null;
        Boolean pendentes = params.situacao == SITUACAO_PENDENTE ? true : params.situacao == SITUACAO_CONCLUIDO ? false : null
        Date dataInicio, dataFim
        boolean erroValidacao = false
        if (params.dataInicio)
            try {
                dataInicio = new SimpleDateFormat("dd/MM/yyyy").parse(params.dataInicio)
            } catch (ParseException e) {
                flash.message = "Erro interpretando informação ${params.dataInicio}. Formato esperado: dd/mm/yyyy"
                erroValidacao = true
            }
        if (params.dataFim)
            try {
                dataFim = new SimpleDateFormat("dd/MM/yyyy").parse(params.dataFim)
            } catch (ParseException e) {
                flash.message = "Erro interpretando informação ${params.dataFim}. Formato esperado: dd/mm/yyyy"
                erroValidacao = true
            }

        List<PedidoCertidaoProcessoDTO> processos = []
        if (! erroValidacao)
            processos = pedidoCertidaoProcessoService.getProcessos([cad: params.cad,
                    dadosCertidao: params.dadosCertidao, idUsuarioSistema: idUsuarioSistema, pendentes: pendentes,
                    numeroAR: params.numeroAR, cartorio: params.cartorio, dataInicio: dataInicio, dataFim: dataFim]);

        render(view: "/processo/pedidoCertidao/list", model: [processos: processos, ususariosDisponiveis: getOperadoresOrdenadosController(false)])
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def gravar(String numeroAR, String observacoesInternas, String id) {
        try {
            pedidoCertidaoProcessoService.grava(id, numeroAR, observacoesInternas)
        } catch (Exception e) {
            e.printStackTrace()
            return render(status: 200, text: '<div class="errors" role="status">Erro atualizando dados: '+e.message+'</div>')
        }
        render(status: 200/*success*/, text: '<div class="message" role="status">Pedido atualizado com sucesso</div>' )
    }

    /**
     * Define eventuais templates customizados para exibição dos campos a serem preenchidos
     */
    @Override
    protected String getTemplateProcessoEspecifico() {
        return "/processo/pedidoCertidao/showDetails"
    }
/*
    @Secured([DefinicaoPapeis.STR_USUARIO])
    def create() {
        pedidoCertidaoProcessoService.novoProcesso(segurancaService.usuarioLogado, 10, segurancaService.usuarioLogado.id, "nascimento de Fulana em 10/10/2010")
//        oficioProcessoService.novoProcesso(segurancaService.usuarioLogado, 10, segurancaService.usuarioLogado.id, "Juiza Maria", "visita a criança em situação de abandono", "01-2016")
        return list()
    }
*/
    @Secured([DefinicaoPapeis.STR_USUARIO])
    def create() {
        render(view: "/processo/pedidoCertidao/create", model: [processoInstance: new PedidoCertidaoProcessoDTO(), operadores: getOperadoresOrdenadosController(true)])
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveNew(PedidoCertidaoProcessoDTO processoDTO) {
        Familia familia = null
        //Valida cad digitado
        if (processoDTO.cadTransiente) {
            familia = cidadaoService.obtemFamiliaPeloCad(processoDTO.cadTransiente, false)
            if (! familia) {
                flash.message = "Erro! Cadastro de familia nao encontrado"
                return render(view: "/processo/pedidoCertidao/create", model: [processoInstance: processoDTO, operadores: getOperadoresOrdenadosController(true)])
            }
        }
        //Cria nova instancia do processo na engine BPM
        ProcessInstance novoProcesso = pedidoCertidaoProcessoService.novoProcesso(processoDTO.operadorResponsavel,
                familia?.id, processoDTO.operadorResponsavel.id, processoDTO.dadosCertidao, null, processoDTO.cartorio,
                processoDTO.numeroAR, processoDTO.observacoesInternas)

        //Mostra processo recem criado
        flash.message = "Pedido registrado com sucesso"
        redirect(action: 'mostraProcesso', id: novoProcesso.id)
    }

}
