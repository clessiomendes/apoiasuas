<%@ page import="org.apoiasuas.cidadao.Telefone" %>

<g:hiddenField name="idFamilia" value="${localDtoFamilia.id}"/>
<table id="tableTelefones" class="pure-table pure-table-striped pure-table-bordered">
    <tr>
        <th style="text-align: center;">DDD</th>
        <th style="text-align: center;">Telefone *</th>
        <th class="hide-on-mobile" style="text-align: center;">
            Observações<g:helpTooltip chave="help.telefone.obs"/>
        </th>
        <th colspan="2" style="text-align: right;">
        </th>
    </tr>

    <g:each in="${localDtoFamilia.telefones.sort{ it.dateCreated ? it.dateCreated : new Date(2999, 1, 1) }}" var="telefone" status="i">
        <g:render template="/familia/telefone/linhaTabela" model="${[telefone: telefone]}"/>
    </g:each>

    <tr> <td colspan="5">
            <input id="btnAdicionarTelefone" type="button" class="speed-button-adicionar" title="Adicionar telefone" onclick="adicionarTelefone();"/>
    </td> </tr>

</table>

<table id="tabelaModelo" class="hidden">
    <g:render template="/familia/telefone/linhaTabela" model="${[telefone: new Telefone()]}"/>
</table>

