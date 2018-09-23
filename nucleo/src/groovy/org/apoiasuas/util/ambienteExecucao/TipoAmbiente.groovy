package org.apoiasuas.util.ambienteExecucao

import org.apoiasuas.util.ApoiaSuasException
import org.codehaus.groovy.runtime.InvokerHelper
import org.hibernate.cfg.Configuration
import org.hibernate.dialect.Dialect

import javax.sql.DataSource
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.security.AccessController
import java.security.PrivilegedAction

/**
 * Responsavel por obter o parametros de configuracao (das conexoes de banco de dados ou da aplicacao como um todo)
 * Primeiramente, tentar obter estas configuracoes do arquivo DataSourcesApoiaSuas.config e ConfigApoiaSuas.config
 * Em seguida, tenta (e sobrescreve) com as configuracoes do arquivo DataSourcesApoiaSuas.properties e ConfigApoiaSuas.properties
 * Por ultimo, tenta (e sobrescreve) com as configuracoes nos parametros da JVM
 */
public abstract class TipoAmbiente {

    private static String DATA_SOURCES_CONFIG = 'DataSourcesApoiaSuas';
    private static String CONFIG = 'ConfigApoiaSuas';
    private static String AMBIENTE = 'ambiente';

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

    public String parametroTeste = "definido em TipoAmbiente.groovy"
    public String modo;

    private String bd;
    private String runtime;

/*
    public TipoAmbiente(String runtime, String modo) {
        throw new ApoiaSuasException("construtor invalido. use  TipoAmbiente(String bd, String runtime, String modo)");
    }
*/

    public TipoAmbiente(String bd, String runtime) {
        this.bd = bd;
        this.runtime = runtime;

        if (!this.bd || !this.runtime)
            throw new ApoiaSuasException("definicao  de banco de dados e ambiente de runtime nao fornecidas");

        Map config = [:];
        resolveParametrosGroovy(config, bd, runtime);
        resolveParametrosProperties(config, bd, runtime);
        resolveParametrosJVM(config, bd, runtime);

/*
        config.each { key, value ->
            if (!TipoAmbiente.metaClass.hasProperty(this, key))
                throw new ApoiaSuasException("parâmetro de configuração '${key}' não esperado em ${this.class.name}");
            this[key] = value;
        }
*/
//        if (Environment.current == Environment.DEVELOPMENT)
//            System.out.println(this.dump());
//        System.out.println("fim da configuracao de banco de dados");
    }

    public void guarda2(String key, String value) {
        if (!TipoAmbiente.metaClass.hasProperty(this, key))
            throw new ApoiaSuasException("parâmetro de configuração '${key}' não esperado em ${this.class.name}");
        this[key] = value;
    }

    /**
     * Faz o parse dos scripts de configuracao DataSourcesApoiaSuas.groovy e ConfigApoiaSuas.groovy, e armazena em 'config'
     */
    private void resolveParametrosGroovy(Map config, String bd, String runtime) {
        resolveParametrosEspecificoGroovy(DATA_SOURCES_CONFIG, config, bd, runtime);
        resolveParametrosEspecificoGroovy(CONFIG, config, bd, runtime);
        resolveParametrosEspecificoGroovy(AMBIENTE, config, bd, runtime);
    }

    private void resolveParametrosEspecificoGroovy(String script, Map config, String bd, String runtime) {
        Class scriptClass = null;
        try {
            scriptClass = AmbienteExecucao.getClassLoader().loadClass(script);
        } catch (ClassNotFoundException e) {
            /*classe nao existe - ignora*/
//            log.info("Configuracao "+script+".groovy nao encontrada. ignorando")
        }

        if (!scriptClass)
            return

        ConfigObject configRaiz = new ConfigSlurper().parse(scriptClass)
        ConfigObject configBD = new ConfigSlurper().parse(scriptClass)[bd]
        ConfigObject configRuntime = new ConfigSlurper().parse(scriptClass)[runtime]
        ConfigObject configBDRuntime = new ConfigSlurper().parse(scriptClass)[bd][runtime]
        configRaiz.each { key, value ->
            if (value instanceof ConfigObject)
                return;
            guarda2(key?.toString(), value?.toString())
//            config.put(key, value)
        }
        configBD.each { key, value ->
            if (value instanceof ConfigObject)
                return;
            guarda2(key?.toString(), value?.toString())
//            config.put(key, value)
        }
        configRuntime.each { key, value ->
            if (value instanceof ConfigObject)
                return;
            guarda2(key?.toString(), value?.toString())
//            config.put(key, value)
        }
        configBDRuntime.each { key, value ->
            if (value instanceof ConfigObject)
                return;
            guarda2(key?.toString(), value?.toString())
//            config.put(key, value)
        }
    }

    /**
     * Buscando as mesmas configuracoes via arquivos de propriedades DataSourcesApoiaSuas.properties e ConfigApoiaSuas.properties (util para alterar configuracoes sem precisar reiniciar o servidor)
     * Obs: sobrescreve os parametros definidos pelo metodo anterior (DSL groovy)
     */
    private void resolveParametrosProperties(Map config, String bd, String runtime) {
        resolveParametrosEspecificoProperties(DATA_SOURCES_CONFIG, config, bd, runtime);
        resolveParametrosEspecificoProperties(CONFIG, config, bd, runtime);
        resolveParametrosEspecificoProperties(AMBIENTE, config, bd, runtime);
    }

    private void resolveParametrosEspecificoProperties(String strProperties, Map config, String bd, String runtime) {
        String nomearquivo = '/' + strProperties + '.properties';
        if (! getClass().getResourceAsStream(nomearquivo))
            return;
        Properties properties = new Properties()
        properties.load(getClass().getResourceAsStream(nomearquivo));

        String prefixoBD = bd + ".";
        String prefixoBDRuntime = bd + "." + runtime + ".";
        String prefixoRuntime = runtime + ".";

        //primeiramente preenche os valores disponiveis na raiz (sem nehum prefixo)
        properties.each { key, value ->
            if (key.toString().contains('.'))
                return;
            guarda2(key, value)
//            config.put(key, value)
        }

        //preenche os valores disponiveis no prefixo bd. (sobrescreve quando coincidir o parametro com algum ja fornecido anteriormente)
        properties.each { key, value ->
            if (key.toString().startsWith(prefixoBD))
                key = key.toString().substring(prefixoBD.length())
            if (key.toString().contains('.'))
                return;
            guarda2(key, value)
//            config.put(key, value)
        }

        //preenche os valores disponiveis no prefixo runtime. (sobrescreve quando coincidir o parametro com algum ja fornecido anteriormente)
        properties.each { key, value ->
            if (key.toString().startsWith(prefixoRuntime))
                key = key.toString().substring(prefixoRuntime.length())
            if (key.toString().contains('.'))
                return;
            guarda2(key, value)
//            config.put(key, value)
        }

        //preenche os valores disponiveis no prefixo bd.runtime. (sobrescreve quando coincidir o parametro com algum ja fornecido anteriormente)
        properties.each { key, value ->
            if (key.toString().startsWith(prefixoBDRuntime))
                key = key.toString().substring(prefixoBDRuntime.length())
            if (key.toString().contains('.'))
                return;
            guarda2(key, value)
//            config.put(key, value)
        }

    }

    /**
     * Buscando as mesmas configuracoes em parametros da JVM
     * Ignora os prefixos de BD e runtime, e usa o prefixo org.apoiasuas
     * Obs: sobrescreve os parametros definidos pelos metodos anteriores (DSL groovy e arquivo properties)
     */
    private void resolveParametrosJVM(Map config, String bd, String runtime) {
        resolveParametrosEspecificoJVM('org.apoiasuas.', config);
        resolveParametrosEspecificoJVM('org.apoiasuas.ds.', config);
        //sobrescreve os parametros por outros identicos mas especificados para o modo (prod, demo, etc)
        resolveParametrosEspecificoJVM(modo+'_org.apoiasuas.', config);
        resolveParametrosEspecificoJVM(modo+'_org.apoiasuas.ds.', config);
    }

    private void resolveParametrosEspecificoJVM(String prefixoCompleto, Map config) {
        this.class.fields.each { field ->
            if (! field.name?.contains("password") && sysProperties(prefixoCompleto+field.name))
                System.out.println("armazenando parametro JVM em TipoAmbiente: "+prefixoCompleto+field.name + " -> " + sysProperties(prefixoCompleto+field.name));
            if (sysProperties(prefixoCompleto+field.name))
                guarda2(field.name, sysProperties(prefixoCompleto+field.name))
//                config.put(field.name, sysProperties(prefixoCompleto+field.name));
        }
    }

    /**
     * Busca um parâmetro de configuração
     * @param nome
     * @return
     */
    public String sysProperties(String nome) {
        if (! nome)
            return null
        String result = System.properties[modo+"_"+nome]?.toString();
        if (! result)
//            try {
                result = System.properties[nome]?.toString();
//            } catch (Exception ex) {
//                System.out.println("nome");
//                System.out.println(nome);
//            }
        return result;
    }

    public Map<String, String> listaPropriedades() {
        Map<String, String> result = [:];

        //copiado de DefaultGroovyMethods.dump()
        Class klass = this.getClass();
        boolean groovyObject = this instanceof GroovyObject;
        while (klass != null) {
            for (final Field field : klass.getDeclaredFields()) {
                if ((field.getModifiers() & Modifier.STATIC) == 0) {
                    if (groovyObject && field.getName().equals("metaClass")) {
                        continue;
                    }
                    if (field.getName().toLowerCase().contains("pass") || field.getName().toLowerCase().contains("senha")) {
                        continue;
                    }
                    AccessController.doPrivileged(new PrivilegedAction() {
                        public Object run() {
                            field.setAccessible(true);
                            return null;
                        }
                    });

                    String key = field.getName();
                    String value;
                    try {
                        value = InvokerHelper.toString(field.get(this));
                    }
                    catch (Exception e) {
                        value = e.toString();
                    }
                    result << [(key): value]
                }
            }

            klass = klass.getSuperclass();
        }

        result << [datasource: sysProperties('org.apoiasuas.datasource')]
        return result.sort{ it.key };
    }

    abstract public Dialect getDialect();

    abstract public String[] atualizacoesDDL(DataSource dataSource, Configuration conf);
}