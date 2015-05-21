package org.apoiasuas.atalhos

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.seguranca.DefinicaoPapeis

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Secured([DefinicaoPapeis.USUARIO_LEITURA])
@Transactional(readOnly = true)
class LinkController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def exibeLinks() {
        respond Link.list(params).sort { it.id }
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Link.list(params), model:[linkInstanceCount: Link.count()]
    }

    def show(Link linkInstance) {
        respond linkInstance
    }

    def create() {
        respond new Link(params)
    }

    @Transactional
    def save(Link linkInstance) {
        if (linkInstance == null) {
            notFound()
            return
        }

        if (linkInstance.hasErrors()) {
            respond linkInstance.errors, view:'create'
            return
        }

        linkInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'link.label', default: 'Link'), linkInstance.id])
                redirect linkInstance
            }
            '*' { respond linkInstance, [status: CREATED] }
        }
    }

    def edit(Link linkInstance) {
        respond linkInstance
    }

    @Transactional
    def update(Link linkInstance) {
        if (linkInstance == null) {
            notFound()
            return
        }

        if (linkInstance.hasErrors()) {
            respond linkInstance.errors, view:'edit'
            return
        }

        linkInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Link.label', default: 'Link'), linkInstance.id])
                redirect linkInstance
            }
            '*'{ respond linkInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Link linkInstance) {

        if (linkInstance == null) {
            notFound()
            return
        }

        linkInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Link.label', default: 'Link'), linkInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'link.label', default: 'Link'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
