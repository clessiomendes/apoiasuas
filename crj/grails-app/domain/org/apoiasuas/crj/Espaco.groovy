package org.apoiasuas.crj

import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.DominioProtegidoServico
import org.apoiasuas.util.Modulos

class Espaco implements DominioProtegidoServico {

    String descricao;
    ServicoSistema servicoSistemaSeguranca
    Date dateCreated
    Date lastUpdated

    static mapping = {
        table schema: Modulos.CRJ;
        id generator: 'native', params: [sequence: Modulos.CRJ+'.sq_espaco']
        servicoSistemaSeguranca fetch: 'join' //por questoes de seguranca, sempre que um link eh obtido do banco de dados, o servicoSistema precisara ser consultado
    }

    static constraints = {
        descricao(nullable: false, maxSize: 255, unique: 'servicoSistemaSeguranca'); //Cria um índice composto e único contendo os campos servicoSistema(id) e descricao
        servicoSistemaSeguranca(nullable: false);
        dateCreated(nullable: false);
        lastUpdated(nullable: false);
    }

}
