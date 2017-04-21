<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#create-familia" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="create-familia" class="content scaffold-create" role="main">
			<h1><g:message code="default.create.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors model="${[familiaInstance:familiaInstance, cidadaoInstance:cidadaoInstance]}">
			<ul class="errors" role="alert">
				<g:eachError model="${[familiaInstance:familiaInstance, cidadaoInstance:cidadaoInstance]}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form url="[resource:familiaInstance, action:'save']" >
				<g:tabs id="tabs" style="margin: 5px;">
					<g:tab id="tabEditFamilia" titulo="família" template="tabEditFamilia"/>
					<g:tab id="tabReferencia" titulo="referência familiar" template="/cidadao/form" model="[prefixoEntidade: 'cidadao.']"/>
				</g:tabs>
				<fieldset class="buttons">
					<g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
