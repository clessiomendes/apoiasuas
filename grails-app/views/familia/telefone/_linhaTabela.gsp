%{--Mostra eventuais erros de validacao:                        --}%
<g:hasErrors bean="${telefone}">
    <tr> <td colspan="3"> <ul class="errors" role="alert">
        <g:eachError bean="${telefone}" var="error">
            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
        </g:eachError>
    </ul></td></tr>
</g:hasErrors>

<tr id="linhaTelefone${i}">

    <g:hiddenField name="remover" class="removerTelefone"  value="false"/>
    <g:hiddenField name="idTelefone" class="idTelefone" value="${telefone?.id}"/>

    <td class="${hasErrors(bean: telefone, field: 'DDD', 'error')}">
        <g:textField name="ddd" size="1" maxlength="3" value="${telefone?.DDD}"/>
    </td>
    <td class="${hasErrors(bean: telefone, field: 'numero', 'error')}">
        <g:textField name="numero" class="importante" size="10" maxlength="40" value="${telefone?.numero}"/>
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
