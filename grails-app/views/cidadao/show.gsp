
<%@ page import="org.apoiasuas.cidadao.Cidadao" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'cidadao.label', default: 'Cidadao')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-cidadao" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="list" action="procurarCidadao"><g:message message="Procurar"/></g:link></li>
			</ul>
		</div>
		<div id="show-cidadao" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list cidadao">

                <g:if test="${cidadaoInstance?.nomeCompleto}">
                    <li class="fieldcontain">
                        <span id="nomeCompleto-label" class="property-label"><g:message code="cidadao.nomeCompleto.label" default="Nome Completo" /></span>

                        <span class="property-value" aria-labelledby="nomeCompleto-label"><g:fieldValue bean="${cidadaoInstance}" field="nomeCompleto"/></span>

                    </li>
                </g:if>

                <g:if test="${cidadaoInstance?.parentescoReferencia}">
                    <li class="fieldcontain">
                        <span id="parentescoReferencia-label" class="property-label"><g:message code="cidadao.parentescoReferencia.label" default="Parentesco Referencia" /></span>

                        <span class="property-value" aria-labelledby="parentescoReferencia-label"><g:fieldValue bean="${cidadaoInstance}" field="parentescoReferencia"/></span>

                    </li>
                </g:if>

                <g:if test="${cidadaoInstance?.dataNascimento}">
                    <li class="fieldcontain">
                        <span id="dataNascimento-label" class="property-label"><g:message code="cidadao.dataNascimento.label" default="Data Nascimento" /></span>

                        <span class="property-value" aria-labelledby="dataNascimento-label"><g:formatDate date="${cidadaoInstance?.dataNascimento}" /></span>

                    </li>
                </g:if>

                <g:if test="${cidadaoInstance?.familia}">
				<li class="fieldcontain">
					<span id="familia-label" class="property-label"><g:message code="cidadao.familia.label" default="Familia" /></span>
					
						<span class="property-value" aria-labelledby="familia-label"><g:link controller="familia" action="show" id="${cidadaoInstance?.familia?.id}">${cidadaoInstance?.familia?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${cidadaoInstance?.nis}">
				<li class="fieldcontain">
					<span id="nis-label" class="property-label"><g:message code="cidadao.nis.label" default="Nis" /></span>
					
						<span class="property-value" aria-labelledby="nis-label"><g:fieldValue bean="${cidadaoInstance}" field="nis"/></span>
					
				</li>
				</g:if>
			
			</ol>
            <fieldset class="buttons">
                <g:link class="edit" controller="emissaoFormulario" action="escolherFamilia">Formulários</g:link>
            </fieldset>
		</div>
	</body>
</html>
