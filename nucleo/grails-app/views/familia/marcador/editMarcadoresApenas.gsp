<%@ page import="org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.cidadao.Familia" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
		<asset:javascript src="familia/marcador/marcadores.js"/>
	</head>
	<body>
		<div class="nav" role="navigation">
			<ul>
                <li><g:link class="search" controller="cidadao" action="procurarCidadao"><g:message message="Procurar"/></g:link></li>
			</ul>
		</div>
		<div id="edit-familia" class="content scaffold-edit" role="main">
			<h1>Alterar indicadores - ${familiaInstance?.montaDescricao()}</h1>

			<g:render template="/mensagensPosGravacao" model="[bean: familiaInstance]"/>

			<g:form url="[resource:familiaInstance, action:'save']">
				<g:hiddenField name="version" value="${familiaInstance?.version}" />

%{--
				<div class="fieldcontain" style="margin: 0 16px;">
					<div class="property-label">Família</div>
					<div class="property-value">${familiaInstance?.montaDescricao()}</div>
				</div>
--}%

				<fieldset class="form">
					<g:render template="marcador/tabMarcadores" model="[permiteInclusao: 'true']"/>
				</fieldset>
				<fieldset class="buttons">
                    <g:submitButton name="update" class="save" value="${message(code: 'default.button.update.label', default: 'Gravar')}" />
				</fieldset>
			</g:form>
		</div>

		%{-- Define as janelas modais para EDITAR marcadores (para ações, vulnerabilidades, etc) --}%
		<g:render template="marcador/janelaEditMarcador" model="[idPrincipal: 'divEditPrograma', tituloJanela: 'Deseja registrar mais detalhes em relação à participação neste programa?']" />
		<g:render template="marcador/janelaEditMarcador" model="[idPrincipal: 'divEditVulnerabilidade', tituloJanela: 'Deseja registrar mais detalhes em relação a esta vulnerabilidade?']" />
        <g:render template="marcador/janelaEditMarcador" model="[idPrincipal: 'divEditAcao', tituloJanela: 'Deseja registrar mais detalhes em relação a esta ação prevista?']" />
        <g:render template="marcador/janelaEditMarcador" model="[idPrincipal: 'divEditOutroMarcador', tituloJanela: 'Deseja registrar mais detalhes em relação a esta sinalização?']" />

	</body>
</html>
