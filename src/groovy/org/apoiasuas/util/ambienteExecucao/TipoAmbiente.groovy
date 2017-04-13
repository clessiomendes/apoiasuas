package org.apoiasuas.util.ambienteExecucao

import grails.util.Environment
import groovy.util.logging.Log4j
import org.apoiasuas.util.AmbienteExecucao
import org.apoiasuas.util.ApoiaSuasException
import org.hibernate.dialect.Dialect
import org.hibernate.dialect.PostgreSQL81Dialect

@Log4j
/**
 * Responsavel por obter os parametros de configuracao das conexoes de banco de dados
 * Primeiramente, tentar obter estas configuracoes do arquivo DataSourcesApoiaSuas.config
 * Em seguida, tenta (e sobrescreve) com as configuracoes do arquivo DataSourcesApoiaSuas.properties
 * Por ultimo, tenta (e sobrescreve) com as configuracoes nos parametros da JVM
 */
public abstract class TipoAmbiente {

    private static String DATA_SOURCES_CONFIG = 'DataSourcesApoiaSuas';

    public String url = "fool"
    public String username = "fool"
    public String password = "fool"

    public String url_logs = "fool"
    public String username_logs = "fool"
    public String password_logs = "fool"

    public String dialect = "fool"
    public String driverClassName = "fool"

    //pool:
    public String minEvictableIdleTimeMillis = "1800000"
    public String timeBetweenEvictionRunsMillis = "1800000"
    public String numTestsPerEvictionRun = "3"
    public String maxActive = "3"
    public String initialSize = "1"
    public String minIdle = "1"
    public String maxIdle = "1"
    public String testOnBorrow = "true"
    public String testOnConnect = "true"
    public String testWhileIdle = "true"
    public String testOnReturn = "true"
    public String validationQuery = "SELECT 1*1"

    public TipoAmbiente(String bd, String runtime) {
        System.out.println("Definicao de banco de dados 2: ${bd} ${runtime}")
        if (!bd || !runtime)
            throw new ApoiaSuasException("definicao de banco de dados e ambiente de runtime nao fornecidas");

        Map config = [:];
        resolveParametrosGroovy(config, bd, runtime);
        resolveParametrosProperties(config, bd, runtime);
        resolveParametrosJVM(config, bd, runtime);

        config.each { key, value ->
            if (!TipoAmbiente.metaClass.hasProperty(this, key))
                throw new ApoiaSuasException("parâmetro de configuração '${key}' não esperado em ${this.class.name}");
            this[key] = value;
        }
        if (Environment.current == Environment.DEVELOPMENT)
            System.out.println(this.dump());
        System.out.println("fim da configuracao de banco de dados");
    }

    /**
     * Faz o parse do script de configuracao DataSourcesApoiaSuas.groovy e cria um ConfigObject para acesso a essas configuracoes
     */
    private void resolveParametrosGroovy(Map config, String bd, String runtime) {
        Class scriptClass = AmbienteExecucao.getClassLoader().loadClass(DATA_SOURCES_CONFIG);
        if (!scriptClass)
            return

        ConfigObject configRaiz = new ConfigSlurper().parse(scriptClass)
        ConfigObject configBD = new ConfigSlurper().parse(scriptClass)[bd]
        ConfigObject configBDRuntime = new ConfigSlurper().parse(scriptClass)[bd][runtime]
        configRaiz.each { key, value ->
            if (value instanceof ConfigObject)
                return;
            config.put(key, value)
        }
        configBD.each { key, value ->
            if (value instanceof ConfigObject)
                return;
            config.put(key, value)
        }
        configBDRuntime.each { key, value ->
            if (value instanceof ConfigObject)
                return;
            config.put(key, value)
        }
    }

    /**
     * Buscando as mesmas configuracoes via arquivo de propriedades (util para alterar configuracoes sem precisar reiniciar o servidor)
     * Obs: sobrescreve os parametros definidos pelo metodo anterior (DSL groovy)
     */
    private void resolveParametrosProperties(Map config, String bd, String runtime) {
        String nomearquivo = '/' + DATA_SOURCES_CONFIG + '.properties';
        System.out.println(nomearquivo);
        if (! getClass().getResourceAsStream(nomearquivo))
            return;
        Properties properties = new Properties()
        properties.load(getClass().getResourceAsStream(nomearquivo));

/*
        File propertiesFile = new File(DATA_SOURCES_CONFIG + '.properties')
        if (! propertiesFile.exists())
            return;

        Properties properties = new Properties()
        propertiesFile.withInputStream {
            properties.load(it)
        }
*/
        String prefixoBD = bd + ".";
        String prefixoCompleto = prefixoBD + runtime + ".";

        //primeiramente preenche os valores disponiveis na raiz (sem nehum prefixo)
        properties.each { key, value ->
            if (key.toString().contains('.'))
                return;
            config.put(key, value)
        }

        //preenche os valores disponiveis no prefixo bd. (sobrescreve quando coincidir o parametro com algum ja fornecido anteriormente)
        properties.each { key, value ->
            if (key.toString().startsWith(prefixoBD))
                key = key.toString().substring(prefixoBD.length())
            if (key.toString().contains('.'))
                return;
            config.put(key, value)
        }

        //preenche os valores disponiveis no prefixo bd.runtime. (sobrescreve quando coincidir o parametro com algum ja fornecido anteriormente)
        properties.each { key, value ->
            if (key.toString().startsWith(prefixoCompleto))
                key = key.toString().substring(prefixoCompleto.length())
            if (key.toString().contains('.'))
                return;
            config.put(key, value)
        }
    }

    /**
     * Buscando as mesmas configuracoes em parametros da JVM
     * Ignora os prefixos de BD e runtime, e usa o prefixo org.apoiasuas
     * Obs: sobrescreve os parametros definidos pelos metodos anteriores (DSL groovy e arquivo properties)
     */
    private void resolveParametrosJVM(Map config, String bd, String runtime) {
        String prefixoCompleto = 'org.apoiasuas.ds.'
        this.class.fields.each { field ->
            if (System.properties.getProperty(prefixoCompleto+field.name))
                config.put(field.name, System.properties.getProperty(prefixoCompleto+field.name));
        }
    }

    abstract public Dialect getDialect();
}