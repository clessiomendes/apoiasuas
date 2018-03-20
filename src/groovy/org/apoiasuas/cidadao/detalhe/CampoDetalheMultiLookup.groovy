package org.apoiasuas.cidadao.detalhe

import org.apoiasuas.LookupService;
import org.apoiasuas.cidadao.detalhe.CampoDetalhe.Tipo;

/**
 * Created by cless on 07/02/2018.
 */
public class CampoDetalheMultiLookup extends CampoDetalhe {
    protected String[] codigosList = [];
    protected String[] descricoesLookupList = [];

    //Impede que seja instanciado diretamente
    protected CampoDetalheMultiLookup() {}

    public boolean contemCodigo(String codigo) {
        return codigosList.contains(codigo);
    }

    @Override
    public Tipo getTipo() {
        return org.apoiasuas.cidadao.detalhe.CampoDetalhe.Tipo.MULTI_LOOKUP;
    }

    @Override
    public Map toJsonMap() {
        return [tipo: getTipo().toString(), codigosList: codigosList, descricoesLookupList: descricoesLookupList]
    }

    @Override
    protected void fillFromRequest(String nomeCampo, Map params, LookupService lookupService) {
//        valor = params[nomeCampo]
        codigosList = params.list(nomeCampo);
        final String tabela = getTabela(nomeCampo, params);
        descricoesLookupList = codigosList.collect { lookupService.getDescricao(tabela, it) }
    }

    @Override
    public boolean asBoolean() {
        codigosList ? true : false
    }

    @Override
    public String convertToString() {
        return descricoesLookupList?.join(", ");
    }

    @Override
    public boolean notEmpty() {
        if (codigosList)
            return true
        else
            return false
    }
}
