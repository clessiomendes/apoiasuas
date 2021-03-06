import apoiasuas.ImportacaoJob
import org.apoiasuas.ApoiaSuasService
import org.apoiasuas.DetalheService
import org.apoiasuas.FullTextSearchService
import org.apoiasuas.LookupService
import org.apoiasuas.ProgramaService
import org.apoiasuas.cidadao.MarcadorService
import org.apoiasuas.fileStorage.FileStorageService
import org.apoiasuas.formulario.FormularioService
import org.apoiasuas.importacao.ImportarFamiliasService
import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorialService
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.redeSocioAssistencial.ServicoSistemaService
import org.apoiasuas.seguranca.ASMenuBuilder
import org.apoiasuas.seguranca.ApoiaSuasPersistenceListener
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.SegurancaService
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.ambienteExecucao.AmbienteExecucao
import org.apoiasuas.ambienteExecucao.SqlProprietaria
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.apoiasuas.redeSocioAssistencial.RecursosServico
import java.sql.SQLException
import groovy.sql.Sql
import java.util.logging.Level

class NucleoBootStrap {

    def roleHierarchy
    def groovySql
    def dataSource
    def dataSource_log

    SegurancaService segurancaService
    ImportarFamiliasService importarFamiliasService
    FormularioService formularioService
    ApoiaSuasService apoiaSuasService
    ProgramaService programaService
    ServicoSistemaService servicoSistemaService
    AbrangenciaTerritorialService abrangenciaTerritorialService
    DetalheService detalheService
    GrailsApplication grailsApplication
    FullTextSearchService fullTextSearchService
    FileStorageService fileStorageService
    MarcadorService marcadorService
    LookupService lookupService
    ASMenuBuilder menuBuilder

    public final String VW_REFERENCIAS = "create view vw_referencias as select min(id) as referencia_id, familia_id " +
            " from cidadao where habilitado = "+AmbienteExecucao.SQL_FACADE.getBoolean(true)+" and referencia = "+AmbienteExecucao.SQL_FACADE.getBoolean(true)+
            " group by familia_id"

    def init = { servletContext ->

//        ASMenuBuilder menuBuilder = ctx.getBean(ASMenuBuilder.class)
        menuBuilder.montaMenuBasico();

        //Criando um novo metodo "update" em todos os objetos groovy da aplicacao
        Object.metaClass.update = {
            updateAttributesFromMap delegate, it
        }

        //Criando um novo metodo em todos os objetos groovy da aplicacao
//        Object.metaClass.apenasCamposExplicitamenteDeclarados = {
//            updateAttributesFromMap delegate, it
//        }

        //Validando esquema de banco de dados
        validaEsquemaBD()

        //Recriando views
        recriaViewsBD()

//        //Limpando tabelas temporarias
//        limpaTabelasTemporarias()

        //sobrescrevendo a configuracao de seguranca (hierarquia de papeis)
        roleHierarchy.setHierarchy(DefinicaoPapeis.getHierarquiaFormatada())

        ServicoSistema servicoAdm = inicializaServicosSistema();
        UsuarioSistema admin = inicializaAdmin(servicoAdm)

        inicializacoesDiversas(admin)

        abrangenciaTerritorialService.registraJSON()
//        detalheService.registraJSON()

        addPersistenceListener()

        if (AmbienteExecucao.isProducao())
            fullTextSearchService.index()

//        RecursosServico.initTest();

        Sql.LOG.level = Level.FINE; //necessario para gerar log de sqls que nao passam pelo hibernate

    }

    private void addPersistenceListener() {
        grailsApplication.mainContext.eventTriggeringInterceptor.datastores.each { key, datastore ->
            def listener = new ApoiaSuasPersistenceListener(datastore)
            listener.segurancaService = segurancaService
            grailsApplication.mainContext.addApplicationListener(listener)
        }
    }

    private ServicoSistema inicializaServicosSistema() {
        UsuarioSistema.withTransaction { status ->
            try {
                return servicoSistemaService.inicializa()
            } catch (Throwable t) {
                status.setRollbackOnly()
                throw t;
            }
        }
    }

    private UsuarioSistema inicializaAdmin(ServicoSistema servicoAdm) {
        UsuarioSistema.withTransaction { status ->
            try {
                return segurancaService.inicializaSeguranca(servicoAdm)
            } catch (Throwable t) {
                status.setRollbackOnly()
                throw t;
            }
        }
    }

    private void inicializacoesDiversas(UsuarioSistema admin) {
//        log.debug(AmbienteExecucao.CONFIGURACOES_FACADE.parametroTeste)
//        log.debug(AmbienteExecucao.sysProperties("org.apoiasuas.parametroTeste"))
        UsuarioSistema.withTransaction { status ->
            try {
//                importarFamiliasService.inicializaDefinicoes(admin)
                fileStorageService.init();
//                marcadorService.init();
                if (AmbienteExecucao.servidorPrimario)
                    programaService.inicializaProgramas(admin);
//                marcadorService.inicializaMarcadores(admin);

                try {
                    //Descarta eventuais alteracoes em formularios e reinicializa tudo
                    if (AmbienteExecucao.servidorPrimario)
                        formularioService.inicializaFormularios(admin)
                } catch (Throwable t) {
                    if (AmbienteExecucao.desenvolvimento)
                        t.printStackTrace()
                    else
                        throw t
                }

                try {
                    //Alimenta tabelas lookup aa partir dos respectivos arquivos txt de configuracao
//                    if (AmbienteExecucao.servidorPrimario)
                    lookupService.inicializaLookups();
                } catch (Throwable t) {
                    if (AmbienteExecucao.desenvolvimento)
                        t.printStackTrace()
                    else
                        throw t
                }


                ImportacaoJob.schedule(ImportacaoJob.CRON_DEFINITION);

            } catch (Throwable t) {
                status.setRollbackOnly()
                throw t;
            }
        }
    }

    private void recriaViewsBD() {
        try {
            log.debug("drop view vw_referencias")
            groovySql.execute("drop view vw_referencias")
            log.info("Atualizando vw_referencias no banco de dados")
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("Criando vw_referencias pela primeira vez no banco de dados")
        }
        log.debug(VW_REFERENCIAS);
        groovySql.execute(VW_REFERENCIAS);
    }

//Movido para FormularioService (antes de cada nova importação)
    private void limpaTabelasTemporarias() {
        try {
            log.debug("truncate table linha_tentativa_importacao")
            groovySql.execute("truncate table linha_tentativa_importacao")
        } catch (SQLException e) {
            log.error("Erro limpando tabela temporaria de importação")
            e.printStackTrace();
        }
    }

    private void validaEsquemaBD() {
        String[] atualizacoesPendentes = []
        try {
            atualizacoesPendentes += apoiaSuasService.getAtualizacoesPendentes(dataSource, "sessionFactory")
            if (dataSource_log)
                atualizacoesPendentes += apoiaSuasService.getAtualizacoesPendentes(dataSource_log, "sessionFactory_log")
        } catch (Exception e) {
            log.error("Impossível verificar estrutura do banco de dados");
            e.printStackTrace();
        }
        if (atualizacoesPendentes) {
            String erro = "Detectadas atualizacoes pendentes no banco de dados:"
//            Formatter formatter = FormatStyle.DDL.getFormatter();
            atualizacoesPendentes.each {
                erro += "\n" + it + ";"
            }
//            log.error(erro);
            throw new RuntimeException("Startup interrompido. Banco de dados fora de sincronia com a aplicação: "+erro)
        }
    }

    def destroy = {
    }

    /**
     * Metodo a ser injetado em todos os objetos groovy da aplicacao para atualizar atributos quaisquer aa partir de um mapa:
     * Exemplo de uso:
     * meuObjeto.update([campo1: 'valor1', campo2: 'valor2']
     */
    private static Object updateAttributesFromMap(Object instanciaAAtualizar, Map<String, Object> propriedadesASubstituir) {
        propriedadesASubstituir.each { key, value ->
            if (instanciaAAtualizar.hasProperty(key))
                instanciaAAtualizar."${key}" = value
        }
        return instanciaAAtualizar
    }

}