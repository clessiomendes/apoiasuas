<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="importar.cadastro.de.familias"/></title>
    <asset:javascript src="jqery.js"/>
</head>

<body>

<div id="edit-fool" class="content scaffold-edit" role="main">
    <h1><g:message code="???" default="Emissão de Formulários"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
</div>

<g:render template="escolherFamilia"/>

<g:each in="${formulariosDisponiveis}" var="tipo">
    <fieldset class="embedded"><legend class="collapsable" style="cursor:pointer;">${tipo.key}</legend>
        <g:each in="${tipo.value}" var="formulario">
            <g:actionSubmitOpcaoFomulario formulario="${formulario}"/>
        </g:each>
    </fieldset>
</g:each>

</body>
</html>
