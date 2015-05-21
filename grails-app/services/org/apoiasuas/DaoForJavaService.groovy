package org.apoiasuas

import grails.transaction.Transactional
import org.apoiasuas.importacao.StatusImportacao
import org.codehaus.groovy.grails.support.SoftThreadLocalMap
import org.springframework.transaction.annotation.Propagation
import org.apoiasuas.importacao.DefinicoesImportacaoFamilias
import org.apoiasuas.importacao.LinhaTentativaImportacao
import org.apoiasuas.importacao.TentativaImportacao

class DaoForJavaService {

    def sessionFactory
    def segurancaService
    static transactional = false

    @Transactional(propagation = Propagation.MANDATORY)
    public void gravaNovaLinhaTentativaImportacao(LinhaTentativaImportacao linha) {
        linha.save(failOnError: true)
    }

    @Transactional(propagation = Propagation.MANDATORY)
    def gravaTentativaImportacao(TentativaImportacao importacao) {
        if (!importacao.save(failOnError: true)) {
            log.error(["Erro ao gravar ": importacao])
            throw new RuntimeException(["Erro ao gravar ": importacao])
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    void clearGorm() {
            sessionFactory.currentSession.flush()
            sessionFactory.currentSession.clear()
            //Limpando informacoes desnecessarias relativas a validacao de dominios do Grails (http://burtbeckwith.com/blog/?p=73)
            ((SoftThreadLocalMap)org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP).get().clear()
            log.debug("Limpando caches")
    }

    @Transactional(propagation = Propagation.MANDATORY)
    TentativaImportacao novaTentativaImportacao() {
        TentativaImportacao result = new TentativaImportacao()
        result.criador = segurancaService.usuarioLogado
        result.dateCreated = new Date()
        result.setStatus(StatusImportacao.EM_ANDAMENTO)
        result.save(failOnError: true)
        return result
    }

    @Transactional(propagation = Propagation.MANDATORY)
    void atualizaDefinicoes(int linhaDoCabecalho, int abaDaPlanilha) {
        DefinicoesImportacaoFamilias definicoes = DefinicoesImportacaoFamilias.findAll().first();
        definicoes.linhaDoCabecalho = linhaDoCabecalho
        definicoes.abaDaPlanilha = abaDaPlanilha
        definicoes.save()
    }
}
