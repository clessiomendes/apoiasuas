<%
    org.apoiasuas.cidadao.Monitoramento localDtoMonitoramento = monitoramentoInstance
%>


<div id="edit-monitoramento" class="content scaffold-edit" role="main">
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${localDtoMonitoramento}">
        <ul class="errors" role="alert">
            <g:eachError bean="${localDtoMonitoramento}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>

    <g:formRemote url="[action:'saveMonitoramento', id: localDtoMonitoramento.id]" name="saveMonitoramento"
                  onFailure="janelaModal.loadHTML(XMLHttpRequest.responseText);"
                  onSuccess="janelaModal.confirmada();">
        <g:hiddenField name="version" value="${localDtoMonitoramento?.version}" />

        <fieldset class="form">
            <g:render template="monitoramento/formMonitoramento"/>
        </fieldset>

        <fieldset class="buttons">
            <g:submitButton name="update" class="save" value="Gravar" />
            <input type="button" class="cancel" onclick="janelaModal.cancelada();" value="Cancelar" />
        </fieldset>

    </g:formRemote>
</div>

