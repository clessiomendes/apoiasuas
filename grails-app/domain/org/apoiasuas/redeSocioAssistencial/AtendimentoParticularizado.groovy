package org.apoiasuas.redeSocioAssistencial

import groovy.time.TimeCategory
import org.apoiasuas.agenda.Compromisso
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.seguranca.DominioProtegidoServico
import org.apoiasuas.seguranca.UsuarioSistema

class AtendimentoParticularizado implements DominioProtegidoServico {

    //TEXTO PRETO
    public static final String AMARELO = "#F9E79F"
    public static final String VERMELHO = "#FA5858"
    public static final String VERDE = "#A9DFBF"
    //    public static final String MARROM = "#EDBB99"
//    public static final String ROXO = "#D2B4DE"
//    public static final String ROSA = "#ff4dd2"

    public static final String COMPARECEU = "atendimento (compareceu) - "
    public static final String NAO_COMPARECEU = "atendimento (NÂO compareceu) - "
    public static final String HORARIO_PREENCHIDO = "atendimento agendado - "
    public static final String COMPARECIMENTO_INDEFINIDO = "atendimento (compareceu ?   ) - "
    public static final String LIVRE = "atendimento - horário livre"

    Date dataHora
    String nomeCidadao
    String telefoneContato
    Boolean semTelefone
    Boolean familiaSemCadastro
    Familia familia
    ServicoSistema servicoSistemaSeguranca
    UsuarioSistema tecnico
    Boolean compareceu

    //transientes:
    Compromisso compromisso

    static transients = ['compromisso','cor','tooltip','horarioPreenchido']

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_atendimento_particularizado']
        servicoSistemaSeguranca fetch: 'join' //por questoes de seguranca, sempre que um registro eh obtido do banco de dados, o servicoSistema precisara ser consultado
    }

    static constraints = {
        nomeCidadao(maxSize: 255);
        telefoneContato(maxSize: 255);
        servicoSistemaSeguranca(nullable: false);
        tecnico(nullable: false);
    }

//    @Override
//    public String toString() {
//        return this.toString()
//    }

    public String getCor() {
        String result = null
        use (TimeCategory) {
            if (compareceu == true)
                result = AMARELO //MARROM
            else if (compareceu == false)
                result = AMARELO //ROXO
            else if (horarioPreenchido) { //horario preenchido
                if ((dataHora > (new Date() - 1.hours)))
                    result = AMARELO
                else
                    result = VERMELHO
            } else if (! horarioPreenchido && dataHora < (new Date() - 1.hours)) //horario livre mas ja no passado (nao tem como ser utilizado mais)
                result = AMARELO
        }
        result = result ? result : VERDE;
        return result;
    }

    public Boolean getHorarioPreenchido() {
        return nomeCidadao || familia
    }

    public String getTooltip() {
        String result = null
        if (compareceu == true)
            result = COMPARECEU + (nomeCidadao ?: "(nenhum cidadão definido)") //marrom
        else if (compareceu == false)
            result = NAO_COMPARECEU + (nomeCidadao ?: "(nenhum cidadão definido)") //marrom
        else if (horarioPreenchido) { //horario preenchido
            use (TimeCategory) {
                if (dataHora > new Date() + 1.hours)
                    result = HORARIO_PREENCHIDO + (nomeCidadao ?: "(nenhum cidadão definido)") //marrom
                else
                    result = COMPARECIMENTO_INDEFINIDO + (nomeCidadao ?: "(nenhum cidadão definido)") //marrom
            }
        }
        result = result ? result : LIVRE //marrom
        return result;
//        "#ff4dd2" //rosa
    }

}
