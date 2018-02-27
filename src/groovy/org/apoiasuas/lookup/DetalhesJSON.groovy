package org.apoiasuas.lookup

import org.apoiasuas.cidadao.detalhe.CampoDetalhe

/**
 * Marca classes de dominio cujos campos extendidos são guardados em um único campo, numa estrutura JSON
 */
interface DetalhesJSON {

//    public Object conteudoCampoDetalhe(String nomeCampo);
    public void setDetalhes(String detalhes);
    public void setMapaDetalhes(Map<String, CampoDetalhe> mapaDetalhes);
    public Map<String, CampoDetalhe> getMapaDetalhes();

}