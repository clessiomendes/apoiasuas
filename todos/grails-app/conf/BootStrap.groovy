class BootStrap {

    def grailsApplication

//https://justlearningdotcom.wordpress.com/2012/12/02/grails-bootstrap-under-the-hood/comment-page-1/
//http://docs.grails.org/latest/guide/plugins.html#queryingArtefacts
    
    def init = { servletContext ->
/*
        grailsApplication.serviceClasses.each {
            log.debug(it.fullName);
            def servico = grailsApplication.mainContext.getBean(it.propertyName);
//            if (servico instanceof IASMenuProvider)
//            servico...
        }
        System.out.println("meu bootstrap todos, parametroTesteNucleo: "+grailsApplication.config.parametroTesteNucleo);
//        System.out.println("meu bootstrap todos, parametroTesteCertidao: "+grailsApplication.config.parametroTesteCertidao);
*/
    }

    def destroy = {
    }

}
