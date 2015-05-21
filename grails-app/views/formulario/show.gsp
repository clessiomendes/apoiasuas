
<%@ page import="org.apoiasuas.formulario.Formulario" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'formulario.label', default: 'Formulario')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-formulario" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-formulario" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list formulario">
			
				<g:if test="${formularioInstance?.nome}">
				<li class="fieldcontain">
					<span id="nome-label" class="property-label"><g:message code="formulario.nome.label" default="Nome" /></span>
					
						<span class="property-value" aria-labelledby="nome-label"><g:fieldValue bean="${formularioInstance}" field="nome"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${formularioInstance?.descricao}">
				<li class="fieldcontain">
					<span id="descricao-label" class="property-label"><g:message code="formulario.descricao.label" default="Descricao" /></span>
					
						<span class="property-value" aria-labelledby="descricao-label"><g:fieldValue bean="${formularioInstance}" field="descricao"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${formularioInstance?.formularioPreDefinido}">
				<li class="fieldcontain">
					<span/>
					<span class="property-value" aria-labelledby="codigo-label"><b>Formulário pré-definido</b></span>
				</li>
				</g:if>
			
				<g:if test="${formularioInstance?.template}">
				<li class="fieldcontain">
					<span/>
					<span class="property-value"><g:link action="downloadTemplate" id="${formularioInstance.id}">Baixar formulário padrão</g:link></span>
				</li>
				</g:if>
			
				<g:if test="${formularioInstance?.campos}">
				<li class="fieldcontain">
					<span id="campos-label" class="property-label"><g:message code="formulario.campos.label" default="Campos" /></span>
						<g:each in="${formularioInstance.camposOrdenados}" var="c">
							<span class="property-value" aria-labelledby="campos-label">
								%{--<g:link controller="campoFormulario" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link>--}%
								${c?.encodeAsHTML()}
							</span>
						</g:each>
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:formularioInstance]">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${formularioInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					%{-- Não permite excluir formularios pre-definidos --}%
					<g:if test="${! formularioInstance?.formularioPreDefinido}">
						<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					</g:if>
					<g:link class="edit" action="simularTemplate" resource="${formularioInstance}"><g:message code="fomulario.simular.template" default="Gerar formulário inicial" /></g:link>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
