package org.apoiasuas.seguranca

import org.apoiasuas.redeSocioAssistencial.RecursosServico

/**
 * Created by home64 on 07/04/2015.
 */
class ItemMenuDTO {
    long ordem = Long.MAX_VALUE;
    String descricao;
    Map link;
    String restricaoAcesso;
    RecursosServico recursoServico;
    String hint;
    String imagem;
    String classeCss;

    @Override
    public String toString() {
        return ordem + ", " + descricao
    }
}
