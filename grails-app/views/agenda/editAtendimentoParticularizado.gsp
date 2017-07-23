<%@ page import="org.apoiasuas.redeSocioAssistencial.AtendimentoParticularizado" %>
<%
	org.apoiasuas.redeSocioAssistencial.AtendimentoParticularizado localDtoAtendimento = atentimentoParticularizadoInstance;
%>
<style>
#btnApagarAtendimento {
	display: none;
}
</style>

<script>
    function apagarAtendimento() {
        var mensagem
        if ($("#inputNomeCidadao").val())
            mensagem = ">> ATENÇÃO! Já existe um atendimento agendado neste horário <<\nApagar assim mesmo?"
        else
            mensagem = "Confirma remoção deste horário da agenda?"
        if (confirm(mensagem))
            btnApagarAtendimento.click();
    }
//# sourceURL=editAtendimentoParticularizado
</script>

<div id="edit-atendimento" class="content scaffold-create" role="main">
	<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
	</g:if>

	<g:hasErrors bean="${localDtoAtendimento}">
        <ul class="errors" role="alert">
			<g:eachError bean="${localDtoAtendimento}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>

	<g:form>
        <g:hiddenField name="version" value="${localDtoAtendimento?.version}" />
		<fieldset class="form">
            <g:render template="formAtendimentoParticularizado"/>
		</fieldset>
		<fieldset class="buttons">
		%{--o parametro oculto "data" contem o retorno do post (no nosso caso, um array json contendo o compromisso recem gravado--}%
		<g:submitToRemote url="[action:'saveAtendimento',id:localDtoAtendimento?.id]" onSuccess="sucessoSave(data);" onFailure="janelaModal.loadHTML(XMLHttpRequest.responseText);"
						  class="save" value="Gravar"/>
		<g:submitToRemote url="[action:'compareceuAtendimento',id:localDtoAtendimento?.id]" onSuccess="sucessoSave(data);" onFailure="janelaModal.loadHTML(XMLHttpRequest.responseText);"
						  class="check" value="Compareceu"/>
		<g:submitToRemote url="[action:'naoCompareceuAtendimento',id:localDtoAtendimento?.id]" onSuccess="sucessoSave(data);" onFailure="janelaModal.loadHTML(XMLHttpRequest.responseText);"
						  class="cancel" value="Não Compareceu"/>

		%{--O botão btnApagarAtendimento, do tipo submitToRemote, está invisível para ser substituido na tela pelo botão abaixo, que chama uma função javascript
		 antes de fazer o submit do form--}%
        <input type="button" class="delete" value="Apagar" onclick="apagarAtendimento();"/>
  		<g:submitToRemote url="[action:'deleteAtendimento',id:localDtoAtendimento?.id]" onSuccess="sucessoDelete(data);" onFailure="janelaModal.loadHTML(XMLHttpRequest.responseText);"
                          id="btnApagarAtendimento"/>

		<input type="button" class="close" onclick="janelaModal.cancelada();" value="Fechar" />
	</g:form>
</div>
