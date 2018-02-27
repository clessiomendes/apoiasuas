package org.apoiasuas.cidadao.detalhe

import org.apoiasuas.LookupService
import org.apoiasuas.util.SimNao

/**
 * Created by cless on 07/02/2018.
 */
public class CampoDetalheBoolean extends CampoDetalhe {
    protected String codigo
    protected String descricaoLookup

    //Impede que seja instanciado diretamente
    protected CampoDetalheBoolean() {}

    @Override
    public org.apoiasuas.cidadao.detalhe.CampoDetalhe.Tipo getTipo() {
        return org.apoiasuas.cidadao.detalhe.CampoDetalhe.Tipo.BOOLEAN;
    }

    @Override
    public Map toJsonMap() {
        return [tipo: getTipo(), codigo: codigo, descricaoLookup: descricaoLookup]
    }

    @Override
    protected void fillFromRequest(String nomeCampo, Map params, LookupService lookupService) {
//        valor = params[nomeCampo]
        SimNao sn = null;
        if (params.containsKey(nomeCampo))
            //se o campo esta presente no request mas for vazio, interpretar como nulo
            sn = params[nomeCampo] ? SimNao.valueOf(params[nomeCampo]) : null
        else
            //o campo esta ausente no request (um checkbox não selecionado), interpretar como NAO
            sn = SimNao.NAO;
        codigo = sn ? sn.toString() : null;
        descricaoLookup = sn?.descricao
    }

    @Override
    public boolean asBoolean() {
        SimNao.sim(codigo);
//        valor ? true : false
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

}
