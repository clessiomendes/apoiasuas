package org.apoiasuas.util

import org.apache.log4j.MDC
import org.apoiasuas.log.Log

class LoggingFilters {

    def logEstatisticaService

    def filters = {
        all(controller:'*', action:'*', /*invert: true - desabilitando o filtro*/) {
            before = {
                final String username = request.userPrincipal?.name
                final String sessionId = request.requestedSessionId
                final String novoRequest = request.servletPath?.replaceAll('.dispatch','');
                final String parametros = request.getParameterMap() ? (request.getParameterMap().keySet().join(",")) : "";
                final String valoresParametros = request.getParameterMap() ? (request.getParameterMap().toString()) : "";

                MDC.put('username', username ?: 'N/A');
                MDC.put('requestedSessionId', sessionId ? sessionId.substring(0, Math.min(sessionId.size(), 5)) : null ?: 'N/A');

                log.info("novo request: "+novoRequest);
                MDC.put('log', logEstatisticaService.iniciaLog(username, sessionId, novoRequest, parametros, valoresParametros));
            }
            after = { Map model ->

            }
            afterView = { Exception e ->
                Log log = MDC.get('log');
                if (log)
                    logEstatisticaService.finalizaLog(log);

                MDC.remove('log');
                MDC.remove('username');
                MDC.remove('requestedSessionId');
            }
        }
    }
}
