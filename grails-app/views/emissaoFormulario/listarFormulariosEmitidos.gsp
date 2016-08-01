
<%@ page import="org.apoiasuas.formulario.FormularioEmitido" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'formularioEmitido.label', default: 'FormularioEmitido')}" />
		<title>Formulários emitidos</title>
	</head>
	<body>
		<a href="#list-formularioEmitido" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
                <g:link class="create" action="escolherFamilia">Novo</g:link>
			</ul>
		</div>
		<div id="list-formularioEmitido" class="content scaffold-list" role="main">
			<h1>Formulários já emitidos</h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table class="tabelaListagem">
			<thead>
					<tr>
					
						<g:sortableColumn property="descricao" title="${message(code: 'formularioEmitido.descricao.label', default: 'Descricao')}" />
					
						<g:sortableColumn property="dataPreenchimento" title="${message(code: 'formularioEmitido.dataPreenchimento.label', default: 'Data Preenchimento')}" />
					
						%{--<th><g:message code="formularioEmitido.responsavelPreenchimento.label" default="Responsavel Preenchimento" /></th>--}%

                        <th>Membro</th>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${formularioEmitidoInstanceList}" status="i" var="formularioEmitidoInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="mostrarFormularioEmitido" id="${formularioEmitidoInstance.id}">${fieldValue(bean: formularioEmitidoInstance, field: "descricao")}</g:link></td>
					
						<td><g:formatDate date="${formularioEmitidoInstance.dataPreenchimento}" /></td>
					
						%{--<td>${fieldValue(bean: formularioEmitidoInstance, field: "responsavelPreenchimento")}</td>--}%
					
						<td>${fieldValue(bean: formularioEmitidoInstance, field: "cidadao")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
%{--
			<div class="pagination">
				<g:paginate total="${formularioEmitidoInstanceCount ?: 0}" />
			</div>
--}%
		</div>
	</body>
</html>
