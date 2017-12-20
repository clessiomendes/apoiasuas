
<%@ page import="org.apoiasuas.formulario.CampoFormularioEmitido; org.apoiasuas.formulario.FormularioEmitido" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'formularioEmitido.label', default: 'FormularioEmitido')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-formularioEmitido" class="skip" tabindex="-1"><g:fieldValue bean="${formularioEmitidoInstance}" field="descricao"/></a>
		<div class="nav" role="navigation">
			<ul>
                <li><g:link class="create" action="escolherFamilia">Novo</g:link></li>
				%{--<li><g:link class="list" action="index">Listagem</g:link></li>--}%
			</ul>
		</div>
		<div id="show-formularioEmitido" class="content scaffold-show" role="main">
			<h1><g:fieldValue bean="${formularioEmitidoInstance}" field="descricao"/></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list formularioEmitido">
			
%{--
                <g:if test="${formularioEmitidoInstance?.operadorLogado}">
                    <li class="fieldcontain">
                        <span id="operadorLogado-label" class="property-label"><g:message code="formularioEmitido.responsavelPreenchimento.label" default="Operador do sistema" /></span>
                        <span class="property-value" aria-labelledby="operadorLogado-label">${formularioEmitidoInstance?.operadorLogado?.username}</span>
                    </li>
                </g:if>

				<g:if test="${formularioEmitidoInstance?.familia}">
				<li class="fieldcontain">
					<span id="familia-label" class="property-label"><g:message code="formularioEmitido.familia.label" default="Familia" /></span>
					
						<span class="property-value" aria-labelledby="familia-label">${formularioEmitidoInstance?.familia?.encodeAsHTML()}</span>
					
				</li>
				</g:if>
--}%

                <g:if test="${formularioEmitidoInstance?.campos}">
                    <g:agrupaCampos lista="${formularioEmitidoInstance.getCamposOrdenados()}" campoGrupo="grupo" status="i" var="campoPreenchidoTemp">
                        <% CampoFormularioEmitido campoPreenchido = campoPreenchidoTemp %>
                            <li class="fieldcontain">
                                <span id="campos-label" class="property-label">${campoPreenchido?.descricao}</span>
								%{--<g:if test="${campoPreenchido.conteudoImpresso }">--}%
						        	<span class="property-value" aria-labelledby="campos-label">${campoPreenchido.conteudoImpresso }</span>
								%{--</g:if>--}%
                            </li>
                    </g:agrupaCampos>

				</g:if>
			
			</ol>
		</div>
	</body>
</html>
