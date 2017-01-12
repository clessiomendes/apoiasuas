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
                                          onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/>
                Endereço: <g:textField name="logradouro" id="inputLogradouro" size="23"
                                       onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/>
                Nº <g:textField name="numero" id="inputNumero" size="1"
                             onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/>
            </div> </td>
            <td> <div>
                <g:link onclick="linkProcurarCidadao(this, '${createLink(action: 'procurarCidadaoExecuta')}',
                                                            document.getElementById('inputNomeOuCodigoLegado'),
                                                            document.getElementById('inputNumero'),
                                                            document.getElementById('inputLogradouro'));">
                                    <input id="btnProcurarCidadao" type="button" class="search" value="Procurar"/>
                </g:link>
            </div> </td> </tr>
        </table>
%{--</g:form>--}%

<div id="list-cidadao" class="content scaffold-list" role="main">
    <table class="tabelaListagem">
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
