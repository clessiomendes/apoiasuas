package org.apoiasuas.util

import grails.util.Holders

/**
 * Plugins da aplicação com o objetivo de modularização
 */
public class Modulos {
    public static String NUCLEO = "nucleo"
    public static PEDIDO_CERTIDAO = "pedidocertidao"
    public static CRJ = "crj"

    public static String[] getModulos() {
        return [NUCLEO, PEDIDO_CERTIDAO, CRJ];
    }

    /**
     * Verifica se o modulo (plugin) esta instalado e levanta uma excessao em caso negativo
     */
    public static boolean testaDependencia(String modulo, boolean levantaExcessao) {
        boolean result = Holders.pluginManager.hasGrailsPlugin(modulo)
        if (! result && levantaExcessao)
            throw new ApoiaSuasException("Módulo '${modulo}' não instalado");
        return result;
    }


}