package org.apoiasuas.seguranca

import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.*

@Secured([DefinicaoPapeis.SUPER_USER])
class UsuarioSistemaController {

    static defaultAction = "list"

    def segurancaService

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def show(UsuarioSistema usuarioSistemaInstance) {
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
        preenchePapel(usuarioSistemaInstance)
        render view:"edit", model: [usuarioSistemaInstance:usuarioSistemaInstance]
    }

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def alteraPerfil(UsuarioSistema usuarioSistemaInstance) {
        preenchePapel(usuarioSistemaInstance)
        render view:"edit", model: [usuarioSistemaInstance:usuarioSistemaInstance]
    }

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def save(UsuarioSistema usuarioSistemaInstance) {
        boolean modoCriacao = usuarioSistemaInstance.id == null

        if (usuarioSistemaInstance == null) {
            notFound()
            return
        }

        //Grava
        if (! segurancaService.gravaUsuario(usuarioSistemaInstance, params.get("password1"), params.get("password2"))) {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: [usuarioSistemaInstance:usuarioSistemaInstance])
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'UsuarioSistema.label', default: 'Operador do sistema'), usuarioSistemaInstance.id])
        render view: "show", model: [usuarioSistemaInstance: usuarioSistemaInstance]
    }

    def delete(UsuarioSistema usuarioSistemaInstance) {

        if (usuarioSistemaInstance == null) {
            notFound()
            return
        }

        //Remove
        if (! segurancaService.apagaUsuario(usuarioSistemaInstance)) {
            //exibe o formulario novamente em caso de problemas na validacao
            preenchePapel(usuarioSistemaInstance)
            return render(view:"show", model: [usuarioSistemaInstance:usuarioSistemaInstance])
        }

        request.withFormat {
            form multipartForm {
                //Resposta padr�o para posts html
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'UsuarioSistema.label', default: 'Usuário do sistema'), usuarioSistemaInstance.id])
                redirect action:"list", method:"GET"
            }
            '*'{ render status: NO_CONTENT } //Resposta para restfull applications
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                //Resposta padr�o para posts html
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'UsuarioSistema.label', default: 'Usuário do sistema'), params.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND } //Resposta para restfull applications
        }
    }
}
