package org.apoiasuas.marcador

import org.apoiasuas.ProgramasPreDefinidos
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.cidadao.Marcador
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.UsuarioSistema

class Programa implements Marcador {

    String nome
    String sigla
    ProgramasPreDefinidos programaPreDefinido
    ServicoSistema servicoSistemaSeguranca;

/*
    Date data;
    UsuarioSistema tecnico;
    Boolean habilitado;
    String observacao;
//    Cidadao cidadao;
*/

    //transiente:
    Boolean selected
    static transients = ['selected', 'descricao']

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_programa']
    }

    static constraints = {
        nome(nullable: false, maxSize: 255);
        sigla(nullable: true, maxSize: 10);
        observacao(length: 1000000);
    }

    public String siglaENome() {
        return sigla ? (sigla + (nome ? " ($nome)" : "")) : nome
    }

    public String toString() {
        return nome
    }

    @Override
    public String getDescricao() {
        return nome;
    }

    @Override
    public void setDescricao(String descricao) {
        nome = descricao;
    }
}
