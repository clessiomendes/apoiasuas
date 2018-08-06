<%@ page import="org.apoiasuas.redeSocioAssistencial.ServicoSistema" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'servicoSistema.label', default: 'ServicoSistema')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<a href="#list-servicoSistema" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                     default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="list-servicoSistema" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table class="tabelaListagem">
        <thead>
        <tr>
            <g:sortableColumn property="nome" title="${message(code: 'servicoSistema.nome.label', default: 'Nome')}"/>
            <th><g:message code="servicoSistema.endereco.label" default="Endereco"/></th>
            <th><g:message code="servicoSistema.abrangenciaTerritorial.label" default="Abrangencia Territorial"/></th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${servicoSistemaInstanceList}" status="i" var="servicoSistemaInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                <td><g:link action="show" id="${servicoSistemaInstance.id}">
                    ${fieldValue(bean: servicoSistemaInstance, field: "nome")}
                </g:link></td>
                <td>${fieldValue(bean: servicoSistemaInstance, field: "endereco")}</td>
                <td>${fieldValue(bean: servicoSistemaInstance, field: "abrangenciaTerritorial.nomeCompleto")}</td>
            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <g:paginate total="${servicoSistemaInstanceCount ?: 0}"/>
    </div>
</div>
</body>
</html>
