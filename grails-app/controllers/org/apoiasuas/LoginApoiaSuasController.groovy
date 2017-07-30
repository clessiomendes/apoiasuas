package org.apoiasuas

import grails.plugin.springsecurity.LoginController
import org.apoiasuas.AncestralController
import org.springframework.security.access.annotation.Secured

@Secured('permitAll')
/**
 * Exetensao do controller de login para permitir uma nova action destinada a janela modal de login via ajax
 */
class LoginApoiaSuasController extends LoginController {

	def loginAjax() {
		//apenas exibir a pagina gsp homonima da action
	}

}
