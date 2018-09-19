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

	<g:render template="/mensagensPosGravacao" model="[bean: localDtoAtendimento]"/>

	<g:form>
        <g:hiddenField name="version" value="${localDtoAtendimento?.version}" />
		<fieldset class="form">
            <g:render template="formAtendimentoParticularizado"/>
		</fieldset>
		<fieldset class="buttons">
		%{--o parametro oculto "data" contem o retorno do post (no nosso caso, um array json contendo o compromisso recem gravado--}%
		<g:submitToRemote url="[action:'saveAtendimento',id:localDtoAtendimento?.id]" onSuccess="sucessoSave(data);" onFailure="janelaModal.loadHTML(XMLHttpRequest.responseText);"
						  class="save" value="Gravar" elementId="btnGravar"/>
		<g:submitToRemote url="[action:'compareceuAtendimento',id:localDtoAtendimento?.id]" onSuccess="sucessoSave(data);" onFailure="janelaModal.loadHTML(XMLHttpRequest.responseText);"
						  class="check" value="Compareceu" elementId="btnCompareceu"/>
		<g:submitToRemote url="[action:'naoCompareceuAtendimento',id:localDtoAtendimento?.id]" onSuccess="sucessoSave(data);" onFailure="janelaModal.loadHTML(XMLHttpRequest.responseText);"
						  class="cancel" value="Não Compareceu"  elementId="btnNaoCompareceu"/>

		%{--O botão btnApagarAtendimento, do tipo submitToRemote, está invisível para ser substituido na tela pelo botão abaixo, que chama uma função javascript
		 antes de fazer o submit do form--}%
        <input type="button" class="delete" value="Apagar" onclick="apagarAtendimento();" id="btnApagarVisivel"/>
  		<g:submitToRemote url="[action:'deleteAtendimento',id:localDtoAtendimento?.id]" onSuccess="sucessoDelete(data);" onFailure="janelaModal.loadHTML(XMLHttpRequest.responseText);"
                          elementId="btnApagarAtendimento"/>

		<input type="button" class="close" onclick="janelaModal.cancelada();" value="Fechar"  elementId="btnFechar"/>
	</g:form>
</div>
