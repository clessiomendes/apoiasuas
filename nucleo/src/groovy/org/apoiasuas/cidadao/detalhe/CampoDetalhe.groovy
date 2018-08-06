package org.apoiasuas.cidadao.detalhe

import org.apoiasuas.LookupService
import org.apoiasuas.util.ApoiaSuasException
import org.apoiasuas.util.SimNao

/**
 * Classe abstrata para os diversos tipos de campos de detalhe para as entidades Familia e Cidadao
 * Campos de detalhe são campos que não possuem uma correspondência direta em campos individuais nas
 * tabelas de banco de dados. Eles são convertidos em uma estrutura JSON que é gravada como um t_odo em
 * um único campo do banco de dados. Assim, novos campos podem ser adicionados ou removidos das interfaces
 * do sistema sem afetar a estrutura do banco de dados, dando maior flexibilidade a diferentes modelos
 * de cadastro personalizados para cada ServicoSistema utilizando o sisetma.
 */
public abstract class CampoDetalhe {

    public abstract Tipo getTipo();
    public abstract boolean notEmpty();

    public static enum Tipo {
        PLAIN, BOOLEAN, LOOKUP, MULTI_LOOKUP, MULTI_PLAIN;
    }

    /**
     * expões os atributos do campo em um formato de mapa para ser convertido para JSON
     */
    public abstract Map toJsonMap();

    /**
     * Instancia um objeto CampoDetalhe aa partir do recurso do groovy que transforma um mapa num objeto fazendo
     * correspondencia entre as chaves do primeiro e os atributos do segundo
     */
    public static CampoDetalhe parseJSON(Map campoJSON) {
        return campoJSON ? newInstance(campoJSON.remove('tipo'), campoJSON) : null
    }

    /**
     * Instancia um novo objeto descendente de CampoDetalhe aa partir do tipo passado como parametro
     * Opcionalmente, pode ser passado um mapa de atributos para alimentar os atributos da instância.
     * As chaves serao automaticamente relacionadas aos atributos cujos nomes coincidam (recurso do groovy)
     */
    public static CampoDetalhe newInstance(String tipo, Map mapaAtributos = [:]) {
        try {
            switch (Tipo.valueOf(tipo)) {
                case CampoDetalhe.Tipo.PLAIN:
                    return new CampoDetalheString(mapaAtributos); break
                case CampoDetalhe.Tipo.BOOLEAN:
                    return new CampoDetalheBoolean(mapaAtributos); break
                case CampoDetalhe.Tipo.LOOKUP:
                    return new CampoDetalheLookup(mapaAtributos); break
                case CampoDetalhe.Tipo.MULTI_LOOKUP:
                    return new CampoDetalheMultiLookup(mapaAtributos); break
                case CampoDetalhe.Tipo.MULTI_PLAIN:
                    return new CampoDetalheMultiString(mapaAtributos); break
                default:
                    throw new ApoiaSuasException("Tipo indefinido instanciando CampoDetalhe: '$tipo'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Preenche uma isntância de campo de detalhe obgtendo seus valores aa partir do(s) parametro(s) de request correspondente(s)
     */
    protected abstract void fillFromRequest(String nomeCampo, Map params, LookupService lookupService)

    /**
     * Constroi uma representacao do campo de detalhe inferindo seu tipo, conteudo e eventual descricao aa partir dos parametros vindos do request
     * @param nomeCampo
     * @param params Mapa JÁ FILTRADO dos parametros de detalhe (em geral, de um request HTTP)
     */
    public static CampoDetalhe parseRequest(String nomeCampo, Map params, LookupService lookupService) {
        CampoDetalhe result;
        //hidden indicando o tipo do campo (se nao houver, infere-se que o campo é simples (PLAIN)
        if (params[nomeCampo + "_tipo"] && ! (params[nomeCampo + "_tipo"] instanceof String))
            throw new ApoiaSuasException("Erro processando campo $nomeCampo: múltiplos valores não esperado");
        String tipo = params[nomeCampo + "_tipo"];
        result = newInstance( tipo ?: Tipo.PLAIN.toString() );
        result.fillFromRequest(nomeCampo, params, lookupService);
        return result;
    }

    public abstract boolean asBoolean();

    /**
     * previsto para implementacoes especificas E OBRIGATORIAS para o metodo toString()
     */
    protected abstract convertToString();

    /**
     * Representação String de cada tipo especifico de CampoDetalhe
     */
    public String toString() {
        return convertToString();
    }

    /**
     * Busca no request o nome da tabela Lookup relacionada ao campo passado por parametro
     * (ver tags multiLookup e selectLookup em ApoiaSuasTagLib)
     */
    protected static String getTabela(String nomeCampo, Map params) {
        String tabela = params[nomeCampo + "_tabela"];
        if (!tabela)
            throw new ApoiaSuasException("Não encontrado o parâmetro de request ${params[nomeCampo + "_tabela"]} obrigatório para o campo lookup ${nomeCampo}")
        return tabela;
    }


}


