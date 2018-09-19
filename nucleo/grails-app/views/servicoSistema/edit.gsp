<%@ page import="org.apoiasuas.redeSocioAssistencial.ServicoSistema" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'servicoSistema.label', default: 'Serviço (sistema)')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
	</head>

	<body>
		<div id="edit-servicoSistema" class="content scaffold-edit" role="main">
			<h1><g:message code="default.edit.label" args="[entityName]" /></h1>

			<g:render template="/mensagensPosGravacao" model="[bean: servicoSistemaInstance]"/>

			<g:form url="[resource:servicoSistemaInstance, action:'save']" >
				<g:hiddenField name="version" value="${servicoSistemaInstance?.version}" />
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
                    <g:submitButton name="update" class="save" value="${message(code: 'default.button.update.label', default: 'Update')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
