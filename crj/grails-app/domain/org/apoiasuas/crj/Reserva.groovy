package org.apoiasuas.crj

import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.DominioProtegidoServico
import org.apoiasuas.util.Modulos

class Reserva implements DominioProtegidoServico {

    public static enum Tipo { SIMPLES, RECORRENTE }
    public static enum UnidadeRecorrencia { DIARIO, SEMANAL, MENSAL }
    public static enum TipoDiaRecorrenciaMensal { MES, SEMANA }

    String descricao;
    Espaco espaco;

    ServicoSistema servicoSistemaSeguranca
    Date dateCreated
    Date lastUpdated
    Date dataInicio
    Date horaInicio
    Date dataFim
    Date horaFim
    Tipo tipo
    Boolean diaInteiro
    UnidadeRecorrencia unidadeRecorrencia
    String diasRecorrenciaSemanal
    TipoDiaRecorrenciaMensal tipoDiaRecorrenciaMensal
    Integer totalRecorrencias

//    static hasMany = [historico: HistoricoPedidoCertidao]

    static mapping = {
        table schema: Modulos.CRJ;
        id generator: 'native', params: [sequence: Modulos.CRJ+'.sq_reserva']
        servicoSistemaSeguranca fetch: 'join' //por questoes de seguranca, sempre que um link eh obtido do banco de dados, o servicoSistema precisara ser consultado
    }

    static constraints = {
        descricao(nullable: false, maxSize: 255);
        servicoSistemaSeguranca(nullable: false);
        dateCreated(nullable: false);
        lastUpdated(nullable: false);
        dataInicio(nullable: false);
        tipo(nullable: false);
    }

}
