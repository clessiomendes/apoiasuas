package org.apoiasuas



import grails.test.mixin.*
import org.apoiasuas.marcador.Programa
import spock.lang.*

@TestFor(ProgramaAPAGARController)
@Mock(Programa)
class ProgramaAPAGARControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.menu()

        then:"The model is correct"
            !model.programaInstanceList
            model.programaInstanceCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.programaInstance!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def programa = new Programa()
            programa.validate()
            controller.save(programa)

        then:"The create view is rendered again with the correct model"
            model.programaInstance!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            programa = new Programa(params)

            controller.save(programa)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/programa/show/1'
            controller.flash.message != null
            Programa.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def programa = new Programa(params)
            controller.show(programa)

        then:"A model is populated containing the domain instance"
            model.programaInstance == programa
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def programa = new Programa(params)
            controller.edit(programa)

        then:"A model is populated containing the domain instance"
            model.programaInstance == programa
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/programa/index'
            flash.message != null


        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def programa = new Programa()
            programa.validate()
            controller.update(programa)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.programaInstance == programa

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            programa = new Programa(params).save(flush: true)
            controller.update(programa)

        then:"A redirect is issues to the show action"
            response.redirectedUrl == "/programa/show/$programa.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/programa/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def programa = new Programa(params).save(flush: true)

        then:"It exists"
            Programa.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(programa)

        then:"The instance is deleted"
            Programa.count() == 0
            response.redirectedUrl == '/programa/index'
            flash.message != null
    }
}
