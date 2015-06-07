package org.apoiasuas.cidadao

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.seguranca.DefinicaoPapeis

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
@Secured([DefinicaoPapeis.USUARIO_LEITURA])
class FamiliaController extends AncestralController {

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        render view: 'list', model: [familiaInstanceList: Familia.list(params), familiaInstanceCount: Familia.count()]
//        respond Familia.list(params), model: [familiaInstanceCount: Familia.count()]
    }

    def show(Familia familiaInstance) {
        guardaUltimoSelecionado(null, familiaInstance)
        render view: 'show', model: [familiaInstance: familiaInstance]
    }

/*
    def create() {
        respond new Familia(params)
    }
*/

    @Transactional
    def save(Familia familiaInstance) {
        if (familiaInstance == null) {
            notFound()
            return
        }

        if (familiaInstance.hasErrors()) {
            respond familiaInstance.errors, view: 'create'
            return
        }

        familiaInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'familia.label', default: 'Familia'), familiaInstance.id])
                redirect familiaInstance
            }
            '*' { respond familiaInstance, [status: CREATED] }
        }
    }

    def edit(Familia familiaInstance) {
        render view: 'edit', model: [familiaInstance: familiaInstance]
    }

    @Transactional
    def update(Familia familiaInstance) {
        if (familiaInstance == null) {
            notFound()
            return
        }

        if (familiaInstance.hasErrors()) {
            respond familiaInstance.errors, view: 'edit'
            return
        }

        familiaInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Familia.label', default: 'Familia'), familiaInstance.id])
                redirect familiaInstance
            }
            '*' { respond familiaInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Familia familiaInstance) {

        if (familiaInstance == null) {
            notFound()
            return
        }

        familiaInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Familia.label', default: 'Familia'), familiaInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'familia.label', default: 'Familia'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

}
