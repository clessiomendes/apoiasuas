<%@ page import="org.apoiasuas.cidadao.MarcadorService; org.apoiasuas.cidadao.FamiliaService; org.apoiasuas.util.CollectionUtils; org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.cidadao.Cidadao" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'cidadao.label', default: 'Cidadao')}"/>
    <title>Listar famílias</title>
</head>

<body>

<h1>${tituloListagem}</h1>
<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>

<div id="list-cidadao" class="content scaffold-list" role="main">

    <g:render template="/cidadao/tabelaListagem"/>

    <g:if test="${cidadaoInstanceList.size() >= org.apoiasuas.cidadao.MarcadorService.MAX_FAMILIAS_REFERENCIADAS}">
        <ul class="errors" role="alert"> <li>
                - Atenção! Apenas as ${MarcadorService.MAX_FAMILIAS_REFERENCIADAS} primeiras famílias foram exibidas.
        </li> </ul>
    </g:if>

%{--
    <div class="pagination">
        <g:paginate params="${pageScope.filtro}" total="${cidadaoInstanceCount ?: 0}"/>
    </div>
--}%
</div>
</body>
</html>
