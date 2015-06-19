import org.apache.commons.io.output.ThresholdingOutputStream
import org.apache.log4j.xml.XMLLayout
import org.apoiasuas.formulario.PreDefinidos
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.util.AmbienteExecucao

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

//      M O D I F I C A D A S     P O R     M I M
// Levantar excessao sempre que, ao gravar, a validacao falhar (http://www.acnenomor.com/5840084p1/why-doesnt-grails-notify-me-of-error-at-domain-object-saving)
grails.gorm.failOnError = true
// Permitir que, quando nao declarado, todos os campos podem ser nulos
grails.gorm.default.constraints = {
    '*'(nullable: true)
}

//Permite que as informacoes de login sejam acessiveis aa partir de threads que nao o thread do request
grails.plugin.springsecurity.sch.strategyName = org.springframework.security.core.context.SecurityContextHolder.MODE_INHERITABLETHREADLOCAL

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
    all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

//Obtido de http://stackoverflow.com/questions/2871977/binding-a-grails-date-from-params-in-a-controller/2872291#2872291
grails.databinding.dateFormats = ['dd/MM/yyyy', 'ddMMyyyy HH:mm:ss.S']

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}


grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

environments {
    development {
        grails.logging.jul.usebridge = true //usado pelo log4j
    }
    test {
    }
    production {
        grails.logging.jul.usebridge = false //usado pelo log4j
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j.main = {
//    includeLocation="true"

    appenders {
//        console name:'stdout', layout:pattern(conversionPattern: '%c{1}.%M() -> %m -> %l %n')
        switch (AmbienteExecucao.CURRENT) {
            case AmbienteExecucao.APPFOG:
                console name: 'stdout', layout: pattern(conversionPattern: '(af) %d{dd-MMM HH:mm} %p %c{8} -> %m%n'), threshold:org.apache.log4j.Level.DEBUG
                root {error 'stdout'}
                break
            case AmbienteExecucao.LOCAL:
                file name: 'xml', layout: xml, file: 'c:/workspaces/logs/apoiaSUAS'+new Date().format("yyyy-MM-dd-hh-mm")+'.xml', threshold:org.apache.log4j.Level.DEBUG
                console name: 'stdout', layout: pattern(conversionPattern: '(loc) %d{dd-MMM HH:mm} %p %c{8} -> %m%n'), threshold:org.apache.log4j.Level.DEBUG
                root {error 'stdout', 'xml'}
                break
            default:
                println 'warning! ambiente indefinido para configuracao de logs. Usando padrao: console name: \'stdout\', layout: pattern(conversionPattern: \'(def) %d{dd-MMM HH:mm} %p %c{8} -> %m%n\')'
                console name: 'stdout', layout: pattern(conversionPattern: '(def) %d{dd-MMM HH:mm} %p %c{8} -> %m%n')
                break
        }


    }


    all     'org.apoiasuas',
            'com.mysql.jdbc.log.StandardLogger', //mysql (inclui tempos das SQL se parametro profileSQL=true for passado na url de conexao
            'org.apache.tomcat.jdbc.pool.interceptor',
            'org.apache.tomcat.jdbc.pool',
            'grails.app.controllers',
            'grails.app.services',
            'grails.app.domain',
//            'grails.app.taglib',
//            'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
//            'org.hibernate',
            'net.sf.ehcache.hibernate',
            'grails.app.conf',
            'grails.app.filters',
            'org.hibernate.stat',                            // tempo e numero de registros em cada SQL
            'org.hibernate.type.descriptor.sql',             //mostra os valores passados como parametros para as SQLs
            'org.hibernate.SQL',
            'org.hibernate.type.descriptor.sql.BasicBinder', //mostra os valores passados como parametros para as SQLs
            'org.hibernate.engine.transaction.spi.AbstractTransactionImpl', //inicio e fim das transacoes
            'org.springframework.transaction.interceptor.TransactionInterceptor', //Mostra inicio e fim das transacoes e a que metodos elas estao associadas
            'org.springframework.webflow.engine',
            'org.springframework.webflow',
            'org.springframework.security',                  //login, seguranca, etc
            'com.myjeeva.poi' //debug para o extrator excel

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
		   'org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler' //Limpar mensagem "Warning - shared formulas not yet supported"
	   
}

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'org.apoiasuas.seguranca.UsuarioSistema'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'org.apoiasuas.seguranca.UsuarioSistemaPapel'
grails.plugin.springsecurity.adh.errorPage = null
grails.plugin.springsecurity.logout.postOnly = false
grails.plugin.springsecurity.rejectIfNoRule = true
grails.plugin.springsecurity.fii.rejectPublicInvocations = true
grails.plugin.springsecurity.authority.className = 'org.apoiasuas.seguranca.Papel'
grails.plugin.springsecurity.useSecurityEventListener = true
grails.plugin.springsecurity.onAbstractAuthenticationFailureEvent = { e, appCtx ->   //Exibe eventual mensagem de erro no login
    println "DEBUG auth failed for user $e.authentication.name: $e.exception.message"
}

switch (AmbienteExecucao.CURRENT) {
    case AmbienteExecucao.APPFOG:
        println 'Exigindo canal SEGURO (https)'
        grails.plugin.springsecurity.secureChannel.definition = [ '/**': 'REQUIRES_SECURE_CHANNEL' ]
        break
    default:
        println 'Permitindo canal NÃO seguro (http)'
}

/*
environments {
    production {
        security {
            channelConfig = [secure: ['/**']]
            httpPort = 80
            httpsPort = 443
        }
    }
}
*/

grails.plugin.springsecurity.interceptUrlMap = [ '/importacaoFamilias/restUpload': ['IS_AUTHENTICATED_ANONYMOUSLY']]

grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	'/':                              ['permitAll'],
//	'/index':                         ['permitAll'],
//	'/index.gsp':                     ['permitAll'],
	'/error.gsp':                     ['permitAll'],
	'/403.gsp':                       ['permitAll'],
	'/assets/**':                     ['permitAll'],
	'/**/js/**':                      ['permitAll'],
	'/monitoring/**':                 ["${AmbienteExecucao.isDesenvolvimento() ? 'permitAll' : DefinicaoPapeis.SUPER_USER}"],
	'/**/css/**':                     ['permitAll'],
	'/**/images/**':                  ['permitAll'],
	'/**/favicon.ico':                ['permitAll']
]
//grails.plugin.springsecurity.roleHierarchy = { PerfilUsuarioSistema.hierarquiaFormatada }
grails.plugin.springsecurity.roleHierarchy = 'valor ignorado e sobrescrito em BootStrap.groovy'
