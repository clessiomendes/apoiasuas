package org.apoiasuas.util

import grails.util.Environment

/**
 * Created by home64 on 19/03/2015.
 */
class AmbienteExecucao {
//    public static final Integer DEFAULT = LOCAL_H2

    public static final Integer CURRENT = escolheTipoBD()

    public static final Integer LOCAL_H2 = 0
    public static final Integer LOCAL_MYSQL = 1
    public static final Integer APPFOG_POSTGRES_VALID = 2
    public static final Integer APPFOG_MYSQL = 3
    public static final Integer CLEARDB_MYSQL = 4
    public static final Integer LOCAL_POSTGRES = 5
    public static final Integer APPFOG_POSTGRES_PROD = 6
    public static final Integer CLEVERCLOUD_POSTGRES_PROD = 7
    public static final Integer CLEVERCLOUD_POSTGRES_VALID = 8

    public static final Integer[] LOCAL = [LOCAL_H2, LOCAL_MYSQL, LOCAL_POSTGRES]
    public static final Integer[] APPFOG = [APPFOG_POSTGRES_VALID, APPFOG_MYSQL, CLEARDB_MYSQL, APPFOG_POSTGRES_PROD]
    public static final Integer[] CLEVERCLOUD = [CLEVERCLOUD_POSTGRES_PROD, CLEVERCLOUD_POSTGRES_VALID]

    public static final Integer[] H2 = [LOCAL_H2]
    public static final Integer[] MYSQL = [LOCAL_MYSQL, APPFOG_MYSQL, CLEARDB_MYSQL]
    public static final Integer[] POSTGRES = [APPFOG_POSTGRES_VALID, APPFOG_POSTGRES_PROD, LOCAL_POSTGRES, CLEVERCLOUD_POSTGRES_PROD, CLEVERCLOUD_POSTGRES_VALID]

    public static final Integer[] DESENVOLVIMENTO = [LOCAL_H2, LOCAL_MYSQL, LOCAL_POSTGRES]
    public static final Integer[] VALIDACAO = [APPFOG_POSTGRES_VALID, CLEVERCLOUD_POSTGRES_VALID]
    public static final Integer[] PRODUCAO = [APPFOG_POSTGRES_PROD, CLEVERCLOUD_POSTGRES_PROD]

    public static final Date inicioAplicacao = new Date()

    /**
     * Usado para testes que simulam erros. Garantimos que nunca esses testes serão levados para producao por engano
     */
    public static final boolean SABOTAGEM = sysProperties('org.apoiasuas.sabotagem') == "TRUE" && (Environment.current != Environment.PRODUCTION)

    public static String getForncedorBancoDados() {
        if (CURRENT in H2) return "H2"
        if (CURRENT in POSTGRES) return 'Postgres'
        if (CURRENT in MYSQL) return 'MySql'
        throw new RuntimeException("tipo de banco de dados não definido: ${CURRENT}")
    }

    public static String getAmbienteHospedagem() {
        switch (CURRENT) {
            case LOCAL: return "Local"
            case APPFOG: return 'AppFog'
            case CLEVERCLOUD: return 'Clever-cloud'
            default: throw new RuntimeException("ambiente de hospedagem não definido: ${CURRENT}")
        }
    }

    public static String getAmbienteExecucao() {
        switch (CURRENT) {
            case DESENVOLVIMENTO: return "Desenvolvimento"
            case VALIDACAO: return "Validação"
            case PRODUCAO: return "Produção"
            default: throw new RuntimeException("ambiente de execução não definido: ${CURRENT}")
        }
    }

    public static String getBoolean(boolean valor) {
        switch (CURRENT) {
            case H2 + MYSQL: return valor ? "1" : "0"
            case POSTGRES: return valor ? "TRUE" : "FALSE"
            default: throw new RuntimeException("tipo de banco de dados não definido: ${CURRENT}")
        }
    }

    static def getLiteralInteiro(Integer i) {
        return i?.toString()
    }

    public static String sysProperties(String nome) {
        return System.properties[nome]?.toString()?.toUpperCase()
    }

    /**
     * Obtem a opção de banco de dados do deploy dos parametros da JVM
     * @return
     */
    private static int escolheTipoBD() {
        String ds
//        if (isProducao())
//             ds = 'CLEVERCLOUD_POSTGRES_PROD';
//        else
            ds = sysProperties('org.apoiasuas.datasource')?.toUpperCase()
        System.out.println("Definicao de banco de dados: ${ds}")
        switch (ds) {
            case 'CLEVERCLOUD_POSTGRES_VALID': return CLEVERCLOUD_POSTGRES_VALID
            case 'CLEVERCLOUD_POSTGRES_PROD': return CLEVERCLOUD_POSTGRES_PROD
            case 'APPFOG_POSTGRES_PROD': return APPFOG_POSTGRES_PROD
            case 'APPFOG_POSTGRES_VALID': return APPFOG_POSTGRES_VALID
            case 'LOCAL_POSTGRES': return LOCAL_POSTGRES
            case 'LOCAL_H2': return LOCAL_H2
            case 'LOCAL_MYSQL': return LOCAL_MYSQL
            case 'APPFOG_MYSQL': return APPFOG_MYSQL
            case 'CLEARDB_MYSQL': return CLEARDB_MYSQL
            default: throw new RuntimeException("Definicao de Banco de Dados nao prevista: ${ds}")
        }
    }

    static void sabota(String mensagem) {
        if (SABOTAGEM)
            throw new RuntimeException(mensagem)
    }

    static boolean isDesenvolvimento() {
        return CURRENT in DESENVOLVIMENTO
    }

    static boolean isValidacao() {
        return CURRENT in VALIDACAO
    }

    static boolean isProducao() {
        return CURRENT in PRODUCAO
    }

    static String toString() {
        if (isDesenvolvimento())
            return "(Desenvolvimento)"
        if (isValidacao())
            return "(Validação)"
    }

}
