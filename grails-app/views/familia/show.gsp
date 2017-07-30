<%@ page import="org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.cidadao.Familia" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>

		<div class="nav" role="navigation">
			<ul>
                <li><g:link class="list" controller="cidadao" action="procurarCidadao"><g:message message="Procurar"/></g:link></li>
			</ul>
		</div>

	<div id="show-familia" class="content scaffold-show" role="main">
		<h1><g:message code="default.show.label" args="[entityName]" /></h1>

		<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
		</g:if>

		%{--Exibe pedidos de certidão pendentes--}%
		<g:if test="${pedidosCertidaoPendentes}">
			%{--Aguardando pedido de certidão:--}%
			<ul class="errors" role="alert">
			<g:each in="${pedidosCertidaoPendentes}" var="pedido">
				<li><g:link controller="pedidoCertidaoProcesso" action="mostraProcesso" id="${pedido.id}">Pedido de certidão em <g:formatDate date="${pedido.inicio}" />, situação: ${pedido.situacaoAtual}</g:link></li>
			</g:each>
			</ul>
		</g:if>

		<g:tabs id="tabs" style="margin: 5px;">
			<g:tab id="tabShowFamilia" titulo="família" template="tabShowFamilia"/>
			<g:tab id="tabShowTelefones" titulo="telefones" template="telefone/tabTelefones"/>
			<g:tab id="tabMonitoramento" titulo="monitoramentos" template="monitoramento/tabMonitoramentos" roles="${DefinicaoPapeis.STR_TECNICO}"/>
		</g:tabs>

	</div>
	</body>
</html>
