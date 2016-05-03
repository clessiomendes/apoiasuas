import org.apoiasuas.ApoiaSuasService
import org.apoiasuas.Configuracao
import org.apoiasuas.ConfiguracaoService
import org.apoiasuas.ProgramaService
import org.apoiasuas.ServicoNovoService
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.Formulario
import org.apoiasuas.formulario.FormularioService
import org.apoiasuas.formulario.PreDefinidos
import org.apoiasuas.importacao.ImportarFamiliasService
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.SegurancaService
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.AmbienteExecucao
import org.codehaus.groovy.grails.commons.ApplicationAttributes

import javax.servlet.ServletContext
import java.sql.SQLException

class BootStrap {

    def roleHierarchy
    def groovySql

    SegurancaService segurancaService
    ImportarFamiliasService importarFamiliasService
    FormularioService formularioService
    ApoiaSuasService apoiaSuasService
    ProgramaService programaService
    ConfiguracaoService configuracaoService
    ServicoNovoService servicoNovoService

    public final String VW_REFERENCIAS = "create view vw_referencias as select min(id) as referencia_id, familia_id " +
            " from cidadao where referencia = "+AmbienteExecucao.SqlProprietaria.getBoolean(true)+
            " group by familia_id"

    def init = { servletContext ->

        AmbienteExecucao.inicioAplicacao

        //Criando um novo metodo "update" em todos os objetos groovy da aplicacao
        Object.metaClass.update = {
            updateAttributesFromMap delegate, it
        }

        //Validando esquema de banco de dados
        validaEsquemaBD()

        //Recriando views
        recriaViewsBD()

//        //Limpando tabelas temporarias
//        limpaTabelasTemporarias()

        //sobrescrevendo a configuracao de seguranca (hierarquia de papeis)
        roleHierarchy.setHierarchy(DefinicaoPapeis.hierarquiaFormatada)

        inicializacoesServicos()

        servicoNovoService.registraJSON()

        inicializaConfiguracoes(servletContext)
    }

    private void inicializaConfiguracoes(ServletContext servletContext) {
        UsuarioSistema.withTransaction { status ->
            try {
                configuracaoService.inicializa()
            } catch (Throwable t) {
                status.setRollbackOnly()
                throw t;
            }
        }
        servletContext.configuracao = configuracaoService.getConfiguracaoReadOnly()
    }

    private void inicializacoesServicos() {
        UsuarioSistema.withTransaction { status ->
            try {
                UsuarioSistema admin = segurancaService.inicializaSeguranca()

                importarFamiliasService.inicializaDefinicoes(admin)
                programaService.inicializaProgramas(admin)

                try {
                    //Se ambiente de desenvolvimento, descarta eventuais alteracoes em formularios e reinicializa tudo
                    formularioService.inicializaFormularios(admin, AmbienteExecucao.desenvolvimento) //Para producao, acionar o menu "Reinstalar formularios pre-definidos"

                } catch (Throwable t) {
                    if (AmbienteExecucao.desenvolvimento)
                        t.printStackTrace()
                    else
                        throw t
                }

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
//    private void limpaTabelasTemporarias() {
//        try {
//            log.debug("truncate table linha_tentativa_importacao")
//            groovySql.execute("truncate table linha_tentativa_importacao")
//        } catch (SQLException e) {
//            log.error("Erro limpando tabela temporaria de importação")
//            e.printStackTrace();
//        }
//    }

    private void validaEsquemaBD() {
        String[] atualizacoesPendentes = apoiaSuasService.getAtualizacoesPendentes()
        if (atualizacoesPendentes) {
            String erro = "Detectadas atualizacoes pendentes no banco de dados:"
            atualizacoesPendentes.each { erro += "\n" + it + ";" }
            log.error(erro);
            throw new RuntimeException("Banco de dados fora de sincronia com a aplicação (ver mensagens anteriores). Startup interrompido.")
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