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
    <tbody>
        <tr> <td colspan="3" style="text-align: center; font-weight: bold">
                ${FamiliaController.TELEFONES_CADASTRADOS}
                <input id="editarTelefones" type="button" value="Alterar"  class="edit"
                                             title="Clique para incluir, remover ou alterar telefones no cadastro."
                                             onclick="editTelefones();">
        </td> </tr>
        <g:each in="${localDtoTelefones.findAll { it.origem == FamiliaController.TELEFONES_CADASTRADOS}}" status="i" var="telefone">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                <td> ${telefone.numero} </td>
                <td> <g:formatDate date="${telefone.data}"/> </td>
                <td> ${telefone.observacoes} </td>
            </tr>
        </g:each>
        <tr> <td colspan="3" style="text-align: center; font-weight: bold">
                ${FamiliaController.TELEFONES_AGENDAMENTO}
        </td> </tr>
        <g:each in="${localDtoTelefones.findAll { it.origem == FamiliaController.TELEFONES_AGENDAMENTO}}" status="i" var="telefone">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                <td> ${telefone.numero} </td>
                <td> <g:formatDate date="${telefone.data}"/> </td>
                <td> ${telefone.observacoes} </td>
            </tr>
        </g:each>
    </tbody>
</table>