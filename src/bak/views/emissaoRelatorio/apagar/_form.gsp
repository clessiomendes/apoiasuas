<%@ page import="org.apoiasuas.formulario.Formulario" %>



<div class="fieldcontain ${hasErrors(bean: formularioInstance, field: 'nome', 'error')} required">
    <label for="nome">
        <g:message code="formulario.nome.label" default="Nome"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="nome" required="" value="${formularioInstance?.nome}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: formularioInstance, field: 'descricao', 'error')} ">
    <label for="descricao">
        <g:message code="formulario.descricao.label" default="Descricao"/>

    </label>
    <g:textField name="descricao" value="${formularioInstance?.descricao}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: formularioInstance, field: 'formularioPreDefinido', 'error')} ">
    <label for="codigo">
        <g:message code="formulario.codigo.label" default="Codigo"/>

    </label>
    <g:textField name="codigo" value="${formularioInstance?.codigo}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: formularioInstance, field: 'template', 'error')} required">
    <label for="template">
        <g:message code="formulario.template.label" default="Template"/>
        <span class="required-indicator">*</span>
    </label>
    <input type="file" id="template" name="template"/>

</div>

<div class="fieldcontain ${hasErrors(bean: formularioInstance, field: 'campos', 'error')} ">
    <label for="campos">
        <g:message code="formulario.campos.label" default="Campos"/>

    </label>

    <ul class="one-to-many">
        <g:each in="${formularioInstance?.campos ?}" var="c">
            <li><g:link controller="campoFormulario" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></li>
        </g:each>
        <li class="add">
            <g:link controller="campoFormulario" action="create"
                    params="['formulario.id': formularioInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'campoFormulario.label', default: 'CampoFormulario')])}</g:link>
        </li>
    </ul>

</div>

