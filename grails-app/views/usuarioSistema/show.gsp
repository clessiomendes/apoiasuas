
<%@ page import="org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.seguranca.UsuarioSistema" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'usuarioSistema.label', default: 'Operador do sistema')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-usuarioSistema" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
                <sec:ifAnyGranted roles="${org.apoiasuas.seguranca.DefinicaoPapeis.SUPER_USER}">
				    <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                </sec:ifAnyGranted>
			</ul>
		</div>
		<div id="show-usuarioSistema" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
<uploader:uploader id="yourUploaderId" />
			<ol class="property-list usuarioSistema">
			
				<li class="fieldcontain">
					<span id="nomeCompleto-label" class="property-label"><g:message code="usuarioSistema.nomeCompleto.label" default="Nome Completo" /></span>
					
						<span class="property-value" aria-labelledby="nomeCompleto-label"><g:fieldValue bean="${usuarioSistemaInstance}" field="nomeCompleto"/></span>
					
				</li>

				<g:if test="${usuarioSistemaInstance?.username}">
					<li class="fieldcontain">
						<span id="username-label" class="property-label"><g:message code="usuarioSistema.username.label" default="Nome Simplificado" /></span>

						<span class="property-value" aria-labelledby="username-label"><g:fieldValue bean="${usuarioSistemaInstance}" field="username"/></span>

					</li>
				</g:if>

				<g:if test="${usuarioSistemaInstance?.papel}">
					<li class="fieldcontain">
						<span id="papel-label" class="property-label"><g:message code="usuarioSistema.papel.label" default="Perfil de acesso" /></span>
						<span class="property-value" aria-labelledby="papel-label">
							${message(code:'DefinicaoPapeis.'+fieldValue(bean: usuarioSistemaInstance, field: "papel"))}
						</span>
					</li>
				</g:if>

                <li class="fieldcontain">
                    <span id="enabled-label" class="property-label"></span>
                    <span class="property-value" aria-labelledby="criador-label">${usuarioSistemaInstance?.enabled ? "Operador habilitado" : "Operador desabilitado"}</span>
                </li>

                <li class="fieldcontain">
                    <span id="criador-label" class="property-label"><g:message code="usuarioSistema.criador.label" default="Criação" /></span>
                    <span class="property-value" aria-labelledby="criador-label">${usuarioSistemaInstance?.criador?.username?.encodeAsHTML()} em <g:formatDate date="${usuarioSistemaInstance?.dateCreated}" format="dd/MM/yyyy HH:mm" /></span>
                </li>

                <li class="fieldcontain">
					<span id="ultimoAlterador-label" class="property-label"><g:message code="usuarioSistema.ultimoAlterador.label" default="Alteração" /></span>
					<span class="property-value" aria-labelledby="ultimoAlterador-label">${usuarioSistemaInstance?.ultimoAlterador?.username?.encodeAsHTML()} em <g:formatDate date="${usuarioSistemaInstance?.lastUpdated}" format="dd/MM/yyyy HH:mm" /></span>
				</li>
			
			</ol>
			<g:form url="[resource:usuarioSistemaInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
                <sec:ifAnyGranted roles="${DefinicaoPapeis.SUPER_USER}">
					<g:link class="edit" action="edit" resource="${usuarioSistemaInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                </sec:ifAnyGranted>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
