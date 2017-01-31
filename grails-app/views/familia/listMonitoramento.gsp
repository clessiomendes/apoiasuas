<table class="tabelaListagem">
    <thead><tr>
        <th>Data</th>
        <th>Descrição</th>
        <th>Situação</th>
    </tr></thead>
    <tbody>
    <g:each in="${monitoramentoInstanceList}" status="i" var="monitoramentoInstance"> <% org.apoiasuas.cidadao.Monitoramento monitoramento = monitoramentoInstance %>
        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
            <td><a href="javascript:void(0)" onclick='abreJanela("ver monitoramento","${createLink(action:'showMonitoramento', id: monitoramentoInstance.id)}");'>
                <g:formatDate date="${monitoramento.dataCriacao}"/>
            </a></td>
            <td><a href="javascript:void(0)" onclick='abreJanela("ver monitoramento","${createLink(action:'showMonitoramento', id: monitoramentoInstance.id)}");'>
                ${monitoramento.memoCortado}
            </a></td>
            <td><a href="javascript:void(0)" onclick='abreJanela("ver monitoramento","${createLink(action:'showMonitoramento', id: monitoramentoInstance.id)}");'>
                ${monitoramento.situacao}
            </a></td>
        </tr>
    </g:each>
    </tbody>
</table>
