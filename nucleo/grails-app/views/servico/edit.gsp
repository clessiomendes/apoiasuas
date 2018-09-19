<%@ page import="org.apoiasuas.redeSocioAssistencial.Servico" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'servico.label', default: 'Servico')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
        <asset:javascript src="cropper/cropper.js"/>
        <asset:stylesheet src="cropper/cropper.css"/>
        <asset:javascript src="servico/servico.js"/>
        <asset:stylesheet src="servico/servico.less"/>
        <asset:stylesheet src="sessao.less"/>
        <asset:javascript src="sessao.js"/>
	</head>

	<body>
		<a href="#edit-servico" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <h1>Alterar - Rede intersetorial e socioassistencial<span class="hide-on-mobile"> (serviços, benefícios e programas)</span></h1>
%{--
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="search" action="list">Procurar</g:link></li>
				<li><g:link class="create" action="create">Incluir novo</g:link></li>
			</ul>
		</div>
--}%
		<div id="edit-servico" class="content scaffold-edit" role="main">
			<g:render template="/mensagensPosGravacao" model="[bean: servicoInstance]"/>
			%{--<g:form enctype="multipart/form-data">--}%
			<g:form action="save">
				<g:hiddenField name="id" value="${servicoInstance.id}"/>
				<g:hiddenField name="version" value="${servicoInstance?.version}" />
				<fieldset class="form">
					<g:set var="beanCamposEdicao" scope="request" value="${servicoInstance}"/>
					<tmpl:form/>
				</fieldset>
				<fieldset class="buttons sticky-footer">
                    <input type="button" class="save" value="Gravar" onclick="submitProtegido(this.form)"/>
				    %{--<g:actionSubmit action="save" class="save" value="Gravar" onclick="this.form.action='${createLink(action:'save')}';"/>--}%
				    <g:actionSubmit action="list" class="cancel" value="Cancelar" onclick="this.form.action='${createLink(action:'list')}';"/>
				</fieldset>
			</g:form>
		</div>

	</body>
</html>

