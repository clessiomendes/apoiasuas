package org.apoiasuas

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured
import groovy.sql.Sql
import groovy.time.TimeCategory
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.ItemMenuDTO
import org.apoiasuas.util.AmbienteExecucao
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.dialect.PostgreSQL81Dialect
import org.hibernate.tool.hbm2ddl.DatabaseMetadata

@Secured([DefinicaoPapeis.USUARIO_LEITURA])
class InicioController {

    public static final A = "A"

    def springSecurityService
    SessionFactory sessionFactory
    def grailsApplication
    def apoiaSuasService
    def importarFamiliasService

    /**
     * Exibicao da tela de menu inicial
     */
    def index() {
        List<GrailsControllerClass> opcoes = []
        List<GrailsControllerClass> outrasOpcoes = []
/*
        opcoes = grailsApplication.controllerClasses.findAll{ it.fullName in [
                    'org.apoiasuas.formulario.EmissaoFormularioController',
                    'org.apoiasuas.importacao.ImportacaoFamiliasController',
                    'org.apoiasuas.seguranca.UsuarioSistemaController'
            ] }
*/
        if (! AmbienteExecucao.isProducao()) {
            outrasOpcoes = grailsApplication.controllerClasses.minus(opcoes)
        }

//        List<ItemMenuDTO> ops = []
//        ops << itemMenu('Emissão de formulários', EmissaoFormularioController.getClass(), [DefinicaoPapeis.USUARIO_LEITURA])
//        ops << itemMenu('Emissão de formulários', forward(controller: 'EmissaoFormularioController', action: 'escolherFamilia'), 'meIgnore')

        //armazena na sessao a data e hora da ultima importacao concluida
        if (! session.ultimaImportacao)
            session.ultimaImportacao = importarFamiliasService.ultimaImportacao

        render view:'index', model: [/*opcoes: opcoes,*/ outrasOpcoes: outrasOpcoes]
    }

    def status() {
        request.setAttribute("atualizacoesPendentesBD", apoiaSuasService.atualizacoesPendentes);
    }

    private ItemMenuDTO[] itemMenu(String descricao, Class classeController, String[] papeisAcesso) {
        String url = grailsApplication.getControllerClass(classeController.name)
        log.debug("Novo item de menu: descricao ${descricao}, url ${url}, papeisAcesso ${papeisAcesso}")
        return new ItemMenuDTO(descricao: descricao, url: url, papeisAcesso: papeisAcesso)
    }

    private ItemMenuDTO[] itemMenu(String descricao, String url, String[] papeisAcesso) {
        log.debug("Novo item de menu: descricao ${descricao}, url ${url}, papeisAcesso ${papeisAcesso}")
        return new ItemMenuDTO(descricao: descricao, url: url, papeisAcesso: papeisAcesso)
    }

    def recarregarConfiguracoes() {
        SpringSecurityUtils.reloadSecurityConfig()
        render(view: 'index')
    }
}
