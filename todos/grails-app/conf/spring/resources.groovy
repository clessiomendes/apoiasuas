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
import org.apoiasuas.ambienteExecucao.AmbienteExecucao
import org.apoiasuas.util.ApplicationContextHolder
import org.codehaus.groovy.grails.commons.spring.BeanConfiguration
import org.codehaus.groovy.grails.commons.spring.DefaultBeanConfiguration

// Place your Spring DSL code here
beans = {

    springConfig.addAlias 'fileStorageService', 'localFSService'
    localFSService(LocalFSService) {
        caminhoRepositorio=AmbienteExecucao.CONFIGURACOES_FACADE.caminhoRepositorio
    }

    localeResolver(org.springframework.web.servlet.i18n.SessionLocaleResolver) {
        defaultLocale = new Locale("pt","BR")
        java.util.Locale.setDefault(defaultLocale)
    }

    /**
     * Escolher a implementacao a usar para o servico de importacao de familias
     */
    servicoImportarFamilias(ImportarFamiliasBHService) { BeanConfiguration bean ->
        bean.autowire = 'byName'
//        sessionFactory = ref("sessionFactory")
    }

/*
Create Spring bean for Groovy SQL.
groovySql is the name of the bean and can be used
for injection.
*/
    groovySql(groovy.sql.Sql, ref('dataSource'))

    applicationContextHolder(ApplicationContextHolder) { BeanConfiguration bean ->
        bean.factoryMethod = 'getInstance'
    }

    segurancaListener(SegurancaListener) { BeanConfiguration bean ->
        bean.autowire = 'byName'
    }

    /**
     * Subsitui o mecanismo padrão do spring para instanciar usuarios de segurança usando a classe ApoiaSuasUser
     */
    userDetailsService(ApoiaSuasDetailsService) { BeanConfiguration bean ->
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

    menuBuilder(org.apoiasuas.seguranca.ASMenuBuilder) { BeanConfiguration bean ->
        bean.autowire = 'byName'
    }

//    controllerFacade(org.apoiasuas.facade.ControllerFacade) { BeanConfiguration bean ->
//        bean.autowire = 'byName'
//    }

}