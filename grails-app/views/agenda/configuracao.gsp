<script>
    $(document).ready(function() {
        //auotmaticamente configura inputs com base nas classes css timepicker-agenda
        $('.timepicker-agenda').timepicker({
            //disableTextInput: true,
            minTime: '00:00',
            maxTime: '23:30',
            //noneOption: true,
            //forceRoundTime: true,
            step: 30,
            timeFormat: 'H:i' //18:00
        });

    });
</script>

<div id="configuracao" class="content scaffold-create" role="main">
	<g:if test="${flash.message}">
    	<div class="message" role="status">${flash.message}</div>
	</g:if>
%{--
	<g:hasErrors bean="${localDtoCompromisso}">
        <ul class="errors" role="alert">
            <g:eachError bean="${localDtoCompromisso}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
	</g:hasErrors>
--}%

	<g:form>
		<fieldset class="form">
            <div class="fieldcontain">
                <label>Tipos de compromisso</label>
                <g:checkBox style="margin-left: 20px" name="atendimentos" id="checkAtendimentos" checked="${configuracao.atendimentos}"/> atendimentos
                <g:checkBox style="margin-left: 20px" name="outrosCompromissos" id="checkOutrosCompromissos" checked="${configuracao.outrosCompromissos}"/> outros compromissos
            </div>
            <div class="fieldcontain">
                <label>Mostrar na tela</label>
                <span style="margin-left: 20px" >
                    hora inicial: <g:textField class="timepicker-agenda" name="minTime" id="selectMinTime" size="4" value="${configuracao.minTime}" />
                    hora final: <g:textField class="timepicker-agenda" name="maxTime" id="selectMaxTime" size="4" value="${configuracao.maxTime}" />
                </span>
            </div>
            <div class="fieldcontain">
                <label></label>
                <g:checkBox style="margin-left: 20px" name="weekends" id="checkWeekends" checked="${configuracao.weekends}"/> fins de semana
            </div>
            <div class="fieldcontain">
                <label>Semana iniciando em</label>
                <g:select from="${inicioSemana.entrySet()}" name="firstDay" optionKey="key" optionValue="value" value="${configuracao.firstDay}" />
            </div>
		</fieldset>
		<fieldset class="buttons">
	%{--o parametro oculto "data" contem o retorno do post (no nosso caso, um array json contendo o compromisso recem gravado--}%
		<g:submitToRemote url="[action:'saveConfiguracao']" onFailure="janelaModal.loadHTML(XMLHttpRequest.responseText);"
                          onSuccess="janelaModal.confirmada();" class="save" value="Gravar"/>
 		<input type="button" class="close" onclick="janelaModal.cancelada();" value="Fechar" />
	</g:form>
</div>