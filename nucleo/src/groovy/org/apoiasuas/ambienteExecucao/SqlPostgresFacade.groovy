package org.apoiasuas.ambienteExecucao

/**
 * Suporte ao banco de dados PotgresSql (versão 8.0 ou superior)
 */
public class SqlPostgresFacade implements ISqlFacade {

    public String concat(String... args) {
        if (!args)
            return "null"
        String result = ""
        args.eachWithIndex { arg, i -> result += (i > 0 ? " || " : "") + "coalesce($arg,'')" }
        return result
    }

    public String idade(String dataNascimento) {
        return "cast (extract(year from age($dataNascimento)) as integer)"
    }

    public String dateToString(String data) {
        return "to_char($data, 'DD/MM/YYYY')"
    }

    public String currentDate() {
        return "CURRENT_DATE"
    }

    public String getBoolean(boolean valor) {
        return valor ? "TRUE" : "FALSE"
    }

    public String StringToNumber(String s) {
        switch (AmbienteExecucao.CONFIGURACOES_FACADE) {
            default: return "str_2_int($s)"
        }
    }

    public String valorNaoNulo(String possivelNulo, String naoNulo) {
        return "coalesce($possivelNulo, $naoNulo)";
    }
}
