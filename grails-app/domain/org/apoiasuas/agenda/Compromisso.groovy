package org.apoiasuas.agenda

import org.apoiasuas.cidadao.Familia
import org.apoiasuas.redeSocioAssistencial.AtendimentoParticularizado
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.DominioProtegidoServico
import org.apoiasuas.seguranca.UsuarioSistema

class Compromisso implements DominioProtegidoServico {

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
//    UsuarioSistema responsavel
    List<UsuarioSistema> participantes = []

    static transients = ['cor','tooltip']

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
            return "#AED6F1";
    }

    public String getTooltip() {
        if (tipo && tipo.atendimento)
            return atendimentoParticularizado.tooltip
        else
            return "Compromisso";
    }

}
