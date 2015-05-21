<%@ page import="org.apoiasuas.seguranca.UsuarioSistema" %>

<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'usuarioSistema.label', default: 'Usuário do sistema')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<r:require module="fileuploader"/>
	</head>
	<body>
		<a href="#list-usuarioSistema" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-usuarioSistema" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>

			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="nomeCompleto" title="${message(code: 'usuarioSistema.nomeCompleto.label', default: 'Nome Completo')}" />
					
						<g:sortableColumn property="username" title="${message(code: 'usuarioSistema.username.label', default: 'Nome Simplificado')}" />
%{--
						<g:sortableColumn property="perfil" title="${message(code: 'usuarioSistema.perfil.label', default: 'Perfil')}" />
--}%
					</tr>
				</thead>
				<tbody>
				<g:each in="${usuarioSistemaInstanceList}" status="i" var="usuarioSistemaInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${usuarioSistemaInstance.id}">${fieldValue(bean: usuarioSistemaInstance, field: "nomeCompleto")}</g:link></td>
					
						<td>${fieldValue(bean: usuarioSistemaInstance, field: "username")}</td>
					
%{--
						<td>${message(code:'PerfilUsuarioSistema.'+fieldValue(bean: usuarioSistemaInstance, field: "perfil"))}</td>
--}%

					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${usuarioSistemaInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
