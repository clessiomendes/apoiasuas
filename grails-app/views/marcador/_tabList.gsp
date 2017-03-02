<div class="nav" role="navigation" style="margin-top: 5px; margin-bottom: 5px;">
    <ul>
        <li><g:link class="create" action="create${entityName}">Novo</g:link></li>
        <li><g:form action="list">
                Filtrar apenas se disponíveis para
                <g:select name="idServicoSistema" noSelection="${['':'qualquer serviço']}"
                          from="${servicosDisponiveis.collect{it.nome}}" keys="${servicosDisponiveis.collect{it.id}}"
                          value="${servicoEscolhido}" onchange="submit();"  />
        </g:form></li>
    </ul>
</div>
%{--<g:actionSubmit action="create${entityName}" class="create" value="Novo"/>--}%

<table class="tabelaListagem">
    <thead>
    <tr><g:sortableColumn property="descricao" title="descrição" /><g:sortableColumn property="habilitado" title="." /></tr>
    </thead>
    <tbody>
    <g:each in="${marcadorInstanceList}" status="i" var="marcadorInstance">
        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
            <td><g:link action="show${entityName}" id="${marcadorInstance.id}">${fieldValue(bean: marcadorInstance, field: "descricao")}</g:link></td>
            <td>${marcadorInstance.habilitado ? "habilitado" : "desabilitado"}</td>
        </tr>
    </g:each>
    </tbody>
</table>
