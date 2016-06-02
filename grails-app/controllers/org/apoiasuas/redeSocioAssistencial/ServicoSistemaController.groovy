package org.apoiasuas.redeSocioAssistencial

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.SegurancaService

import javax.servlet.http.HttpSession

@Secured([DefinicaoPapeis.STR_SUPER_USER])
class ServicoSistemaController extends AncestralServicoController {

    private static final String SESSION_SERVICO_SISTEMA_ATUAL = "SESSION_SERVICO_SISTEMA_ATUAL"

    static defaultAction = "list"
    SegurancaService segurancaService
    ServicoSistemaService servicoSistemaService

    def list(Integer max) {
        params.max = max ?: 20
        render view: 'list', model: [servicoSistemaInstanceList: ServicoSistema.listOrderByNome(params), servicoSistemaInstanceCount: ServicoSistema.count()]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def editCurrent() {
        return edit(segurancaService.servicoLogado)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def edit(ServicoSistema servicoSistemaInstance) {
        if (servicoSistemaInstance == null)
            servicoSistemaInstance = segurancaService.servicoLogado
        //evitar erros de lazy initialization
        servicoSistemaInstance = servicoSistemaService.get(servicoSistemaInstance.id)
        render view: 'edit', model: [servicoSistemaInstance: servicoSistemaInstance, territoriosDisponiveis: getAreasAtuacaoDisponiveis(servicoSistemaInstance.abrangenciaTerritorial)]
    }

    def create() {
        ServicoSistema servicoSistema = new ServicoSistema(params)
        servicoSistema.habilitado = true
//        servicoSistema.abrangenciaTerritorial = servicoSistemaService.getServicoSistemaReadOnly().abrangenciaTerritorial
        render view: 'create', model: [servicoSistemaInstance: servicoSistema, territoriosDisponiveis: getAreasAtuacaoDisponiveis(null)]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def show(ServicoSistema servicoSistemaInstance) {
        if (! servicoSistemaInstance)
            return notFound()
        render view: 'show', model: [
                servicoSistemaInstance: servicoSistemaInstance,
                hierarquiaTerritorial: abrangenciaTerritorialService.JSONhierarquiaTerritorial(servicoSistemaInstance.abrangenciaTerritorial)
        ]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def save(ServicoSistema servicoSistemaInstance) {
        if (! servicoSistemaInstance)
            return notFound()

        boolean modoCriacao = servicoSistemaInstance.id == null

        if (modoCriacao && ! segurancaService.superUser) {
            flash.message = "Você não tem permissão para criar novo Serviço com acesso ao sistema";
            return show(servicoSistemaInstance)
        }

        atribuiAbrangenciaTerritorial(servicoSistemaInstance)

        //Grava
        if (! servicoSistemaInstance.validate()) {
            //exibe o formulario novamente em caso de problemas na validacao
            return show(servicoSistemaInstance)
        }

        //Grava
        servicoSistemaService.grava(servicoSistemaInstance)
        flash.message = "Dados do serviço ${servicoSistemaInstance.nome} atualizados"
        if (segurancaService.isSuperUser())
            return show(servicoSistemaInstance)
        else {
            return edit(servicoSistemaInstance)
        }

//        if (servicoSistemaInstance.id == getSessionServicoSistemaAtual(session).id) //Atualiza
//            setSessionServicoSistemaAtual(session, servicoSistemaService.getServicoSistemaReadOnly());
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'servico.label', default: 'Servico'), params.id])
        return redirect(action: "list")
    }
/*
    public static void setSessionServicoSistemaAtual(HttpSession session, ServicoSistema servicoSistema) {
        log.debug("guardando servico sistema ${servicoSistema.nome}")
        session[SESSION_SERVICO_SISTEMA_ATUAL] = servicoSistema
    }

    public static ServicoSistema getSessionServicoSistemaAtual(HttpSession session) {
        return (ServicoSistema) session[SESSION_SERVICO_SISTEMA_ATUAL];
    }
*/
}
