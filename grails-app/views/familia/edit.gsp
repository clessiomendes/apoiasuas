<%@ page import="org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.cidadao.Familia" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#edit-familia" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
                <li><g:link class="list" controller="cidadao" action="procurarCidadao"><g:message message="Procurar"/></g:link></li>
				%{--<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>--}%
				%{--<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>--}%
			</ul>
		</div>
		<div id="edit-familia" class="content scaffold-edit" role="main">
			<h1><g:message code="default.edit.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${familiaInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${familiaInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form url="[resource:familiaInstance, action:'save']">
				<g:hiddenField name="version" value="${familiaInstance?.version}" />
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>

				<fieldset class="buttons">
                    <g:submitButton name="update" class="save" value="${message(code: 'default.button.update.label', default: 'Gravar')}" />
				</fieldset>
			</g:form>
		</div>

		%{-- Define as janelas modais para incluir novos marcadores (para ações, vulnerabilidades, etc) --}%
		<g:render template="janelaNovoMarcador" model="[idPrincipal: 'divPrograma', tituloJanela: 'Deseja criar um novo programa do município/estado/união?',
														marcadoresDisponiveis: programasDisponiveis, fieldsetMarcadores: 'fieldsetProgramas',
														classeMaracadores: 'marcadores-programa', hiddenNovosMarcadores: FamiliaController.HIDDEN_NOVOS_PROGRAMAS]" />

		<g:render template="janelaNovoMarcador" model="[idPrincipal: 'divAcao', tituloJanela: 'Deseja criar um novo tipo de ação padronizada?',
														marcadoresDisponiveis: acoesDisponiveis, fieldsetMarcadores: 'fieldsetAcoes',
														classeMaracadores: 'marcadores-acao', hiddenNovosMarcadores: FamiliaController.HIDDEN_NOVAS_ACOES]" />

		<g:render template="janelaNovoMarcador" model="[idPrincipal: 'divVulnerabilidade', tituloJanela: 'Deseja criar um novo tipo de vulnerabilidade padronizada?',
														marcadoresDisponiveis: vulnerabilidadesDisponiveis, fieldsetMarcadores: 'fieldsetVulnerabilidades',
														classeMaracadores: 'marcadores-vulnerabilidade', hiddenNovosMarcadores: FamiliaController.HIDDEN_NOVAS_VULNERABILIDADES]" />

	</body>
</html>
