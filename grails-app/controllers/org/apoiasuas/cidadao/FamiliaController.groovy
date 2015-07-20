package org.apoiasuas.cidadao

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.programa.Programa
import org.apoiasuas.seguranca.DefinicaoPapeis

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Secured([DefinicaoPapeis.USUARIO_LEITURA])
class FamiliaController extends AncestralController {

    def familiaService
    def segurancaService

    def index(Integer max) {
        redirect(controller: 'cidadao', action: 'procurarCidadao')
//        render view: 'list', model: [familiaInstanceList: Familia.list(params), familiaInstanceCount: Familia.count()]
    }

    def show(Familia familiaInstance) {
        if (! familiaInstance)
            return notFound()

        guardaUltimaFamiliaSelecionada(familiaInstance)
        render view: 'show', model: [familiaInstance: familiaInstance]
    }

/*
    def create() {
        respond new Familia(params)
    }
*/

    def save(Familia familiaInstance, ProgramasCommand programasCommand) {
        if (! familiaInstance)
            return notFound()

        boolean modoCriacao = familiaInstance.id == null

        //Grava
        if (! familiaService.grava(familiaInstance, programasCommand)) {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: [familiaInstance:familiaInstance])
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'familia.label', default: 'Família'), familiaInstance.id])
        return show(familiaInstance)
    }

    def edit(Familia familiaInstance) {
        def programasDisponiveis = Programa.all
        //Marca dentre os programas disponiveis, aqueles que estão atualmente associados à família
        programasDisponiveis.each { programaDisponivel ->
            programaDisponivel.selected = familiaInstance.programas.find { it.programa == programaDisponivel }
        }
        render view: 'edit', model: [familiaInstance: familiaInstance, programasDisponiveis: programasDisponiveis, operadores: segurancaService.getOperadoresOrdenados()]
    }

/*
    def delete(Familia familiaInstance) {
        if (! familiaInstance)
            return notFound()

        familiaService.apaga(familiaInstance)
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'Familia.label', default: 'Família'), familiaInstance.id])
        redirect action:"index"

        if (familiaInstance == null) {
            notFound()
            return
        }
    }
*/

    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'Familia.label', default: 'Família'), params.id])
        return redirect(controller: 'cidadao', action: 'procurarCidadao')
    }

    def obtemLogradouros(String term) {
        if (term)
            render familiaService.procurarLogradouros(term) as JSON
    }

}

class ProgramasCommand {
    List<ProgramaCommand> programasdisponiveis = [].withLazyDefault { new ProgramaCommand() }
}

class ProgramaCommand {
    String id
    String selected
}
