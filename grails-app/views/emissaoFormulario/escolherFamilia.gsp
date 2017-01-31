<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="emissao.formularios"/></title>
</head>


<body>

<g:render template="/baixarArquivo"/>

<div id="edit-fool" class="content scaffold-edit" role="main">
    <h1><g:message code="emissao.formularios"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <fieldset class="form">

        <g:render template="escolherFamilia"/>

        <g:each in="${formulariosDisponiveis}" var="tipo">
            <fieldset class="embedded" style="padding: 10px"><legend class="collapsable" style="cursor:pointer;">${tipo.key}</legend>
                <g:each in="${tipo.value}" var="formulario">
                    <g:actionSubmitOpcaoFomulario formulario="${formulario}"/>
                </g:each>
            </fieldset>
        </g:each>

    </fieldset>%{--class="form"--}%
</div>

</body>
</html>
