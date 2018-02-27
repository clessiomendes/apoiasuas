package org.apoiasuas.redeSocioAssistencial

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.CidadaoService
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.formulario.FormularioService
import org.apoiasuas.formulario.PreDefinidos
import org.apoiasuas.seguranca.DefinicaoPapeis

@Secured([DefinicaoPapeis.STR_USUARIO])
class ServicoController extends AncestralServicoController {

    ServicoService servicoService
    FormularioService formularioService
    CidadaoService cidadaoService
    def familiaService

    static defaultAction = "list"

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def getServico(Long idServico, Long idFamilia, Long idCidadao) {
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

        if (servico.encaminhamentoPadrao) {
            Cidadao cidadao = cidadaoService.obtemCidadao(idCidadao)
            Familia familia = familiaService.obtemFamilia(idFamilia)
            servico.encaminhamentoPadrao = servico.encaminhamentoPadrao
                    .replaceAll("(?i)%nome%", cidadao?.getNomeCompleto() ?: "")
                    .replaceAll("(?i)%endereco%", familia?.endereco.obtemEnderecoCompleto() ?: "")
                    .replaceAll("(?i)%telefone%", familia?.getTelefonesToString() ?: "")
                    .replaceAll("(?i)%nis%", cidadao?.getNis() ?: "")
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
        render view: 'show', model: getModelExibicao(servicoInstance)
    }

    def create() {
        Servico servico = new Servico(params)
        servico.podeEncaminhar = true
        servico.habilitado = true
        render view: 'create', model: getModelEdicao(servico)
    }

    def save(Servico servicoInstance) {
        if (! servicoInstance)
            return notFound()

        boolean modoCriacao = servicoInstance.id == null

        servicoInstance.abrangenciaTerritorial = atribuiAbrangenciaTerritorial();

        //Validações:
        boolean validado = servicoInstance.validate();
        validado = validado & validaVersao(servicoInstance);
        //Grava
        if (validado) {
            servicoService.grava(servicoInstance)
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: getModelEdicao(servicoInstance))
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'servico.label', default: 'Serviço'), servicoInstance.apelido])
        render view: 'show', model: getModelExibicao(servicoInstance)
    }

    def edit(Servico servicoInstance) {
        if (! servicoInstance)
            return notFound()
        render view: 'edit', model: getModelEdicao(servicoInstance)
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

    private LinkedHashMap<String, Object> getModelEdicao(Servico servicoInstance) {
        [servicoInstance: servicoInstance, JSONAbrangenciaTerritorial: getAbrangenciasTerritoriaisEdicao(servicoInstance?.abrangenciaTerritorial)]
    }

    private Map<String, Object> getModelExibicao(Servico servicoInstance) {
        [servicoInstance: servicoInstance,
         formularioEncaminhamento: formularioService.getFormularioPreDefinido(PreDefinidos.ENCAMINHAMENTO),
         JSONAbrangenciaTerritorial: getAbrangenciasTerritoriaisExibicao(servicoInstance.abrangenciaTerritorial)]
    }

}
