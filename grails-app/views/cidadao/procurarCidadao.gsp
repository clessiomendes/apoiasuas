<%@ page import="org.apoiasuas.util.CollectionUtils; org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.cidadao.Cidadao" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'cidadao.label', default: 'Cidadao')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>

    <style type="text/css">
    table.search {margin: 0;}
    table.search tr:hover { background: none; }
    table.search td { padding: 0; }
    table.search div { text-indent: 0; }
    </style>

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

<g:form action="procurarCidadaoExecuta">
    <fieldset class="buttons">
        <table class="search">
            <tr>
                <td>
                    <div>
                        Nome ou Cad: <g:textField name="nomeOuCodigoLegado" size="23" autofocus=""/>
                        Endereço: <g:textField name="logradouro" size="23"/> Nº <g:textField name="numero" size="1"/>
                        %{--<td>Cad:<g:textField name="codigoLegado" size="1"/></td>--}%
                    </div>
%{--
                    <div id="expansivel" style="display: none">
                        Segundo membro: <g:textField name="segundoMembro" size="23" autofocus=""/>
                    </div>
--}%
                </td>
                <td>
                    <div>
%{--    SLIDEDOWN                    <input type="button" id="slidedown" class="slidedown" value="." title="mais opções" onclick="expandePesquisa();"/>--}%
                        <g:submitButton name="procurar" class="search" value="Procurar"/>
                    </div>
                </td>
            </tr>
        </table>
    </fieldset>
</g:form>

<div id="list-cidadao" class="content scaffold-list" role="main">
    <table>
        <thead><tr>
            <th><g:message code="cidadao.familia.codigoLegado.label" default="Cad"/></th>
            %{--<th><g:sortableColumn property="nomeCompleto" title="${message(code: 'cidadao.nomeCompleto.label', default: 'Nome')}" /></th>--}%
            <th><g:message code="cidadao.nomeCompleto.label" default="Nome"/></th>
            <th><g:message code="cidadao.parentescoReferencia.label" default="Parentesco"/></th>
            <th><g:message code="cidadao.familia.endereco.label" default="Endereco"/></th>
            <th><g:message code="cidadao.familia.telefones.label" default="Telefones"/></th>
            <th><g:message code="cidadao.idade.label" default="Idade"/></th>
        </tr></thead>

        <tbody>
        <g:each in="${cidadaoInstanceList}" status="i" var="cidadao">
            %{Cidadao cidadaoInstance = cidadao}%
            <tr class="${cidadaoInstance.familia.tecnicoReferencia ? 'tecnicoReferencia' : (i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link title="${cidadaoInstance?.familia?.tecnicoReferencia ? "Referência: "+cidadaoInstance.familia.tecnicoReferencia : null}" action="selecionarFamilia" id="${cidadaoInstance.familia.id}">
                    ${fieldValue(bean: cidadaoInstance, field: "familia.codigoLegado")}
                </g:link></td>

                <td><g:link title="${cidadaoInstance?.familia?.tecnicoReferencia ? "Referência: "+cidadaoInstance.familia.tecnicoReferencia : null}" controller="cidadao" action="show" id="${cidadaoInstance.id}">
                    ${raw(cidadaoInstance.nomeCompleto)}
                </g:link></td>

                <td>${fieldValue(bean: cidadaoInstance, field: "parentescoReferencia")}</td>

                <td>${raw( org.apoiasuas.util.CollectionUtils.join([
                        cidadaoInstance.familia.endereco.tipoENomeLogradouro,
                        cidadaoInstance.familia.endereco.numero,
                        cidadaoInstance.familia.endereco.complemento,
                        cidadaoInstance.familia.endereco.bairro], ", ") ?: "" )}</td>

                <td>${cidadaoInstance.familia.getTelefonesToString()}</td>

                <td>${cidadaoInstance.idade}</td>

            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <g:paginate params="${pageScope.filtro}" total="${cidadaoInstanceCount ?: 0}"/>
    </div>
</div>
</body>
</html>
