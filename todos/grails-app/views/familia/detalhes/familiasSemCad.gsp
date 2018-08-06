<%@ page import="org.apoiasuas.util.CollectionUtils; org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.cidadao.Cidadao" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="Familia"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <asset:stylesheet src="relatorio/familias-sem-cad.less"/>
    <asset:javascript src="datepicker-pt-BR.js"/>
</head>

<body>

<h1>Famílias sem número de cadastro</h1>

<div class="buttons">
    <g:form action="familiasSemCad">
        Procurar por: <nobr>nome ou cad <g:textField name="nomeOuCad" id="inputNomeOuCad" size="23" autofocus=""/></nobr>
        <nobr>data <g:textField class="dateMask datepicker" name="dataCriacao" id="dataCriacao" size="6" maxlength="10"/></nobr>
        <nobr>técnico <g:select name="criador" noSelection="${['':'']}" from="${ususariosDisponiveis.collect{it.username}}" keys="${ususariosDisponiveis.collect{it.id}}"/></nobr>
        <g:submitButton name="procurar" value="Procurar" class="search" />
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
