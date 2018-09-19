package org.apoiasuas

import grails.plugin.springsecurity.SecurityTagLib
import org.apoiasuas.redeSocioAssistencial.RecursosServico

class ApoiaSuasSecurityTagLib {
    static defaultEncodeAs = [taglib:'raw']
    static namespace = "assec"

    def segurancaService;

    /**
     * Sobrescrevendo a tag de segurança padrão do Spring Security sec:access:
     * Renders the body if the specified expression (a String; the 'expression' attribute)
     * evaluates to <code>true</code> or if the specified URL is allowed.
     *
     * @attr expression the expression to evaluate
     * @attr url the URL to check
     * @attr method the method of the URL, defaults to 'GET'
     * @attr acessoServico verifica se o serviço logado tem acesso a determinada funcionalidade.
     * @attr showto teste de exibicao para customiazacao por abrangencia territorial. Deve conter um ou mais CustomizacoesService.Codigos
     * @attr hidefrom teste de exibicao para customiazacao por abrangencia territorial. Deve conter um ou mais CustomizacoesService.Codigos
     * Ex: acessoServico='inclusaoMembroFamiliar' (todas as opções disponíveis são obtidas de AcessoSeguranca em ServicoSistema)
     */
    def access = { attrs, body ->
        log.debug("sobrescrevendo implementação padrão da tag sec:access")

        ApoiaSuasTagLib apoiaSuasTagLib = grailsAttributes.applicationContext.getBean(ApoiaSuasTagLib.name)

        if (attrs.containsKey('showto') && ! apoiaSuasTagLib.testaCustomizacao(attrs, 'showto'))
            return;
        attrs.remove('showto');

        if (attrs.containsKey('hidefrom') && apoiaSuasTagLib.testaCustomizacao(attrs, 'hidefrom'))
            return;
        attrs.remove('hidefrom');

        RecursosServico acessoServico = attrs.remove("acessoServico")
        if (! segurancaService.acessoRecursoServico(acessoServico))
            return;
        if (! attrs.expression && ! attrs.url && ! attrs.action && ! attrs.controller && ! attrs.mapping)
            attrs.put("expression","true");
        SecurityTagLib secTagLib = grailsApplication.mainContext.getBean('grails.plugin.springsecurity.SecurityTagLib')
        secTagLib.access.call(attrs, body);
    }

}
