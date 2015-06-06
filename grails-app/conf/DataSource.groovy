import org.apoiasuas.formulario.PreDefinidos
import org.apoiasuas.util.AmbienteExecucao

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
    //dbCreate="validate"
    if (! AmbienteExecucao.isProducao() && "true".equalsIgnoreCase(AmbienteExecucao.sysProperties('org.apoiasuas.recriarBD'))) {
        println("recriando banco de dados")
        dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
    }
    pooled = true
    jmxExport = true

    switch (AmbienteExecucao.CURRENT) {
        case AmbienteExecucao.LOCAL_MYSQL:
            println("Ambiente: MySQL local " + environment)
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
            url = "jdbc:mysql://localhost/apoiasuas" //+ PARAMETROS_MYSQL_VIA_URL
            username = "admin"
            password = "senha"
            break

        case AmbienteExecucao.LOCAL_POSTGRES:
            println("Ambiente: Postgres local " + environment)
            driverClassName = "org.postgresql.Driver"
            dialect = "org.hibernate.dialect.PostgreSQLDialect"
            url = "jdbc:postgresql://localhost:5432/apoiasuas"
            username = "postgres"
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

        case AmbienteExecucao.APPFOG_POSTGRES_VALID:
            println("Ambiente: Postgres Appfog " + environment)
            def envVar = System.env.VCAP_SERVICES
            def credentials = envVar ? grails.converters.JSON.parse(envVar)["postgresql-9.1"][0]["credentials"] : null
            driverClassName = "org.postgresql.Driver"
            dialect = "org.hibernate.dialect.PostgreSQLDialect"
            url = credentials ? "jdbc:postgresql://${credentials.hostname}:${credentials.port}/${credentials.name}" : ""
            username = credentials ? credentials.username : ""
            password = credentials ? credentials.password : ""
            break

        case AmbienteExecucao.APPFOG_POSTGRES_PROD:
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
//configuracao de pool:
    properties {
        maxActive = 5
        initialSize = 5
        minEvictableIdleTimeMillis = 1800000
        timeBetweenEvictionRunsMillis = 1800000
        numTestsPerEvictionRun = 3
        testOnBorrow = true
        testOnConnect = true
        testWhileIdle = true
        testOnReturn = true
        validationQuery = "SELECT 1"
    }

}

/*
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:~/test"
//            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:prodDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
            properties {
                // See http://grails.org/doc/latest/guide/conf.html#dataSource for documentation
                jmxEnabled = true
                initialSize = 5
                maxActive = 50
                minIdle = 5
                maxIdle = 25
                maxWait = 10000
                maxAge = 10 * 60000
                timeBetweenEvictionRunsMillis = 5000
                minEvictableIdleTimeMillis = 60000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 15000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                jdbcInterceptors = "ConnectionState"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
            }
        }
    }
*/

/*                                                OPENSHIFT
MySQL 5.5 database added.  Please make note of these credentials:

       Root User: adminXP4sKFZ
   Root Password: 22uLUgGunTZW
   Database Name: testes

Connection URL: mysql://$OPENSHIFT_MYSQL_DB_HOST:$OPENSHIFT_MYSQL_DB_PORT/

You can manage your new MySQL database by also embedding phpmyadmin.
The phpmyadmin username and password will be the same as the MySQL credentials above.
*/
