package org.apoiasuas

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.seguranca.AcessoNegadoPersistenceException
import org.apoiasuas.seguranca.DefinicaoPapeis
import grails.transaction.Transactional

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class LinkController extends AncestralController {

    static defaultAction = "list"
    LinkService linkService
    def beforeInterceptor = [action: this.&interceptaSeguranca/*("ola")*/, (ENTITY_CLASS_ENTRY):Link.class, only: ['show','edit', 'delete', 'update', 'save']]

    def exibeLinks() {
        respond Link.findAllByServicoSistemaSeguranca(getServicoCorrente(), params).sort { it.id }
//        respond Link.list(params).sort { it.id }
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Link.findAllByServicoSistemaSeguranca(getServicoCorrente(), params), model:[linkInstanceCount: Link.count()]
    }

    def show(Link linkInstance) {
        if (! linkInstance)
            return notFound()
        render view: 'show', model: [ linkInstance: linkInstance ]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def create() {
        Link link = new Link(params)
        respond link
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    @Transactional
    def save(Link linkInstance) {
        if (linkInstance.hasErrors()) {
            respond linkInstance.errors, view:'create'
            return
        }

        //Grava
        if (linkInstance.validate()) {
            linkService.grava(linkInstance)
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: "create", model: [linkInstance:linkInstance])
        }
        flash.message = message(code: 'default.created.message', args: [message(code: 'link.label'), linkInstance.toString()])
        return show(linkInstance)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def edit(Link linkInstance) {
        if (! linkInstance)
            return notFound()
        render view: 'edit', model: [linkInstance: linkInstance]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    @Transactional
    def update(Link linkInstance) {
        if (! linkInstance)
            return notFound()

        if (linkInstance.hasErrors()) {
            respond linkInstance.errors, view:'edit'
            return
        }

        //Grava
        if (linkInstance.validate()) {
            linkService.grava(linkInstance)
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: "show", model: [linkInstance:linkInstance])
        }
        flash.message = message(code: 'default.updated.message', args: [message(code: 'link.label'), linkInstance.toString()])
        return show(linkInstance)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    @Transactional
    def delete(Link linkInstance) {
        if (! linkInstance)
            return notFound()

        linkService.apaga(linkInstance)
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'link.label'), linkInstance.descricao])
        redirect action:"list"
    }

    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'link.label', default: 'Link'), params.id])
        return redirect(action: "list");
    }

}
