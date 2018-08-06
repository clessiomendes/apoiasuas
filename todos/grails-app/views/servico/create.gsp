<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'servico.label', default: 'Servico')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
        <asset:javascript src="cropper/cropper.js"/>
        <asset:stylesheet src="cropper/cropper.css"/>
        <asset:javascript src="servico/servico.js"/>
        <asset:stylesheet src="servico/servico.less"/>
        <asset:stylesheet src="sessao.less"/>
        <asset:javascript src="sessao.js"/>
	</head>
	<body>
		<a href="#create-servico" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <h1>Novo - Rede intersetorial e socioassistencial<span class="hide-on-mobile"> (serviços, benefícios e programas)</span></h1>

%{--
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="search" action="list">Procurar</g:link></li>
			</ul>
		</div>
--}%
		<div id="create-servico" class="content scaffold-create" role="main">
			<g:if test="${flash.message}">
			    <div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${servicoInstance}">
                <ul class="errors" role="alert">
                    <g:eachError bean="${servicoInstance}" var="error">
                    <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                    </g:eachError>
                </ul>
			</g:hasErrors>
			<g:form action="save">
				<fieldset class="form">
                    <g:set var="beanCamposEdicao" scope="request" value="${servicoInstance}"/>
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons  sticky-footer">
                    <input type="button" class="save" value="Gravar" onclick="submitProtegido(this.form)"/>
				    %{--<g:actionSubmit action="save" class="save" value="Gravar" onclick="this.form.action='${createLink(action:'save')}';"/>--}%
				    <g:actionSubmit action="list" class="cancel" value="Cancelar" onclick="this.form.action='${createLink(action:'list')}';"/>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
