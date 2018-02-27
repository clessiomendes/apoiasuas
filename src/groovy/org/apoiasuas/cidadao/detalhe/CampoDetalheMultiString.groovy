package org.apoiasuas.cidadao.detalhe

import org.apoiasuas.LookupService

/**
 * Created by cless on 07/02/2018.
 */
public class CampoDetalheMultiString extends CampoDetalhe {
    protected String[] valoresList = [];

    //Impede que seja instanciado diretamente
    protected CampoDetalheMultiString() {}

    @Override
    public org.apoiasuas.cidadao.detalhe.CampoDetalhe.Tipo getTipo() {
        return org.apoiasuas.cidadao.detalhe.CampoDetalhe.Tipo.MULTI_PLAIN;
    }

    @Override
    public Map toJsonMap() {
        return [tipo: getTipo(), valoresList: valoresList]
    }

    @Override
    protected void fillFromRequest(String nomeCampo, Map params, LookupService lookupService) {
        valoresList = params.list(nomeCampo);
    }

    @Override
    public boolean asBoolean() {
        valoresList ? true : false
    }

    @Override
    public String convertToString() {
        return valoresList?.join(", ");
    }

    @Override
    public boolean notEmpty() {
        if (valoresList)
            return true
        else
            return false
    }
}
