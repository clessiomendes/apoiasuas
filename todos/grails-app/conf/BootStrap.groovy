class BootStrap {

    def grailsApplication

    def init = { servletContext ->
        System.out.println("meu bootstrap todos, parametroTesteNucleo: "+grailsApplication.config.parametroTesteNucleo);
        System.out.println("meu bootstrap todos, parametroTesteCertidao: "+grailsApplication.config.parametroTesteCertidao);
    }

    def destroy = {
    }

}
