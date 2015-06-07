<%=packageName ? "package ${packageName}\n\n" : ''%>

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

class ${className}Controller {

    static defaultAction = "list"
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        render view: "list", model:[${propertyName}InstanceList:${className}.list(params), ${propertyName}Count: ${className}.count()]
    }

    def show(${className} ${propertyName}) {
        if (! ${propertyName})
            return notFound()
        render view: "show", model: [${propertyName}Instance:${propertyName}Instance]
    }

    def create() {
        render view:"create", model: [${propertyName}Instance:new ${className}(params)]
    }

    def edit(${className} ${propertyName}) {
        if (! ${propertyName})
            return notFound()
        render view:"edit", model: [${propertyName}Instance:${propertyName}Instance]
    }

    def save(${className} ${propertyName}) {
        boolean modoCriacao = ${propertyName}.id == null

        if (${propertyName} == null) {
            notFound()
            return
        }

        if (! $implementarServico$.grava(${propertyName})) {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: [${propertyName}:${propertyName}])
        }
        flash.message = message(code: 'default.created.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), ${propertyName}.id])
        render view: "show", model: [${propertyName}Instance: ${propertyName}Instance]
    }

    def delete(${className} ${propertyName}) {
        if (! ${propertyName})
            return notFound()

        $implementarServico$.apaga(${propertyName})

        flash.message = message(code: 'default.deleted.message', args: [message(code: '${className}.label', default: '${className}'), ${propertyName}.id])
        redirect action:"list"
    }

    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), params.id])
        redirect action: "list"
    }
}
