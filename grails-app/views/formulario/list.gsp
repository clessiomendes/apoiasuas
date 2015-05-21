
<%@ page import="org.apoiasuas.formulario.Formulario" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'formulario.label', default: 'Formulario')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-formulario" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
%{--
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
--}%
			</ul>
		</div>
		<div id="list-formulario" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="nome" title="${message(code: 'formulario.nome.label', default: 'Nome')}" />
					
						<g:sortableColumn property="descricao" title="${message(code: 'formulario.descricao.label', default: 'Descricao')}" />

					</tr>
				</thead>
				<tbody>
				<g:each in="${formularioInstanceList}" status="i" var="formularioInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${formularioInstance.id}">${fieldValue(bean: formularioInstance, field: "nome")}</g:link></td>
					
						<td>${fieldValue(bean: formularioInstance, field: "descricao")}</td>

					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${formularioInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
