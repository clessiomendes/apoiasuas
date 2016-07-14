package org.apoiasuas.seguranca

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.codehaus.groovy.grails.commons.GrailsControllerClass

@Secured([DefinicaoPapeis.STR_SUPER_USER])
class UsuarioSistemaController extends AncestralController {

    def usuarioSistemaService
    def beforeInterceptor = [action: this.&interceptaSeguranca, entity:UsuarioSistema.class, only: ['show','edit', 'delete', 'update', 'save']]
    static defaultAction = "list"

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

        def listUsuarios = usuarioSistemaService.listUsuarios(filtro, params.offset, params.max)
        render view: "list", model:[usuarioSistemaInstanceList: listUsuarios, usuarioSistemaInstanceCount: listUsuarios.getTotalCount(), servicosDisponiveis: ServicoSistema.listOrderByNome(), filtro: params.findAll { it.value }]
    }

    def create() {
        UsuarioSistema usuarioSistemaInstance = new UsuarioSistema(params)
        usuarioSistemaInstance.enabled = true
        render view:"create", model: getEditCreateModel(usuarioSistemaInstance)
    }

    def edit(UsuarioSistema usuarioSistemaInstance) {
        if (! usuarioSistemaInstance)
            return notFound()
        preenchePapel(usuarioSistemaInstance)
        render view:"edit", model: getEditCreateModel(usuarioSistemaInstance)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def alteraPerfil(UsuarioSistema usuarioSistemaInstance) {
        if (segurancaService.usuarioLogado.id != usuarioSistemaInstance.id)
            return notFound()
        edit(usuarioSistemaInstance)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def save(UsuarioSistema usuarioSistemaInstance) {
        if (! usuarioSistemaInstance)
            return notFound()

        boolean modoCriacao = usuarioSistemaInstance.id == null

//  ATENCAO! A validacao do usuario e feita dentro da chamada gravaUsuario, no servico
//        if (! usuarioSistemaInstance.validate())
        if (! usuarioSistemaService.gravaUsuario(usuarioSistemaInstance, params.get("password1"), params.get("password2")))
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: getEditCreateModel(usuarioSistemaInstance))

        flash.message = message(code: 'default.updated.message', args: [message(code: 'usuarioSistema.label', default: 'Operador do sistema'), usuarioSistemaInstance.id])
        render view: "show", model: [usuarioSistemaInstance: usuarioSistemaInstance]
    }

    def delete(UsuarioSistema usuarioSistemaInstance) {
        if (! usuarioSistemaInstance)
            return notFound()

        //Remove
        if (! usuarioSistemaService.apagaUsuario(usuarioSistemaInstance)) {
            //exibe o formulario novamente em caso de problemas na validacao
            preenchePapel(usuarioSistemaInstance)
            return render(view:"show", model: [usuarioSistemaInstance:usuarioSistemaInstance])
        }

        flash.message = message(code: 'default.deleted.message', args: [message(code: 'usuarioSistema.label', default: 'Usuário do sistema'), usuarioSistemaInstance.id])
        redirect action:"list"
    }

    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'usuarioSistema.label', default: 'Usuário do sistema'), params.id])
        return redirect(controller: "inicio")
    }

    @Override
    protected interceptaSeguranca() {
        if (params?.getIdentifier() && ! segurancaService.isSuperUser()) {
            if (UsuarioSistema.get(params.getIdentifier())?.servicoSistemaSeguranca?.id != segurancaService.servicoLogado?.id) {
                flash.message = new AcessoNegadoPersistenceException(segurancaService.usuarioLogado.username, "Operador do sistema", UsuarioSistema.get(params.getIdentifier())?.username)
                redirect(controller: "inicio")
                return false
            }
        }
    }

    private Map getEditCreateModel(UsuarioSistema usuarioSistemaInstance) {
        return [usuarioSistemaInstance:usuarioSistemaInstance, servicosDisponiveis: ServicoSistema.listOrderByNome()]
    }

}

@grails.validation.Validateable
class FiltroUsuarioSistemaCommand implements Serializable {
    String nome
    String servicoSistema
}
