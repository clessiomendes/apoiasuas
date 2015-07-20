
<%@ page import="org.apoiasuas.cidadao.Familia" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>

		<a href="#show-familia" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
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
			<ol class="property-list familia">
			
				<g:if test="${familiaInstance?.codigoLegado}">
				<li class="fieldcontain">
					<span id="codigoLegado-label" class="property-label"><g:message code="familia.codigoLegado.label" default="Codigo Legado" /></span>
					<span class="property-value" aria-labelledby="codigoLegado-label"><g:fieldValue bean="${familiaInstance}" field="codigoLegado"/></span>
				</li>
				</g:if>

                <g:if test="${familiaInstance?.tecnicoReferencia}">
                    <li class="fieldcontain">
                        <span id="tecnicoReferencia-label" class="property-label"><g:message code="familia.tecnicoReferencia.label" default="Técnico de referência" /></span>
                        <span style="color: red" class="property-value" aria-labelledby="tecnicoReferencia-label"><g:fieldValue bean="${familiaInstance}" field="tecnicoReferencia"/></span>
                    </li>
                </g:if>

                <g:if test="${familiaInstance?.programas}">
                    <li class="fieldcontain">
                        <span id="membros-label" class="property-label"><g:message code="familia.programas.label" default="Programas" /></span>
                        <g:each in="${familiaInstance.programas}" var="programaFamilia">
                            <span class="property-value" aria-labelledby="programas-label">${programaFamilia?.programa.siglaENome()}</span>
                        </g:each>
                    </li>
                </g:if>

                <g:if test="${familiaInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="familia.dateCreated.label" default="Data Cadastro" /></span>
					<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${familiaInstance?.dateCreated}" /></span>
				</li>
				</g:if>
			
				<g:if test="${familiaInstance?.endereco}">
				<li class="fieldcontain">
					<span id="endereco-label" class="property-label"><g:message code="familia.endereco.label" default="Endereço" /></span>
					<span class="property-value" aria-labelledby="endereco-label"> ${familiaInstance.endereco} </span>
					<span class="property-value" aria-labelledby="endereco-label"> ${familiaInstance.endereco.CEP ? "CEP "+familiaInstance.endereco.CEP +", " : ""}
                        ${familiaInstance.endereco.municipio ? familiaInstance.endereco.municipio +", " : ""}
                        ${familiaInstance.endereco.UF ? familiaInstance.endereco.UF : ""}
                    </span>
				</li>
				</g:if>

				<g:if test="${familiaInstance?.membros}">
				<li class="fieldcontain">
					<span id="membros-label" class="property-label"><g:message code="familia.membros.label" default="Membros" /></span>
					
						<g:each in="${familiaInstance.membros}" var="m">
						<span class="property-value" aria-labelledby="membros-label"><g:link controller="cidadao" action="show" id="${m.id}">${m?.nomeCompleto }</g:link> ${m.parentescoReferencia ? " ("+m.parentescoReferencia+")" : ""} </span>
						</g:each>
					
				</li>
				</g:if>
%{--
                <g:if test="${familiaInstance?.situacaoFamilia}">
                    <li class="fieldcontain">
                        <span id="situacaoFamilia-label" class="property-label"><g:message code="familia.situacaoFamilia.label" default="Situacao Familia" /></span>

                        <span class="property-value" aria-labelledby="situacaoFamilia-label"><g:fieldValue bean="${familiaInstance}" field="situacaoFamilia"/></span>

                    </li>
                </g:if>
--}%
                <g:if test="${familiaInstance?.telefones}">
				<li class="fieldcontain">
					<span id="telefones-label" class="property-label"><g:message code="familia.telefones.label" default="Telefones" /></span>
					
						<g:each in="${familiaInstance.telefones}" var="t">
						<span class="property-value" aria-labelledby="telefones-label">${t?.encodeAsHTML()}</span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
            <fieldset class="buttons">
                <g:link class="edit" controller="emissaoFormulario" action="listarFormulariosEmitidos" params="[idFamilia: familiaInstance.id]" >Formulários</g:link>
                <g:link class="edit" action="edit" resource="${familiaInstance}"><g:message code="default.button.edit.label" default="Editar" /></g:link>
            </fieldset>
		</div>
	</body>
</html>
