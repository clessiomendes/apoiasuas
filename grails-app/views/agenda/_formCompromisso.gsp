<%@ page import="org.apoiasuas.agenda.Compromisso" %>
<%
    org.apoiasuas.agenda.Compromisso localDtoCompromisso = compromissoInstance
%>

<asset:javascript src="especificos/jquery.timepicker.js"/>
<asset:stylesheet src="especificos/jquery.timepicker.css"/>

<script>
    //auotmaticamente configura inputs com base nas classes css abaixo:
    //.timepicker-agenda, .timepicker
    $(document).ready(function() {
        $('.timepicker-agenda').timepicker({
            //disableTextInput: true,
            maxTime: '18:00',
            minTime: '08:00',
            //noneOption: true,
            //forceRoundTime: true,
            step: 30,
            timeFormat: 'H:i' //18:00
        });
    });
    //# sourceURL=formCompromisso
</script>

<f:with bean="${localDtoCompromisso}">
    <g:hiddenField name="tipo" value="${localDtoCompromisso.tipo}"/>
    <g:hiddenField name="habilitado" value="${localDtoCompromisso.habilitado}"/>
    <f:field property="descricao" widget-size="40"/>

    <div class="fieldcontain ${hasErrors(bean: localDtoCompromisso, field: 'inicio', 'error')} ">
        <label>
            de<span class="required-indicator">*</span>
        </label>
        <g:textField name="dataInicio" id="dataInicio" size="9" value="${localDtoCompromisso?.inicio?.format("dd/MM/yyyy")}" />
        <g:textField class="timepicker-agenda" name="horaInicio" id="horaInicio" size="4" value="${localDtoCompromisso?.inicio?.format("HH:mm")}" />
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoCompromisso, field: 'fim', 'error')} ">
        <label>
            até<span class="required-indicator">*</span>
        </label>
        <g:textField name="dataFim" id="dataFim" size="9" value="${localDtoCompromisso?.fim?.format("dd/MM/yyyy")}" />
        <g:textField class="timepicker-agenda" name="horaFim" id="horaFim" size="4" value="${localDtoCompromisso?.fim?.format("HH:mm")}" />
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoCompromisso, field: 'responsavel', 'error')} ">
        <label for="responsavel">
            <nobr>Responsável<span class="required-indicator">*</span>
        </nobr>
        </label>
        <g:select id="responsavel" name="responsavel.id" from="${operadores}" optionKey="id" value="${localDtoCompromisso?.responsavel?.id}" class="many-to-one" noSelection="['': '']"/>
    </div>
</f:with>
