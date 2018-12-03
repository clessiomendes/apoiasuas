package org.apoiasuas.ambienteExecucao

import org.apoiasuas.util.ApoiaSuasException

import java.nio.file.Path

/**
 * Implementacoes especificas para o ambiente de hospedagem da clever cloud www.
 */
class ConfiguracoesClevercloudFacade extends ConfiguracoesFacade {

    public static String APP_HOME = "APP_HOME";

    @Override
    protected void aposSetCaminhoRepositorio() {
        //considera o caminho para o respositorio como um caminho relativo aa partir da variavel de ambiente proprietaria do
        //ambiente clevercloud APP_HOME ou um caminho absoluto, caso inicie com /
        if (! caminhoRepositorio)
            return;
//            throw new ApoiaSuasException("Variavel de ambiente org.apoiasuas.caminhoRepositorio obrigatoria no ambiente clevercloud");
        Path p = java.nio.file.Paths.get(caminhoRepositorio)
        System.out.print("caminhoRepositorio>>"+p.toString())
        if (! p.absolute)
            p = java.nio.file.Paths.get(sysProperties(APP_HOME), caminhoRepositorio);
        System.out.print("caminhoRepositorio recalculado>>"+p.toString())
        caminhoRepositorio = p.toString();
    }

}
