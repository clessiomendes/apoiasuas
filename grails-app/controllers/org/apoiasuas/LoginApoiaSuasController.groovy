package org.apoiasuas

import grails.plugin.springsecurity.LoginController
import org.apoiasuas.AncestralController
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.util.AmbienteExecucao
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.vote.AuthenticatedVoter

@Secured('permitAll')
/**
 * Exetensao do controller de login para permitir uma nova action destinada a janela modal de login via ajax
 */
class LoginApoiaSuasController extends LoginController {

    def springSecurityService

    public static final String URL_AJAX_SUCCESS_LOGIN = "/loginApoiaSuas/ajaxSuccess"

	def loginAjax() {
		//apenas exibir a pagina gsp homonima da action
	}

    def ajaxSuccess() {
        render status: 200;
    }

//    @Secured([AuthenticatedVoter.IS_AUTHENTICATED_REMEMBERED])
    def keepLoggedIn() {
//        if (AmbienteExecucao.isDesenvolvimento())
//            render status: springSecurityService.loggedIn ? 200 : 999
//        else
            render status: springSecurityService.loggedIn ? 200 : 406 /*not acceptable*/
    }

}
