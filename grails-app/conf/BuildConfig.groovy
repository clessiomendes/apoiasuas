import grails.util.Environment

grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.war.file = "target/${appName}-${appVersion}.war"

//grails.project.war.exploded.dir = "C:/workspaces/openshift/webapps/ROOT"    //openshift

//grails.war.exploded = true
/* Estes parametros nao serao mais definidos em arquivo de configuracao, e sim no script de deploy (deploy-af.bat)
if (Environment.current == Environment.DEVELOPMENT) {
    grails.project.war.exploded.dir = "C:/workspaces/appfog/apoiasuas/validacao"    //appfog
} else if(Environment.current == Environment.PRODUCTION) {
    grails.project.war.exploded.dir = "C:/workspaces/appfog/apoiasuas/producao"    //appfog
}
*/

grails.project.fork = [
        test: false,
        run : false
/*
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    test: [maxMemory: 768, minMemory: 64, debug: false, daemon:true],
    // configure settings for the run-app JVM
    run: [maxMemory: 768, minMemory: 64, debug: false, forkReserve:false],
    // configure settings for the run-war JVM
    war: [maxMemory: 768, minMemory: 64, debug: false, forkReserve:false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 768, minMemory: 64, debug: false]
*/
]

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
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.

        test "org.grails:grails-datastore-test-support:1.0-grails-2.4"
        test 'org.gmock:gmock:0.8.2'

//        runtime('com.h2database:h2:1.4.185') {
//            transitive = false
//        }
        runtime 'mysql:mysql-connector-java:5.1.12'
        runtime 'org.liquibase:liquibase-core:3.3.2'
        runtime 'org.postgresql:postgresql:9.3-1101-jdbc41'

//        development {
//            compile 'xerces:xerces:2.4.0'
//        }

        compile "org.apache.poi:poi:3.9"
        compile "org.apache.poi:poi-ooxml:3.9"
        compile "commons-beanutils:commons-beanutils:1.9.2"
        compile "com.google.guava:guava:18.0"

        compile("org.docx4j:docx4j:3.2.1") { //manipulacao de documentos docx
            excludes "com.google.guava:guava:17.0"
            excludes "org.antlr:antlr-runtime:3.3" //o plugin LESS Grails Asset-Pipeline exige versao 3.5
        }
        //XDocReport (geracao de relatorio a partir de templates docx ou odt)
        compile "fr.opensagres.xdocreport:fr.opensagres.xdocreport.core:1.0.4"
        compile "fr.opensagres.xdocreport:fr.opensagres.xdocreport.document:1.0.4"
        compile "fr.opensagres.xdocreport:fr.opensagres.xdocreport.template:1.0.4"
//        compile "fr.opensagres.xdocreport:fr.opensagres.xdocreport.converter:1.0.4"
        compile "fr.opensagres.xdocreport:fr.opensagres.xdocreport.document.docx:1.0.4" //suporte a .docx
        compile "fr.opensagres.xdocreport:fr.opensagres.xdocreport.document.odt:1.0.4" //suporte a .odt
        compile "fr.opensagres.xdocreport:fr.opensagres.xdocreport.template.velocity:1.0.4" //suporte a velocity
//        compile "fr.opensagres.xdocreport:fr.opensagres.xdocreport.converter.docx.xwpf:1.0.4" //conversao PDF e XHTML
//        compile "fr.opensagres.xdocreport:fr.opensagres.xdocreport.converter.docx.docx4j:1.0.4" //conversao PDF e XHTML
//        compile "fr.opensagres.xdocreport:fr.opensagres.xdocreport.converter.fop.docx:1.0.4" //conversao PDF e XHTML
//        compile "fr.opensagres.xdocreport:org.apache.poi.xwpf.converter.pdf:1.0.4" //conversao PDF e XHTML

        build("org.grails:grails-docs:2.4.3") {
            excludes "com.lowagie:itext:2.0.8"
        }
        compile "com.lowagie:itext:2.1.7" //iText

//        compile "org.apache.xmlgraphics:batik-util:1.7"

//        compile ... //Liquibase
        compile "org.apache.velocity:velocity:1.7" //Velocity

        //		compile "com.myjeeva.poi:excelReader:1.2"

    }

    plugins {
        runtime ":hibernate4:4.3.5.5" // or ":hibernate:3.6.10.17"
        build ":release:3.0.1", {
            export = false
        }

        compile ":scaffolding:2.1.2"
        compile ':cache:1.1.8'
        compile ":asset-pipeline:2.1.5"
        compile ":spring-security-core:2.0-RC4" //autenticacao e authority, definicao de usuarios e papeis.
//        compile ":filterpane:2.4.5" //filtros para as telas de busca
//        compile ":perf4j:0.1.1" //plugin para profiling minucioso
//        compile ":ajax-uploader:1.1"
        compile ":jquery:1.11.1"
        compile ":jquery-ui:1.10.4"
        runtime ":resources:1.2.14"
        compile ":joda-time:1.5"
        compile ":excel-export:0.2.1"
//        compile ":nerderg-form-tags:2.1.3" //taglib com componente de data (dentre outros)

/*
        switch (AmbienteExecucao.CURRENT) {
            case AmbienteExecucao.LOCAL_POSTGRES:
                compile ":grails-melody:1.54.0" //plugin para profiling
                break
        }
*/
        if (Environment.current == Environment.DEVELOPMENT)  {
            runtime "org.grails.plugins:console:1.5.6"
//            compile ":grails-melody:1.54.0" //plugin para profiling
        }

        // Uncomment these to enable additional asset-pipeline capabilities
        //compile ":sass-asset-pipeline:1.9.0"
        compile ":less-asset-pipeline:2.1.0"
        //compile ":coffee-asset-pipeline:1.8.0"
        //compile ":handlebars-asset-pipeline:1.3.0.3"

        //compile ":app-info:1.1.1" --INCONPATIVEL COM GRAILS 2.4
        //compile ":app-info-hibernate:0.4.1"

//        compile "org.grails.plugins:js-tree:0.3.1" //componente visual de treeview

        compile ":camunda:0.5.0" //componente de BPM
//        build ':jetty:2.0.3'
        build ":tomcat:7.0.55"

        compile ':elasticsearch:0.1.0'
//        compile "org.grails.plugins:hibernate-filter:0.3.2" //Hibernate filters

    }
}
