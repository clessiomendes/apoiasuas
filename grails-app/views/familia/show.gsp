
<%@ page import="org.apoiasuas.cidadao.Familia" %>
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

		<g:javascript>
			$(document).ready(function() {
				$("#tabs").tabs();
			} );
		</g:javascript>

		<div id="tabs" style="margin: 5px;">
			<ul>
				<li><a href="#tabFamilia">família</a> </li>
	%{--			<li><a href="#tabMarcador">programas, ações...</a> </li>--}%
				<li><a href="#tabMonitoramento">monitoramentos</a> </li>
			</ul>
			<div id="tabFamilia">
				<g:render template="tabShowFamilia"/>
			</div>
			<div id="tabMonitoramento">
				<g:render template="tabMonitoramento"/>
			</div>
		</div>

		<fieldset class="buttons">
			<g:link class="add" controller="emissaoFormulario" action="escolherFamilia">Emitir formulário</g:link>
			<g:link class="list" controller="emissaoFormulario" action="listarFormulariosEmitidosFamilia" params="[idFamilia: familiaInstance.id]" >Formulários emitidos</g:link>
            <g:link class="edit" action="edit" resource="${familiaInstance}">Alterar dados</g:link>
			<g:link class="edit" controller="familia" action="editAcompanhamentoFamilia" id="${familiaInstance.id}">Acompanhamento</g:link>
		</fieldset>
	</div>
	</body>
</html>
