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

<a href="#list-cidadao" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                              default="Skip to content&hellip;"/></a>

<h1>Procurar cidadão</h1>
<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>

%{--<g:form action="procurarCidadaoExecuta">--}%
        <table class="parametrosPesquisa">
            <tr> <td> <div>
                Nome ou Cad: <g:textField name="nomeOuCodigoLegado" id="inputNomeOuCodigoLegado" size="23" autofocus=""
                                          onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"
                                          value="${defaultNomePesquisa}"/>
                Endereço: <g:textField name="logradouro" id="inputLogradouro" size="23"
                                       onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/>
                Nº <g:textField name="numero" id="inputNumero" size="1"
                             onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/>
            </div> </td>
            <td> <div>
                <g:link onclick="linkProcurarCidadao(this, '${createLink(action: actionButtonProcurar, controller: controllerButtonProcurar)}',
                                                            document.getElementById('inputNomeOuCodigoLegado'),
                                                            document.getElementById('inputNumero'),
                                                            document.getElementById('inputLogradouro'));">
                                    <input id="btnProcurarCidadao" type="button" class="search" value="Procurar"/>
                </g:link>
            </div> </td> </tr>
        </table>
%{--</g:form>--}%

<div id="list-cidadao" class="content scaffold-list" role="main">

    <g:render template="tabelaListagem"/>

    <div class="pagination">
        <g:paginate params="${pageScope.filtro}" total="${cidadaoInstanceCount ?: 0}"/>
    </div>
</div>
</body>
</html>
