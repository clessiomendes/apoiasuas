
<%@ page import="org.apoiasuas.importacao.TentativaImportacao" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'tentativaImportacao.label', default: 'TentativaImportacao')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-tentativaImportacao" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="laranja" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-tentativaImportacao" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<th><g:message code="tentativaImportacao.criador.label" default="Criador" /></th>
					
						<g:sortableColumn property="informacoesDoProcessamento" title="${message(code: 'tentativaImportacao.informacoesDoProcessamento.label', default: 'Informacoes Do Processamento')}" />
					
						<g:sortableColumn property="dateCreated" title="${message(code: 'tentativaImportacao.dateCreated.label', default: 'Date Created')}" />
					
						<g:sortableColumn property="lastUpdated" title="${message(code: 'tentativaImportacao.lastUpdated.label', default: 'Last Updated')}" />
					
						<g:sortableColumn property="linhasPreProcessadas" title="${message(code: 'tentativaImportacao.linhasPreProcessadas.label', default: 'Linhas Pre Processadas')}" />
					
						<g:sortableColumn property="linhasProcessadasConclusao" title="${message(code: 'tentativaImportacao.linhasProcessadasConclusao.label', default: 'Linhas Processadas Conclusao')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${tentativaImportacaoInstanceList}" status="i" var="tentativaImportacaoInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${tentativaImportacaoInstance.id}">${fieldValue(bean: tentativaImportacaoInstance, field: "criador")}</g:link></td>
					
						<td>${fieldValue(bean: tentativaImportacaoInstance, field: "informacoesDoProcessamento")}</td>
					
						<td><g:formatDate date="${tentativaImportacaoInstance.dateCreated}" /></td>
					
						<td><g:formatDate date="${tentativaImportacaoInstance.lastUpdated}" /></td>
					
						<td>${fieldValue(bean: tentativaImportacaoInstance, field: "linhasPreProcessadas")}</td>
					
						<td>${fieldValue(bean: tentativaImportacaoInstance, field: "linhasProcessadasConclusao")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${tentativaImportacaoInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
