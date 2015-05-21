
<%@ page import="org.apoiasuas.cidadao.Cidadao" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'cidadao.label', default: 'Cidadao')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-cidadao" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="laranja" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="procurarCidadao"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-cidadao" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list cidadao">
			
				<g:if test="${cidadaoInstance?.referencia}">
				<li class="fieldcontain">
					<span id="referencia-label" class="property-label"><g:message code="cidadao.referencia.label" default="Referencia" /></span>
					
						<span class="property-value" aria-labelledby="referencia-label"><g:formatBoolean boolean="${cidadaoInstance?.referencia}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${cidadaoInstance?.criador}">
				<li class="fieldcontain">
					<span id="criador-label" class="property-label"><g:message code="cidadao.criador.label" default="Criador" /></span>
					
						<span class="property-value" aria-labelledby="criador-label"><g:link controller="usuarioSistema" action="show" id="${cidadaoInstance?.criador?.id}">${cidadaoInstance?.criador?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${cidadaoInstance?.ultimoAlterador}">
				<li class="fieldcontain">
					<span id="ultimoAlterador-label" class="property-label"><g:message code="cidadao.ultimoAlterador.label" default="Ultimo Alterador" /></span>
					
						<span class="property-value" aria-labelledby="ultimoAlterador-label"><g:link controller="usuarioSistema" action="show" id="${cidadaoInstance?.ultimoAlterador?.id}">${cidadaoInstance?.ultimoAlterador?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${cidadaoInstance?.familia}">
				<li class="fieldcontain">
					<span id="familia-label" class="property-label"><g:message code="cidadao.familia.label" default="Familia" /></span>
					
						<span class="property-value" aria-labelledby="familia-label"><g:link controller="familia" action="show" id="${cidadaoInstance?.familia?.id}">${cidadaoInstance?.familia?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${cidadaoInstance?.dataNascimento}">
				<li class="fieldcontain">
					<span id="dataNascimento-label" class="property-label"><g:message code="cidadao.dataNascimento.label" default="Data Nascimento" /></span>
					
						<span class="property-value" aria-labelledby="dataNascimento-label"><g:formatDate date="${cidadaoInstance?.dataNascimento}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${cidadaoInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="cidadao.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${cidadaoInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${cidadaoInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="cidadao.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${cidadaoInstance?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${cidadaoInstance?.nis}">
				<li class="fieldcontain">
					<span id="nis-label" class="property-label"><g:message code="cidadao.nis.label" default="Nis" /></span>
					
						<span class="property-value" aria-labelledby="nis-label"><g:fieldValue bean="${cidadaoInstance}" field="nis"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${cidadaoInstance?.nomeCompleto}">
				<li class="fieldcontain">
					<span id="nomeCompleto-label" class="property-label"><g:message code="cidadao.nomeCompleto.label" default="Nome Completo" /></span>
					
						<span class="property-value" aria-labelledby="nomeCompleto-label"><g:fieldValue bean="${cidadaoInstance}" field="nomeCompleto"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${cidadaoInstance?.origemImportacaoAutomatica}">
				<li class="fieldcontain">
					<span id="origemImportacaoAutomatica-label" class="property-label"><g:message code="cidadao.origemImportacaoAutomatica.label" default="Origem Importacao Automatica" /></span>
					
						<span class="property-value" aria-labelledby="origemImportacaoAutomatica-label"><g:formatBoolean boolean="${cidadaoInstance?.origemImportacaoAutomatica}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${cidadaoInstance?.parentescoReferencia}">
				<li class="fieldcontain">
					<span id="parentescoReferencia-label" class="property-label"><g:message code="cidadao.parentescoReferencia.label" default="Parentesco Referencia" /></span>
					
						<span class="property-value" aria-labelledby="parentescoReferencia-label"><g:fieldValue bean="${cidadaoInstance}" field="parentescoReferencia"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:cidadaoInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${cidadaoInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
