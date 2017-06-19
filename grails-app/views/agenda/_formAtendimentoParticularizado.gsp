<%@ page import="org.apoiasuas.cidadao.Familia; org.apoiasuas.cidadao.Cidadao; org.apoiasuas.redeSocioAssistencial.AtendimentoParticularizado" %>
<%
    org.apoiasuas.redeSocioAssistencial.AtendimentoParticularizado localDtoAtendimento = atentimentoParticularizadoInstance;
    org.apoiasuas.cidadao.Cidadao localCidadaoCandidato = cidadaoCandidato;
    org.apoiasuas.cidadao.Familia localFamiliaCandidata = familiaCandidata;
%>

<asset:javascript src="especificos/jquery.timepicker.js"/>
<asset:stylesheet src="especificos/jquery.timepicker.css"/>

<script>
    var idCidadaoCandidatoSelecao = "${localCidadaoCandidato?.id}"
    var nomeCidadaoCandidatoSelecao = "${localCidadaoCandidato?.nomeCompleto}"
    var idFamiliaCandidataSelecao = "${localFamiliaCandidata?.id}"
    var cadFamiliaCandidataSelecao = "${localFamiliaCandidata?.cad}"

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
        $('#checkSemTelefone').change();
    });

    function chekTelefoneChange(check) {
        var $inputTelefoneContato = $('#inputTelefoneContato');
        if (check.checked)
            $inputTelefoneContato.val('').prop('disabled', true)
        else
            $inputTelefoneContato.prop('disabled', false)
    }

    /**
     * Para liberar um horário preenchido previamente, uma serie de medidas precisam ser tomadas em relacao aos componentes visuais (e escondidos):
     * remover o hidden e o span indicativo da familia agendada, caso exista; remover dados de telefone;
     * alterar a situação para LIVRE e remover informação de comparecimento
     */
    function liberarClick() {
//        idCidadaoCandidatoSelecao = null
//        nomeCidadaoCandidatoSelecao = null
//        idFamiliaCandidataSelecao = null
//        cadFamiliaCandidataSelecao = null
        $('#inputNomeCidadao').val('');

        $('#hiddenIdFamilia').val('');
        $('#spanCad').hide();

        $('#inputTelefoneContato').val('');
        $('#checkSemTelefone').prop('checked', false).change();

        $('#spanSituacao').text("${AtendimentoParticularizado.LIVRE}").css("background-color","${AtendimentoParticularizado.VERDE}");
        $('#hiddenCompareceu').val('');

        $('#btnLimpar').hide();
    }

    function agendarFamiliaCandidata() {
        var $inputNomeCidadao = $('#inputNomeCidadao')
        //Pede confirmação se o horario estiver ocupado
        if ($inputNomeCidadao.val())
            if (! confirm("Este horário já está preenchido para "+$('#inputNomeCidadao').val()+
                        ".\nDeseja substituí-lo por um novo agendamento?"))
                return false;

        $inputNomeCidadao.val('');
        $('#inputTelefoneContato').val('');
        $('#checkSemTelefone').prop('checked', false).change();
        if (idFamiliaCandidataSelecao)
            $('#hiddenIdFamilia').val(idFamiliaCandidataSelecao);
        if (cadFamiliaCandidataSelecao)
            $('#spanCad').text("cad "+cadFamiliaCandidataSelecao);
        if (nomeCidadaoCandidatoSelecao)
            $inputNomeCidadao.val(nomeCidadaoCandidatoSelecao);
        $('#divAgendandoAtendimento').slideUp();
    }
    //# sourceURL=formAtendimentoParticularizado
</script>

<f:with bean="${localDtoAtendimento}">

    <g:hiddenField id="hiddenIdFamilia" name="familia.id" value="${localDtoAtendimento?.familia?.id}" />
    <g:hiddenField id="hiddenCompareceu" name="compareceu" value="${localDtoAtendimento?.compareceu}" />

    %{--Mostrar apenas quando houver uma familia candidata (na sessao)--}%
    <g:if test="${localFamiliaCandidata}">
        <div id="divAgendandoAtendimento" class="fieldcontain">
            <label></label>
            <input type="button" class="atendimento" onclick="agendarFamiliaCandidata()" title="Clique para agendar esta família neste horário"
                   value="Agendar ${localCidadaoCandidato ? localCidadaoCandidato.nomeCompleto + " -" : ""} cad ${localFamiliaCandidata?.cad}"/>
        </div>
    </g:if>

    <f:field property="nomeCidadao">
        <input type="text" id="inputNomeCidadao" name="nomeCidadao" size="40" maxlength="255" value="${localDtoAtendimento.nomeCidadao}"/>
        <span id="spanCad">${localDtoAtendimento.familia ? "cad "+localDtoAtendimento.familia.cad : ""}</span>
    </f:field>

%{--
    boolean temTelefone = localDtoAtendimento.telefoneContato != null;
    if (localDtoAtendimento.nomeCidadao && ! localDtoAtendimento.telefoneContato)
        temTelefone = false
    <g:set var="temTelefone" value="${! localDtoAtendimento.nomeCidadao || localDtoAtendimento.telefoneContato}"/>
--}%
    <div class="fieldcontain ${hasErrors(bean: localDtoAtendimento, field: 'telefoneContao', 'error')} ">
        <label>Telefone</label>
        <g:textField name="telefoneContato" id="inputTelefoneContato" size="11" value="${localDtoAtendimento.telefoneContato}" />
        <g:checkBox name="semTelefone" id="checkSemTelefone" checked="${localDtoAtendimento.semTelefone}" onchange="chekTelefoneChange(this);"/> sem telefone
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoAtendimento, field: 'tecnico', 'error')} ">
        <label for="tecnico.id">
            <nobr>Responsável<span class="required-indicator">*</span>
            </nobr>
        </label>
        <g:select id="tecnico" name="tecnico.id" from="${operadores}" optionKey="id" value="${localDtoAtendimento?.tecnico?.id}" class="many-to-one" noSelection="['': '']"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoAtendimento, field: 'dataHora', 'error')} ">
        <label>
            Horário<span class="required-indicator">*</span>
        </label>
        <g:textField name="data" id="data" size="9" value="${localDtoAtendimento?.dataHora?.format("dd/MM/yyyy")}" />
        <g:textField class="timepicker-agenda" name="hora" id="hora" size="4" value="${localDtoAtendimento?.dataHora?.format("HH:mm")}" />
    </div>

    <div class="fieldcontain">
        <label></label>
        <span id="spanSituacao" style="padding: 0.5em 0.7em; display: inline-block; border-radius: 0.3em; background-color: ${localDtoAtendimento.getCor()}">
            ${localDtoAtendimento.getTooltip()}
            <g:if test="${localDtoAtendimento.nomeCidadao}">
                <input type="button" id="btnLiberar" class="speed-button-liberar" onclick="liberarClick(); return false;" title="Liberar horário"/>
            </g:if>
        </span>
    </div>

</f:with>
