<%@ page import="org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'abrangenciaTerritorial.label')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>

        %{-- Para o componente treeview: --}%
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css" />
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.1/jquery.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>
	</head>

	<body>
		<a href="#edit-abrangenciaTerritorial" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="edit-abrangenciaTerritorial" class="content scaffold-edit" role="main">
			<h1><g:message code="default.edit.label" args="[entityName]" /></h1>
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

            <g:form onsubmit="submeteTerritoriosAtuacao(this)"  id="formAreaAtuacao" url="[resource:abrangenciaTerritorialInstance, action:'save']" >
            <fieldset class="form">
                <g:render template="form"/>
            </fieldset>
            <fieldset class="buttons">
                <g:submitButton name="update" class="save" value="${message(code: 'default.button.update.label', default: 'Gravar')}" />
            </fieldset>
            </g:form>
		</div>
	</body>
</html>