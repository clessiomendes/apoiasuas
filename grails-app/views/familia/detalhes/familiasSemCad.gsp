<%@ page import="org.apoiasuas.util.CollectionUtils; org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.cidadao.Cidadao" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="Familia"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <asset:stylesheet src="familia/detalhes/familias-sem-cad.less"/>
</head>

<body>

<h1>Famílias sem número de cadastro</h1>

<div class="buttons">
    <g:form action="familiasSemCad">
        <nobr>Procurar por nome ou cad: <g:textField name="nomeOuCad" id="inputNomeOuCad" size="23" autofocus=""/></nobr>
        <g:submitButton name="procurar" value="" class="speed-button-procurar"/>
    </g:form>
</div>

<div id="list-familias" role="main">

    <g:if test="${familiaInstanceCount}">
        <g:render template="/familia/detalhes/tabelaListagem" />
%{--
        <div class="pagination">
            <g:paginate params="${pageScope.filtro}" total="${familiaInstanceCount ?: 0}"/>
        </div>
--}%
    </g:if>
    <g:else>
        <div style="margin: 10px">Nenhum resultado encontrado.</div>
    </g:else>

</div>
</body>
</html>
