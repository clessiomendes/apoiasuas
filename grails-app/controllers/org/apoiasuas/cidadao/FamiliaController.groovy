package org.apoiasuas.cidadao

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.processo.PedidoCertidaoProcessoDTO
import org.apoiasuas.programa.Programa
import org.apoiasuas.seguranca.DefinicaoPapeis

import javax.servlet.http.HttpSession

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class FamiliaController extends AncestralController {

    def beforeInterceptor = [action: this.&interceptaSeguranca, entity:Familia.class, only: ['show','edit', 'delete', 'update', 'save']]
    private static final String SESSION_ULTIMA_FAMILIA = "SESSION_ULTIMA_FAMILIA"
    def familiaService
    def pedidoCertidaoProcessoService

    def index(Integer max) {
        redirect(controller: 'cidadao', action: 'procurarCidadao')
//        render view: 'list', model: [familiaInstanceList: Familia.list(params), familiaInstanceCount: Familia.count()]
    }

    def show(Familia familiaInstance) {
        if (! familiaInstance)
            return notFound()

        List<PedidoCertidaoProcessoDTO> pedidosCertidaoPendentes = pedidoCertidaoProcessoService.pedidosCertidaoPendentes(familiaInstance.id)

        guardaUltimaFamiliaSelecionada(familiaInstance)
        render view: 'show', model: [familiaInstance: familiaInstance, pedidosCertidaoPendentes: pedidosCertidaoPendentes]
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
            return render(view: modoCriacao ? "create" : "edit" , model: getEditCreateModel(familiaInstance))
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'familia.label', default: 'Família'), familiaInstance.id])
        return show(familiaInstance)
    }

    private Map getEditCreateModel(Familia familiaInstance) {
        List<Programa> programasDisponiveis = Programa.all
        //Marca dentre os programas disponiveis, aqueles que estão atualmente associados à família
        programasDisponiveis.each { programaDisponivel ->
            programaDisponivel.selected = familiaInstance.programas.find { it.programa == programaDisponivel }
        }
        return [familiaInstance: familiaInstance, programasDisponiveis: programasDisponiveis, operadores: segurancaService.getOperadoresOrdenados()]
    }

    def edit(Familia familiaInstance) {
        render view: 'edit', model: getEditCreateModel(familiaInstance)
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
        else {
            response.status = 500
            return render ([errorMessage: "parametro vazio"] as JSON)
        }
    }

    public static Familia getUltimaFamilia(HttpSession session) {
        return session[SESSION_ULTIMA_FAMILIA]
    }

    public static void setUltimaFamilia(HttpSession session, Familia familia) {
        session[SESSION_ULTIMA_FAMILIA] = familia
    }
}

class ProgramasCommand {
    List<ProgramaCommand> programasdisponiveis = [].withLazyDefault { new ProgramaCommand() }
}

class ProgramaCommand {
    String id
    String selected
}
