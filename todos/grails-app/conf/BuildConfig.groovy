import grails.util.Environment

grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6

grails.project.war.exploded.dir = "c:/temp/deploy-cc/apoiasuas.war"
grails.war.exploded = true

grails.project.fork = [
        test: false,
        run : false
]

System.out.println("Environment.current "+Environment.current+" em BuildConfig.groovy")

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
        excludes "grails-docs"
        //necessario porque a versao do itext 2.0.8 estava em conflito com itext 2.1.7 usado pelo xdocreport
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false
    // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()
        mavenRepo 'http://repo.spring.io/milestone'
        mavenRepo "http://repo.grails.org/grails/core"
        mavenRepo "http://repo.grails.org/grails/plugins"
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }

    dependencies {

    }

    plugins {
        build ":release:3.0.1", {
            export = false
        }
    }
}

//ATENÇÃO: usar apenas caixa baixa e letras (sem "-", "_", etc)
grails.plugin.location."nucleo" = "..//nucleo";
grails.plugin.location."pedidocertidao" = "..//pedidocertidao";
grails.plugin.location."crj" = "..//crj";

