package org.apoiasuas.seguranca

import grails.plugin.springsecurity.web.authentication.AjaxAwareAuthenticationSuccessHandler
import org.springframework.security.core.Authentication
import org.springframework.security.web.savedrequest.SavedRequest

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

/**
 * Created by cless on 28/06/2018.
 */
class ApoiaSuasSuccessHandler extends AjaxAwareAuthenticationSuccessHandler {
    @Override
    protected String determineTargetUrl(HttpServletRequest request,HttpServletResponse response) {

//            logger.info("ok")
//            return "/dev/null";
            return super.determineTargetUrl(request, response)
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) throws ServletException, IOException {
        try {
//            checkIfTheUserIsBadPerson(request.getSession(),authentication)
//            handle(request,response,authentication)
            SavedRequest savedRequest = this.requestCache?.getRequest(request, response);
            if (savedRequest) {
                boolean isPost = "POST".equals(savedRequest.getMethod())
                super.onAuthenticationSuccess(request, response, authentication);
            }
//            super.clearAuthenticationAttributes(request)
        }
        finally {
            // always remove the saved request
//            requestCache.removeRequest(request, response)
        }
    }
}
