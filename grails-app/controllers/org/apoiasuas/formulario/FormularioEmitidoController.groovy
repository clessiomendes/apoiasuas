package org.apoiasuas.formulario


import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class FormularioEmitidoController {

    def create() {
        respond new FormularioEmitido(params)
    }

    @Transactional
    def save(FormularioEmitido formularioEmitidoInstance) {
        if (formularioEmitidoInstance == null) {
            notFound()
            return
        }

        if (formularioEmitidoInstance.hasErrors()) {
            respond formularioEmitidoInstance.errors, view: 'create'
            return
        }

        formularioEmitidoInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'formularioEmitido.label', default: 'FormularioEmitido'), formularioEmitidoInstance.id])
                redirect formularioEmitidoInstance
            }
            '*' { respond formularioEmitidoInstance, [status: CREATED] }
        }
    }

    def edit(FormularioEmitido formularioEmitidoInstance) {
        respond formularioEmitidoInstance
    }

    @Transactional
    def update(FormularioEmitido formularioEmitidoInstance) {
        if (formularioEmitidoInstance == null) {
            notFound()
            return
        }

        if (formularioEmitidoInstance.hasErrors()) {
            respond formularioEmitidoInstance.errors, view: 'edit'
            return
        }

        formularioEmitidoInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'FormularioEmitido.label', default: 'FormularioEmitido'), formularioEmitidoInstance.id])
                redirect formularioEmitidoInstance
            }
            '*' { respond formularioEmitidoInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(FormularioEmitido formularioEmitidoInstance) {

        if (formularioEmitidoInstance == null) {
            notFound()
            return
        }

        formularioEmitidoInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'FormularioEmitido.label', default: 'FormularioEmitido'), formularioEmitidoInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'formularioEmitido.label', default: 'FormularioEmitido'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
