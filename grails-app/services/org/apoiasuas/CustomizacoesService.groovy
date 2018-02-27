package org.apoiasuas

import grails.transaction.Transactional
import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial

@Transactional(readOnly = true)
class CustomizacoesService {

    public static enum Codigos {
        BELO_HORIZONTE, BELO_HORIZONTE_HAVAI_VENTOSA, BELO_HORIZONTE_VISTA_ALEGRE
    }

    def segurancaService;
    static scope = "session"
    private List<Codigos> codigos = null;

    public boolean contem(Codigos codigo) {
        return contem([codigo]);
    }

    public boolean contem(List<Codigos> codigos) {
        if (this.codigos == null)
            inicializa();
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
