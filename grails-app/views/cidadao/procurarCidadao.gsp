<%@ page import="org.apoiasuas.util.CollectionUtils; org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.cidadao.Cidadao" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'cidadao.label', default: 'Cidadao')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <asset:javascript src="especificos/procurarCidadao.js"/>
</head>

<g:javascript>
    function expandePesquisa() {
        $("#expansivel").slideDown(1000);
        $("#slidedown").hide();
        return false;
    };
</g:javascript>

<body>

<h1>Procurar cidadão</h1>
<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>

<g:hasErrors>
    <ul class="errors" role="alert">
        <g:eachError var="error">
            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
        </g:eachError>
    </ul>
</g:hasErrors>

%{--<g:form action="procurarCidadaoExecuta">--}%
            <div class="buttons">
                Nome ou Cad: <g:textField name="nomeOuCodigoLegado" id="inputNomeOuCodigoLegado" size="23" autofocus=""
                                          onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"
                                          value="${defaultNomePesquisa}"/>
                Endereço: <g:textField name="logradouro" id="inputLogradouro" size="23"
                                       onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/>
                Nº <g:textField name="numero" id="inputNumero" size="1"
                             onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/>
                <g:link onclick="linkProcurarCidadao(this, '${createLink(action: actionButtonProcurar, controller: controllerButtonProcurar)}',
                                                            document.getElementById('inputNomeOuCodigoLegado'),
                                                            document.getElementById('inputNumero'),
                                                            document.getElementById('inputLogradouro'));">
                    <input id="btnProcurarCidadao" type="button" class="speed-button-procurar"/>
                </g:link>
            </div>
%{--</g:form>--}%

<div id="list-cidadao" class="content scaffold-list" role="main">

    <g:render template="tabelaListagem"/>

    <div class="pagination">
        <g:paginate params="${pageScope.filtro}" total="${cidadaoInstanceCount ?: 0}"/>
    </div>
</div>
</body>
</html>
