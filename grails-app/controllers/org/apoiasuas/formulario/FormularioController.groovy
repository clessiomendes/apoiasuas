package org.apoiasuas.formulario

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.docx4j.openpackaging.io.SaveToZipFile
import org.docx4j.openpackaging.packages.WordprocessingMLPackage

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Secured([DefinicaoPapeis.USUARIO])
class FormularioController {

    static defaultAction = "list"
    def formularioService

    @Transactional(readOnly = true)
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        render view: 'list', model: [formularioInstanceList: Formulario.listOrderByNome(params), formularioInstanceCount: Formulario.count()]
    }

    @Transactional(readOnly = true)
    def show(Formulario formularioInstance) {
        render view: 'show', model: [formularioInstance: formularioInstance]
    }

    def create() {
        render view: 'create', model: [formularioInstance: new Formulario(params)]
    }

    @Transactional
    def save(Formulario formularioInstance) {
        if (formularioInstance == null) {
            notFound()
            return
        }

        if (formularioInstance.hasErrors()) {
            respond formularioInstance.errors, view: 'create'
            return
        }

        formularioInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'formulario.label', default: 'Formulario'), formularioInstance.id])
                redirect formularioInstance
            }
            '*' { respond formularioInstance, [status: CREATED] }
        }
    }

    @Transactional(readOnly = true)
    def edit(Formulario formularioInstance) {
        render view: 'edit', model: [formularioInstance: formularioInstance]
    }

    def reinicializarFormulariosPreDefinidos() {
        formularioService.inicializaFormularios(null, true)
        flash.message = "Formulários reinicializados. Eventuas alterações feitas pelos operadores foram descartadas."
        forward(action: 'list')
    }

    def downloadTemplate(Formulario formulario) {
        response.contentType = 'application/octet-stream'
        response.setHeader 'Content-disposition', "attachment; filename=\"${formulario.geraNomeArquivo()}\""
        response.outputStream.write(formulario.template)
        response.outputStream.flush()
    }

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def simularTemplate(Formulario formularioInstance) {
        WordprocessingMLPackage word = formularioService.simularTemplate(formularioInstance)

        response.contentType = 'application/octet-stream'
        response.setHeader 'Content-disposition', "attachment; filename=\"${formularioInstance.geraNomeArquivo()}\""
        SaveToZipFile saver = new SaveToZipFile(word);
        saver.save( response.outputStream );
    }

    @Transactional
    def update(Formulario formularioInstance) {
        if (formularioInstance == null) {
            notFound()
            return
        }

        if (formularioInstance.hasErrors()) {
            respond formularioInstance.errors, view: 'edit'
            return
        }

        formularioInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Formulario.label', default: 'Formulario'), formularioInstance.id])
                redirect formularioInstance
            }
            '*' { respond formularioInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Formulario formularioInstance) {

        if (formularioInstance == null) {
            notFound()
            return
        }

        formularioInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Formulario.label', default: 'Formulario'), formularioInstance.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'formulario.label', default: 'Formulario'), params.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
