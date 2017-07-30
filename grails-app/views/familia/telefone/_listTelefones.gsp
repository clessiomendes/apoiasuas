<%@ page import="org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.redeSocioAssistencial.RecursosServico" %>
<%
    List<Map> localDtoTelefones = telefonesList;
%>

<style>
    #tabelaTelefones, #tabelaTelefones td {
        border: 1px solid darkgray;
    }
</style>

<table id="tabelaTelefones" class="tabelaListagem">
    %{--<thead><tr>
        <th>Número</th>
        <th>Data</th>
        <th></th>
    </tr></thead>--}%
    <tbody>
        <tr>
        </tr>
    <g:set var="ultimaOrigem" value=""/>
    <g:each in="${localDtoTelefones}" status="i" var="telefone">
        <g:if test="${ultimaOrigem != telefone.origem}">
            <td colspan="3" style="text-align: center; font-weight: bold">
                ${telefone.origem}
                <g:if test="${telefone.origem == FamiliaController.TELEFONES_CADASTRADOS}">
                        <input id="editarTelefones" type="button" value="Alterar"  class="edit"
                                                     title="Clique para incluir, remover ou alterar telefones no cadastro."
                                                     onclick="editTelefones();">
                </g:if>
            </td>
            <g:set var="ultimaOrigem" value="${telefone.origem}"/>
        </g:if>
        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
            <td>
                ${telefone.numero}
            </td>
            <td>
                <g:formatDate date="${telefone.data}"/>
            </td>
            <td>
                ${telefone.observacoes}
            </td>
        </tr>
    </g:each>
    </tbody>
</table>