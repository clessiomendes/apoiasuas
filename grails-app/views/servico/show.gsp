
<%@ page import="org.apoiasuas.servico.Servico" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'servico.label', default: 'Servico')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-servico" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-servico" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list servico">
			
				<g:if test="${servicoInstance?.apelido}">
				<li class="fieldcontain">
					<span id="apelido-label" class="property-label"><g:message code="servico.apelido.label" default="Apelido" /></span>
					
						<span class="property-value" aria-labelledby="apelido-label"><g:fieldValue bean="${servicoInstance}" field="apelido"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${servicoInstance?.nomeFormal}">
				<li class="fieldcontain">
					<span id="nomeFormal-label" class="property-label"><g:message code="servico.nomeFormal.label" default="Nome Formal" /></span>
					
						<span class="property-value" aria-labelledby="nomeFormal-label"><g:fieldValue bean="${servicoInstance}" field="nomeFormal"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${servicoInstance?.encaminhamentoPadrao}">
				<li class="fieldcontain">
					<span id="encaminhamentoPadrao-label" class="property-label"><g:message code="servico.encaminhamentoPadrao.label" default="Encaminhamento Padrao" /></span>
					
						<span class="property-value" aria-labelledby="encaminhamentoPadrao-label"><g:fieldValue bean="${servicoInstance}" field="encaminhamentoPadrao"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${servicoInstance?.endereco}">
				<li class="fieldcontain">
					<span id="endereco-label" class="property-label"><g:message code="servico.endereco.label" default="Endereco" /></span>
					
						<span class="property-value" aria-labelledby="endereco-label"><g:fieldValue bean="${servicoInstance}" field="endereco"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:servicoInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${servicoInstance}"><g:message code="default.button.edit.label" default="Editar" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Remover')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Confirma remoção?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
