package org.apoiasuas.util

import grails.util.Environment
import org.apoiasuas.util.ambienteExecucao.PostgreCleverCloud
import org.apoiasuas.util.ambienteExecucao.PostgreLocal
import org.apoiasuas.util.ambienteExecucao.TipoAmbiente

/**
 * Created by home64 on 19/03/2015.
 */
class AmbienteExecucao {
//    public static final Integer DEFAULT = LOCAL_H2

    public static final TipoAmbiente LOCAL_POSTGRE2 = new PostgreLocal();
    public static final TipoAmbiente CLEVERCLOUD_POSTGRE_PROD2 = new PostgreCleverCloud();
    public static final TipoAmbiente CURRENT2 = escolheTipoBD2();

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
    public static
    final Integer[] POSTGRES = [APPFOG_POSTGRES_VALID, APPFOG_POSTGRES_PROD, LOCAL_POSTGRES, CLEVERCLOUD_POSTGRES_PROD, CLEVERCLOUD_POSTGRES_VALID]

    public static final Integer[] DESENVOLVIMENTO = [LOCAL_H2, LOCAL_MYSQL, LOCAL_POSTGRES]
    public static final Integer[] VALIDACAO = [APPFOG_POSTGRES_VALID, CLEVERCLOUD_POSTGRES_VALID]
    public static final Integer[] PRODUCAO = [APPFOG_POSTGRES_PROD, CLEVERCLOUD_POSTGRES_PROD]

    public static final Date inicioAplicacao = new Date()

    //TODO: implementar (e testar) outras engines de banco de dados em SqlProprietaria
    public static final class SqlProprietaria {

        public static String concat(String... args) {
            if (!args)
                return "null"
            switch (AmbienteExecucao.CURRENT) {
                case AmbienteExecucao.POSTGRES:
                    String result = ""
                    args.eachWithIndex { arg, i -> result += (i > 0 ? " || " : "") + "coalesce($arg,'')" }
                    return result
                default: throw new RuntimeException("recurso dataNascimento() não implementado para engine de banco " +
                        "de dados: ${AmbienteExecucao.CURRENT}")
            }
        }

        public static String idade(String dataNascimento) {
            switch (AmbienteExecucao.CURRENT) {
                case AmbienteExecucao.POSTGRES: return "cast (extract(year from age($dataNascimento)) as integer)"
                default: throw new RuntimeException("recurso dataNascimento() não implementado para engine de banco " +
                        "de dados: ${AmbienteExecucao.CURRENT}")
            }
        }

        public static String dateToString(String data) {
            switch (AmbienteExecucao.CURRENT) {
                case AmbienteExecucao.POSTGRES: return "to_char($data, 'DD/MM/YYYY')"
                default: throw new RuntimeException("recurso dataNascimento() não implementado para engine " +
                        "de banco de dados: ${AmbienteExecucao.CURRENT}")
            }
        }

        public static String currentDate() {
            switch (AmbienteExecucao.CURRENT) {
                case AmbienteExecucao.POSTGRES: return "CURRENT_DATE"
                default: throw new RuntimeException("recurso currentDate() não implementado para engine " +
                        "de banco de dados: ${AmbienteExecucao.CURRENT}")
            }
        }

        public static String getBoolean(boolean valor) {
            switch (AmbienteExecucao.CURRENT) {
                case AmbienteExecucao.H2 + AmbienteExecucao.MYSQL: return valor ? "1" : "0"
                case AmbienteExecucao.POSTGRES: return valor ? "TRUE" : "FALSE"
                default: throw new RuntimeException("recurso getBoolean() não implementado para engine " +
                        "de banco de dados: ${AmbienteExecucao.CURRENT}")
            }
        }

        public static String StringToNumber(String s) {
            switch (AmbienteExecucao.CURRENT) {
//                case H2 + MYSQL: return ""
//                case POSTGRES: return "cast( ( REGEXP_REPLACE('0' || COALESCE( $s ,'0'), '[^0-9]+', '', 'g') ) as integer)" //ver http://stackoverflow.com/a/30582589/1916198
//                case AmbienteExecucao.POSTGRES: return "to_number( $s ,'999999999999999.99999999')" //ver http://stackoverflow.com/a/18021967/1916198
                default: return "str_2_int($s)"
//                default: throw new RuntimeException("recurso StringToNumer() não implementado para engine " +
//                        "de banco de dados: ${AmbienteExecucao.CURRENT}")
            }
        }

        public static String valorNaoNulo(String possivelNulo, String naoNulo) {
            switch (AmbienteExecucao.CURRENT) {
                case AmbienteExecucao.POSTGRES: return "coalesce($possivelNulo, $naoNulo)";
                default: throw new RuntimeException("recurso valorNaoNulo() não implementado para engine " +
                        "de banco de dados: ${AmbienteExecucao.CURRENT}")
            }
        }
    }

    /**
     * Usado para testes que simulam erros. Garantimos que nunca esses testes serão levados para producao por engano
     */
    public static
    final boolean SABOTAGEM = "true".equalsIgnoreCase(sysProperties('org.apoiasuas.sabotagem')) && (Environment.current != Environment.PRODUCTION)

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

    static def getLiteralInteiro(Integer i) {
        return i?.toString()
    }

    public static String sysProperties(String nome) {
        return System.properties[nome]?.toString();
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
            default:
                if (isDesenvolvimento())
                    return LOCAL_POSTGRES
                else
                    throw new RuntimeException("Definicao de Banco de Dados nao prevista: ${ds}")
        }
    }

    private static final TipoAmbiente escolheTipoBD2() {
        String ds = sysProperties('org.apoiasuas.datasource')?.toUpperCase();
        switch (ds) {
            case 'CLEVERCLOUD_POSTGRES_PROD': return CLEVERCLOUD_POSTGRE_PROD2
            case 'LOCAL_POSTGRES': return LOCAL_POSTGRE2
            default:
                if (isDesenvolvimento())
                    return LOCAL_POSTGRE2
                else
                    throw new RuntimeException("Definicao de Banco de Dados nao prevista: ${ds}")
        }
    }

    public static String getCaminhoRepositorioArquivos() {
        String result = ""
        if (CURRENT in CLEVERCLOUD)
            result += sysProperties("APP_HOME") + File.separator + "repositorio-fs-prod"
        else
            result += sysProperties("user.home")
        result += File.separator + "apoiasuas-repositorio"
        return result
    }

    public static void sabota(String mensagem) {
        if (SABOTAGEM)
            throw new RuntimeException(mensagem)
    }

    public static boolean isDesenvolvimento() {
        return CURRENT in DESENVOLVIMENTO
    }

    public static boolean isValidacao() {
        return CURRENT in VALIDACAO
    }

    public static boolean isProducao() {
        return CURRENT in PRODUCAO
    }

    public static String toString() {
        if (isDesenvolvimento())
            return "(Desenvolvimento)"
        if (isValidacao())
            return "(Validação)"
    }

/*
    public static String getDatabaseDependency() {
        return 'org.postgresql:postgresql:9.3-1101-jdbc41';

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
            default:
                if (isDesenvolvimento())
                    return LOCAL_POSTGRES
                else
                    throw new RuntimeException("Definicao de Banco de Dados nao prevista: ${ds}")
        }
    }
*/

}
