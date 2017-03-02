package org.apoiasuas.marcador

import org.apoiasuas.ProgramasPreDefinidos
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.UsuarioSistema

class Programa implements Marcador {

    String descricao
    String sigla
    ProgramasPreDefinidos programaPreDefinido
    ServicoSistema servicoSistemaSeguranca;
    Boolean habilitado;

    //transientes:
    Date data;
    UsuarioSistema tecnico;
    Boolean selecionado;
    String observacao;
    static transients = ['data', 'tecnico', 'selecionado', 'observacao']

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_programa']
    }

    static constraints = {
        descricao(nullable: false, blank: false, maxSize: 255);
        sigla(nullable: true, maxSize: 10);
//        observacao(length: 1000000);
    }

    public String siglaENome() {
        return sigla ? (sigla + (descricao ? " ($descricao)" : "")) : descricao
    }

    public String toString() {
        return descricao
    }

}
