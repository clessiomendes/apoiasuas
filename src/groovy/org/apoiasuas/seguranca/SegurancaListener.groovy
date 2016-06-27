package org.apoiasuas.seguranca

import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.redeSocioAssistencial.ServicoSistemaController
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent

/**
 * Created by admin on 15/05/2016.
 */
class SegurancaListener implements ApplicationListener<AuthenticationSuccessEvent> {

    SegurancaService segurancaService;

    void onApplicationEvent(AuthenticationSuccessEvent event) {
//        System.out.println("logado")
//        event.authentication
//        UsuarioSistema usuario = segurancaService.usuarioLogado
//        usuario.servicoSistemaSessaoCorrente = ServicoSistema.get(3/*Vila Antena*/)
//        UsuarioSistema usuario = UsuarioSistema.get(event.authentication.principal.id)
//        def session = grails.plugin.springsecurity.web.SecurityRequestHolder.getRequest().getSession(false)
//        ServicoSistemaController.setSessionServicoSistemaAtual(session, usuario.servicoSistemaSeguranca);
    }
}
