package org.apoiasuas.formulario


import grails.test.mixin.*
import spock.lang.*

@TestFor(FormularioController)
@Mock(Formulario)
class FormularioControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void "Test the index action returns the correct model"() {

        when: "The index action is executed"
        controller.list()

        then: "The model is correct"
        !model.formularioInstanceList
        model.formularioInstanceCount == 0
    }

    void "Test the create action returns the correct model"() {
        when: "The create action is executed"
        controller.create()

        then: "The model is correctly created"
        model.formularioInstance != null
    }

    void "Test the save action correctly persists an instance"() {

        when: "The save action is executed with an invalid instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'
        def formulario = new Formulario()
        formulario.validate()
        controller.save(formulario)

        then: "The create view is rendered again with the correct model"
        model.formularioInstance != null
        view == 'create'

        when: "The save action is executed with a valid instance"
        response.reset()
        populateValidParams(params)
        formulario = new Formulario(params)

        controller.save(formulario)

        then: "A redirect is issued to the show action"
        response.redirectedUrl == '/formulario/show/1'
        controller.flash.message != null
        Formulario.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when: "The show action is executed with a null domain"
        controller.show(null)

        then: "A 404 error is returned"
        response.status == 404

        when: "A domain instance is passed to the show action"
        populateValidParams(params)
        def formulario = new Formulario(params)
        controller.show(formulario)

        then: "A model is populated containing the domain instance"
        model.formularioInstance == formulario
    }

    void "Test that the edit action returns the correct model"() {
        when: "The edit action is executed with a null domain"
        controller.edit(null)

        then: "A 404 error is returned"
        response.status == 404

        when: "A domain instance is passed to the edit action"
        populateValidParams(params)
        def formulario = new Formulario(params)
        controller.edit(formulario)

        then: "A model is populated containing the domain instance"
        model.formularioInstance == formulario
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when: "Update is called for a domain instance that doesn't exist"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'PUT'
        controller.update(null)

        then: "A 404 error is returned"
        response.redirectedUrl == '/formulario/index'
        flash.message != null


        when: "An invalid domain instance is passed to the update action"
        response.reset()
        def formulario = new Formulario()
        formulario.validate()
        controller.update(formulario)

        then: "The edit view is rendered again with the invalid instance"
        view == 'edit'
        model.formularioInstance == formulario

        when: "A valid domain instance is passed to the update action"
        response.reset()
        populateValidParams(params)
        formulario = new Formulario(params).save(flush: true)
        controller.update(formulario)

        then: "A redirect is issues to the show action"
        response.redirectedUrl == "/formulario/show/$formulario.id"
        flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when: "The delete action is called for a null instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'DELETE'
        controller.delete(null)

        then: "A 404 is returned"
        response.redirectedUrl == '/formulario/index'
        flash.message != null

        when: "A domain instance is created"
        response.reset()
        populateValidParams(params)
        def formulario = new Formulario(params).save(flush: true)

        then: "It exists"
        Formulario.count() == 1

        when: "The domain instance is passed to the delete action"
        controller.delete(formulario)

        then: "The instance is deleted"
        Formulario.count() == 0
        response.redirectedUrl == '/formulario/index'
        flash.message != null
    }
}
