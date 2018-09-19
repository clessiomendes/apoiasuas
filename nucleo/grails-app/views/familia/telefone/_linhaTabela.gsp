<tr id="linhaTelefone${i}" class="linha-telefone">

    <g:hiddenField name="remover" class="removerTelefone"  value="false"/>
        <g:hiddenField name="idTelefone" class="idTelefone" value="${telefone?.id}"/>

    <td class="${hasErrors(bean: telefone, field: 'DDD', 'error')}">
        <g:textField name="ddd" class="ddd-telefone" size="1" maxlength="3" value="${telefone?.DDD}"/>
    </td>
    <td class="${hasErrors(bean: telefone, field: 'numero', 'error')}">
        <g:textField name="numero" class="importante numero-telefone" size="10" maxlength="40" value="${telefone?.numero}"/>
    </td>
    <td class="hide-on-mobile ${hasErrors(bean: telefone, field: 'obs', 'error')}">
        <g:textArea style="height:19px" name="obs" class="obs" maxlength="1000" value="${telefone?.obs}"/>
    </td>
    <td>
        <span style="font-size: 0.75em">
            &nbsp;<g:formatDate date="${telefone?.dateCreated}" format="(MMM/yyyy)"/>&nbsp;
        </span>
    </td>
    <td> <g:if test="${telefone?.id}">
        <input type="button" id="removerTelefone" class="speed-button-remover" onclick="removerTelefoneClick(this); return false;" title="remover"/>
    </g:if> </td>
</tr>
