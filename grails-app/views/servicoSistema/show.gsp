<%@ page import="org.apoiasuas.redeSocioAssistencial.ServicoSistema" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'servicoSistema.label', default: 'ServicoSistema')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-servicoSistema" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                     default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-servicoSistema" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list servicoSistema">

        <g:if test="${servicoSistemaInstance?.nome}">
            <li class="fieldcontain">
                <span id="nome-label" class="property-label"><g:message code="servicoSistema.nome.label" default="Nome"/></span>
                <span class="property-value" aria-labelledby="nome-label"><g:fieldValue bean="${servicoSistemaInstance}" field="nome"/></span>
            </li>
        </g:if>

        <g:if test="${servicoSistemaInstance?.endereco}">
            <li class="fieldcontain">
                <span id="endereco-label" class="property-label"><g:message code="servicoSistema.endereco.label" default="Endereco"/></span>
                <span class="property-value" aria-labelledby="endereco-label"><g:fieldValue bean="${servicoSistemaInstance}" field="endereco"/></span>
            </li>
        </g:if>

        <g:if test="${servicoSistemaInstance?.abrangenciaTerritorial}">
            <li class="fieldcontain">
                <span id="abrangenciaTerritorial-label" class="property-label"><g:message
                        code="servicoSistema.abrangenciaTerritorial.label" default="Abrangencia Territorial"/></span>
                <span class="property-value" aria-labelledby="abrangenciaTerritorial-label">
                    <g:render template="/abrangenciaTerritorial"/>
                </span>
            </li>
        </g:if>

        <g:if test="${servicoSistemaInstance?.site}">
            <li class="fieldcontain">
                <span id="site-label" class="property-label"><g:message code="servicoSistema.site.label" default="Site"/></span>
                <span class="property-value" aria-labelledby="site-label"><g:fieldValue bean="${servicoSistemaInstance}" field="site"/></span>
            </li>
        </g:if>

        <g:if test="${servicoSistemaInstance?.telefone}">
            <li class="fieldcontain">
                <span id="telefone-label" class="property-label"><g:message code="servicoSistema.telefone.label" default="Telefone"/></span>
                <span class="property-value" aria-labelledby="telefone-label"><g:fieldValue bean="${servicoSistemaInstance}" field="telefone"/></span>
            </li>
        </g:if>

    </ol>
    <g:form url="[resource: servicoSistemaInstance, action: 'delete']">
        <fieldset class="buttons">
            <g:link class="edit" action="edit" resource="${servicoSistemaInstance}"><g:message
                    code="default.button.edit.label" default="Edit"/></g:link>
            <g:actionSubmit class="delete" action="delete"
                            value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                            onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Tem certeza?')}');"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
