package org.apoiasuas

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.marcador.Programa
import org.apoiasuas.seguranca.DefinicaoPapeis

@Secured([DefinicaoPapeis.STR_USUARIO])
class ProgramaAPAGARController extends AncestralController {

    static defaultAction = "list"
    ProgramaService programaService

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        render view: "list", model:[programaInstanceList:Programa.list(params), programaInstanceCount: Programa.count()]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def show(Programa programaInstance) {
        if (! programaInstance)
            return notFound()
        render view: "show", model: [programaInstance:programaInstance]
    }

    def create() {
        render view:"create", model: [programaInstance:new Programa(params)]
    }

    def edit(Programa programaInstance) {
        if (! programaInstance)
            return notFound()
        render view:"edit", model: [programaInstance:programaInstance]
    }

    def save(Programa programaInstance) {
        boolean modoCriacao = programaInstance.id == null

        if (programaInstance == null) {
            notFound()
            return
        }

        if (! programaService.grava(programaInstance)) {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: [programaInstance:programaInstance])
        }
        flash.message = message(code: 'default.created.message', args: [message(code: 'programa.label', default: 'Programa'), programaInstance.id])
        render view: "show", model: [programaInstance: programaInstance]
    }

    def delete(Programa programaInstance) {
        if (! programaInstance)
            return notFound()

        programaService.apaga(programaInstance)

        flash.message = message(code: 'default.deleted.message', args: [message(code: 'Programa.label', default: 'Programa'), programaInstance.id])
        redirect action:"list"
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'programa.label', default: 'Programa'), params.id])
        redirect action: "list"
    }
}
