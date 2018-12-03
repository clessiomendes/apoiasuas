package org.apoiasuas.ambienteExecucao

/**
 * Interface para geração de SQLs proprietarias especificas de um fornecedor/versao de banco de dados.
 * Implementar "traducao" para cada fornecedor que se deseja suportar.
 */
interface ISqlFacade {
    public String concat(String... args);
    public String idade(String dataNascimento);
    public String dateToString(String data);
    public String currentDate();
    public String getBoolean(boolean valor);
    public String StringToNumber(String s);
    public String valorNaoNulo(String possivelNulo, String naoNulo);
}