package org.apoiasuas.util

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

import javax.servlet.ServletContext

@Singleton
/**
 * Substitui classes Holder do Grails pre 2.0, sem os problemas daquela.
 * Ver http://burtbeckwith.com/blog/?p=1017
 */
class ApplicationContextHolder implements ApplicationContextAware {
    private ApplicationContext ctx

    void setApplicationContext(ApplicationContext applicationContext) {
        ctx = applicationContext
    }

    static ApplicationContext getApplicationContext() {
        getInstance().ctx
    }

    static Object getBean(String name) {
        getApplicationContext().getBean(name)
    }

    static GrailsApplication getGrailsApplication() {
        getBean('grailsApplication')
    }

    static ConfigObject getConfig() {
        getGrailsApplication().config
    }

    static ServletContext getServletContext() {
        getBean('servletContext')
    }

    static GrailsPluginManager getPluginManager() {
        getBean('pluginManager')
    }
}
