<%=packageName ? "package ${packageName}\n\n" : ''%>


import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.seguranca.DefinicaoPapeis

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Secured([DefinicaoPapeis.STR_USUARIO])
class ${className}Controller extends AncestralController {

    static defaultAction = "list"

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        render view: "list", model:[${propertyName}List:${className}.list(params), ${propertyName}Count: ${className}.count()]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def show(${className} ${propertyName}) {
        if (! ${propertyName})
            return notFound()
        render view: "show", model: [${propertyName}:${propertyName}]
    }

    def create() {
        render view:"create", model: [${propertyName}:new ${className}(params)]
    }

    def edit(${className} ${propertyName}) {
        if (! ${propertyName})
            return notFound()
        render view:"edit", model: getEditCreateModel(${propertyName})
    }

    private Map getEditCreateModel(${className} ${propertyName}) {
        //adicione aqui mais entidades a serem disponibilizadas nas telas de criacao e edicao
        return [${propertyName}:${propertyName}]
    }

    def save(${className} ${propertyName}) {
        boolean modoCriacao = ${propertyName}.id == null

        if (${propertyName} == null) {
            notFound()
            return
        }

        if (${propertyName}.validate()) {
            #implementarServico.grava(${propertyName})
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: [${propertyName}:${propertyName}])
        }
        flash.message = message(code: modoCriacao ? 'default.created.message' : "default.updated.message", args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), ${propertyName}.id])
        render view: "show", model: [${propertyName}: ${propertyName}]
    }

    def delete(${className} ${propertyName}) {
        if (! ${propertyName})
            return notFound()

        #implementarServico.apaga(${propertyName})

        flash.message = message(code: 'default.deleted.message', args: [message(code: '${className}.label', default: '${className}'), ${propertyName}.id])
        redirect action:"list"
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), params.id])
        redirect action: "list"
    }
}
