package org.apoiasuas

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.ItemMenuDTO
import org.apoiasuas.util.AmbienteExecucao
import org.codehaus.groovy.grails.commons.GrailsControllerClass

@Secured([DefinicaoPapeis.USUARIO_LEITURA])
class InicioController {

    public static final A = "A"

    def springSecurityService

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

        render view:'index', model: [/*opcoes: opcoes,*/ outrasOpcoes: outrasOpcoes]
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
