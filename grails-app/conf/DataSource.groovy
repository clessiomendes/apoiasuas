import org.apoiasuas.formulario.PreDefinidos
import org.apoiasuas.util.AmbienteExecucao
import org.apoiasuas.http.PreGrailsServletContextListener

import javax.naming.InitialContext

final String PARAMETROS_MYSQL_VIA_URL = "?profileSQL=true"
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

    dialect = AmbienteExecucao.CURRENT2.dialect
//    driverClassName = "net.sf.log4jdbc.DriverSpy"   usado pelo log4jdb para fazer log de sqls
    driverClassName = AmbienteExecucao.CURRENT2.driverClassName

    url = AmbienteExecucao.CURRENT2.url
    username = AmbienteExecucao.CURRENT2.username
    password = AmbienteExecucao.CURRENT2.password

//configuracao de pool:
    properties {
        minEvictableIdleTimeMillis = AmbienteExecucao.CURRENT2.minEvictableIdleTimeMillis
        timeBetweenEvictionRunsMillis = AmbienteExecucao.CURRENT2.timeBetweenEvictionRunsMillis
        numTestsPerEvictionRun = AmbienteExecucao.CURRENT2.numTestsPerEvictionRun
        testOnBorrow = AmbienteExecucao.CURRENT2.testOnBorrow
        testOnConnect = AmbienteExecucao.CURRENT2.testOnConnect
        testWhileIdle = AmbienteExecucao.CURRENT2.testWhileIdle
        testOnReturn = AmbienteExecucao.CURRENT2.testOnReturn
        validationQuery = AmbienteExecucao.CURRENT2.validationQuery
        maxActive = AmbienteExecucao.CURRENT2.maxActive
        initialSize = AmbienteExecucao.CURRENT2.initialSize
        minIdle = AmbienteExecucao.CURRENT2.minIdle
        maxIdle = AmbienteExecucao.CURRENT2.maxIdle
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

    dialect = AmbienteExecucao.CURRENT2.dialect
    driverClassName = AmbienteExecucao.CURRENT2.driverClassName

    url = AmbienteExecucao.CURRENT2.url_logs
    username = AmbienteExecucao.CURRENT2.username_logs
    password = AmbienteExecucao.CURRENT2.password_logs

//configuracao de pool:
    properties {
        minEvictableIdleTimeMillis = AmbienteExecucao.CURRENT2.minEvictableIdleTimeMillis
        timeBetweenEvictionRunsMillis = AmbienteExecucao.CURRENT2.timeBetweenEvictionRunsMillis
        numTestsPerEvictionRun = AmbienteExecucao.CURRENT2.numTestsPerEvictionRun
        testOnBorrow = AmbienteExecucao.CURRENT2.testOnBorrow
        testOnConnect = AmbienteExecucao.CURRENT2.testOnConnect
        testWhileIdle = AmbienteExecucao.CURRENT2.testWhileIdle
        testOnReturn = AmbienteExecucao.CURRENT2.testOnReturn
        validationQuery = AmbienteExecucao.CURRENT2.validationQuery
        maxActive = AmbienteExecucao.CURRENT2.maxActive
        initialSize = AmbienteExecucao.CURRENT2.initialSize
        minIdle = AmbienteExecucao.CURRENT2.minIdle
        maxIdle = AmbienteExecucao.CURRENT2.maxIdle
    }
}

/*
    switch (AmbienteExecucao.CURRENT) {
        case AmbienteExecucao.LOCAL_POSTGRES:
            println("Ambiente: Postgres local " + environment)
            dialect = "org.hibernate.dialect.PostgreSQLDialect"
            driverClassName = "org.postgresql.Driver"

//            String contextPath = PreGrailsServletContextListener.getContextPath();
//            if (contextPath)
//                jndiName = "java:comp/env/jdbc/dslocal"
//            else
//                throw new RuntimeException("parâmetro contextPath não acessível durante inicialização")
//                jndiName = "java:comp/env/jdbc/ds" + contexPath.substring(1)

// -as configuracoes abaixo podem ser migradas para web-app/META-INF/context.xml
            url = "jdbc:postgresql://localhost:5432/apoiasuas"
            username = "postgres"
            password = "senha"
            break

        case AmbienteExecucao.CLEVERCLOUD_POSTGRES_PROD:
            println("Ambiente: Postgres clever-cloud " + environment)
            dialect = "org.hibernate.dialect.PostgreSQLDialect"

//            String contextPath = PreGrailsServletContextListener.getContextPath();
//            if (contextPath)
//                jndiName = "java:comp/env/jdbc/ds" + contextPath.substring(1) //retira a "/" do caminho
//            else
//                throw RuntimeException("parâmetro contextPath não acessível durante inicialização")

// -as configuracoes abaixo podem ser migradas para web-app/META-INF/context.xml
            driverClassName = "org.postgresql.Driver"
            host = System.getProperties().getProperty("POSTGRESQL_ADDON_HOST")
            port = System.getProperties().getProperty("POSTGRESQL_ADDON_PORT")
            dbname = System.getProperties().getProperty("POSTGRESQL_ADDON_DB")
            username = System.getProperties().getProperty("POSTGRESQL_ADDON_USER")
            password = System.getProperties().getProperty("POSTGRESQL_ADDON_PASSWORD")
            dialect = "org.hibernate.dialect.PostgreSQLDialect"
            url = "jdbc:postgresql://${host}:${port}/${dbname}"
            println(url);
            break

        case AmbienteExecucao.CLEVERCLOUD_POSTGRES_VALID:
            println("Ambiente: Postgres clever-cloud " + environment)
            driverClassName = "org.postgresql.Driver"
            host = System.getProperties().getProperty("POSTGRESQL_ADDON_HOST")
            port = System.getProperties().getProperty("POSTGRESQL_ADDON_PORT")
            dbname = System.getProperties().getProperty("POSTGRESQL_ADDON_DB")
            username = System.getProperties().getProperty("POSTGRESQL_ADDON_USER")
            password = System.getProperties().getProperty("POSTGRESQL_ADDON_PASSWORD")
            dialect = "org.hibernate.dialect.PostgreSQLDialect"
            url = "jdbc:postgresql://${host}:${port}/${dbname}"
            break

        case AmbienteExecucao.LOCAL_MYSQL:
            println("Ambiente: MySQL local " + environment)
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
            url = "jdbc:mysql://localhost/apoiasuas" //+ PARAMETROS_MYSQL_VIA_URL
            username = "admin"
            password = "senha"
            break

        case AmbienteExecucao.LOCAL_H2:
            println("Ambiente: H2 local " + environment)
            driverClassName = "org.h2.Driver"
            url = "jdbc:h2:~/dev;AUTO_SERVER=TRUE"  //mixed mode
            username = "sa"
            password = ""
            break

        case AmbienteExecucao.APPFOG_MYSQL:
            println("Ambiente: Mysql Appfog " + environment)
            def envVar = System.env.VCAP_SERVICES
            def credentials = envVar ? grails.converters.JSON.parse(envVar)["mysql-5.1"][0]["credentials"] : null // See more at: http://refactr.com/blog/2012/08/grails-tip-deploy-to-the-cloud-with-appfog/#sthash.Ar8jHzD5.dpuf
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
            url = (credentials ? "jdbc:mysql://${credentials.hostname}:${credentials.port}/${credentials.name}" : "") //+ PARAMETROS_MYSQL_VIA_URL
            username = credentials ? credentials.username : ""
            password = credentials ? credentials.password : ""
            break

        case AmbienteExecucao.CLEARDB_MYSQL:
            println("Ambiente: Cleardb/Mysql Appfog " + environment)
            URI dbUri = new URI(System.env.CLEARDB_DATABASE_URL ?: "");
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
            username = dbUri?.getUserInfo()?.split(":")?.getAt(0)
            password = dbUri?.getUserInfo()?.split(":")?.getAt(1)
            url = "jdbc:mysql://" + dbUri?.getHost() + dbUri?.getPath() //+ PARAMETROS_MYSQL_VIA_URL
            break

        case [AmbienteExecucao.APPFOG_POSTGRES_PROD, AmbienteExecucao.APPFOG_POSTGRES_VALID]:
            println("Ambiente: Postgres Appfog " + environment)
            def envVar = System.env.VCAP_SERVICES
            def credentials = envVar ? grails.converters.JSON.parse(envVar)["postgresql-9.1"][0]["credentials"] : null
            driverClassName = "org.postgresql.Driver"
            dialect = "org.hibernate.dialect.PostgreSQLDialect"
            url = credentials ? "jdbc:postgresql://${credentials.hostname}:${credentials.port}/${credentials.name}" : ""
            username = credentials ? credentials.username : ""
            password = credentials ? credentials.password : ""
            break

        default:
            throw new RuntimeException("tipo de banco de dados nao reconhecido")
    }
*/
