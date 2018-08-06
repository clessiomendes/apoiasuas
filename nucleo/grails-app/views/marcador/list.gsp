<%@ page import="org.apoiasuas.marcador.Marcador" %>

<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		%{--<g:set var="entityName" value="${labelMarcador}" />--}%
		%{--<title><g:message code="default.list.label" args="[entityName]" /></title>--}%
		<title>Manutenção de programas, vulnerabilidades, etc</title>

		<g:javascript>
			//Abre automaticamente no último tab selecionado pelo usuário antes do post, caso haja
			document.getElementById('tabs').tabMostradoInicialmente = '${tabAtual}';
		</g:javascript>

	</head>
	<body>
		<div id="list-link" class="content scaffold-list" role="main">
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:tabs id="tabs">
				<g:tab id="tabProgramas" titulo="programas" template="tabList" model="${[entityName: 'Programa', marcadorInstanceList: programasList]}"/>
				<g:tab id="tabVulnerabilidades" titulo="vulnerabilidades" template="tabList" model="${[entityName: 'Vulnerabilidade', marcadorInstanceList: vulnerabilidadesList]}"/>
				<g:tab id="tabAcoes" titulo="ações" template="tabList" model="${[entityName: 'Acao', marcadorInstanceList: acoesList]}"/>
				<g:tab id="tabOutrosMarcadores" titulo="outros marcadores" template="tabList" model="${[entityName: 'OutroMarcador', marcadorInstanceList: outrosMarcadoresList]}"/>
			</g:tabs>

		</div>
	</body>
</html>
