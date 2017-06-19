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

    public static final String COMPARECEU = "atendimento - compareceu"
    public static final String NAO_COMPARECEU = "atendimento - não compareceu"
    public static final String HORARIO_PREENCHIDO = "atendimento - horário preenchido"
    public static final String COMPARECIMENTO_INDEFINIDO = "atendimento - compareceu?"
    public static final String LIVRE = "atendimento - horário livre"

    Date dataHora
    String nomeCidadao
    String telefoneContato
    Boolean semTelefone
    Familia familia
    ServicoSistema servicoSistemaSeguranca
    UsuarioSistema tecnico
    Boolean compareceu

    //transientes:
    Compromisso compromisso

    static transients = ['compromisso','cor','tooltip']

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_atendimento_particularizado']
        servicoSistemaSeguranca fetch: 'join' //por questoes de seguranca, sempre que um registro eh obtido do banco de dados, o servicoSistema precisara ser consultado
    }

    static constraints = {
        nomeCidadao(maxSize: 255);
        telefoneContato(maxSize: 255);
        servicoSistemaSeguranca(nullable: false);
//até ser efetivado o atendimento, a dataHora e o tecnico permanecem vazios
//        tecnico(nullable: false);
    }

//    @Override
//    public String toString() {
//        return this.toString()
//    }

    public String getCor() {
        String result = null
        if (compareceu == true)
            result = AMARELO //MARROM
        else if (compareceu == false)
            result = AMARELO //ROXO
        else if (nomeCidadao) { //horario preenchido
            use (TimeCategory) {
                if (dataHora > new Date() + 1.hours)
                    result = AMARELO
                else
                    result = VERMELHO
            }
        }
        result = result ? result : VERDE;
        return result;
    }

    public String getTooltip() {
        String result = null
        if (compareceu == true)
            result = COMPARECEU //marrom
        else if (compareceu == false)
            result = NAO_COMPARECEU //marrom
        else if (nomeCidadao) { //horario preenchido
            use (TimeCategory) {
                if (dataHora > new Date() + 1.hours)
                    result = HORARIO_PREENCHIDO //marrom
                else
                    result = COMPARECIMENTO_INDEFINIDO //marrom
            }
        }
        result = result ? result : LIVRE //marrom
        return result;
//        "#ff4dd2" //rosa
    }

}
