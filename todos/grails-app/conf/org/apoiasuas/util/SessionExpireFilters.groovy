package org.apoiasuas.util

import javax.servlet.http.Cookie

/**
 * Inicia um contador de tempo para encerramento da sessao no servidor e armazena em um cookie no cliente. Esse cookie
 * e decrementado no cliente e reiniciado a cada requisicao submentida ao servidor, INCLUSIVE REQUISICOES AJAX
 */
class SessionExpireFilters {

    def filters = {
        all(controller: '*', action: '*') {
            before = {
                Cookie newCookie = new Cookie( "expireTime", session.maxInactiveInterval+"" )
                newCookie.path = "/"
                response.addCookie newCookie
            }
            after = { Map model ->
            }
            afterView = { Exception e ->

            }
        }
    }
}
