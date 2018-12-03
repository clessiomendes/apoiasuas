package org.apoiasuas.ambienteExecucao

import org.apoiasuas.util.ApoiaSuasException
import org.codehaus.groovy.runtime.InvokerHelper

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.security.AccessController
import java.security.PrivilegedAction

/**
 * Classe para processar e armazenar todos os parametros de configuracao da aplicacao.
 * Todos os parametros esperados pela aplicacao devem ser declarados como propriedades desta classe e serao automaticamente
 * buscados em diversas fontes de configuracao, na seguinte ordem a seguir.
 * O processamento é feito na subida da aplicação (na instancia singleton estatica desta classe, definida em
 * AmbienteExecucao.CURRENT) e não depende de nenhum framework (Grails, Sping, etc) para ser feito.
 *
 * 1) o valor default (definido com uma atribuicao direta na declaracao da propriedade desta classe)
 * 2) parametros de configuracao nas classes groovy (portanto, compiladas) DataSourcesApoiaSuas.config e ConfigApoiaSuas.config
 * 3) parametros de configuracao nos arquivos (nao compilados e editaveis apos o deploy, no proprio servidor) DataSourcesApoiaSuas.properties e ConfigApoiaSuas.properties
 * 4) variaveis de ambiente definidas ou no sistema operacional ou na linha de comando do java
 *
 * Além disso, dentro de cada arquivo de configuracoes (passos 2 e 3), a "profundidade" com que cada parametro e declarado determina a
 * sua precedencia. Ou seja, se um parametro e definido na raiz ele perde em procedencia para o mesmo parametro definido dentro de
 * outro contexto e assim por diante. Exemplo: Em
 * username=usuariolocal
 * servidor.username=usuarioservidor
 * o conteúdo final do parametro 'username' quando é definido um 'runtimeHospedagem' como 'servidor' será 'usuarioservidor'. Já se não houver
 * nenhum 'runtimeHospedagem' definido, o valor final sera 'usuariolocal'
 *
 * Caso seja necessario extender a classe para um runtime especifico (com configuracoes especificas do servidor de
 * aplicacao ou do ambiente de hospedagem), novos parametros podem ser definidos como propriedades das classes decendentes
 * e serao automaticamente alimentados da mesma forma.
 *
 * Para se ter acesso ao valor definido nessas configuracoes ao longo do codigo, basta acessar estaticamente as propriedades
 * desta classe
 */
public class ConfiguracoesFacade {

    private static String DATA_SOURCES_CONFIG = 'DataSourcesApoiaSuas';
    private static String CONFIG = 'ConfigApoiaSuas';
    private static String AMBIENTE = 'ambiente';

    private static String MENSAGEM_ERRO_VARIAVEIS_OBRIGATORIAS =
    "Definicao de banco de dados nao fornecida como variavel de ambiente ou na linha de comando.\n" +
    "Tal parametro e necessários para decidir o banco de dados a ser utilizado. Exemplo:\n" +
    "java ... -Dorg.apoiasuas.bancoDeDados=postgres";


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

    public String parametroTeste = "definido em ConfiguracoesFacade.groovy"
    public String modo;
    /**
     * o caminho para o repositorio é, por default, a pasta do usuario do sistema operacional
     */
    public String caminhoRepositorio;

    public String bancoDeDados;
    public String runtimeHospedagem;

    public ConfiguracoesFacade() {
        bancoDeDados = System.properties['org.apoiasuas.bancoDeDados']?.toString();
        runtimeHospedagem = System.properties['org.apoiasuas.runtimeHospedagem']?.toString();

        if (!bancoDeDados)
            throw new ApoiaSuasException(MENSAGEM_ERRO_VARIAVEIS_OBRIGATORIAS);
//        Map config = [:];
        antesResolveParametros();
        resolveParametrosGroovy();
        resolveParametrosProperties();
        resolveParametrosJVM();
        aposSetCaminhoRepositorio();
    }

    /**
     * Ponto de interceptacao para ser utilizado por classes espcializadas
     */
    protected void antesResolveParametros() {
    }

    /**
     * Ponto de interceptacao para ser utilizado por classes espcializadas
     */
    protected void aposSetCaminhoRepositorio() {
        if (! caminhoRepositorio)
            caminhoRepositorio = sysProperties("user.home")
        else if (! caminhoRepositorio.startsWith(File.separator))
            caminhoRepositorio = sysProperties("user.home") + File.separator + caminhoRepositorio;
    }

/**
     * Gera instancia sigleton da fachada de configuracoes.
     * Caso exista uma classe correspondente ao nome da class definida na vairavel de ambiente runtimeHospedagem,
     * instancia o singleton aa partir desta classe. Se nao, gera simplesmente uma instancia desta classe
     * @return
     */
    public static ConfiguracoesFacade novaInstancia() {
        //Se nao for definido nenhum runtime, usa a implementacao padrao
        String s = System.properties['org.apoiasuas.runtimeHospedagem']?.toString();
        if (! s)
            return new ConfiguracoesFacade();
        //Converte para tudo minusculo e somente primeira letra maiuscula
        s = org.apache.commons.lang.StringUtils.capitalize(s.toLowerCase());
        s = ConfiguracoesFacade.package.name+".Configuracoes"+s+"Facade";
        try {
            Class<?> clazz = Class.forName(s);
            Constructor<?> ctor = clazz.getConstructor();
            return ctor.newInstance();
        } catch (ClassNotFoundException e) {
            //Se for definido um runtime, mas nao houver implementacao especifica, usa a implementacao padrao
            e.printStackTrace();
            System.out.println("info: Classe especializada de configuracoes nao encontrada. Seguindo com comportamento padrao - "+s);
            return new ConfiguracoesFacade();
        }
    }

    protected void guardaConfiguracao(String key, String value) {
        if (!ConfiguracoesFacade.metaClass.hasProperty(this, key))
            throw new ApoiaSuasException("parâmetro de configuração '${key}' não esperado em ${this.class.name}");
        this[key] = value;
    }

    /**
     * Faz o parse dos scripts de configuracao DataSourcesApoiaSuas.groovy e ConfigApoiaSuas.groovy, e armazena em 'config'
     */
    private void resolveParametrosGroovy() {
        resolveParametrosEspecificoGroovy(DATA_SOURCES_CONFIG);
        resolveParametrosEspecificoGroovy(CONFIG);
        resolveParametrosEspecificoGroovy(AMBIENTE);
    }

    private void resolveParametrosEspecificoGroovy(String script) {
        Class scriptClass = null;
        try {
            scriptClass = AmbienteExecucao.getClassLoader().loadClass(script);
        } catch (ClassNotFoundException e) {
            /*classe nao existe - ignora*/
        }

        if (!scriptClass)
            return

        processaConfiguracoes(new ConfigSlurper().parse(scriptClass))
        processaConfiguracoes(new ConfigSlurper().parse(scriptClass)[bancoDeDados])
        if (runtimeHospedagem) {
            processaConfiguracoes(new ConfigSlurper().parse(scriptClass)[runtimeHospedagem])
            processaConfiguracoes(new ConfigSlurper().parse(scriptClass)[bancoDeDados][runtimeHospedagem])
            processaConfiguracoes(new ConfigSlurper().parse(scriptClass)[runtimeHospedagem][bancoDeDados])
        }
    }

    private void processaConfiguracoes(ConfigObject configObject) {
        configObject.each { key, value ->
            if (value instanceof ConfigObject)
                return;
            guardaConfiguracao(key?.toString(), value?.toString())
        }
    }

    /**
     * Buscando as mesmas configuracoes via arquivos de propriedades DataSourcesApoiaSuas.properties e ConfigApoiaSuas.properties (util para alterar configuracoes sem precisar reiniciar o servidor)
     * Obs: sobrescreve os parametros definidos pelo metodo anterior (DSL groovy)
     */
    private void resolveParametrosProperties() {
        resolveParametrosEspecificoProperties(DATA_SOURCES_CONFIG);
        resolveParametrosEspecificoProperties(CONFIG);
        resolveParametrosEspecificoProperties(AMBIENTE);
    }

    private void resolveParametrosEspecificoProperties(String strProperties) {
        String nomearquivo = '/' + strProperties + '.properties';
        if (! getClass().getResourceAsStream(nomearquivo))
            return;
        Properties properties = new Properties()
        properties.load(getClass().getResourceAsStream(nomearquivo));

        String prefixoBD = bancoDeDados + ".";

        //primeiramente preenche os valores disponiveis na raiz (sem nehum prefixo)
        properties.each { key, value ->
            if (key.toString().contains('.'))
                return;
            guardaConfiguracao(key, value)
//            config.put(key, value)
        }

        //preenche os valores disponiveis no prefixo bd. (sobrescreve quando coincidir o parametro com algum ja fornecido anteriormente)
        properties.each { key, value ->
            if (key.toString().startsWith(prefixoBD))
                key = key.toString().substring(prefixoBD.length())
            if (key.toString().contains('.'))
                return;
            guardaConfiguracao(key, value)
//            config.put(key, value)
        }

        if (runtimeHospedagem) {
            String prefixoBDRuntime = bancoDeDados + "." + runtimeHospedagem + ".";
            String prefixoRuntime = runtimeHospedagem + ".";

            //preenche os valores disponiveis no prefixo runtimeHospedagem. (sobrescreve quando coincidir o parametro com algum ja fornecido anteriormente)
            properties.each { key, value ->
                if (key.toString().startsWith(prefixoRuntime))
                    key = key.toString().substring(prefixoRuntime.length())
                if (key.toString().contains('.'))
                    return;
                guardaConfiguracao(key, value)
    //            config.put(key, value)
            }

            //preenche os valores disponiveis no prefixo bd.runtimeHospedagem. (sobrescreve quando coincidir o parametro com algum ja fornecido anteriormente)
            properties.each { key, value ->
                if (key.toString().startsWith(prefixoBDRuntime))
                    key = key.toString().substring(prefixoBDRuntime.length())
                if (key.toString().contains('.'))
                    return;
                guardaConfiguracao(key, value)
    //            config.put(key, value)
            }
        }

    }

    /**
     * Buscando as mesmas configuracoes em parametros da JVM
     * Ignora os prefixos de BD e runtimeHospedagem, e usa o prefixo org.apoiasuas
     * Obs: sobrescreve os parametros definidos pelos metodos anteriores (DSL groovy e arquivo properties)
     */
    private void resolveParametrosJVM() {
        resolveParametrosEspecificoJVM('org.apoiasuas.');
        resolveParametrosEspecificoJVM('org.apoiasuas.ds.');
        //sobrescreve os parametros por outros identicos mas especificados para o modo (prod, demo, etc)
        resolveParametrosEspecificoJVM(modo+'_org.apoiasuas.');
        resolveParametrosEspecificoJVM(modo+'_org.apoiasuas.ds.');
    }

    private void resolveParametrosEspecificoJVM(String prefixoCompleto) {
        this.class.fields.each { field ->
            if (! field.name?.contains("password") && sysProperties(prefixoCompleto+field.name))
                System.out.println("armazenando parametro JVM em ConfiguracoesFacade: "+prefixoCompleto+field.name + " -> " + sysProperties(prefixoCompleto+field.name));
            if (sysProperties(prefixoCompleto+field.name))
                guardaConfiguracao(field.name, sysProperties(prefixoCompleto+field.name))
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

}