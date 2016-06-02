package org.apoiasuas.redeSocioAssistencial

import org.apoiasuas.AncestralController

import javax.servlet.http.HttpServletRequest

/**
 * Created by admin on 04/05/2016.
 */
class AncestralServicoController extends AncestralController {
    ServicoSistemaService servicoSistemaService
    AbrangenciaTerritorialService abrangenciaTerritorialService

    protected void atribuiAbrangenciaTerritorial(def servicoSistemaInstance/*generico para aceitar tanto Servico quanto ServicoSistema*/) {
        Long idTerritoriosAtuacao = null
        if (request.getParameter("territorioAtuacao")) {
            idTerritoriosAtuacao = request.getParameter("territorioAtuacao").substring(AbrangenciaTerritorialService.ID_TERRITORIOS_ATUACAO.size()).toLong()

// Codigo para ser usado se o checkbox estiver ativado (permitindo a escolha de multiplas abrangencias territoriais)

//            request.getParameter("territorioAtuacao").split(",").each {
//                result += it.substring(AbrangenciaTerritorialService.ID_TERRITORIOS_ATUACAO.size()).toLong()
//            }
        }
        if (idTerritoriosAtuacao)
            servicoSistemaInstance.abrangenciaTerritorial = abrangenciaTerritorialService.getAbrangenciaTerritorial(idTerritoriosAtuacao);
    }

    protected String getAreasAtuacaoDisponiveis(AbrangenciaTerritorial abrangenciaTerritorial) {
        return abrangenciaTerritorialService.JSONareasAtuacaoDisponiveis(null, [abrangenciaTerritorial])
    }


}
