<%@ page import="org.apoiasuas.util.CollectionUtils; org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.cidadao.Cidadao" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'cidadao.label', default: 'Cidadao')}"/>
    <title>Procurar cidadão</title>
    <asset:javascript src="cidadao/procurarCidadao.js"/>
</head>

<body>

<h1>Procurar cidadão</h1>
<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>

<div class="pesquisar">
    <g:render template="/cidadao/filtrosProcurar"/>

    %{--<div style="display: inline-block; vertical-align: bottom;">--}%
        <g:link onclick="linkProcurarCidadao(this, '${createLink(action: actionButtonProcurar, controller: controllerButtonProcurar)}');">
            <input id="btnProcurarCidadao" type="button" class="search" value="Procurar"/>
        </g:link>
    %{--</div>--}%

    <div class="expandir"><asset:image src="slidedown.png" onclick="expandirFiltros(this);"/></div>
</div>

<div id="list-cidadao" class="content scaffold-list" role="main">

    <g:if test="${cidadaoInstanceCount}">
        <g:render template="/cidadao/tabelaListagem" model="[popup: false]" />
        <div class="pagination">
            <g:paginate params="${pageScope.filtro}" total="${cidadaoInstanceCount ?: 0}"/>
        </div>
    </g:if>
    <g:else>
        <div style="margin: 10px">Nenhum resultado encontrado.</div>
    </g:else>

</div>
</body>
</html>
