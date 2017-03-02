<%@ page import="org.apoiasuas.marcador.Marcador" %>

<%
	List<Marcador> marcadoresInstanceListDTO = marcadorInstanceList
%>

<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${labelMarcador}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-link" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table class="tabelaListagem">
			<thead>
					<tr>
						<g:sortableColumn property="descricao" title="descrição" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${marcadoresInstanceListDTO}" status="i" var="marcadorInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link action="show" id="${marcadorInstance.id}">${fieldValue(bean: marcadorInstance, field: "descricao")}</g:link></td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${marcadorInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
