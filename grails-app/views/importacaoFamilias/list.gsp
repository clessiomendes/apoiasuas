<%@ page import="org.apoiasuas.importacao.TentativaImportacao" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'tentativaImportacao.label')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-tentativaImportacao" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="create" action="create"><g:message code="nova.importacao" args="[entityName]" /></g:link></li>
				%{--<li><g:link class="list" action="config">Configurar</g:link></li>--}%
			</ul>
		</div>
		<div id="list-tentativaImportacao" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.error}">
				<div class="errors" role="status">${flash.error}</div>
			</g:if>
			<table class="tabelaListagem">
				<thead>
				<tr>

					<th><g:message code='tentativaImportacao.dateCreated.label'/></th>
					%{--
                                        <g:sortableColumn property="informacoesDoProcessamento" title="${message(code: 'tentativaImportacao.informacoesDoProcessamento.label', default: 'Informacoes Do Processamento')}" />
                    --}%
					<th><g:message code='tentativaImportacao.statusDetalhado.label'/></th>

					<th></th>

				</tr>
				</thead>
				<tbody>
				<g:each in="${tentativasImportacao}" status="i" var="importacao">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

						<td>
							<g:formatDate format="${org.apoiasuas.util.DateUtils.FORMATO_DATA_HORA}" date="${importacao.dateCreated}" />
							(${importacao.criador})
						</td>

%{--
						<td>${fieldValue(bean: importacao, field: "informacoesDoProcessamento")}</td>
--}%
						<td>
							<div style="${importacao.problemaDetectado ? 'color:red' : ''}">
								${fieldValue(bean: importacao, field: "statusDetalhado")}
							</div>
						</td>

						<td><g:link action="show" id="${importacao.id}">
							detalhes
						</g:link></td>

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
