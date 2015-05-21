<%@ page import="org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.cidadao.Cidadao" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'cidadao.label', default: 'Cidadao')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>

</head>

<body>

<a href="#list-cidadao" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                              default="Skip to content&hellip;"/></a>

%{--
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>
--}%

<h1><g:message code="default.list.label" args="[entityName]"/></h1>
<g:if test="${request.message}">
    <div class="message" role="status">${request.message}</div>
</g:if>

<div id="filtro-cidadao">
    <g:form action="procurarCidadao">
%{--
        TODO Centralizar verticalmente o conteúdo da tabela abaixo (filtro de cidadaos) usando css
        <td style="vertical-align:middle">Nome:<g:textField name="nome" size="20" autofocus="" value="${flash.filtroCidadao?.nome}"/></td>
--}%
        <table>
            <tr>
                <td>Nome:<g:textField name="nome" size="20" autofocus="" value="${filtro?.nome}"/></td>
                <td>Logradouro:<g:textField name="logradouro" size="20"
                                            value="${filtro?.logradouro}"/></td>
                <td>Numero:<g:textField name="numero" size="1" value="${filtro?.numero}"/></td>
                <td>Cad:<g:textField name="codigoLegado" size="1" value="${filtro?.codigoLegado}"/></td>
                <td><g:submitButton name="procurar" value="Procurar"/></td>
            </tr>
        </table>
    </g:form>
</div>

<div id="list-cidadao" class="content scaffold-list" role="main">
    <table>
        <thead><tr>
            <th><g:message code="cidadao.familia.codigoLegado.label" default="Cad"/></th>
            %{--<th><g:sortableColumn property="nomeCompleto" title="${message(code: 'cidadao.nomeCompleto.label', default: 'Nome')}" /></th>--}%
            <th><g:message code="cidadao.nomeCompleto.label" default="Nome"/></th>
            <th><g:message code="cidadao.parentescoReferencia.label" default="Parentesco"/></th>
            <th><g:message code="cidadao.familia.endereco.label" default="Endereco"/></th>
            <th><g:message code="cidadao.familia.telefones.label" default="Telefones"/></th>
            <th><g:message code="cidadao.dataNascimento.label" default="Nascimento"/></th>
        </tr></thead>

        <tbody>
        <g:each in="${cidadaoInstanceList}" status="i" var="cidadao">
            %{Cidadao cidadaoInstance = cidadao}%
            <tr class="${cidadaoInstance.familia.familiaAcompanhada ? 'familiaAcompanhada' : (i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link title="${cidadaoInstance?.familia?.mostraTecnicoAcompanhamento()}" action="selecionarFamilia" id="${cidadaoInstance.familia.id}">
                    ${fieldValue(bean: cidadaoInstance, field: "familia.codigoLegado")}
                </g:link></td>

                <td><g:link title="${cidadaoInstance?.familia?.mostraTecnicoAcompanhamento()}" controller="cidadao" action="show" id="${cidadaoInstance.id}">
                    ${fieldValue(bean: cidadaoInstance, field: "nomeCompleto")}
                </g:link></td>

                <td>${fieldValue(bean: cidadaoInstance, field: "parentescoReferencia")}</td>

                <td>${fieldValue(bean: cidadaoInstance, field: "familia.endereco")}</td>

                <td>${cidadaoInstance.familia.getTelefonesToString()}</td>

                <td><g:formatDate date="${cidadaoInstance.dataNascimento}"/></td>

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
