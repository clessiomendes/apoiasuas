
<%@ page import="org.apoiasuas.servico.Servico" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'servico.label', default: 'Servico')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-servico" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-servico" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="apelido" title="${message(code: 'servico.apelido.label', default: 'Apelido')}" />
					
						<g:sortableColumn property="nomeFormal" title="${message(code: 'servico.nomeFormal.label', default: 'Nome Formal')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${servicoInstanceList}" status="i" var="servicoInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${servicoInstance.id}">${fieldValue(bean: servicoInstance, field: "apelido")}</g:link></td>
					
						<td>${fieldValue(bean: servicoInstance, field: "nomeFormal")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${servicoInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
