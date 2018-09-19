package org.apoiasuas.redeSocioAssistencial

import com.google.common.base.CaseFormat
import org.apoiasuas.cidadao.Endereco
import org.apoiasuas.util.StringUtils

/**
 * Created by admin on 02/05/2016.
 */
class ServicoSistema {

    public enum Tokens { CRJ }

    String nome
    String telefone
    String site
    String email
    Endereco endereco
    AbrangenciaTerritorial abrangenciaTerritorial
    Boolean habilitado
    String recursos
    AcessoSeguranca acessoSeguranca
    Tokens token

    static transients = ['urlSite']
    static embedded = ['endereco','acessoSeguranca']
    static mapping = {
//        cache 'nonstrict-read-write' //sempre mantem em cache o servico sistema logado
        id generator: 'native', params: [sequence: 'sq_servico_sistema']
        nome length: 80
        telefone length: 30
        site length: 80
        email length: 80
        recursos length: 10000
    }
    static constraints = {
        endereco(nullable: true)
        abrangenciaTerritorial(nullable: true)
    }

    public String getUrlSite() {
        if (! site)
            return site
        return site.toLowerCase().startsWith("http") ? site : "http://"+site
    }

    public List<RecursosServico> recursosSelecionados() {
        //se o atributo recursos for nulo ou vazio, retorna lista vazia
        if (! recursos)
            return []
                //quebra em strings
        return  recursos?.split(",")?.
                //elimina eventuais codigos sem correspondencia no enum
                findAll { RecursosServico.contains(it) }?.
                //converte de string para enum
                collect{ RecursosServico.valueOf(it) };
    }

    public boolean contemRecurso(RecursosServico recurso) {
        return recursosSelecionados().contains(recurso);
    }
}

class AcessoSeguranca {
    String inibirAtendimentoApos
}

/**
 * ENUM
 */
enum RecursosServico {
    INCLUSAO_FAMILIA(10, "cadastrar novas famílias", true),
    INCLUSAO_MEMBRO_FAMILIAR(20, "incluir novos membros familiares (cidadãos)", true),
    CADASTRO_DETALHADO(30, "permitir cadastros mais detalhados de famílias e cidadãos", true),
    PEDIDOS_CERTIDAO(40, "gerencia o processo de pedidos de certidão para outros municípios - versão 1.0", true),
    PEDIDOS_CERTIDAO_2_0(45, "gerencia o processo de pedidos de certidão para outros municípios - versão 2.0", true),
    PLANO_ACOMPANHAMENTO(50, "emitir planos de acompanhamento familiar à partir do modelo previsto no sistema", true),
    IDENTIFICACAO_PELO_CODIGO_LEGADO(60, "utilizar o código legado como identificador principal de busca de famílias", true),
    RESERVAS(70, "gerenciar reservas de espaços (módulo CRJ)", true)

    String descricao
    Boolean disponivel
    int ord

    /**
     * Converte a lista de enums para uma lista de strings a serem testados antes de uma possivel conversao string->enum
     */
    private static Set<String> _values = new HashSet<>();
    static{
        for (RecursosServico recurso : RecursosServico.values()) {
            _values.add(recurso.name());
        }
    }
    public static boolean contains(String value){
        return _values.contains(value);
    }

    public RecursosServico(int ord, String descricao, Boolean disponivel) {
        this.ord = ord;
        this.descricao = descricao;
        this.disponivel = disponivel;
    }

    public static List<RecursosServico> recursosDisponiveis() {
        return values().findAll { it.disponivel }.sort { it.ord }
    }

    public String getPropriedade() {
        return StringUtils.upperToCamelCase(this.toString(), CaseFormat.LOWER_CAMEL)
    }

    /**
     * Verifica compatibilidade entre o enum RecursosServico e os atributos em AcessoSeguranca, em busca de possiveis
     * erros de digitação
     */
//    public static void initTest() {
//        final AcessoSeguranca temp = new AcessoSeguranca();
//        values().each {
//            temp.getProperty(it.propriedade)
//        }
//    }
}
