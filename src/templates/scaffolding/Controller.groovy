<%=packageName ? "package ${packageName}\n\n" : ''%>


import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.seguranca.DefinicaoPapeis

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Secured([DefinicaoPapeis.USUARIO])
class ${className}Controller {

    static defaultAction = "list"

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        render view: "list", model:[${propertyName}List:${className}.list(params), ${propertyName}Count: ${className}.count()]
    }

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
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
        render view:"edit", model: [${propertyName}:${propertyName}]
    }

    def save(${className} ${propertyName}) {
        boolean modoCriacao = ${propertyName}.id == null

        if (${propertyName} == null) {
            notFound()
            return
        }

        if (! #implementarServico.grava(${propertyName})) {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: [${propertyName}:${propertyName}])
        }
        flash.message = message(code: 'default.created.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), ${propertyName}.id])
        render view: "show", model: [${propertyName}: ${propertyName}]
    }

    def delete(${className} ${propertyName}) {
        if (! ${propertyName})
            return notFound()

        #implementarServico.apaga(${propertyName})

        flash.message = message(code: 'default.deleted.message', args: [message(code: '${className}.label', default: '${className}'), ${propertyName}.id])
        redirect action:"list"
    }

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), params.id])
        redirect action: "list"
    }
}
