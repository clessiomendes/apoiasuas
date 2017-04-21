<script>
    function removerTelefoneClick(hiddenRemover, linhaTelefone) {
        hiddenRemover.value = "true";
        $(linhaTelefone).hide();
    }

    $(document).ready(function() {
//Inicialização dos eventos
        $(".obs").focus(function () {
            $(this).css("height", "54px");
        });

        $(".obs").blur(function () {
            $(this).css("height", "19px");
        });
    });

</script>

<g:hiddenField name="idFamilia" value="${localDtoFamilia.id}"/>
<table id="tableTelefones" class="pure-table pure-table-striped pure-table-bordered" style="max-width: 400px; margin: 10px auto;">
    <tr>
        <th style="text-align: center;">DDD</th>
        <th style="text-align: center;">Numero *</th>
        <th style="text-align: center;">
            Observações<g:helpTooltip chave="help.telefone.obs"/>
        </th>
        <th style="text-align: center;"></th>
    </tr>

    <g:each in="${localDtoFamilia.telefones.sort{ it.dateCreated ? it.dateCreated : new Date(2999, 1, 1) }}" var="telefone" status="i">
    %{--Mostra eventuais erros de validacao:                        --}%
        <g:hasErrors bean="${telefone}">
            <tr> <td colspan="3"> <ul class="errors" role="alert">
                <g:eachError bean="${telefone}" var="error">
                    <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul></td></tr>
        </g:hasErrors>

        <g:hiddenField name="idTelefone" id="idTelefone${i}" value="${telefone?.id}"/>
        <g:hiddenField name="remover" id="removerTelefone${i}"  value="false"/>
        <tr id="linhaTelefone${i}">
            <td class="${hasErrors(bean: telefone, field: 'DDD', 'error')}">
                <g:textField name="ddd" id="ddd${i}" size="1" maxlength="3" value="${telefone?.DDD}"/>
            </td>
            <td class="${hasErrors(bean: telefone, field: 'numero', 'error')}">
                <g:textField name="numero" id="numero${i}" size="10" maxlength="40" value="${telefone?.numero}"/>
            </td>
            <td class="${hasErrors(bean: telefone, field: 'obs', 'error')}">
                <g:textArea style="height:19px" cols="40" name="obs" class="obs" id="obs${i}" maxlength="1000" value="${telefone?.obs}"/>
            </td>
            <td> <g:if test="${telefone?.id}">
                <input type="button" id="removerTelefone" class="speed-button-remover" onclick="removerTelefoneClick(removerTelefone${i}, linhaTelefone${i}); return false;" title="remover"/>
            </g:if> </td>
        </tr>
    </g:each>
</table>
