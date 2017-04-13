postgre {
    dialect = "org.hibernate.dialect.PostgreSQLDialect"
    driverClassName = "org.postgresql.Driver"

    minEvictableIdleTimeMillis = 1800000
    timeBetweenEvictionRunsMillis = 1800000
    numTestsPerEvictionRun = 3
    testOnBorrow = true
    testOnConnect = true
    testWhileIdle = true
    testOnReturn = true
    validationQuery = "SELECT 1*1"
    maxActive = 3
    initialSize = 1
    minIdle = 1
    maxIdle = 1

    local {
        url = "jdbc:postgresql://localhost:5432/apoiasuas"
        username = "postgres"
        password = "senha"

        url_logs = "jdbc:postgresql://localhost:5432/apoiasuas_log"
        username_logs = "postgres"
        password_logs = "senha"
    }

    cleverCloud {
        username = System.getProperties().getProperty("POSTGRESQL_ADDON_USER")
        password = System.getProperties().getProperty("POSTGRESQL_ADDON_PASSWORD")
        url = "jdbc:postgresql://"+
                System.getProperties().getProperty("POSTGRESQL_ADDON_HOST")+":"+
                System.getProperties().getProperty("POSTGRESQL_ADDON_PORT")+"/"+
                System.getProperties().getProperty("POSTGRESQL_ADDON_DB");

        username_logs = "uwbhk7dyxjsqyljjw0qj"
        password_logs = "cKaeAGNxltuXxLI8iKNF"
        url_logs = "postgresql://cKaeAGNxltuXxLI8iKNF@bcxpj29zvmrjzfl-postgresql.services.clever-cloud.com:" + //host
                "5432/" + //port
                "bcxpj29zvmrjzfl"; //db
    }
}
