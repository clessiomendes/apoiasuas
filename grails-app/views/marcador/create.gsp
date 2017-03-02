<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${labelMarcador}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="create-link" class="content scaffold-create" role="main">
			<h1><g:message code="default.create.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${marcadorInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${marcadorInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form controller="marcador" action="save" id="${marcadorInstance.id}">
				<fieldset class="form">
					<g:render template="form"/>
					<f:field label="Disponível para" bean="${marcadorInstance}" property="servicoSistemaSeguranca" widget-from="${servicosDisponiveis.collect{it.nome}}" widget-keys="${servicosDisponiveis.collect{it.id}}"/>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="create" class="save" value="${message(code: 'default.button.update.label', default: 'Gravar')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
