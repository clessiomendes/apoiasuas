package org.apoiasuas

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.seguranca.DefinicaoPapeis

@Secured([DefinicaoPapeis.USUARIO])
class ConfiguracaoController extends AncestralController {

    def configuracaoService
    static defaultAction = "edit"

    def index() {

    }

    def edit(Configuracao configuracaoInstance) {
        if (configuracaoInstance == null)
            configuracaoInstance = configuracaoService.getConfiguracaoReadOnly()
        render view: 'edit', model: [configuracaoInstance: configuracaoInstance]
    }

    @Secured([DefinicaoPapeis.USUARIO])
    def save(Configuracao configuracaoInstance) {
        //Grava
        if (! configuracaoService.grava(configuracaoInstance)) {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: "edit" , model: [configuracaoInstance:configuracaoInstance])
        }
        servletContext.configuracao = configuracaoService.getConfiguracaoReadOnly()
        flash.message = "Configuração atualizada"
        return edit(configuracaoInstance)
    }


}
