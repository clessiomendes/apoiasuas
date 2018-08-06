
<%@ page import="org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'abrangenciaTerritorial.label')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-abrangenciaTerritorial" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-abrangenciaTerritorial" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table class="tabelaListagem">
			<thead>
					<tr>
                        <g:sortableColumn property="nomeCompleto" title="${message(code: 'abrangenciaTerritorial.nome.label', default: 'Nome')}" />
						<g:sortableColumn property="habilitado" title="${message(code: 'abrangenciaTerritorial.habilitado.label', default: 'Habilitado')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${abrangenciaTerritorialInstanceList}" status="i" var="abrangenciaTerritorialInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td><g:link action="show" id="${abrangenciaTerritorialInstance.id}">${fieldValue(bean: abrangenciaTerritorialInstance, field: "nomeCompleto")}</g:link></td>
                        <td>${abrangenciaTerritorialInstance.habilitado ? "sim" : "não"}</td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${abrangenciaTerritorialInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
