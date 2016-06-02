<%@ page import="org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'abrangenciaTerritorial.label')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>

        %{-- Para o componente treeview: --}%
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css" />
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.1/jquery.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>
    </head>

<g:javascript>
    $(document).ready(function() {
        $('#div_territoriosAtuacao').jstree({
            //'plugins' : ['checkbox'],
            'core' : {
                'data' : ${raw(hierarquiaTerritorial)}
            },
            "rules":{
                multiple : false
            },
            "ui" : {
                "select_limit" : 0  //no selection
            }//ui
        });//jstree
    });//function
</g:javascript>

	<body>
		<a href="#show-abrangenciaTerritorial" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-abrangenciaTerritorial" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list abrangenciaTerritorial">
				<g:if test="${abrangenciaTerritorialInstance?.nome}">
				<li class="fieldcontain">
					<span id="nome-label" class="property-label"><g:message code="abrangenciaTerritorial.nome.label" default="Nome" /></span>
					<span class="property-value" aria-labelledby="nome-label"><g:fieldValue bean="${abrangenciaTerritorialInstance}" field="nome"/></span>
				</li>
				</g:if>

                <li class="fieldcontain">
                    <span id="habilitado-label" class="property-label"><g:message code="abrangenciaTerritorial.habilitado.label" default="Habilitado" /></span>
                    <span class="property-value" aria-labelledby="habilitado-label"><td>${abrangenciaTerritorialInstance.habilitado ? "sim" : "não"}</td></span>
                </li>

                <li class="fieldcontain">
                    <span id="pai-label" class="property-label"><g:message code="abrangenciaTerritorial.mae.label" default="Subordinado a" /></span>
                    <span class="property-value" aria-labelledby="pai-label"><div id="div_territoriosAtuacao"/></span>
                </li>

			</ol>
			<g:form url="[resource:abrangenciaTerritorialInstance, action:'delete']">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${abrangenciaTerritorialInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Tem certeza?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
