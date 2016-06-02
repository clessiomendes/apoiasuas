package org.apoiasuas

import grails.transaction.Transactional
import org.apoiasuas.processo.ProcessoService

@Transactional
class LinkService {

    @Transactional
    public Link grava(Link link) {
        return link.save()
    }

    @Transactional
    public Link apaga(Link link) {
        return link.delete()
    }

}
