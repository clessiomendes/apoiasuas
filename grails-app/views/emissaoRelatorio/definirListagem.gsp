<%@ page import="org.apoiasuas.CustomizacoesService; org.apoiasuas.util.StringUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Listagens</title>
</head>

<body>

<div id="edit-fool" class="content scaffold-edit" role="main">
    <h1>Emissão de Listagens</h1>

    <sec:access showto="${CustomizacoesService.Codigos.BELO_HORIZONTE}">
		<div class="nav" role="navigation">
			<ul>
                <li><g:link class="list novo-recurso" controller="familiaDetalhado" action="familiasSemCad">Famílias sem Cad</g:link></li>
			</ul>
		</div>
    </sec:access>

    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <g:hasErrors bean="${definicaoListagem}">
        <ul class="errors" role="alert">
            <g:eachError bean="${definicaoListagem}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>

    <g:form>

        <g:tabs id="tabs" style="margin: 5px;">
            <g:tab id="tabPrincipal" titulo="principal" template="tabPrincipal"/>
            <g:tab id="tabMarcadores" titulo="programas, ações..." template="/familia/marcador/tabMarcadores" model="[permiteInclusao: 'false']"/>
        </g:tabs>

        <fieldset class="buttons">
            %{--<g:submitButton name="list" class="edit" value="Gerar listagem" />--}%
            <g:actionSubmit value="Download de planilha" action="downloadListagem"/>
            <g:actionSubmit value="Exibir na tela" action="exibeListagem"/>
        </fieldset>

    </g:form>

</div>

</body>
</html>
