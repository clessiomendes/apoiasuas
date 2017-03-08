package org.apoiasuas.redeSocioAssistencial

import com.google.common.base.CaseFormat
import org.apoiasuas.cidadao.Endereco
import org.apoiasuas.util.StringUtils
import org.hibernate.HibernateException
import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.ParameterizedType
import org.hibernate.usertype.UserType

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types

/**
 * Created by admin on 02/05/2016.
 */
class ServicoSistema {
    String nome
    String telefone
    String site
    Endereco endereco
    AbrangenciaTerritorial abrangenciaTerritorial
    Boolean habilitado
    AcessoSeguranca acessoSeguranca

//    AbrangenciaTerritorial abrangenciaTerritorial

    static transients = ['urlSite']
    static embedded = ['endereco', 'acessoSeguranca']
    static mapping = {
        id generator: 'native', params: [sequence: 'sq_servico_sistema']
        nome length: 80
        telefone length: 30
        site length: 80
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

}

class AcessoSeguranca {
    boolean inclusaoMembroFamiliar
    boolean inclusaoFamilia
    boolean cadastroDetalhado
    boolean pedidosCertidao
    boolean planoAcompanhamento
    boolean identificacaoPeloCodigoLegado
}

/**
 * ENUM cujas entradas correspondem literalmente (convertidas de maíusculas para camel-case) às propriedades da classe AcessoSeguranca
 */
enum RecursosServico {
    INCLUSAO_MEMBRO_FAMILIAR, INCLUSAO_FAMILIA, CADASTRO_DETALHADO, PEDIDOS_CERTIDAO, PLANO_ACOMPANHAMENTO,
    IDENTIFICACAO_PELO_CODIGO_LEGADO

    public String getPropriedade() {
        return StringUtils.upperToCamelCase(this.toString(), CaseFormat.LOWER_CAMEL)
    }

    /**
     * Verifica compatibilidade entre o enum RecursosServico e os atributos em AcessoSeguranca, em busca de possiveis
     * erros de digitação
     */
    public static void initTest() {
        final AcessoSeguranca temp = new AcessoSeguranca();
        values().each {
            temp.getProperty(it.propriedade)
        }
    }
}
