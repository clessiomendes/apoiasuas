package org.apoiasuas.cidadao

import grails.transaction.Transactional
import groovy.sql.GroovyRowResult

@Transactional(readOnly = true)
class MonitoramentoService {

    def groovySql;
    def segurancaService;

    public static final int MAX_MONITORAMENTOS_LISTAGEM = 200;

    @Transactional
    public Monitoramento gravaMonitoramento(Monitoramento monitoramento) {
        if (monitoramento.efetivado)
            monitoramento.suspenso = false;
        return monitoramento.save();
    }

    @Transactional
    public boolean apagaMonitoramento(Monitoramento monitoramento) {
        monitoramento.delete();
        return true;
    }

    @Transactional
    public boolean efetivaMonitoramento(Monitoramento monitoramento) {
        monitoramento.efetivado = true;
        monitoramento.dataEfetivada = new Date();
        gravaMonitoramento(monitoramento);
        return true;
    }

    @Transactional(readOnly = true)
    /**
     * Retorna quantidade monitoramentos agrupados por situação (executado, atrasado...)
     * idTecnico filtro opcional por tecnico
     */
    public Map<String, Long> qntMonitoramentosAgruparSituacao(Long idTecnico) {
        def filtros = [:];
        String sqlCase = "CASE ";
        Monitoramento.Situacao.values().each {
            sqlCase += "\n    WHEN ${it.getSqlWhere('m')} THEN '${it.label}' "
        }
        sqlCase += " END";
        String sql = "select " + sqlCase +  " as situacao, " +
                "\n count(distinct  m.id) as qntMonitoramentos " +
                baseSqlMonitoramentos(filtros, idTecnico) +
                "\n group by " + sqlCase;

        log.debug("\n"+sql);
        List<GroovyRowResult> resultado = groovySql.rows(sql, filtros) ;
        Map<Monitoramento.Situacao, Long> result = [:];

        Monitoramento.Situacao.values().each { Monitoramento.Situacao situacao ->
            result.put(situacao, 0); //para que apareçam também os resultados vazios...
            resultado.each {
                if (it['situacao'] == situacao.label)
                    result.put(situacao, it['qntMonitoramentos'])
            }
        }

        return result.sort{ it.key?.label?.toLowerCase() };
    }

    private String baseSqlMonitoramentos(Map filtros, Long idTecnico) {
        String result =
                "\n from monitoramento m join familia f on f.id = m.familia" +
                        "\n where f.servico_sistema_seguranca_id = :id_servico_seguranca";
        filtros << [id_servico_seguranca: segurancaService.servicoLogado.id];
        if (idTecnico) {
            result += "\n and m.responsavel_id = :id_tecnico";
            filtros << [id_tecnico: idTecnico]
        }
        return result
    }

    @Transactional(readOnly = true)
    /**
     * Retorna listagem de monitoramentos filtrando por situacao e ou tecnico
     * situacao filtro opcional por situacao
     * idTecnico filtro opcional por um tecnico especifico
     */
    public List<Monitoramento> getMonitoramentos(Monitoramento.Situacao situacao, Long idTecnico) {
        def filtros = [:]
        String sql = 'select distinct m.id as idMonitoramento ' + baseSqlMonitoramentos(filtros, idTecnico);
        if (situacao) {
            sql += " and ("+situacao.getSqlWhere("m")+")";
        }
        List<Monitoramento> result = []
        log.debug("SQL getMonitoramentos:" +"\n" + sql + "\n "+filtros);
        if (filtros.isEmpty())
            filtros = []
        groovySql.rows(sql, filtros, 0, MAX_MONITORAMENTOS_LISTAGEM).each {
            Monitoramento monitoramento = Monitoramento.get(it['idMonitoramento']);
            if (monitoramento)
                result << monitoramento
        }
        return result.sort();
    }

}
