<table class="tabelaListagem">
    <thead><tr>
        <th>Data</th>
        <th>Descrição</th>
        %{--<th>Situação</th>--}%
    </tr></thead>
    <tbody>
    <g:each in="${auditoriaAcompanhamentoList}" status="i" var="auditoria">
        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
            <td>
                <g:formatDate date="${auditoria.dateCreated}"/>
            </td>
            <td>
                ${auditoria.descricao}
            </td>
        </tr>
    </g:each>
    </tbody>
</table>
