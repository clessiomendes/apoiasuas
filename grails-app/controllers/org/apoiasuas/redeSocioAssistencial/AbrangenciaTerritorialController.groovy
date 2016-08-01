package org.apoiasuas.redeSocioAssistencial

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.seguranca.DefinicaoPapeis

import javax.servlet.http.HttpServletRequest

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class AbrangenciaTerritorialController extends AncestralController {

    static defaultAction = "list"

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        render view: "list", model: [abrangenciaTerritorialInstanceList: AbrangenciaTerritorial.listOrderByNome(params), abrangenciaTerritorialInstanceCount: AbrangenciaTerritorial.count()]
    }

    def show(AbrangenciaTerritorial abrangenciaTerritorialInstance) {
        if (!abrangenciaTerritorialInstance)
            return notFound()
        render view: "show", model: [abrangenciaTerritorialInstance: abrangenciaTerritorialInstance, JSONAbrangenciaTerritorial: abrangenciaTerritorialService.JSONAbrangenciasTerritoriaisExibicao(abrangenciaTerritorialInstance.mae) ]
    }

    @Secured([DefinicaoPapeis.STR_SUPER_USER])
    def create() {
        AbrangenciaTerritorial abrangenciaTerritorial = new AbrangenciaTerritorial(params)
        abrangenciaTerritorial.habilitado = true
        render view: "create", model: [abrangenciaTerritorialInstance: abrangenciaTerritorial, territoriosDisponiveis: abrangenciaTerritorialService.JSONAbrangenciasTerritoriaisEdicao(null, [])]
    }

    @Secured([DefinicaoPapeis.STR_SUPER_USER])
    def edit(AbrangenciaTerritorial abrangenciaTerritorialInstance) {
        if (!abrangenciaTerritorialInstance)
            return notFound()
        render view: "edit", model: [abrangenciaTerritorialInstance: abrangenciaTerritorialInstance, territoriosDisponiveis: abrangenciaTerritorialService.JSONAbrangenciasTerritoriaisEdicao(abrangenciaTerritorialInstance, abrangenciaTerritorialInstance.mae ? [abrangenciaTerritorialInstance.mae] : [])]
    }

    @Secured([DefinicaoPapeis.STR_SUPER_USER])
    def save(AbrangenciaTerritorial abrangenciaTerritorialInstance) {
        boolean modoCriacao = abrangenciaTerritorialInstance.id == null
        if (abrangenciaTerritorialInstance == null) {
            notFound()
            return
        }

        Long[] idsTerritoriosAtuacao = obtemTerritoriosRequest(request)

        assert idsTerritoriosAtuacao.size() < 2, "Mais de uma area de atuacao selecionada. Apenas uma era esperada"
        if (idsTerritoriosAtuacao)
            abrangenciaTerritorialInstance.mae = abrangenciaTerritorialService.getAbrangenciaTerritorial(idsTerritoriosAtuacao[0]);

        if (! abrangenciaTerritorialService.gravaAbrangenciaTerritorial(abrangenciaTerritorialInstance) ) {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit", model: [abrangenciaTerritorialInstance: abrangenciaTerritorialInstance])
        }
        flash.message = "$abrangenciaTerritorialInstance.nomeCompleto gravada"
        return show(abrangenciaTerritorialInstance)
//        render view: "show", model: [abrangenciaTerritorialInstance: abrangenciaTerritorialInstance]
    }

    private List<Long> obtemTerritoriosRequest(HttpServletRequest httpServletRequest) {
        Long[] result = []
        if (request.getParameter("territoriosAtuacao"))
            request.getParameter("territoriosAtuacao").split(",").each {
                result += it.substring(AbrangenciaTerritorialService.ID_TERRITORIOS_ATUACAO.size()).toLong()
            }
        log.debug("idsAreaAtuacao: " + result.join(","));
        return result;
    }

    @Secured([DefinicaoPapeis.STR_SUPER_USER])
    def delete(AbrangenciaTerritorial abrangenciaTerritorialInstance) {
        if (!abrangenciaTerritorialInstance)
            return notFound()

        abrangenciaTerritorialService.apagaAbrangenciaTerritorial(abrangenciaTerritorialInstance)

        flash.message = message(code: 'default.deleted.message', args: [message(code: 'abrangenciaTerritorial.label'), abrangenciaTerritorialInstance.id])
        redirect action: "list"
    }

    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'abrangenciaTerritorial.label'), params.id])
        redirect action: "list"
    }

}
