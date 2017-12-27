<%@ page import="org.apoiasuas.cidadao.Cidadao" %>

<table class="tabelaListagem">
    <thead><tr>
        <th><g:message code="cidadao.familia.codigoLegado.label" default="Cad"/></th>
        %{--<th><g:sortableColumn property="nomeCompleto" title="${message(code: 'cidadao.nomeCompleto.label', default: 'Nome')}" /></th>--}%
        <th><g:message code="cidadao.nomeCompleto.label" default="Nome"/></th>
        <th class="hide-on-mobile"><g:message code="cidadao.parentescoReferencia.label" default="Parentesco"/></th>
        <th><g:message code="cidadao.familia.endereco.label" default="Endereco"/></th>
        %{--Removendo telefones da tela de resultado por questão de performance (eles nao tem como ser buscado na SQL original--}%
        %{--<th><g:message code="cidadao.familia.telefones.label" default="Telefones"/></th>--}%
        <th class="hide-on-mobile"><g:message code="cidadao.idade.label" default="Idade"/></th>
    </tr></thead>

    <tbody>
    <g:each in="${cidadaoInstanceList}" status="i" var="cidadao">
        %{Cidadao cidadaoInstance = cidadao}%
        <tr class="${cidadaoInstance.familia.tecnicoReferencia ? 'tecnicoReferencia' : (i % 2) == 0 ? 'even' : 'odd'}">

            <g:if test="${popup}">
                <td><g:link title="${cidadaoInstance?.familia?.tecnicoReferencia ? "Referência: "+cidadaoInstance.familia.tecnicoReferencia : null}"
                            onclick="selecionaPopup(event, {idFamilia: ${cidadaoInstance.familia.id}, cad: '${cidadaoInstance.familia.cad}'})">
                    ${fieldValue(bean: cidadaoInstance, field: "familia.cad")}
                </g:link></td>
                <td><g:link title="${cidadaoInstance?.familia?.tecnicoReferencia ? "Referência: "+cidadaoInstance.familia.tecnicoReferencia : null}"
                            onclick="selecionaPopup(event, {idFamilia: ${cidadaoInstance.familia.id}, cad: '${cidadaoInstance.familia.cad}'})">
                    ${raw(cidadaoInstance.nomeCompleto)}
                </g:link></td>
            </g:if>

            <g:else>
                <td><g:link title="${cidadaoInstance?.familia?.tecnicoReferencia ? "Referência: "+cidadaoInstance.familia.tecnicoReferencia : null}"
                            controller="${controllerLinkFamilia}" action="${actionLinkFamilia}" id="${cidadaoInstance.familia.id}">
                    ${fieldValue(bean: cidadaoInstance, field: "familia.cad")}
                </g:link></td>

                <td><g:link title="${cidadaoInstance?.familia?.tecnicoReferencia ? "Referência: "+cidadaoInstance.familia.tecnicoReferencia : null}"
                            controller="${controllerLinkCidadao}" action="${actionLinkCidadao}" id="${cidadaoInstance.id}">
                    ${raw(cidadaoInstance.nomeCompleto)}
                </g:link></td>
            </g:else>

            <td class="hide-on-mobile">${fieldValue(bean: cidadaoInstance, field: "parentescoReferencia")}</td>

            <td>${raw( org.apoiasuas.util.CollectionUtils.join([
                    cidadaoInstance.familia.endereco?.tipoENomeLogradouro,
                    cidadaoInstance.familia.endereco?.numero,
                    cidadaoInstance.familia.endereco?.complemento,
                    cidadaoInstance.familia.endereco?.bairro], ", ") ?: "" )}</td>

            %{--Removendo telefones da tela de resultado por questão de performance (eles nao tem como ser buscado na SQL original--}%
            %{--<td>${cidadaoInstance.familia.getTelefonesToString()}</td>--}%

            <td class="hide-on-mobile">${cidadaoInstance.idade}</td>

        </tr>
    </g:each>
    </tbody>
</table>
