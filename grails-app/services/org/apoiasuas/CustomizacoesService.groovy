package org.apoiasuas

import grails.transaction.Transactional
import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial

@Transactional(readOnly = true)
/**
 * ESCOPO DE SESSÃO - pode ser injetado normalmente em um controller com "def customizacoesService".
 * No entanto, para outros servicos com escopo singleton (o default) ou para taglibs, deve ser injetado o proxy "def customizacoesServiceProxy"
 */
class CustomizacoesService {

    public static enum Codigos {
        BELO_HORIZONTE, BELO_HORIZONTE_HAVAI_VENTOSA, BELO_HORIZONTE_VISTA_ALEGRE
    }

    def segurancaService;
    static scope = "session"
    private List<Codigos> codigos = null;

    /**
     * Verifica se o codigo de customizacao passado está previsto para esse servicoSistema
     */
    public boolean contem(Codigos codigo) {
        return contem([codigo]);
    }

    /**
     * Verifica se PELO MENOS UM dos codigos de customizacao passados esta previsto para esse servicoSistema
     */
    public boolean contem(List<Codigos> codigos) {
        if (this.codigos == null)
            inicializa();
        //verifica se PELO MENOS UM dos codigo passados esta previsto para esse servicoSistema
        return this.codigos.intersect(codigos)
    }

    /**
     * Chamado apenas uma vez por sessao(login), e somente quando for utilizado
     */
    private void inicializa() {
        log.debug("inicializando customizacoes na sessao");
        this.codigos = segurancaService.getAbrangenciasTerritoriaisAcessiveis().findAll {
            it.codigoCustomizacoes
        }.collect { Codigos.valueOf(it.codigoCustomizacoes) }
    }

}
