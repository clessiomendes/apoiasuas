package org.apoiasuas.redeSocioAssistencial

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.formulario.FormularioService
import org.apoiasuas.formulario.PreDefinidos
import org.apoiasuas.seguranca.DefinicaoPapeis

@Secured([DefinicaoPapeis.STR_USUARIO])
class ServicoController extends AncestralServicoController {

    ServicoService servicoService
    FormularioService formularioService

    static defaultAction = "list"

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def getServico(Long idServico) {
        Servico servico = Servico.get(idServico)
        String endereco = ""
        if (! servico) {
            response.status = 500
            return render ([errorMessage: "Servico $idServico nao encontrado"] as JSON)
        }

        if (servico.endereco)
            endereco += servico.endereco.toString()
        if (servico.telefones) {
            String telefones = endereco ? ", telefone: " : "telefone: "
            endereco += servico.telefones ? telefones + servico.telefones : ""
        }
        render servico.properties + [enderecoCompleto: endereco] as JSON
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def list(String palavraChave) {
        params.max = 20
        PagedResultList servicos = servicoService.procurarServico(palavraChave, params)
        if (servicos && servicos.size() > 1) {
            //Exibir tela listando todos os serviços que respondem ao criterio buscado
            render view: 'list', model: [servicoInstanceList: servicos, servicoInstanceCount: servicos.totalCount]
        } else {
            return show(servicos[0]);
        }
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def show(Servico servicoInstance) {
        if (! servicoInstance)
            return notFound()
        render view: 'show', model: [
                servicoInstance: servicoInstance,
                formularioEncaminhamento: formularioService.getFormularioPreDefinido(PreDefinidos.ENCAMINHAMENTO),
                hierarquiaTerritorial: abrangenciaTerritorialService.JSONhierarquiaTerritorial(servicoInstance.abrangenciaTerritorial)
        ]
    }

    def create() {
        Servico servico = new Servico(params)
        servico.podeEncaminhar = true
        servico.habilitado = true
        render view: 'create', model: [servicoInstance: servico, territoriosDisponiveis: getAreasAtuacaoDisponiveis(null)]
    }

    def save(Servico servicoInstance) {
        if (! servicoInstance)
            return notFound()

        boolean modoCriacao = servicoInstance.id == null

        atribuiAbrangenciaTerritorial(servicoInstance)

        //Validações:
//        if (! servicoInstance.abrangenciaTerritorial) //exibe o formulario novamente em caso de problemas na validacao
//            return render(view: 'preencherFormulario', model: [templateCamposCustomizados: getTemplateCamposCustomizados(formulario), dtoFormulario: formulario, usuarios: segurancaService.getOperadoresOrdenadosController() ])

        //Grava
        if (servicoInstance.validate()) {
            servicoService.grava(servicoInstance)
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: [servicoInstance:servicoInstance, territoriosDisponiveis: getAreasAtuacaoDisponiveis(servicoInstance?.abrangenciaTerritorial)])
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'servico.label', default: 'Serviço'), servicoInstance.apelido])
        return show(servicoInstance)
    }

    def edit(Servico servicoInstance) {
        if (! servicoInstance)
            return notFound()
        render view: 'edit', model: [servicoInstance: servicoInstance, territoriosDisponiveis: getAreasAtuacaoDisponiveis(servicoInstance.abrangenciaTerritorial)]
    }

    def delete(Servico servicoInstance) {
        if (! servicoInstance)
            return notFound()

        servicoService.apaga(servicoInstance)
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'servico.label', default: 'Servico'), servicoInstance.apelido])
        redirect action:"list"
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'servico.label', default: 'Servico'), params.id])
        return redirect(action: "list")
    }
}
