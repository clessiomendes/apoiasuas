package org.apoiasuas.redeSocioAssistencial

import grails.transaction.Transactional
import org.apoiasuas.seguranca.SegurancaService
import org.hibernate.Hibernate

class ServicoSistemaService {

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

//    @Transactional(readOnly = true)
//    public ServicoSistema getServicoSistemaReadOnly() {
//        ServicoSistema servicoSistema = segurancaService.getUsuarioLogado()?.servicoSistemaSeguranca
//        servicoSistema?.discard()
//        return servicoSistema
//    }

}
