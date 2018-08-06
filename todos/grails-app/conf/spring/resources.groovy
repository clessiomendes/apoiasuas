import org.apoiasuas.CustomizacoesService
import org.apoiasuas.fileStorage.LocalFSService
import org.apoiasuas.formulario.FormularioCurriculoService
import org.apoiasuas.redeSocioAssistencial.ServicoSistemaService
import org.apoiasuas.importacao.ImportacaoFamiliasController
import org.apoiasuas.importacao.ImportarFamiliasBHService
import org.apoiasuas.importacao.ImportarFamiliasService
import org.apoiasuas.seguranca.ApoiaSuasDetailsService
import org.apoiasuas.seguranca.ApoiaSuasPersistenceListener
import org.apoiasuas.seguranca.SegurancaListener
import org.apoiasuas.services.ImportarFamiliasJavaService
import org.apoiasuas.util.ambienteExecucao.AmbienteExecucao
import org.apoiasuas.util.ApplicationContextHolder

// Place your Spring DSL code here
beans = {


//  springConfig.addAlias 'fileStorageService', 'cleverCloudFSService'
    springConfig.addAlias 'fileStorageService', 'localFSService'
    localFSService(LocalFSService) {
        caminhoRepositorio=AmbienteExecucao.getCaminhoRepositorioArquivos()
    }

    localeResolver(org.springframework.web.servlet.i18n.SessionLocaleResolver) {
        defaultLocale = new Locale("pt","BR")
        java.util.Locale.setDefault(defaultLocale)
    }

/*
	importarFamiliasJava(ImportarFamiliasJavaService) {
		daoForJavaService = ref("daoForJavaService")
		importarFamiliasJava = ref("importarFamiliasJava")
		segurancaService = ref("segurancaService")
	}
*/
//	roleHierarchy(RoleHierarchyImpl)

    /**
     * Escolher a implementacao a usar para o servico de importacao de familias
     */
    servicoImportarFamilias(ImportarFamiliasBHService) { bean ->
        bean.autowire = 'byName'
//        sessionFactory = ref("sessionFactory")
    }

/*
Create Spring bean for Groovy SQL.
groovySql is the name of the bean and can be used
for injection.
*/
    groovySql(groovy.sql.Sql, ref('dataSource'))

    applicationContextHolder(ApplicationContextHolder) { bean ->
        bean.factoryMethod = 'getInstance'
    }

    segurancaListener(SegurancaListener) { bean ->
        bean.autowire = 'byName'
    }

    /**
     * Subsitui o mecanismo padrão do spring para instanciar usuarios de segurança usando a classe ApoiaSuasUser
     */
    userDetailsService(ApoiaSuasDetailsService) { bean ->
        bean.autowire = 'byName'
    }

    /**
     * Necessario para que um servico com escopo de "session" possa ser injetado (IOC) em um servico/taglib de escopo "singleton"
     * Toda chamada a um servico no escopo de sessao ou de request aa partir de um servico de escopo singleton (ou de uma taglib) precisa passar por um proxy (já chamadas de controllers, nao precisam)
     */
    customizacoesServiceProxy(org.springframework.aop.scope.ScopedProxyFactoryBean) {
        targetBeanName = 'customizacoesService'
        proxyTargetClass = true
    }

    /**
     * Sobrescrevendo a classe que implementa um dos passos do login
     */
//    authenticationSuccessHandler(org.apoiasuas.seguranca.ApoiaSuasSuccessHandler) {
//        /* Reusing the security configuration */
//        def conf = SpringSecurityUtils.securityConfig
//        /* Configuring the bean */
//        requestCache = ref('requestCache')
//        redirectStrategy = ref('redirectStrategy')
//        defaultTargetUrl = conf.successHandler.defaultTargetUrl
//        alwaysUseDefaultTargetUrl = conf.successHandler.alwaysUseDefault
//        targetUrlParameter = conf.successHandler.targetUrlParameter
//        ajaxSuccessUrl = conf.successHandler.ajaxSuccessUrl
//        useReferer = conf.successHandler.useReferer
//    }

}