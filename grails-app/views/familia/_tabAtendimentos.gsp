<%@ page import="org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.redeSocioAssistencial.RecursosServico" %>
<%
    List<Map> localDtoAtendimentos = atendimentosList;
%>

<style>
    #tabelaAtendimentos, #tabelaAtendimentos td {
        border: 1px solid darkgray;
    }
</style>

<table id="tabelaAtendimentos" class="tabelaListagem">
    <tbody>
    <thead><tr>
        <th>Data</th>
        <th>Cidadão</th>
        <th>Técnico</th>
        <th></th>
    </tr></thead>
    <g:each in="${localDtoAtendimentos}" status="i" var="atendimento">
        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
            <td>
                <g:formatDate date="${atendimento.dataHora}" format="dd/MM/yyyy HH:mm"/>
            </td>
            <td>
                ${atendimento.nomeCidadao}
            </td>
            <td>
                ${atendimento.tecnico.username}
            </td>
            <td>
                %{--${atendimento.getTooltip()}--}%
                ${atendimento.compareceu == false ? "não compareceu" : ""}
            </td>
        </tr>
    </g:each>
    </tbody>
</table>