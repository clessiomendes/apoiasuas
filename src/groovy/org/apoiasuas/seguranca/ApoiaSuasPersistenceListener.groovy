package org.apoiasuas.seguranca

import groovy.util.logging.Log4j
import org.grails.datastore.mapping.core.Datastore
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEventListener
import org.grails.datastore.mapping.engine.event.PostLoadEvent
import org.grails.datastore.mapping.engine.event.PreDeleteEvent
import org.grails.datastore.mapping.engine.event.PreInsertEvent
import org.grails.datastore.mapping.engine.event.PreUpdateEvent
import org.grails.datastore.mapping.engine.event.SaveOrUpdateEvent
import org.grails.datastore.mapping.engine.event.ValidationEvent

/**
 * Event Listener para todas as ações de alteracao e consulta ao banco de dados
 * Baseado em http://blog.andresteingress.com/2014/10/04/grails-principal-stamp/
 */
@Log4j
class ApoiaSuasPersistenceListener extends AbstractPersistenceEventListener {

    SegurancaService segurancaService

    ApoiaSuasPersistenceListener(Datastore datastore) {
        super(datastore)
    }

    @Override
    protected void onPersistenceEvent(AbstractPersistenceEvent event) {

        segurancaService.testaAcessoDominio(event.entityObject)

/*
        def entityObject = event.entityObject

        if (entityObject && temPropriedadeSeguranca(entityObject)) {
            if (entityObject.id)
                testaAcessoDominio(entityObject)
            else
                setServicoSistema(entityObject)
        }
*/
    }

/*
    private boolean temPropriedadeSeguranca(def entityObject) {
        return entityObject.metaClass.hasProperty(entityObject, ATRIBUTO_SERVICO_SISTEMA)
    }

    private void setServicoSistema(def entityObject)  {
        if (! segurancaService.getUsuarioLogado())
            return
        ServicoSistema servicoSistema = segurancaService.servicoLogado
        def propriedadeServicoSistema = entityObject.metaClass.getMetaProperty(ATRIBUTO_SERVICO_SISTEMA)
        if (propriedadeServicoSistema)
            propriedadeServicoSistema.setProperty(entityObject, servicoSistema)
    }
*/

    @Override
    boolean supportsEventType(Class eventType) {
//        return true
        return eventType.isAssignableFrom(PreInsertEvent) ||
                eventType.isAssignableFrom(PreUpdateEvent) ||
                eventType.isAssignableFrom(PreDeleteEvent) ||
                eventType.isAssignableFrom(ValidationEvent) ||
                eventType.isAssignableFrom(PreInsertEvent) ||
                eventType.isAssignableFrom(PreUpdateEvent) ||
                eventType.isAssignableFrom(SaveOrUpdateEvent) ||
                eventType.isAssignableFrom(PostLoadEvent)

//      exclude: PostDeleteEvent, PreLoadEvent
    }

}