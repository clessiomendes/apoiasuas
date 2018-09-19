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
				<li><g:link class="list" action="list">Listar</g:link></li>
			</ul>
		</div>
		<div id="create-link" class="content scaffold-create" role="main">
			<h1><g:message code="default.create.label" args="[entityName]" /></h1>

			<g:render template="/mensagensPosGravacao" model="[bean: marcadorInstance]"/>

			<g:form controller="marcador" action="save${entityName}" id="${marcadorInstance.id}">
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="create" class="save" value="${message(code: 'default.button.update.label', default: 'Gravar')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
