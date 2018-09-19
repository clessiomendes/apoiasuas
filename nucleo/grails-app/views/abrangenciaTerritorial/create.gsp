<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'abrangenciaTerritorial.label')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>

	<body>
		<a href="#create-abrangenciaTerritorial" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="create-abrangenciaTerritorial" class="content scaffold-create" role="main">
			<h1><g:message code="default.create.label" args="[entityName]" /></h1>
%{--
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${abrangenciaTerritorialInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${abrangenciaTerritorialInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
--}%
			<g:render template="/mensagensPosGravacao" model="[bean: abrangenciaTerritorialInstance]"/>

			<g:form onsubmit="submeteTerritoriosAtuacao(this)" url="[resource:abrangenciaTerritorialInstance, action:'save']" >
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Novo')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
