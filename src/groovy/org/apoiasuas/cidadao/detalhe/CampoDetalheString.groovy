package org.apoiasuas.cidadao.detalhe

import org.apoiasuas.LookupService

/**
 * Created by cless on 07/02/2018.
 */
public class CampoDetalheString extends CampoDetalhe {
    protected String valor

    //Impede que seja instanciado diretamente
    protected CampoDetalheString() {}

    @Override
    public org.apoiasuas.cidadao.detalhe.CampoDetalhe.Tipo getTipo() {
        return org.apoiasuas.cidadao.detalhe.CampoDetalhe.Tipo.PLAIN;
    }

    @Override
    public Map toJsonMap() {
        return [tipo: getTipo(), valor: valor];
    }

    @Override
    protected void fillFromRequest(String nomeCampo, Map params, LookupService lookupService) {
        valor = params[nomeCampo]
    }

    @Override
    public boolean asBoolean() {
        valor ? true : false
    }

    @Override
    public String convertToString() {
        return valor;
    }

    @Override
    public boolean notEmpty() {
        if (valor)
            return true
        else
            return false
    }
}
