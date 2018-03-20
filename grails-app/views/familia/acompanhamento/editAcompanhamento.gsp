<%@ page import="org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.cidadao.Familia" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
	</head>
	<body>
		<g:render template="/baixarArquivo"/>

		<div class="nav" role="navigation">
			<ul>
                <li><g:link class="list" controller="${controllerButtonProcurar}" action="${actionButtonProcurar}">Procurar outra família</g:link></li>
				%{--<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>--}%
				%{--<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>--}%
			</ul>
		</div>
		<div id="edit-familia" class="content scaffold-edit" role="main">
			<h1>Acompanhamento Familiar</h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>

			%{--Procurar por erros tanto na instancia de familia quanto na de acompanhamento--}%
			<g:hasErrors model="${[familiaInstance:familiaInstance, acompanhamentoInstance:familiaInstance.acompanhamentoFamiliar]}">
				<ul class="errors" role="alert">
					<g:eachError model="${[familiaInstance:familiaInstance, acompanhamentoInstance:familiaInstance.acompanhamentoFamiliar]}" var="error">
						<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
					</g:eachError>
				</ul>
			</g:hasErrors>

			<g:form id="${familiaInstance.id}">
				<g:hiddenField name="version" value="${familiaInstance?.version}" />

				<g:tabs id="tabs" style="margin: 5px;">
					<g:tab id="tabEditFamilia" titulo="família" template="tabEditFamilia"/>
					<g:tab id="tabMarcadores" titulo="programas, ações..." template="marcador/tabMarcadores" model="[permiteInclusao: 'true']"/>
					<g:tab id="tabMonitoramento" titulo="monitoramento" template="monitoramento/tabMonitoramentos"/>
					<g:tab id="tabAcompanhamento" titulo="plano de acompanhamento" template="acompanhamento/tabAcompanhamento"/>
					<g:tab id="tabHistorico" titulo="histórico" template="acompanhamento/tabHistoricoAcompanhamento" model="[auditoriaAcompanhamentoList: auditoriaAcompanhamentoList]"/>
    %{--<g:render template="monitoramento/listMonitoramento" model="[monitoramentoInstanceList: localDtoFamilia.monitoramentos?.sort()]"/>--}%
				</g:tabs>


				<fieldset class="buttons">
                    <g:actionSubmit class="save" value="gravar" action="saveAcompanhamento"/>
                    <g:actionSubmit class="print" value="emitir plano" action="saveAndDownloadAcompanhamento"/>
                    %{--<g:submitButton name="gravaAcompanhamento" class="save" value="gravar e emitir plano" />--}%
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
