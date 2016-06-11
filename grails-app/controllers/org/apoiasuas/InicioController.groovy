package org.apoiasuas

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.importacao.ImportacaoFamiliasController
import org.apoiasuas.importacao.ImportarFamiliasService
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.redeSocioAssistencial.ServicoSistemaService
import org.apoiasuas.seguranca.ApoiaSuasUser
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.ItemMenuDTO
import org.apoiasuas.seguranca.SegurancaService
import org.apoiasuas.seguranca.UsuarioSistema
import org.apoiasuas.util.AmbienteExecucao
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsControllerClass

import javax.sql.DataSource

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class InicioController extends AncestralController {

    public static final A = "A"

    GrailsApplication grailsApplication
    SegurancaService segurancaService
    ApoiaSuasService apoiaSuasService
    ImportarFamiliasService importarFamiliasService
    ServicoSistemaService servicoSistemaService
    DataSource dataSource

    static defaultAction = "actionInicial"

    def actionInicial() {
/*
        UsuarioSistema usuarioLogado = segurancaService.getUsuarioLogado();
        ApoiaSuasUser principal = segurancaService.getPrincipal();

        if (! principal.servicoSistemaSessaoCorrente) {
            //primeiro acesso à tela de menu nessa sessao
            if (segurancaService.isSuperUser()) {
                return render(view: 'escolheServicoSistema', model: [servicosDisponiveis: ServicoSistema.findAllByHabilitado(true).sort{it.nome}])
            } else { //demais usuarios nao administradores
                principal.servicoSistemaSessaoCorrente = usuarioLogado.servicoSistemaSeguranca
            }
        }
         */

        if (segurancaService.getUsuarioLogado().temPerfil(DefinicaoPapeis.STR_RECEPCAO))
            return redirect(controller: "cidadao", action: "procurarCidadao")
        else
            return redirect(action: "menu")
    }

    /**
     * Exibicao da tela de menu inicial
     */
    def menu() {
        List<GrailsControllerClass> opcoes = []
        List<GrailsControllerClass> outrasOpcoes = []

        if (! AmbienteExecucao.isProducao()) {
            outrasOpcoes = grailsApplication.controllerClasses.minus(opcoes)
        }

        //armazena na sessao a data e hora da ultima importacao concluida
        if (! ImportacaoFamiliasController.getDataUltimaImportacao(session))
            ImportacaoFamiliasController.setDataUltimaImportacao(session, importarFamiliasService.getDataUltimaImportacao())

        render view:'menu', model: [/*opcoes: opcoes,*/ outrasOpcoes: outrasOpcoes]
    }

    def status() {
        String fornecedorVersaoBancoDeDados = dataSource.getConnection().getMetaData().getDatabaseProductName() + " " + dataSource.getConnection().getMetaData().getDatabaseProductVersion()
        render view: "status", model: [atualizacoesPendentesBD: apoiaSuasService.atualizacoesPendentes,
                                       ocupacaoBD: apoiaSuasService.ocupacaoBD(),
                                       fornecedorVersaoBancoDeDados: fornecedorVersaoBancoDeDados
        ]
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
        render(view: 'menu')
    }

    def servicoEscolhido() {
        if (! params.servicoSistema)
            return render(view: 'escolheServicoSistema', model: [servicosDisponiveis: ServicoSistema.findAllByHabilitado(true).sort{it.nome}])

        segurancaService.setServicoLogado(ServicoSistema.get(params.servicoSistema))
        return redirect(action: "menu")
    }

    def escolheServicoSistema() {
        return render(view: 'escolheServicoSistema', model: [servicosDisponiveis: ServicoSistema.findAllByHabilitado(true).sort{it.nome}])
    }

}
