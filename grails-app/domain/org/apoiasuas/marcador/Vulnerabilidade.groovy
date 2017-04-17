package org.apoiasuas.marcador

import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.UsuarioSistema

class Vulnerabilidade implements Marcador {

    String descricao;
    ServicoSistema servicoSistemaSeguranca;
    Boolean habilitado;

    //transientes:
    Date data;
    UsuarioSistema tecnico;
    Boolean selecionado;
    String observacao;
    static transients = ['data', 'tecnico', 'selecionado', 'observacao']

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_vulnerabilidade'];
        version defaultValue: "0";
    }

    static constraints = {
        descricao(nullable: false, blank: false, maxSize: 255);
    }

}
