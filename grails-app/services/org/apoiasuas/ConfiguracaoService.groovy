package org.apoiasuas

import grails.transaction.Transactional
import org.apache.commons.lang.StringEscapeUtils
import org.apoiasuas.util.HqlPagedResultList
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import java.util.regex.Pattern

class ConfiguracaoService {

    @Transactional
    public Configuracao grava(Configuracao configuracao) {
        return configuracao.save()
    }

    @Transactional
    /**
     * Ao ser executado pela primeira vez, verifica se já existe um registro na tabela de configurações e, em caso
     * contrário, cria um
     */
    public void inicializa() {
        if (Configuracao.getAll().isEmpty()) {
            Configuracao configuracao = new Configuracao()
            configuracao.id = 1
            configuracao.save()
        }
    }

    @Transactional(readOnly = true)
    public Configuracao getConfiguracaoReadOnly() {
        // TODO: Ao implementar o acesso simultâneo à partir de diferentes equipamentos (multi-tenant), retornar a configuração do equipamento atual, e não a última
        Configuracao configuracao = Configuracao.getAll().first() //Presume-se um único registro no banco de dados
        configuracao.discard()
        return configuracao
    }

}
