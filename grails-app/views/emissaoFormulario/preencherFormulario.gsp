<%@ page import="org.apoiasuas.formulario.PreDefinidos; org.apoiasuas.formulario.Formulario; org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.cidadao.Familia" %>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>${dtoFormulario.nome}</title>
</head>

<body>

<div class="nav" role="navigation">
    <ul><li><g:link class="create" action="escolherFamilia">
            <g:message code="formulario.iniciar.outro" default="Novo"/>
    </g:link></li></ul>
</div>

<h1>${dtoFormulario.nome}</h1>

<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>

<g:hasErrors bean="${dtoFormulario}">
    <ul class="errors" role="alert">
        <g:eachError bean="${dtoFormulario}" var="error">
            <li><g:message error="${error}"/></li>
        </g:eachError>
    </ul>
</g:hasErrors>

<g:form>

    <input type="hidden" id="cidadao.id" name="cidadao.id" value="${dtoFormulario?.cidadao?.id}">
    <input type="hidden" id="familia.id" name="familia.id" value="${dtoFormulario?.cidadao?.familia?.id}">
    <input type="hidden" id="idFormulario" name="idFormulario" value="${dtoFormulario?.id}">
    <input type="hidden" id="formularioEmitido.id" name="formularioEmitido.id" value="${dtoFormulario?.formularioEmitido?.id}">

    <ol class="property-list">

        <g:if test="${templateCamposCustomizados}">%{-- renderiza um template customizado para o formulario sendo editado --}%
            <g:render template="${templateCamposCustomizados}" model="${['dtoFormulario', dtoFormulario]}"/>
        </g:if>
        <g:else>
            <g:agrupaCampos lista="${dtoFormulario.camposOrdenados}" campoGrupo="grupo" status="i" var="campo">
                    <g:divCampoFormularioCompleto campoFormulario="${campo}" focoInicial="${i == 1}"/>
            </g:agrupaCampos>
        </g:else>

    </ol>

    <fieldset class="buttons">
        <g:actionSubmit class="save" action="imprimirFormulario" value="Gerar formulário"/>
        %{--Botão de imprimir e gravar. Ocultar em alguns formularios (onde a gravação pode causar equívocos) ou se o operador nao tiver permissão.--}%
        <sec:ifAllGranted roles="${DefinicaoPapeis.USUARIO}">
            <g:if test="${! [org.apoiasuas.formulario.PreDefinidos.CERTIDOES].contains(dtoFormulario.formularioPreDefinido)}">
                <g:actionSubmit class="save" action="imprimirFormularioGravando"
                                value="Gerar formulário atualizando cadastro"/>
            </g:if>
        </sec:ifAllGranted>
    </fieldset>
    </div>
</g:form>
</body>
</html>
