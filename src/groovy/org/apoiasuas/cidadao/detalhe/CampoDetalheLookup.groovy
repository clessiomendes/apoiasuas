package org.apoiasuas.cidadao.detalhe

import org.apoiasuas.LookupService;

/**
 * Created by cless on 07/02/2018.
 */
public class CampoDetalheLookup extends CampoDetalhe {
    protected String codigo
    protected String descricaoLookup

    //Impede que seja instanciado diretamente
    protected CampoDetalheLookup() {}


    @Override
    public org.apoiasuas.cidadao.detalhe.CampoDetalhe.Tipo getTipo() {
        return org.apoiasuas.cidadao.detalhe.CampoDetalhe.Tipo.LOOKUP;
    }

    @Override
    public Map toJsonMap() {
        return [tipo: getTipo().toString(), codigo: codigo, descricaoLookup: descricaoLookup]
    }

    @Override
    protected void fillFromRequest(String nomeCampo, Map params, LookupService lookupService) {
        codigo = params[nomeCampo];
        final String tabela = getTabela(nomeCampo, params);
        descricaoLookup = lookupService.getDescricao(tabela, codigo)
    }

    @Override
    public boolean asBoolean() {
        codigo ? true : false
    }

    @Override
    public String convertToString() {
        return descricaoLookup;
    }

    @Override
    public boolean notEmpty() {
        if (codigo)
            return true
        else
            return false
    }

    public Boolean outros() {
        if (codigo)
            return codigo.trim() == "0"
        else
            return null
    }
}
