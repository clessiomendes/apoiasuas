package org.apoiasuas.http

import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.redeSocioAssistencial.ServicoSistemaController
import org.apoiasuas.redeSocioAssistencial.ServicoSistemaService
import org.apoiasuas.seguranca.SegurancaService
import org.apoiasuas.util.ApplicationContextHolder

import javax.servlet.http.HttpSessionEvent
import javax.servlet.http.HttpSessionListener

/**
 * Created by admin on 05/05/2016.
 */
class ApoiaSuasHttpSessionListener implements HttpSessionListener {

    /**
     * Grava veriáveis na sessão a cada login
     * @return
     */
    @Override
    public void sessionCreated(HttpSessionEvent event) {
//        SegurancaService segurancaService = ApplicationContextHolder.getGrailsApplication().getMainContext().getBean("segurancaService");
//        ServicoSistema servicoSistema = segurancaService.getUsuarioLogado().getServicoSistemaSeguranca();
//        ServicoSistemaController.setSessionServicoSistemaAtual(event.getSession(), servicoSistema);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
    }
}
