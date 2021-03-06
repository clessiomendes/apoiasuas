<%@ page import="org.apoiasuas.marcador.Marcador" %>
<%
	Marcador marcadorDTO = marcadorInstance;
%>

<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${labelMarcador}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>

	<body>

		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="list" action="list">Listar</g:link></li>
				<li><g:link class="create" action="create${entityName}">Novo</g:link></li>
			</ul>
		</div>
		<div id="show-link" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list link">
		%{--		<li class="fieldcontain">
					<span id="descricao-label" class="property-label"><g:message code="link.descricao.label" default="Descricao" /></span>
					<span class="property-value" aria-labelledby="descricao-label"><f:display bean="marcadorInstance" property="descricao"/></span>
				</li>
        --}%
				<f:display label="Descrição" bean="${marcadorDTO}" property="descricao"/>
				<li class="fieldcontain">
					<span class="property-label">Disponível para</span>
					<span class="property-value">${marcadorDTO.servicoSistemaSeguranca?.nome ?: "todos os serviços do sistema"}</span>
				</li>
				<li class="fieldcontain">
					<span class="property-label"></span>
					<span class="property-value">${marcadorDTO.habilitado ? "habilitado" : "desabilitado"}</span>
				</li>
            </ol>
			<g:form controller="marcador" action="delete" id="${marcadorDTO.id}">
				<fieldset class="buttons">
					<g:if test="${podeAlterar == true}">
						<g:link class="edit" action="edit${entityName}" id="${marcadorDTO.id}"><g:message code="default.button.edit.label" default="Editar" /></g:link>
						%{--<g:actionSubmit class="delete" action="delete${entityName}" value="${message(code: 'default.button.delete.label', default: 'Apagar')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Confirma remoção?')}');" />--}%
					</g:if>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
