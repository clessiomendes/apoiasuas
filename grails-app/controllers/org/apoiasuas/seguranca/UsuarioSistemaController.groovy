package org.apoiasuas.seguranca

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController

import static org.springframework.http.HttpStatus.*

@Secured([DefinicaoPapeis.SUPER_USER])
class UsuarioSistemaController extends AncestralController {

    static defaultAction = "list"

    SegurancaService segurancaService

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
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

    def list(Integer max) {
        //Atualiza o parametro do request "max"
        //http://groovy.codehaus.org/Operators#Operators-ElvisOperator(?:)
        params.max = max ?: 20

        //Gerando resposta � partir de uma listagem de usuarioSistema filtrada por params
        render view: "list", model:[usuarioSistemaInstanceList:UsuarioSistema.list(params), usuarioSistemaInstanceCount: UsuarioSistema.count()]
    }

    def create() {
        UsuarioSistema novoUsuario = new UsuarioSistema(params)
        novoUsuario.enabled = true
        render view:"create", model: [usuarioSistemaInstance:novoUsuario]
    }

    def edit(UsuarioSistema usuarioSistemaInstance) {
        if (! usuarioSistemaInstance)
            return notFound()
        preenchePapel(usuarioSistemaInstance)
        render view:"edit", model: [usuarioSistemaInstance:usuarioSistemaInstance]
    }

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def alteraPerfil(UsuarioSistema usuarioSistemaInstance) {
        if (! usuarioSistemaInstance)
            return notFound()
        preenchePapel(usuarioSistemaInstance)
        render view:"edit", model: [usuarioSistemaInstance:usuarioSistemaInstance]
    }

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def save(UsuarioSistema usuarioSistemaInstance) {
        if (! usuarioSistemaInstance)
            return notFound()

        boolean modoCriacao = usuarioSistemaInstance.id == null

        //Grava
        if (! segurancaService.gravaUsuario(usuarioSistemaInstance, params.get("password1"), params.get("password2"))) {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: [usuarioSistemaInstance:usuarioSistemaInstance])
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'UsuarioSistema.label', default: 'Operador do sistema'), usuarioSistemaInstance.id])
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

        flash.message = message(code: 'default.deleted.message', args: [message(code: 'UsuarioSistema.label', default: 'Usuário do sistema'), usuarioSistemaInstance.id])
        redirect action:"list"
    }

    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'UsuarioSistema.label', default: 'Usuário do sistema'), params.id])
        return redirect(action: "list")
    }
}
