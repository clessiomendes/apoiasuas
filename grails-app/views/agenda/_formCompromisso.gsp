<%@ page import="org.apoiasuas.agenda.Compromisso" %>
<%
    org.apoiasuas.agenda.Compromisso localDtoCompromisso = compromissoInstance
%>

<asset:javascript src="especificos/jquery.timepicker.js"/>
<asset:stylesheet src="especificos/jquery.timepicker.css"/>
<asset:javascript src="especificos/datepicker-pt-BR.js"/>

<script>

    $(document).ready(function() {
        //auotmaticamente configura inputs com base na classe css .timepicker-agenda
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

    /**
    * Adiciona um novo select (clonado de selectParticipanteBase) aa tela para selecao de participantes
    * @param valorInicial se fornecido, pre selecionad uma opcao do select
    */
    function criaSelectParticipante(valorInicial) {
        var $selectClonado = $("#selectParticipanteBase").clone();
        var $valoresParticipantes = $("#valoresParticipantes")
        if (valorInicial)
            $selectClonado.val(valorInicial);
        $selectClonado.show();

        //O evento onchange do select deve disparar a criacao de um novo select para que mais usuarios possam ser selecionados
        $selectClonado.change( function() {
            //Ignorar caso o select acionado nao seja o ultimo
            if (this == $valoresParticipantes.find("[name='participantesAux']").last()[0])
                criaSelectParticipante();
        });

        $valoresParticipantes.append($selectClonado);
    }

    //# sourceURL=formCompromisso
</script>

<fieldset class="form">

<f:with bean="${localDtoCompromisso}">
    <g:hiddenField name="tipo" value="${localDtoCompromisso.tipo}"/>
    <g:hiddenField name="habilitado" value="${localDtoCompromisso.habilitado}"/>

    <div class="fieldcontain ${hasErrors(bean: localDtoCompromisso, field: 'descricao', 'error')} ">
        <div class="property-label">
            Descrição<span class="required-indicator">*</span>
        </div>
        <div class="property-value">
            <g:textField name="descricao" size="60" value="${localDtoCompromisso?.descricao}" />
        </div>
    </div>

    <br>

    <div class="fieldcontain ${hasErrors(bean: localDtoCompromisso, field: 'inicio', 'error')} ">
        <div class="property-label">
            de<span class="required-indicator">*</span>
        </div>
        <g:textField name="dataInicio" class="dateMask datepicker" id="dataInicio" size="9" value="${localDtoCompromisso?.inicio?.format("dd/MM/yyyy")}" />
        <g:textField class="timepicker-agenda" name="horaInicio" id="horaInicio" size="4" value="${localDtoCompromisso?.inicio?.format("HH:mm")}" />
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoCompromisso, field: 'fim', 'error')} ">
        <div class="property-label">
            até<span class="required-indicator">*</span>
        </div>
        <g:textField name="dataFim" class="dateMask datepicker" id="dataFim" size="9" value="${localDtoCompromisso?.fim?.format("dd/MM/yyyy")}" />
        <g:textField class="timepicker-agenda" name="horaFim" id="horaFim" size="4" value="${localDtoCompromisso?.fim?.format("HH:mm")}" />
    </div>

    <br>

    <div id="divParticipantes" class="fieldcontain ${hasErrors(bean: localDtoCompromisso, field: 'participantes', 'error')} ">
        <div class="property-label">
            Participantes
        </div>
        <div id="valoresParticipantes" class="property-value">
            <g:select style="display:none;" id="selectParticipanteBase" name="participantesAux" from="${operadores}" optionKey="id" class="many-to-one" noSelection="['': '']"/>
            <script>
                %{--Cria um select para cada valor já preenchido para a colecao de participantes--}%
                <g:each in="${localDtoCompromisso.participantes}" var="usuarioSistema">
                    criaSelectParticipante('${usuarioSistema.id}');
                </g:each>
                %{--Cria um select vazio para ser preenchido com um novo participante--}%
                criaSelectParticipante();
            </script>
        </div>
    </div>
</f:with>

</fieldset>
