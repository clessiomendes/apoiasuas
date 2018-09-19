package org.apoiasuas.redeSocioAssistencial

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.seguranca.DefinicaoPapeis

@Secured([DefinicaoPapeis.STR_SUPER_USER])
class ServicoSistemaController extends AncestralServicoController {

    private static final String SESSION_SERVICO_SISTEMA_ATUAL = "SESSION_SERVICO_SISTEMA_ATUAL"

    static defaultAction = "list"
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
        render view: 'edit', model: getModelEdicao(servicoSistemaInstance)
    }

    def create() {
        ServicoSistema servicoSistema = new ServicoSistema(params)
        servicoSistema.habilitado = true
//        servicoSistema.abrangenciaTerritorial = servicoSistemaService.getServicoSistemaReadOnly().abrangenciaTerritorial
        render view: 'create', model: getModelEdicao(servicoSistema)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def show(ServicoSistema servicoSistemaInstance) {
        if (! servicoSistemaInstance)
            return notFound()
        render view: 'show', model: getModelExibicao(servicoSistemaInstance)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def save(ServicoSistema servicoSistemaInstance) {
        if (! servicoSistemaInstance)
            return notFound()

        boolean modoCriacao = servicoSistemaInstance.id == null

        if (modoCriacao && ! segurancaService.isSuperUser()) {
            flash.message = "Você não tem permissão para criar novo Serviço com acesso ao sistema";
            return render(view: 'show', model: getModelExibicao(servicoSistemaInstance))
        }

        //Converte os checkboxes selecionados no formulario em uma string separada por virgulas de codigos de RecursosServico
        servicoSistemaInstance.recursos = params.recursos.findAll{RecursosServico.contains(it.key)}.collect{it.key}.join(",");

        servicoSistemaInstance.abrangenciaTerritorial = atribuiAbrangenciaTerritorial();

        //Validações:
        boolean validado = servicoSistemaInstance.validate();
        validado = validado & validaVersao(servicoSistemaInstance);

        if (! validado) {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: 'edit', model: getModelExibicao(servicoSistemaInstance))
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

    private LinkedHashMap<String, Object> getModelEdicao(ServicoSistema servicoSistemaInstance) {
        [servicoSistemaInstance: servicoSistemaInstance, JSONAbrangenciaTerritorial: getAbrangenciasTerritoriaisEdicao(servicoSistemaInstance?.abrangenciaTerritorial)]
    }

    private Map<String, Object> getModelExibicao(ServicoSistema servicoSistemaInstance) {
        [servicoSistemaInstance: servicoSistemaInstance,
         JSONAbrangenciaTerritorial: getAbrangenciasTerritoriaisExibicao(servicoSistemaInstance.abrangenciaTerritorial)]
    }
}
