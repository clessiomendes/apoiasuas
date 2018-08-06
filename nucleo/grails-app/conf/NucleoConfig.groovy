import org.apache.log4j.Level
import org.apache.log4j.RollingFileAppender
import org.apoiasuas.LoginApoiaSuasController
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.util.ambienteExecucao.AmbienteExecucao

parametroTesteNucleo = "lalala";

//      M O D I F I C A D A S     P O R     M I M
// Levantar excessao sempre que, ao gravar, a validacao falhar (http://www.acnenomor.com/5840084p1/why-doesnt-grails-notify-me-of-error-at-domain-object-saving)
grails.gorm.failOnError = true
// Permitir que, quando nao declarado, todos os campos podem ser nulos
grails.gorm.default.constraints = {
    '*'(nullable: true)
}

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

grails.views.javascript.library = "jquery"

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
        grails.logging.jul.usebridge = false //usado pelo log4j
    }
    test {
    }
    production {
        grails.logging.jul.usebridge = false //usado pelo log4j
//        grails.serverURL = "https://apoiacras.cleverapps.io"
    }
}

//                                              ============   log4j configuration    ===========
grails.logging.jul.usebridge = true //permite logar também bibliotecas que usam java.util.logging.Logger (JUL)
log4j.main = {
//logs, em arquivos separados:
    def errorAppender = new RollingFileAppender(name: 'errorFile', append: true, maxFileSize: '10000KB',
            file: AmbienteExecucao.getCaminhoRepositorioArquivos()+'/logs/error.log',
            maxBackupIndex: 3, layout: pattern(conversionPattern: '%d{dd/MM/yy HH:mm:ss} %X{username} %X{requestedSessionId} %c{8} -> %m%n'), threshold: Level.ERROR);
    def infoAppender = new RollingFileAppender(name: 'infoFile', append: true, maxFileSize: '10000KB',
            file: AmbienteExecucao.getCaminhoRepositorioArquivos()+'/logs/info.log',
            maxBackupIndex: 3, layout: pattern(conversionPattern: '%d{dd-MMM HH:mm:ss} %X{username} %X{requestedSessionId} %c{8} -> %m%n'), threshold: Level.INFO);
//estatísticas de uso de recursos do sistema
    def memAppender = new RollingFileAppender(name: 'memFile', append: true, maxFileSize: '2000KB',
            file: AmbienteExecucao.getCaminhoRepositorioArquivos()+'/logs/mem.log',
            maxBackupIndex: 2, layout: pattern(conversionPattern: '%d{dd/MM/yyyy;HH:mm};%m%n'));
//sqls
    def sqlAppender = new RollingFileAppender(name: 'sqlFile', append: true, maxFileSize: '2000KB',
            file: AmbienteExecucao.getCaminhoRepositorioArquivos()+'/logs/sql.log',
            maxBackupIndex: 2, layout: pattern(conversionPattern: '%d{dd/MM/yyyy;HH:mm:ss};%X{username};%X{requestedSessionId};%m%n'));

    switch (AmbienteExecucao.CURRENT2) {
    case AmbienteExecucao.CLEVERCLOUD:
        appenders {
            appender errorAppender;
            appender infoAppender;
            appender memAppender;
            appender sqlAppender;
            console name: 'stdout', layout: pattern(conversionPattern: '(as) %d{dd-MMM HH:mm:ss} %X{username} %X{requestedSessionId} %c{8} -> %m%n'), threshold: org.apache.log4j.Level.ERROR
        }
        root { error 'stdout','errorFile', 'infoFile' } //se nao for alterado explicitamente, o nivel de log padrao para todas as classes (loggers) eh "error"
        break
/*
    case AmbienteExecucao.APPFOG:
        appenders {
            console name: 'console', layout: pattern(conversionPattern: '(af) %d{dd-MMM HH:mm:ss} %X{username} %X{requestedSessionId} %c{8} -> %m%n'), threshold:org.apache.log4j.Level.ERROR
        }
        root { error 'console' } //se nao for alterado explicitamente, o nivel de log padrao para todas as classes (loggers) eh "error"
        break
*/
    case AmbienteExecucao.LOCAL:
        appenders {
//                    console name: 'sqlFile', file: 'c:/workspaces/logs/apoiasuassql.log', layout: pattern(conversionPattern: '(loc) %d{dd-MMM HH:mm:ss} %p %c{8} -> %m%n'), threshold:org.apache.log4j.Level.ALL
//                  file name: 'xml', layout: xml, file: 'c:/workspaces/logs/apoiaSUAS'+new Date().format("yyyy-MM-dd-hh-mm")+'.xml', threshold:org.apache.log4j.Level.DEBUG
            appender errorAppender;
            appender infoAppender;
            appender memAppender;
            appender sqlAppender;
            console name: 'stdout', layout: pattern(conversionPattern: '(cc) %d{dd-MMM HH:mm:ss} %X{username} %X{requestedSessionId} %c{8} -> %m%n'), threshold: org.apache.log4j.Level.DEBUG
        }
        root { error 'stdout', 'errorFile', 'infoFile' } //se nao for alterado explicitamente, o nivel de log padrao para todas as classes (loggers) eh "error"

        break
    default:
        println 'warning! ambiente indefinido para configuracao de logs. Usando padrao: console name: \'stdout\', layout: pattern(conversionPattern: \'(def) %d{dd-MMM HH:mm} %p %c{8} -> %m%n\')'
        console name: 'console', layout: pattern(conversionPattern: '(def) %d{dd-MMM HH:mm:ss} %p %c{8} -> %m%n')
        root { error 'console' } //se nao for alterado explicitamente, o nivel de log padrao para todas as classes (loggers) eh "error"
    }

    //Liga o appender sqlFile (sql.log) ao trace do hibernate
    debug additivity: true, sqlFile: [
//            'org.hibernate.engine.transaction.spi', //begin, commit
//            'org.hibernate.stat.internal', //tempo de execucao de cada HQL
//            'org.hibernate.type.descriptor.sql.BasicBinder', //parametros PASSADOS para as SQLs
//            'org.hibernate.type.descriptor.sql.BasicExtractor', //parametros RETORNADOS pelas SQLs
            'org.hibernate.SQL' //comandos SQL (e a tradução HQL - SQL, quando for executado um HQL)
            ,'groovy.sql.Sql' //sql executado diretamente, sem o hibernate
    ]

    //Liga o appender memFile (mem.log) à Job que gera as estatísticas de recursos do sistema
    all additivity: false, memFile: ['grails.app.jobs.apoiasuas.MemLoggingJob']

    all 'org.apoiasuas',
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
            'org.hibernate.type.descriptor.sql.BasicExtractor', //parametros RETORNADOS pelas SQLs
//            'org.hibernate.type.descriptor.sql.BasicBinder', //mostra os valores passados como parametros para as SQLs
            'org.hibernate.engine.transaction.spi.AbstractTransactionImpl', //inicio e fim das transacoes
            'org.springframework.transaction.interceptor.TransactionInterceptor', //Mostra inicio e fim das transacoes e a que metodos elas estao associadas
            'org.springframework.webflow.engine',
            'org.springframework.webflow',
            'org.springframework.security',                  //login, seguranca, etc
            'com.myjeeva.poi', //debug para o extrator excel
            'org.camunda.bpm.engine.persistence', //BPM Engine
            'org.camunda.bpm',   //BPM Engine
            'org.grails.plugins.elasticsearch',  //ElasticSearch
            'grails.app.jobs' //Quartz
//So eh preciso especificar nivel "error" para pacotes internos aos definidos acima nos quais se deseja desligar o log
    error   'org.camunda.bpm.engine.jobexecutor' //desligar logs de job da Engine BPM
//            'org.codehaus.groovy.grails.web.servlet',        // controllers
//            'org.codehaus.groovy.grails.web.pages',          // GSP
//            'org.codehaus.groovy.grails.web.sitemesh',       // layouts
//            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
//            'org.codehaus.groovy.grails.web.mapping',        // URL mapping
//            'org.codehaus.groovy.grails.commons',            // core / classloading
//            'org.codehaus.groovy.grails.plugins',            // plugins
//            'org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler' //Limpar mensagem "Warning - shared formulas not yet supported"

}

//Permite que as informacoes de login sejam acessiveis aa partir de threads que nao o thread do request
grails.plugin.springsecurity.sch.strategyName = org.springframework.security.core.context.SecurityContextHolder.MODE_INHERITABLETHREADLOCAL

//Força sempre o redirecionamento parao menu inicial apos uma tela de login
//grails.plugins.springsecurity.successHandler.alwaysUseDefault = true
//grails.plugins.springsecurity.successHandler.defaultTargetUrl = '/'

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'org.apoiasuas.seguranca.UsuarioSistema'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'org.apoiasuas.seguranca.UsuarioSistemaPapel'
grails.plugin.springsecurity.adh.errorPage = null
grails.plugin.springsecurity.logout.postOnly = false
grails.plugin.springsecurity.rejectIfNoRule = true
grails.plugin.springsecurity.fii.rejectPublicInvocations = true
grails.plugin.springsecurity.authority.className = 'org.apoiasuas.seguranca.Papel'
grails.plugin.springsecurity.useSecurityEventListener = true //Necessario para acionar a classe SegurancaListener
grails.plugin.springsecurity.roleHierarchy = 'valor ignorado e sobrescrito em BootStrap.groovy'
//grails.plugin.springsecurity.successHandler.alwaysUseDefault = true
grails.plugin.springsecurity.successHandler.ajaxSuccessUrl = LoginApoiaSuasController.URL_AJAX_SUCCESS_LOGIN;

/*
switch (AmbienteExecucao.CURRENT) {
    case AmbienteExecucao.APPFOG:
        println 'Exigindo canal SEGURO (https)'
        grails.plugin.springsecurity.secureChannel.definition = [ '/**': 'REQUIRES_SECURE_CHANNEL' ]
        break
    default:
        println 'Permitindo canal NÃO seguro (http)'
}

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

//grails.plugin.springsecurity.interceptUrlMap = [ '/importacaoFamilias/restUpload': ['IS_AUTHENTICATED_ANONYMOUSLY']]

environments {
    production {
//Redirecionamento automático para HTTPS
        grails.plugin.springsecurity.auth.forceHttps = true
        grails.plugin.springsecurity.secureChannel.useHeaderCheckChannelSecurity = true
        grails.plugin.springsecurity.portMapper.httpPort = 80
        grails.plugin.springsecurity.portMapper.httpsPort = 443
        grails.plugin.springsecurity.secureChannel.secureHeaderName = 'X-Forwarded-Proto'
        grails.plugin.springsecurity.secureChannel.secureHeaderValue = 'http'
        grails.plugin.springsecurity.secureChannel.insecureHeaderName = 'X-Forwarded-Proto'
        grails.plugin.springsecurity.secureChannel.insecureHeaderValue = 'https'
        grails.plugin.springsecurity.secureChannel.definition = [
                '/assets/**': 'ANY_CHANNEL',// make js, css, images available to logged out pages
                '/**'       : 'REQUIRES_SECURE_CHANNEL',
        ]
    }
}

grails.plugin.springsecurity.controllerAnnotations.staticRules = [
    '/':                              ['permitAll'],
//    '/**/**':                         ['permitAll'],
    '/grails-errorhandler/**':                     ['permitAll'],
    '/error.gsp':                     ['permitAll'],
	'/403.gsp':                       ['permitAll'],
    '/**/*.css':                      ['permitAll'],
    '/**/*.less':                     ['permitAll'],
    '/assets/**':                     ['permitAll'],
    '/loginApoiaSuas/**':             ['permitAll'],
    '/log4j/**':                     ['permitAll'],
    '/**/js/**':                      ['permitAll'],
    '/searchable/**':                 ["${AmbienteExecucao.isDesenvolvimento() ? 'permitAll' : DefinicaoPapeis.STR_SUPER_USER}"],
    '/console/**':                    ["${AmbienteExecucao.isDesenvolvimento() ? 'permitAll' : DefinicaoPapeis.STR_SUPER_USER}"],
	'/monitoring/**':                 ["${AmbienteExecucao.isDesenvolvimento() ? 'permitAll' : DefinicaoPapeis.STR_SUPER_USER}"],
	'/**/css/**':                     ['permitAll'],
    '/**/images/**':                  ['permitAll'],
//    '/**/servico/imagem/**':          ['permitAll'],
	'/**/favicon.ico':                ['permitAll']
]
//grails.plugin.springsecurity.roleHierarchy = { PerfilUsuarioSistema.hierarquiaFormatada }

//grails.plugin.springsecurity.onAuthenticationSuccessEvent = { AuthenticationSuccessEvent event, GrailsWebApplicationContext appCtx ->
//    System.out.println("logado")
//    UsuarioSistema usuario = UsuarioSistema.get(event.authentication.principal.id)
//    ServicoSistemaController.setSessionServicoSistemaAtual(event.authentication.details , usuario.servicoSistemaSeguranca);
//}

camunda {
    deployment.scenario = "embedded" // (or "shared", "none")
    engine {
        configuration {
            databaseType = "postgres" // one of (as of writing): [h2, mysql, oracle, postgres, mssql, db2]
            databaseTablePrefix = "bpm."
            databaseSchema = "bpm"
            jobExecutorActivate = false
            deploymentResources = []
            databaseSchemaUpdate = false //schemaaupdate nao esta funcionando em schema separado
        }
    }
}

elasticSearch {
//    index.name = 'apoiasuasES';
    client.mode = 'local';
    datastoreImpl = 'hibernateDatastore';
    //Configuracoes passadas diretamente pelo plugin para o motor nativo do ElasticSearch
    index {
        store.type = 'simplefs'
        analysis.analyzer.default.type = 'brazilian';
        analysis.analyzer.default.filter = 'asciifolding';
    };
    includeTransients = false;
    bulkIndexOnStartup = true;
    development {
        disableAutoIndex = false;
    }
    production {
        disableAutoIndex = false;
    }

//    elasticSearch.index.name = "apoiasuas"
//    elasticSearch.path.data = 'c://temp//es';
}

assets {
    configOptions = [
            less: [
                    compiler: 'standard'
            ]
    ]
}
