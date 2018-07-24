package org.apoiasuas.cidadao

import org.apoiasuas.cidadao.detalhe.CampoDetalhe
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.UsuarioSistema

class Auditoria {

    public static enum Tipo { MUDANCA_TECNICO_FAMILIA("Mudança do técnico de referência da família"),
//        ACOMPANHAMENTO_FAMILIA("Acompanhamento familiar"),
        PROGRAMA_FAMILIA("Participação em programa, serviço ou benefício");

        public static List<Tipo> TIPOS_ACOMPANHAMENTO = [MUDANCA_TECNICO_FAMILIA, PROGRAMA_FAMILIA];

        public String descricao
        public Tipo(String descricao) {
            this.descricao = descricao;
        }
    }

    Tipo tipo;
    Familia familia;
    Cidadao cidadao;
    String descricao;
    String detalhes; //armazena informações estruturadas especificas de cada tipo de auditoria (como referencias a outras entidades)
    UsuarioSistema criador;
    ServicoSistema servicoSistemaSeguranca
    Date dateCreated;

    //TRANSIENTES:
    Map<String, CampoDetalhe> mapaDetalhes = [:]

    public setCidadao(Cidadao cidadao) {
        if (cidadao?.familia)
            familia = cidadao.familia;
    }

    static transients = ['mapaDetalhes'];

    static mapping = {
        familia index:'Auditoria_Familia_Idx';
        cidadao index:'Auditoria_Cidadao_Idx';
        criador index:'Auditoria_Criador_Idx';
        tipo index:'Auditoria_Tipo_Idx';
        id generator: 'native', params: [sequence: 'sq_auditoria'];
        servicoSistemaSeguranca fetch: 'join', //por questoes de seguranca, sempre que um cidadao eh obtido do banco de dados, o servicoSistema precisara ser consultado
                                index:'Auditoria_Servico_Sistema_Idx';
        detalhes length: 1000000;
        descricao length: 1024;
    }

    static constraints = {
        id(bindable: true)
        tipo(nullable: false)
        servicoSistemaSeguranca(nullable: false)
        criador(nullable: false)
    }

    public String toString() {
        return tipo.descricao + " - data: " + dateCreated?.format("dd/MM/yyyy") + " - operador: " + criador?.username
    }
/*
    public String montaDescricao() {
        switch (tipo) {
            case Tipo.MUDANCA_REFERENCIA:
                UsuarioSistema tecnicoAnterior, tecnicoPosterior;
                if (mapaDetalhes['tecnicoAnterior'])
                    tecnicoAnterior = Usu
                if ( && mapaDetalhes['tecnicoPosterior'])
                    return "Técnico de referência alterado de "
            default: throw new RuntimeException("Tipo não tratada: ${tipo}");
        }
    }
*/

}
