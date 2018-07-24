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
    public static final TipoAmbiente CLEVERCLOUD_POSTGRE_DEMO2 = new PostgreCleverCloud();
    public static final TipoAmbiente CLEVERCLOUD_POSTGRE_VALID2 = new PostgreCleverCloud();
    public static final TipoAmbiente CURRENT2 = escolheTipoBD2();

    public static final TipoAmbiente[] LOCAL = [LOCAL_POSTGRE2]
    public static final TipoAmbiente[] CLEVERCLOUD = [CLEVERCLOUD_POSTGRE_PROD2, CLEVERCLOUD_POSTGRE_VALID2, CLEVERCLOUD_POSTGRE_DEMO2]

    public static final TipoAmbiente[] POSTGRES = [LOCAL_POSTGRE2, CLEVERCLOUD_POSTGRE_PROD2, CLEVERCLOUD_POSTGRE_DEMO2, CLEVERCLOUD_POSTGRE_VALID2]

    public static final TipoAmbiente[] DESENVOLVIMENTO = [LOCAL_POSTGRE2]
    public static final TipoAmbiente[] VALIDACAO = [CLEVERCLOUD_POSTGRE_VALID2]
    public static final TipoAmbiente[] PRODUCAO = [CLEVERCLOUD_POSTGRE_PROD2]
    public static final TipoAmbiente[] DEMO = [CLEVERCLOUD_POSTGRE_DEMO2]

    public static final TipoAmbiente[] H2 = []
    public static final TipoAmbiente[] MYSQL = []

    public static final Date inicioAplicacao = new Date()

    //TODO: implementar (e testar) outras engines de banco de dados em SqlProprietaria
    public static final class SqlProprietaria {

        public static String concat(String... args) {
            if (!args)
                return "null"
            switch (AmbienteExecucao.CURRENT2) {
                case AmbienteExecucao.POSTGRES:
                    String result = ""
                    args.eachWithIndex { arg, i -> result += (i > 0 ? " || " : "") + "coalesce($arg,'')" }
                    return result
                default: throw new RuntimeException("recurso dataNascimento() não implementado para engine de banco " +
                        "de dados: ${AmbienteExecucao.CURRENT2}")
            }
        }

        public static String idade(String dataNascimento) {
            switch (AmbienteExecucao.CURRENT2) {
                case AmbienteExecucao.POSTGRES: return "cast (extract(year from age($dataNascimento)) as integer)"
                default: throw new RuntimeException("recurso dataNascimento() não implementado para engine de banco " +
                        "de dados: ${AmbienteExecucao.CURRENT2}")
            }
        }

        public static String dateToString(String data) {
            switch (AmbienteExecucao.CURRENT2) {
                case AmbienteExecucao.POSTGRES: return "to_char($data, 'DD/MM/YYYY')"
                default: throw new RuntimeException("recurso dataNascimento() não implementado para engine " +
                        "de banco de dados: ${AmbienteExecucao.CURRENT2}")
            }
        }

        public static String currentDate() {
            switch (AmbienteExecucao.CURRENT2) {
                case AmbienteExecucao.POSTGRES: return "CURRENT_DATE"
                default: throw new RuntimeException("recurso currentDate() não implementado para engine " +
                        "de banco de dados: ${AmbienteExecucao.CURRENT2}")
            }
        }

        public static String getBoolean(boolean valor) {
            switch (AmbienteExecucao.CURRENT2) {
                case AmbienteExecucao.H2 + AmbienteExecucao.MYSQL: return valor ? "1" : "0"
                case AmbienteExecucao.POSTGRES: return valor ? "TRUE" : "FALSE"
                default: throw new RuntimeException("recurso getBoolean() não implementado para engine " +
                        "de banco de dados: ${AmbienteExecucao.CURRENT2}")
            }
        }

        public static String StringToNumber(String s) {
            switch (AmbienteExecucao.CURRENT2) {
//                case H2 + MYSQL: return ""
//                case POSTGRES: return "cast( ( REGEXP_REPLACE('0' || COALESCE( $s ,'0'), '[^0-9]+', '', 'g') ) as integer)" //ver http://stackoverflow.com/a/30582589/1916198
//                case AmbienteExecucao.POSTGRES: return "to_number( $s ,'999999999999999.99999999')" //ver http://stackoverflow.com/a/18021967/1916198
                default: return "str_2_int($s)"
//                default: throw new RuntimeException("recurso StringToNumer() não implementado para engine " +
//                        "de banco de dados: ${AmbienteExecucao.CURRENT}")
            }
        }

        public static String valorNaoNulo(String possivelNulo, String naoNulo) {
            switch (AmbienteExecucao.CURRENT2) {
                case AmbienteExecucao.POSTGRES: return "coalesce($possivelNulo, $naoNulo)";
                default: throw new RuntimeException("recurso valorNaoNulo() não implementado para engine " +
                        "de banco de dados: ${AmbienteExecucao.CURRENT2}")
            }
        }
    }

    /**
     * Usado para testes que simulam erros. Garantimos que nunca esses testes serão levados para producao por engano
     */
    public static
    final boolean SABOTAGEM = "true".equalsIgnoreCase(sysProperties('org.apoiasuas.sabotagem')) && (Environment.current != Environment.PRODUCTION)

    public static String getForncedorBancoDados() {
        if (CURRENT2 in H2) return "H2"
        if (CURRENT2 in POSTGRES) return 'Postgres'
        if (CURRENT2 in MYSQL) return 'MySql'
        throw new RuntimeException("tipo de banco de dados não definido: ${CURRENT2}")
    }

    public static String getAmbienteHospedagem() {
        switch (CURRENT2) {
            case LOCAL: return "Local"
            case CLEVERCLOUD: return 'clever-cloud'
            default: throw new RuntimeException("ambiente de hospedagem não definido: ${CURRENT2}")
        }
    }

    public static String getAmbienteExecucao() {
        switch (CURRENT2) {
            case DESENVOLVIMENTO: return "Desenvolvimento"
            case VALIDACAO: return "Validação"
            case PRODUCAO: return "Produção"
            case DEMO: return "Demonstração"
            default: throw new RuntimeException("ambiente de execução não definido: ${CURRENT2}")
        }
    }

    static def getLiteralInteiro(Integer i) {
        return i?.toString()
    }

    /**
     * Busca um parâmetro de configuração
     * @param nome
     * @return
     */
    public static String sysProperties(String nome) {
        return System.properties[nome]?.toString();
    }

    /**
     * Obtem a opção de banco de dados do deploy dos parametros da JVM
     * @return
     */
    private static final TipoAmbiente escolheTipoBD2() {
        String ds = sysProperties('org.apoiasuas.datasource')?.toUpperCase();
        switch (ds) {
            case 'CLEVERCLOUD_POSTGRES_PROD': return CLEVERCLOUD_POSTGRE_PROD2
            case 'CLEVERCLOUD_POSTGRES_DEMO': return CLEVERCLOUD_POSTGRE_DEMO2
            case 'CLEVERCLOUD_POSTGRES_VALID': return CLEVERCLOUD_POSTGRE_VALID2
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
        if (CURRENT2 in CLEVERCLOUD)
            result += sysProperties("APP_HOME") + File.separator + sysProperties("org.apoiasuas.caminhoRepositorio")
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
        return CURRENT2 in DESENVOLVIMENTO
    }

    public static boolean isValidacao() {
        return CURRENT2 in VALIDACAO
    }

    public static boolean isProducao() {
        return CURRENT2 in PRODUCAO
    }

    public static boolean isDemonstracao() {
        return CURRENT2 in DEMO
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
        switch (CURRENT2) {
            case LOCAL: return true;
            case CLEVERCLOUD: return sysProperties('INSTANCE_NUMBER')?.equals("0");
            default: throw new RuntimeException("impossível definir servidor primário em um ambiente (possivelmente) clusterizado")
        }
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
