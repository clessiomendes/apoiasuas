import org.apoiasuas.formulario.PreDefinidos
import org.apoiasuas.ambienteExecucao.AmbienteExecucao
import org.apoiasuas.http.PreGrailsServletContextListener

import javax.naming.InitialContext

//final String PARAMETROS_MYSQL_VIA_URL = "?profileSQL=true"
//final String PARAMETROS_MYSQL_VIA_URL = "?connectTimeout=0&socketTimeout=0&autoReconnectForPools=true&profileSQL=true"

hibernate {
    cache.use_second_level_cache = true //sem cache agiliza importacoes em batch?
    cache.use_query_cache = false
//    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
    flush.mode = 'manual' // OSIV session flush mode outside of transactional context
    if (! AmbienteExecucao.isProducao()) {
        generate_statistics = true
        format_sql = false //sql em multiplas linhas e identada
        use_sql_comments = true
    }
}

dataSource {
    dbCreate = ""
    if (! AmbienteExecucao.isProducao() && "true".equalsIgnoreCase(AmbienteExecucao.sysProperties('org.apoiasuas.recriarBD'))) {
        println("recriando banco de dados")
        dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
    }
    pooled = true
    jmxExport = true

    dialect = AmbienteExecucao.CONFIGURACOES_FACADE.dialect
//    driverClassName = "net.sf.log4jdbc.DriverSpy"   usado pelo log4jdb para fazer log de sqls
    driverClassName = AmbienteExecucao.CONFIGURACOES_FACADE.driverClassName

    url = AmbienteExecucao.CONFIGURACOES_FACADE.url
    System.out.println("AmbienteExecucao.CONFIGURACOES_FACADE.username "+AmbienteExecucao.CONFIGURACOES_FACADE.username);
    username = AmbienteExecucao.CONFIGURACOES_FACADE.username
    password = AmbienteExecucao.CONFIGURACOES_FACADE.password

//configuracao de pool:
    properties {
        minEvictableIdleTimeMillis = AmbienteExecucao.CONFIGURACOES_FACADE.minEvictableIdleTimeMillis
        timeBetweenEvictionRunsMillis = AmbienteExecucao.CONFIGURACOES_FACADE.timeBetweenEvictionRunsMillis
        numTestsPerEvictionRun = AmbienteExecucao.CONFIGURACOES_FACADE.numTestsPerEvictionRun
        testOnBorrow = AmbienteExecucao.CONFIGURACOES_FACADE.testOnBorrow
        testOnConnect = AmbienteExecucao.CONFIGURACOES_FACADE.testOnConnect
        testWhileIdle = AmbienteExecucao.CONFIGURACOES_FACADE.testWhileIdle
        testOnReturn = AmbienteExecucao.CONFIGURACOES_FACADE.testOnReturn
        validationQuery = AmbienteExecucao.CONFIGURACOES_FACADE.validationQuery
        maxActive = AmbienteExecucao.CONFIGURACOES_FACADE.maxActive
        initialSize = AmbienteExecucao.CONFIGURACOES_FACADE.initialSize
        minIdle = AmbienteExecucao.CONFIGURACOES_FACADE.minIdle
        maxIdle = AmbienteExecucao.CONFIGURACOES_FACADE.maxIdle
    }
}

dataSource_log {
    dbCreate = ""
    if (! AmbienteExecucao.isProducao() && "true".equalsIgnoreCase(AmbienteExecucao.sysProperties('org.apoiasuas.recriarBD'))) {
        println("recriando banco de dados")
        dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
    }
    pooled = true
    jmxExport = true

    dialect = AmbienteExecucao.CONFIGURACOES_FACADE.dialect
    driverClassName = AmbienteExecucao.CONFIGURACOES_FACADE.driverClassName

    url = AmbienteExecucao.CONFIGURACOES_FACADE.url_logs
    username = AmbienteExecucao.CONFIGURACOES_FACADE.username_logs
    password = AmbienteExecucao.CONFIGURACOES_FACADE.password_logs

//configuracao de pool:
    properties {
        minEvictableIdleTimeMillis = AmbienteExecucao.CONFIGURACOES_FACADE.minEvictableIdleTimeMillis
        timeBetweenEvictionRunsMillis = AmbienteExecucao.CONFIGURACOES_FACADE.timeBetweenEvictionRunsMillis
        numTestsPerEvictionRun = AmbienteExecucao.CONFIGURACOES_FACADE.numTestsPerEvictionRun
        testOnBorrow = AmbienteExecucao.CONFIGURACOES_FACADE.testOnBorrow
        testOnConnect = AmbienteExecucao.CONFIGURACOES_FACADE.testOnConnect
        testWhileIdle = AmbienteExecucao.CONFIGURACOES_FACADE.testWhileIdle
        testOnReturn = AmbienteExecucao.CONFIGURACOES_FACADE.testOnReturn
        validationQuery = AmbienteExecucao.CONFIGURACOES_FACADE.validationQuery
        maxActive = AmbienteExecucao.CONFIGURACOES_FACADE.maxActive
        initialSize = AmbienteExecucao.CONFIGURACOES_FACADE.initialSize
        minIdle = AmbienteExecucao.CONFIGURACOES_FACADE.minIdle
        maxIdle = AmbienteExecucao.CONFIGURACOES_FACADE.maxIdle
    }
}
