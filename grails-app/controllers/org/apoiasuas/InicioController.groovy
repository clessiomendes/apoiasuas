package org.apoiasuas

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured
import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apoiasuas.fileStorage.FileStorageService
import org.apoiasuas.importacao.ImportacaoFamiliasController
import org.apoiasuas.importacao.ImportarFamiliasService
import org.apoiasuas.redeSocioAssistencial.ServicoService
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.redeSocioAssistencial.ServicoSistemaService
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.ItemMenuDTO
import org.apoiasuas.util.AmbienteExecucao
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsControllerClass

import javax.sql.DataSource

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class InicioController extends AncestralController {

    public static final A = "A"

    GrailsApplication grailsApplication
    ApoiaSuasService apoiaSuasService
    ImportarFamiliasService importarFamiliasService
    ServicoSistemaService servicoSistemaService
    ServicoService servicoService
    DataSource dataSource
    FileStorageService fileStorageService

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
            ImportacaoFamiliasController.setDataUltimaImportacao(session, importarFamiliasService.getDataUltimaImportacaoBD(segurancaService.servicoLogado))

        //Busca um servico aleatorio da rede socio assistencial para usar como anuncio
        render view:'menu', model: [/*opcoes: opcoes,*/ outrasOpcoes: outrasOpcoes, servicoAnuncio: servicoService.getServicoParaAnuncio()]
    }

    def status() {
//        String fornecedorVersaoBancoDeDados = dataSource.getConnection().getMetaData().getDatabaseProductName() + " " + dataSource.getConnection().getMetaData().getDatabaseProductVersion()
        Map logAppenders = [:]
        Logger.getRootLogger().getAllAppenders().each {
            if (it instanceof AppenderSkeleton) {
                logAppenders.put(it.name, ((AppenderSkeleton)it).getThreshold())
            }
//                ((AppenderSkeleton)it).setThreshold(Level.toLevel(level))
        }
        List logLevels = [Level.OFF, Level.FATAL, Level.ERROR, Level.WARN,
                          Level.INFO, Level.DEBUG, Level.ALL];

        render view: "status", model: [/*atualizacoesPendentesBD: apoiaSuasService.atualizacoesPendentes,*/
                                       ocupacaoBD: apoiaSuasService.ocupacaoBD(),
                                       fornecedorVersaoBancoDeDados: null,//fornecedorVersaoBancoDeDados,
                                       configuracoesRepostiorio: fileStorageService.showConfig(),
                                       logAppenders: logAppenders, logLevels: logLevels
        ]
    }

    @Secured([DefinicaoPapeis.STR_SUPER_USER])
    def changeLog(String level) {

        Logger.getRootLogger().getAllAppenders().each {
            if (it instanceof AppenderSkeleton)
                if (params.containsKey(it.name)) {
                    ((AppenderSkeleton)it).setThreshold(Level.toLevel(params.get(it.name)))
                    log.debug("Log ${it.name} em nivel de ${params.get(it.name)}")
                }
        }
        return status()
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
            return render(view: '/login/escolheServicoSistema', model: [servicosDisponiveis: ServicoSistema.findAllByHabilitado(true).sort{it.nome}])

        segurancaService.setServicoLogado(ServicoSistema.get(params.servicoSistema))
        return redirect(action: "menu")
    }

    def escolheServicoSistema() {
        return render(view: '/login/escolheServicoSistema', model: [servicosDisponiveis: ServicoSistema.findAllByHabilitado(true).sort{it.nome}])
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def alive() {
        response.status = 200 //OK
        return render ([mensagem: "apoiasuas ok"] as JSON)
    }

}
