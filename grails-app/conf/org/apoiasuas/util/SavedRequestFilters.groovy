package org.apoiasuas.util

/**
 * Tentantiva (FRUSTRADA) de recuperar os dados do request após uma tela intermediaria de login ser acionada durante o submit
 *
 * https://stackoverflow.com/questions/24787790/grails-spring-security-saved-request-empty-parameters-and-unbound-command
 */
class SavedRequestFilters {
    def filters = {
        // only after a login/auth check to see if there are any saved parameters
        savedRequestCheck(controller: 'login', action: 'auth') {
            after = {
/*
                org.springframework.security.web.savedrequest.SavedRequest savedRequest = new org.springframework.security.web.savedrequest.HttpSessionRequestCache().getRequest(request, response)
                if (savedRequest) {
                    // store the parameters and target uri into the session for later use
                    session['savedRequestParams'] = [
                        uri: savedRequest.getRedirectUrl(),
                        data: savedRequest.getParameterMap()
                    ]
                }
*/
            }
        }

        all(controller:'*', action:'*') {
            before = {
/*
                // if the session contains dr saved request parameters
                if (session['savedRequestParams']) {
                    def savedRequestParams = session['savedRequestParams']
                    // only if the target uri is the current will the params be needed
                    if (savedRequestParams.uri.indexOf("/${controllerName}/") > -1) {
                        savedRequestParams.data.each { k, v ->
                            params[k] = v.join(",")
                        }
                        // clear the session storage
                        session['savedRequestParams'] = null
                    }
                }
*/
            }
        }

    }
}
