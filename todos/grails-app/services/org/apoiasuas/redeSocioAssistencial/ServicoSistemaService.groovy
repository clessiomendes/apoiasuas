package org.apoiasuas.redeSocioAssistencial

import grails.transaction.Transactional
import org.apoiasuas.seguranca.SegurancaService
import org.hibernate.Hibernate

@Transactional(readOnly = true)
class ServicoSistemaService {

    def groovySql

    private static final String SQL_PROXIMO_CODIGO_LEGADO = '''\
            SELECT max ( CAST( REGEXP_REPLACE(COALESCE(codigo_legado, '0'), '[^0-9]*' ,'0') as integer) ) +1
            FROM familia where codigo_legado !~ '[^0-9]'
        '''

    public static final String NOME_SERVICO_ADM_SISTEMA = "Administração do Sistema"
    SegurancaService segurancaService

    @Transactional
    public ServicoSistema grava(ServicoSistema servicoSistema) {
        return servicoSistema.save()
    }

    @Transactional
    /**
     * Ao ser executado pela primeira vez, verifica se já existe um registro na tabela de configurações e, em caso
     * contrário, cria um
     */
    public ServicoSistema inicializa() {
        def servicoAdm = ServicoSistema.findByNome(NOME_SERVICO_ADM_SISTEMA)

        if (! servicoAdm) {
            ServicoSistema servicoSistema = new ServicoSistema()
            servicoSistema.habilitado = true
            servicoSistema.nome = NOME_SERVICO_ADM_SISTEMA
            servicoAdm = servicoSistema.save()
        }

        return servicoAdm
    }

    @Transactional(readOnly = true)
    public ServicoSistema get(long id) {
        ServicoSistema result = ServicoSistema.get(id)
        Hibernate.initialize(result.abrangenciaTerritorial)
        return result
    }

    @Transactional(readOnly = true)
    /**
     * Gera um novo codigo legado somando 1 ao ultimo EXCLUSIVAMENTE NUMERICO presente no banco de dados.
     */
    public String proximoCodigoLegado() {
//        def sql = new Sql(sessionFactory.currentSession.connection())
//        sql.rows("SELECT max(id) from familia")
//        groovySql.find()

        def sqlResult = groovySql.firstRow(SQL_PROXIMO_CODIGO_LEGADO);
        log.debug("Proximo codigo legado: ${sqlResult[0]}");
//        SELECT max ( CAST( REGEXP_REPLACE(COALESCE(codigo_legado, '0'), '[^0-9]*' ,'0') as integer) ) FROM familia where codigo_legado !~ '[^0-9]';
        return sqlResult[0] ?: '1'; //se for o primeiro, começa do 1
    }
}
