package org.apoiasuas.lookup

/**
 * Created by clessio on 21/01/2018.
 */
class LookupRecord {

    Integer codigo;
    String descricao;
    boolean ativo;

    public void setCodigo(Integer codigo) {
        this.codigo = Math.abs(codigo);
    }

    public Integer getCodigo() {
        return Math.abs(codigo);
    }

}
