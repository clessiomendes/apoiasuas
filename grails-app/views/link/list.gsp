<%
	List<Link> linkInstanceListDTO = linkInstanceList
%>

<%@ page import="org.apoiasuas.Link" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'link.label', default: 'Link')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-link" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
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
			<table>
			<thead>
					<tr>
						<g:sortableColumn property="descricao" title="${message(code: 'link.descricao.label')}" />
						<g:sortableColumn property="tipo" title="${message(code: 'link.tipo.label')}" />
%{--
						<g:sortableColumn property="url" title="${message(code: 'link.url.label', default: 'Url')}" />
--}%
					</tr>
				</thead>
				<tbody>
				<g:each in="${linkInstanceListDTO}" status="i" var="linkInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link action="show" id="${linkInstance.id}">${fieldValue(bean: linkInstance, field: "descricao")}</g:link></td>
						<td>${message(code: "link."+linkInstance.tipo+".label")}</td>
%{--
						<td>${fieldValue(bean: linkInstance, field: "url")}</td>
--}%
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${linkInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
