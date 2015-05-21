
<%@ page import="org.apoiasuas.seguranca.UsuarioSistema" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'usuarioSistema.label', default: 'Usuário do sistema')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-usuarioSistema" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
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
							%{--<g:fieldValue bean="${usuarioSistemaInstance}" field="papel"/>--}%
							${message(code:'DefinicaoPapeis.'+fieldValue(bean: usuarioSistemaInstance, field: "papel"))}
						</span>

					</li>
				</g:if>

			%{--
                            <g:if test="${usuarioSistemaInstance?.perfil}">
                            <li class="fieldcontain">
                                <span id="perfil-label" class="property-label"><g:message code="usuarioSistema.perfil.label" default="Perfil" /></span>

                                    <span class="property-value" aria-labelledby="situacao-label">
                                    ${message(code:'PerfilUsuarioSistema.'+fieldValue(bean: usuarioSistemaInstance, field: "perfil"))}
                                    </span>


                            </li>
                            </g:if>
            --}%

				<li class="fieldcontain">
					<span id="criador-label" class="property-label"><g:message code="usuarioSistema.criador.label" default="Criação" /></span>
					<span class="property-value" aria-labelledby="criador-label"><g:link controller="usuarioSistema" action="show" id="${usuarioSistemaInstance?.criador?.id}">${usuarioSistemaInstance?.criador?.username?.encodeAsHTML()}</g:link> em <g:formatDate date="${usuarioSistemaInstance?.dateCreated}" format="dd/MM/yyyy HH:mm" /></span>
				</li>
				
				<li class="fieldcontain">
					<span id="ultimoAlterador-label" class="property-label"><g:message code="usuarioSistema.ultimoAlterador.label" default="Alteração" /></span>
					<span class="property-value" aria-labelledby="ultimoAlterador-label"><g:link controller="usuarioSistema" action="show" id="${usuarioSistemaInstance?.ultimoAlterador?.id}">${usuarioSistemaInstance?.ultimoAlterador?.username?.encodeAsHTML()}</g:link> em <g:formatDate date="${usuarioSistemaInstance?.lastUpdated}" format="dd/MM/yyyy HH:mm" /></span>
				</li>
			
			</ol>
			<g:form url="[resource:usuarioSistemaInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${usuarioSistemaInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
