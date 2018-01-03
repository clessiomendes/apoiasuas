package org.apoiasuas.agenda

import org.apoiasuas.cidadao.Familia
import org.apoiasuas.redeSocioAssistencial.AtendimentoParticularizado
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.DominioProtegidoServico
import org.apoiasuas.seguranca.UsuarioSistema

class Compromisso implements DominioProtegidoServico {

    public static final String CSS_AMARELO = "fc-amarelo"
    public static final String CSS_VERMELHO = "fc-vermelho"
    public static final String CSS_VERDE = "fc-verde"
    public static final String CSS_AZUL = "fc-azul"

    public static enum Tipo {
        ATENDIMENTO_PARTICULARIZADO("atendimento particularizado"),
        ATIVIDADE_COLETIVA("atividade coletiva"),
        OUTROS("outros")

        String descricao;
        public Tipo(String descricao) {
            this.descricao = descricao;
        }
        public boolean isAtendimento() {
            return this == ATENDIMENTO_PARTICULARIZADO;
        }
    }

    Date inicio
    Date fim
    String descricao
    Boolean habilitado
    AtendimentoParticularizado atendimentoParticularizado
    Tipo tipo
    ServicoSistema servicoSistemaSeguranca
    List<UsuarioSistema> participantes = []
    String mensagem

    static transients = ['cor','tooltip','mensagem']

    static hasMany = [participantes: UsuarioSistema]

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_compromisso']
        servicoSistemaSeguranca fetch: 'join' //por questoes de seguranca, sempre que um registro eh obtido do banco de dados, o servicoSistema precisara ser consultado
//        atendimentoParticularizado fetch: 'join' //por questoes de seguranca, sempre que um cidadao eh obtido do banco de dados, o servicoSistema precisara ser consultado
    }

    static constraints = {
        inicio(nullable: false);
        tipo(nullable: false);
        fim(nullable: false);
        descricao(nullable: false, maxSize: 255);
        servicoSistemaSeguranca(nullable: false);
        habilitado(nullable: false);
    }

    @Override
    public String toString() {
        return descricao
    }

    public String getCor() {
        if (tipo && tipo.atendimento)
            return atendimentoParticularizado.cor
        else
            return CSS_AZUL;
    }

    public String getTooltip() {
        if (tipo && tipo.atendimento)
            return atendimentoParticularizado.tooltip
        else
            return "Compromisso";
    }

}
