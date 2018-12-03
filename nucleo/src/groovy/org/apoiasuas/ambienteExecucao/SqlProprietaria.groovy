package org.apoiasuas.ambienteExecucao

public class SqlProprietaria {

    public static String concat(String... args) {
        if (!args)
            return "null"
//            switch (AmbienteExecucao.CONFIGURACOES_FACADE) {
//                case AmbienteExecucao.POSTGRES:
        String result = ""
        args.eachWithIndex { arg, i -> result += (i > 0 ? " || " : "") + "coalesce($arg,'')" }
        return result
//                default: throw new RuntimeException("recurso dataNascimento() não implementado para engine de banco " +
//                        "de dados: ${AmbienteExecucao.CONFIGURACOES_FACADE}")
//            }
    }

    public static String idade(String dataNascimento) {
//            switch (AmbienteExecucao.CONFIGURACOES_FACADE) {
//                case AmbienteExecucao.POSTGRES:
        return "cast (extract(year from age($dataNascimento)) as integer)"
//                default: throw new RuntimeException("recurso dataNascimento() não implementado para engine de banco " +
//                        "de dados: ${AmbienteExecucao.CONFIGURACOES_FACADE}")
//            }
    }

    public static String dateToString(String data) {
//            switch (AmbienteExecucao.CONFIGURACOES_FACADE) {
//                case AmbienteExecucao.POSTGRES:
        return "to_char($data, 'DD/MM/YYYY')"
//                default: throw new RuntimeException("recurso dataNascimento() não implementado para engine " +
//                        "de banco de dados: ${AmbienteExecucao.CONFIGURACOES_FACADE}")
//            }
    }

    public static String currentDate() {
//            switch (AmbienteExecucao.CONFIGURACOES_FACADE) {
//                case AmbienteExecucao.POSTGRES:
        return "CURRENT_DATE"
//                default: throw new RuntimeException("recurso currentDate() não implementado para engine " +
//                        "de banco de dados: ${AmbienteExecucao.CONFIGURACOES_FACADE}")
//            }
    }

    public static String getBoolean(boolean valor) {
//            switch (AmbienteExecucao.CONFIGURACOES_FACADE) {
//                case AmbienteExecucao.H2 + AmbienteExecucao.MYSQL:
//                    return valor ? "1" : "0"
//                case AmbienteExecucao.POSTGRES:
        return valor ? "TRUE" : "FALSE"
//                default: throw new RuntimeException("recurso getBoolean() não implementado para engine " +
//                        "de banco de dados: ${AmbienteExecucao.CONFIGURACOES_FACADE}")
//            }
    }

    public static String StringToNumber(String s) {
        switch (AmbienteExecucao.CONFIGURACOES_FACADE) {
//                case H2 + MYSQL: return ""
//                case POSTGRES: return "cast( ( REGEXP_REPLACE('0' || COALESCE( $s ,'0'), '[^0-9]+', '', 'g') ) as integer)" //ver http://stackoverflow.com/a/30582589/1916198
//                case AmbienteExecucao.POSTGRES: return "to_number( $s ,'999999999999999.99999999')" //ver http://stackoverflow.com/a/18021967/1916198
            default: return "str_2_int($s)"
//                default: throw new RuntimeException("recurso StringToNumer() não implementado para engine " +
//                        "de banco de dados: ${AmbienteExecucao.CURRENT}")
        }
    }

    public static String valorNaoNulo(String possivelNulo, String naoNulo) {
//            switch (AmbienteExecucao.CONFIGURACOES_FACADE) {
//                case AmbienteExecucao.POSTGRES:
        return "coalesce($possivelNulo, $naoNulo)";
//                default: throw new RuntimeException("recurso valorNaoNulo() não implementado para engine " +
//                        "de banco de dados: ${AmbienteExecucao.CONFIGURACOES_FACADE}")
//            }
    }
}
