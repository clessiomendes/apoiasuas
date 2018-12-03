package org.apoiasuas.ambienteExecucao

import grails.util.Environment
import groovy.sql.Sql
import org.apoiasuas.util.ApoiaSuasException
import org.hibernate.cfg.Configuration
import org.hibernate.dialect.Dialect
import org.hibernate.tool.hbm2ddl.DatabaseMetadata

import javax.sql.DataSource
import java.lang.reflect.Constructor

/**
 * Classe estatica contendo metodos que retornam informacoes do ambiente de execucao, como parametros de configuracao,
 * versao do runtime (producao, desenvolvient, etc), ambiente de hospedagem (generico, clever cloud, etc).
 * Tambem contem uma inner class para tradução
 */
public abstract class AmbienteExecucao {

    public static String DESENVOLVIMENTO = "dev";
    public static String VALIDACAO = "valid";
    public static String PRODUCAO = "prod";
    public static String DEMONSTRACAO = "demo";

    /**
     * Definição do singleton de configuracoes do ambiente, armazenado de forma estática na classe
     */
    public static final ConfiguracoesFacade CONFIGURACOES_FACADE = ConfiguracoesFacade.novaInstancia();
    public static final ISqlFacade SQL_FACADE = instanciaFachadaSql();

    public static final Date inicioAplicacao = new Date()

    /**
     * Usado para testes que simulam erros. Garantimos que nunca esses testes serão levados para producao por engano
     */
    public static
    final boolean SABOTAGEM = "true".equalsIgnoreCase(sysProperties('org.apoiasuas.sabotagem')) && (Environment.current != Environment.PRODUCTION)

    public static String getLiteralInteiro(Integer i) {
        return i?.toString()
    }

    /**
     * Busca um parâmetro de configuração
     * @param nome
     * @return
     */
    public static String sysProperties(String nome) {
        if (CONFIGURACOES_FACADE)
            return CONFIGURACOES_FACADE.sysProperties(nome)?.toString()
        else
            return System.properties[nome]?.toString()
    }

    public static void sabota(String mensagem) {
        if (SABOTAGEM)
            throw new RuntimeException(mensagem)
    }

    public static boolean isDesenvolvimento() {
        return CONFIGURACOES_FACADE.modo == DESENVOLVIMENTO
    }

    public static boolean isValidacao() {
        return CONFIGURACOES_FACADE.modo == VALIDACAO
    }

    public static boolean isProducao() {
        return CONFIGURACOES_FACADE.modo == PRODUCAO
    }

    public static boolean isDemonstracao() {
        return CONFIGURACOES_FACADE.modo == DEMONSTRACAO
    }

    public static String toString() {
        if (isDesenvolvimento())
            return "(Desenvolvimento)"
        if (isValidacao())
            return "(Validação)"
    }

    /**
     * Em ambientes clusterizados, determina se o servidor corrente é o primario
     */
    public static boolean isServidorPrimario() {
        return true;
/*
        switch (CONFIGURACOES_FACADE) {
            case LOCAL: return true;
            case CLEVERCLOUD: return CONFIGURACOES_FACADE.sysProperties('INSTANCE_NUMBER')?.equals("0");
            default: throw new RuntimeException("impossível definir servidor primário em um ambiente (possivelmente) clusterizado")
        }
*/
    }

    public static List getConfiguracoes() {
        List result = []
        result << getConfiguracao('NucleoConfig')
        result << getConfiguracao('NaoExisteConfig')
        return result.findAll { it != null}
    }

    private static Class getConfiguracao(String arquivoConfiguracao) {
        try {
            return AmbienteExecucao.getClassLoader().loadClass(arquivoConfiguracao);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Dialect getDialect() {
        if (! CONFIGURACOES_FACADE.dialect)
            throw new ApoiaSuasException("dialeto de banco de dados não definido como parametro de configuracao da aplicacao (parametro dialect)")
        Class<?> clazz = Class.forName(CONFIGURACOES_FACADE.dialect);
        Constructor<?> ctor = clazz.getConstructor();
        return ctor.newInstance();
    }

    public static String[] atualizacoesDDL(DataSource dataSource, Configuration conf) {
        DatabaseMetadata metadata = new DatabaseMetadata(new Sql(dataSource).dataSource.connection, getDialect());
        String[] result = conf.generateSchemaUpdateScriptList(getDialect(), metadata).collect {
            it.script
        };
        return result
    }

    /**
     * Gera instancia sigleton da fachada de Banco de dados.
     * Caso exista uma classe correspondente ao nome da class definida na vairavel de ambiente bancoDeDados,
     * instancia o singleton aa partir desta classe. Se nao, gera um erro
     */
    public static ISqlFacade instanciaFachadaSql() {
        String s = System.properties['org.apoiasuas.bancoDeDados']?.toString();
        if (! s)
            throw new ApoiaSuasException(ConfiguracoesFacade.MENSAGEM_ERRO_VARIAVEIS_OBRIGATORIAS);

        //Converte para tudo minusculo e somente primeira letra maiuscula
        s = org.apache.commons.lang.StringUtils.capitalize(s.toLowerCase());
        //Adiciona o pacorte, o prefixo e o sufixo no nome da classe
        s = ConfiguracoesFacade.package.name+".Sql"+s+"Facade";
        try {
            Class<?> clazz = Class.forName(s);
            Constructor<?> ctor = clazz.getConstructor();
            return ctor.newInstance();
        } catch (ClassNotFoundException e) {
            System.out.println("erro: Classe especializada de sql nao encontrada - "+s);
            throw e;
        }
    }


}
