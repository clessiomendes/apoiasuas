package org.apoiasuas.http;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by admin on 27/04/2016.
 */
public class PreGrailsServletContextListener implements ServletContextListener {

    static String contextPath = "";

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    //Run this before web application is started
    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("PreGrailsServletContextListener started "+event.getServletContext().getContextPath());
        contextPath = event.getServletContext().getContextPath();
    }

    public static String getContextPath() {
        return contextPath;
    }
}