package org.apoiasuas

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.Servico
import org.apoiasuas.formulario.PreDefinidos
import org.apoiasuas.seguranca.DefinicaoPapeis

@Secured([DefinicaoPapeis.USUARIO])
class ServicoController {

    def servicoService
    def formularioService

    static defaultAction = "list"

    @Secured([DefinicaoPapeis.USUARIO_LEITURA])
    def getServico(Long idServico) {
        Servico servico = Servico.get(idServico)
        String endereco = ""
        if (! servico)
            render "" as JSON

        if (servico.endereco)
            endereco += servico.endereco.toString()
        if (servico.telefones) {
            String telefones = endereco ? ", telefone: " : "telefone: "
            endereco += servico.telefones ? telefones + servico.telefones : ""
        }
        render servico.properties + [enderecoCompleto: endereco] as JSON
    }

    def list(String palavraChave) {
        params.max = 20
        PagedResultList servicos = servicoService.procurarServico(palavraChave, params)
        render view: 'list', model: [servicoInstanceList: servicos, servicoInstanceCount: servicos.totalCount]
    }

    def show(Servico servicoInstance) {
        if (! servicoInstance)
            return notFound()
        render view: 'show', model: [servicoInstance: servicoInstance, formularioEncaminhamento: formularioService.getFormularioPreDefinido(PreDefinidos.ENCAMINHAMENTO)]
    }

    def create() {
        Servico servico = new Servico(params)
        servico.podeEncaminhar = true
        render view: 'create', model: [servicoInstance: servico]
    }

    @Secured([DefinicaoPapeis.USUARIO])
    def save(Servico servicoInstance) {
        if (! servicoInstance)
            return notFound()

        boolean modoCriacao = servicoInstance.id == null

        //Grava
        if (! servicoService.grava(servicoInstance)) {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: [servicoInstance:servicoInstance])
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'servico.label', default: 'Serviço'), servicoInstance.id])
        return show(servicoInstance)
    }

    def edit(Servico servicoInstance) {
        if (! servicoInstance)
            return notFound()
        render view: 'edit', model: [servicoInstance: servicoInstance]
    }

    def delete(Servico servicoInstance) {
        if (! servicoInstance)
            return notFound()

        servicoService.apaga(servicoInstance)
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'Servico.label', default: 'Servico'), servicoInstance.id])
        redirect action:"list"
    }

    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'servico.label', default: 'Servico'), params.id])
        return redirect(action: "list")
    }
}
