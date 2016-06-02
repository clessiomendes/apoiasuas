package org.apoiasuas.seguranca

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.codehaus.groovy.grails.commons.GrailsControllerClass

@Secured([DefinicaoPapeis.STR_SUPER_USER])
class UsuarioSistemaController extends AncestralController {

    def beforeInterceptor = [action: this.&interceptaSeguranca, entity:UsuarioSistema.class, only: ['show','edit', 'delete', 'update', 'save']]
    static defaultAction = "list"

    SegurancaService segurancaService

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def show(UsuarioSistema usuarioSistemaInstance) {
        if (! usuarioSistemaInstance)
            return notFound()
        preenchePapel(usuarioSistemaInstance)
        render view: "show", model: [usuarioSistemaInstance:usuarioSistemaInstance]
    }

    private void preenchePapel(UsuarioSistema usuarioSistemaInstance) {
        final papeis = segurancaService.getPapeisUsuario(usuarioSistemaInstance)
        usuarioSistemaInstance.papel = papeis ? papeis.first().authority : null
    }

    def list(FiltroUsuarioSistemaCommand filtro) {
        params.offset = params.offset ?: 0
        params.max = params.max ?: 20

        def listUsuarios = segurancaService.listUsuarios(filtro, params.offset, params.max)
        render view: "list", model:[usuarioSistemaInstanceList: listUsuarios, usuarioSistemaInstanceCount: listUsuarios.getTotalCount(), servicosDisponiveis: ServicoSistema.listOrderByNome(), filtro: params.findAll { it.value }]
    }

    def create(UsuarioSistema usuarioSistemaInstance) {
        if (! usuarioSistemaInstance)
            usuarioSistemaInstance = new UsuarioSistema(params)
        usuarioSistemaInstance.enabled = true
        render view:"create", model: [usuarioSistemaInstance:usuarioSistemaInstance, servicosDisponiveis: ServicoSistema.listOrderByNome()]
    }

    def edit(UsuarioSistema usuarioSistemaInstance) {
        if (! usuarioSistemaInstance)
            return notFound()
        preenchePapel(usuarioSistemaInstance)
        render view:"edit", model: [usuarioSistemaInstance:usuarioSistemaInstance, servicosDisponiveis: ServicoSistema.listOrderByNome()]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def alteraPerfil(UsuarioSistema usuarioSistemaInstance) {
        if (! usuarioSistemaInstance)
            return notFound()
        preenchePapel(usuarioSistemaInstance)
        render view:"edit", model: [usuarioSistemaInstance:usuarioSistemaInstance]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def save(UsuarioSistema usuarioSistemaInstance) {
        if (! usuarioSistemaInstance)
            return notFound()

        boolean modoCriacao = usuarioSistemaInstance.id == null

        if (! usuarioSistemaInstance.validate())
//            return render(view: modoCriacao ? "create" : "edit" , model: [usuarioSistemaInstance:usuarioSistemaInstance, servicosDisponiveis: ServicoSistema.listOrderByNome()])
            return modoCriacao ? create(usuarioSistemaInstance) : edit(usuarioSistemaInstance)
        else //Grava
            segurancaService.gravaUsuario(usuarioSistemaInstance, params.get("password1"), params.get("password2"))
//        if (! segurancaService.gravaUsuario(usuarioSistemaInstance, params.get("password1"), params.get("password2"))) {
            //exibe o formulario novamente em caso de problemas na validacao
//            return render(view: modoCriacao ? "create" : "edit" , model: [usuarioSistemaInstance:usuarioSistemaInstance])

        flash.message = message(code: 'default.updated.message', args: [message(code: 'usuarioSistema.label', default: 'Operador do sistema'), usuarioSistemaInstance.id])
        render view: "show", model: [usuarioSistemaInstance: usuarioSistemaInstance]
    }

    def delete(UsuarioSistema usuarioSistemaInstance) {
        if (! usuarioSistemaInstance)
            return notFound()

        //Remove
        if (! segurancaService.apagaUsuario(usuarioSistemaInstance)) {
            //exibe o formulario novamente em caso de problemas na validacao
            preenchePapel(usuarioSistemaInstance)
            return render(view:"show", model: [usuarioSistemaInstance:usuarioSistemaInstance])
        }

        flash.message = message(code: 'default.deleted.message', args: [message(code: 'usuarioSistema.label', default: 'Usuário do sistema'), usuarioSistemaInstance.id])
        redirect action:"list"
    }

    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'usuarioSistema.label', default: 'Usuário do sistema'), params.id])
        return redirect(action: "list")
    }

    @Override
    protected interceptaSeguranca() {
        if (params?.getIdentifier() && ! segurancaService.isSuperUser()) {
            if (UsuarioSistema.get(params.getIdentifier())?.servicoSistemaSeguranca != segurancaService.servicoLogado) {
                flash.message = new AcessoNegadoPersistenceException(segurancaService.usuarioLogado.username, "Operador do sistema", UsuarioSistema.get(params.getIdentifier())?.username)
                redirect(controller: "menu")
                return false
            }
        }
    }

}

@grails.validation.Validateable
class FiltroUsuarioSistemaCommand implements Serializable {
    String nome
    String servicoSistema
}
