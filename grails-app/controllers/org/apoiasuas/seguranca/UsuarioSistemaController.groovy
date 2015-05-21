package org.apoiasuas.seguranca

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.seguranca.UsuarioSistema

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Secured([DefinicaoPapeis.SUPER_USER])
class UsuarioSistemaController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    static defaultAction = "list"

// Servicos ==========>>
    def importarFamiliasJavaService
    def daoServiceForJava
    def segurancaService
// <<========== Servi�os

    def afterInterceptor = { model, modelAndView ->
        println "Current view is ${modelAndView?.viewName}"
//		if (model.someVar) modelAndView.viewName = "/mycontroller/someotherview"
//		println "View is now ${modelAndView.viewName}"
    }

    def show(UsuarioSistema usuarioSistemaInstance) {
        preenchePapel(usuarioSistemaInstance)
        respond usuarioSistemaInstance
    }

    private void preenchePapel(UsuarioSistema usuarioSistemaInstance) {
        final papeis = segurancaService.getPapeisUsuario(usuarioSistemaInstance)
        usuarioSistemaInstance.papel = papeis ? papeis.first().authority : null
    }

    def teste() {
//		def classeJava = new TestJavaClass();
//		classeJava.faca();
        log.error "calling teste"
        importarFamiliasJavaService.excelStreamedReader(null);
        redirect action:"list"
    }

    def list(Integer max) {
        //Atualiza o parametro do request "max"
        //http://groovy.codehaus.org/Operators#Operators-ElvisOperator(?:)
        params.max = max ?: 20

        //Gerando resposta � partir de uma listagem de usuarioSistema filtrada por params
        //Parametro model define quaisquer outros objetos que se deseja passar MAS EXCLUSIVAMENTE PARA RESPOSTAS HTML
        //http://grails.org/doc/2.3.x/ref/Controllers/respond.html
        respond UsuarioSistema.list(params), model:[usuarioSistemaInstanceCount: UsuarioSistema.count()]
    }

    def create() {
        UsuarioSistema novoUsuario = new UsuarioSistema(params)
        respond novoUsuario
    }

    def save(UsuarioSistema usuarioSistemaInstance) {

        if (usuarioSistemaInstance == null) {
            notFound()
            return
        }

        if (usuarioSistemaInstance.hasErrors()) {
            respond usuarioSistemaInstance.errors, view:'create'
            return
        }

        segurancaService.gravaNovoUsuario(usuarioSistemaInstance)

//        //Define o papel no sistema de seguranca
//        Papel papel = Papel.findByAuthority(usuarioSistemaInstance.papel)
//        new UsuarioSistemaPapel(usuarioSistema: usuarioSistemaInstance, papel: papel).save()

        request.withFormat {
            form multipartForm {
                //Resposta padr�o para posts html
                flash.message = message(code: 'default.created.message', args: [message(code: 'UsuarioSistema.label', default: 'Usuário do sistema'), usuarioSistemaInstance.id])
                redirect usuarioSistemaInstance
            }
            '*' { respond usuarioSistemaInstance, [status: CREATED] } //Resposta para restfull applications
        }
    }

    def edit(UsuarioSistema usuarioSistemaInstance) {
        preenchePapel(usuarioSistemaInstance)
        respond usuarioSistemaInstance
    }

    def update(UsuarioSistema usuarioSistemaInstance) {
        if (usuarioSistemaInstance == null) {
            notFound()
            return
        }

        if (usuarioSistemaInstance.hasErrors()) {
            respond usuarioSistemaInstance.errors, view:'edit'
            return
        }

        segurancaService.atualizaUsuario(usuarioSistemaInstance)

        request.withFormat {
            form multipartForm {
                //Resposta padrao para posts html
                flash.message = message(code: 'default.updated.message', args: [message(code: 'UsuarioSistema.label'), usuarioSistemaInstance.id])
                redirect usuarioSistemaInstance
            }
            '*'{ respond usuarioSistemaInstance, [status: OK] } //Resposta para restfull applications
        }
    }

    def delete(UsuarioSistema usuarioSistemaInstance) {

        if (usuarioSistemaInstance == null) {
            notFound()
            return
        }

        segurancaService.apagaUsuario(usuarioSistemaInstance)

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
