<%@ page import="org.apoiasuas.seguranca.DefinicaoPapeis" %>
<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance
%>

<div id="edit-telefones" class="content scaffold-edit" role="main">
    <g:formRemote url="[action:'saveTelefones', id: localDtoFamilia.id]" name="saveTelefones"
                  onFailure="janelaModal.loadHTML(XMLHttpRequest.responseText);"
                  onSuccess="janelaModal.confirmada();">

        <fieldset class="form">
            <g:render template="telefone/formTelefones" model="${[localDtoFamilia: localDtoFamilia]}"/>
        </fieldset>

        <fieldset class="buttons">
            <g:submitButton roles="${DefinicaoPapeis.STR_USUARIO}" name="update" class="save" value="Gravar" />
            <input type="button" class="cancel" onclick="janelaModal.cancelada();" value="Cancelar" />
        </fieldset>

    </g:formRemote>
</div>

