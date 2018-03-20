<%@ page import="org.apoiasuas.agenda.Compromisso; org.apoiasuas.cidadao.Familia; org.apoiasuas.cidadao.Cidadao; org.apoiasuas.redeSocioAssistencial.AtendimentoParticularizado" %>
<%
    org.apoiasuas.redeSocioAssistencial.AtendimentoParticularizado localDtoAtendimento = atentimentoParticularizadoInstance;
    org.apoiasuas.cidadao.Cidadao localCidadaoCandidato = cidadaoCandidato;
    org.apoiasuas.cidadao.Familia localFamiliaCandidata = familiaCandidata;
%>

<asset:javascript src="datepicker-pt-BR.js"/>

<script>
    var janelaModalProcurarCidadao = new JanelaModalAjax();

    var idCidadaoCandidatoSelecao = "${localCidadaoCandidato?.id}"
    var nomeCidadaoCandidatoSelecao = "${localCidadaoCandidato?.nomeCompleto}"
    var idFamiliaCandidataSelecao = "${localFamiliaCandidata?.id}"
    var cadFamiliaCandidataSelecao = "${localFamiliaCandidata?.cad}"

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

        $('#checkSemTelefone').change();
    });

    function checkTelefoneChange(check) {
        var $inputTelefoneContato = $('#inputTelefoneContato');
        if (check.checked)
            $inputTelefoneContato.val('').prop('disabled', true)
        else
            $inputTelefoneContato.prop('disabled', false)
    }

    function checkCadChange(check) {
        var $inputTelefoneContato = $('#inputTelefoneContato');
        if (check.checked) {
            $('#hiddenIdFamilia').val('');
            $('#spanCad').text('');
        }
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
        $('#spanCad').text('');
        $('#checkSemCad').prop('checked', false).change();

        $('#inputTelefoneContato').val('');
        $('#checkSemTelefone').prop('checked', false).change();

        %{--$('#spanSituacao').text("${AtendimentoParticularizado.LIVRE}").css("background-color","${org.apoiasuas.agenda.Compromisso.CSS_VERDE}");--}%
        $('#spanSituacao').text("${AtendimentoParticularizado.LIVRE}").removeClass("${org.apoiasuas.agenda.Compromisso.CSS_AMARELO}");
        $('#spanSituacao').text("${AtendimentoParticularizado.LIVRE}").addClass("${org.apoiasuas.agenda.Compromisso.CSS_VERDE}");
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
            $('#spanCad').text(" "+cadFamiliaCandidataSelecao+" ");
        if (nomeCidadaoCandidatoSelecao)
            $inputNomeCidadao.val(nomeCidadaoCandidatoSelecao);
        $('#divAgendandoAtendimento').slideUp();
    }

    function selecionaPopup(event, result) {
        event.preventDefault();
//        alert('selectFamilia '+JSON.stringify(result));
        if (result.idFamilia)
            $('#hiddenIdFamilia').val(result.idFamilia);
        if (result.cad)
            $('#spanCad').text(" "+result.cad+" ");
        $('#checkSemCad').prop('checked', false);
        janelaModalProcurarCidadao.confirmada();
        return false;
    }

    function popupProcurarCidadao() {
        var url = "${createLink(controller: 'cidadao', action:'procurarCidadaoPopup')}";
        if ($('#inputNomeCidadao').val())
            url += "?nomeOuCad="+$('#inputNomeCidadao').val();
        janelaModalProcurarCidadao.abreJanela({ titulo: "Pesquisa no Banco de Dados", largura: 900, url: url })
    }
    //# sourceURL=formAtendimentoParticularizado
</script>

<g:hiddenField id="hiddenIdFamilia" name="familia.id" value="${localDtoAtendimento?.familia?.id}" />
<g:hiddenField id="hiddenCompareceu" name="compareceu" value="${localDtoAtendimento?.compareceu}" />

%{--Mostrar apenas quando houver uma familia candidata (na sessao)--}%
<g:if test="${localFamiliaCandidata}">
    <div id="divAgendandoAtendimento" class="fieldcontain">
        <label></label>
        <input type="button" class="atendimento" onclick="agendarFamiliaCandidata()" title="Clique para agendar esta família neste horário"
               value="Agendar ${localCidadaoCandidato ? localCidadaoCandidato.nomeCompleto + " -" : ""} cad ${localFamiliaCandidata?.cad}"/>
    </div>

    <br>
</g:if>

<div class="tamanho-nomes fieldcontain ${hasErrors(bean: localDtoAtendimento, field: 'nomeCidadao', 'error')} ">
    <label>Nome</label>
    <g:textField name="nomeCidadao" maxlength="255" id="inputNomeCidadao" value="${localDtoAtendimento.nomeCidadao}" />
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoAtendimento, field: 'familia', 'error')} ">
    <label>Cad</label>
    <span id="spanCad">${localDtoAtendimento.familia?.cad}</span>
    <input type="button" class="search field-button" value="Procurar" onclick='popupProcurarCidadao();'/>
    &nbsp;&nbsp;<g:checkBox name="familiaSemCadastro" id="checkSemCad" checked="${localDtoAtendimento.familiaSemCadastro}" onchange="checkCadChange(this);"/>
    <span>família sem cadastro</span>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoAtendimento, field: 'telefoneContato', 'error')} ">
    <label>Telefone</label>
    <g:textField name="telefoneContato" id="inputTelefoneContato" size="11" value="${localDtoAtendimento.telefoneContato}" />
    &nbsp;&nbsp;<g:checkBox name="semTelefone" id="checkSemTelefone" checked="${localDtoAtendimento.semTelefone}" onchange="checkTelefoneChange(this);"/>
    <span>sem telefone</span>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoAtendimento, field: 'tecnico', 'error')} ">
    <label for="tecnico.id">
        <nobr>Responsável<span class="required-indicator">*</span>
        </nobr>
    </label>
    <g:select style="max-width: 10em" id="tecnico" name="tecnico.id" from="${operadores}" optionKey="id"
              value="${localDtoAtendimento?.tecnico?.id}" class="many-to-one" noSelection="['': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoAtendimento, field: 'dataHora', 'error')} ">
    <label>
        Horário<span class="required-indicator">*</span>
    </label>
    <g:textField name="data" class="dateMask datepicker" id="data" size="9" value="${localDtoAtendimento?.dataHora?.format("dd/MM/yyyy")}" />
    <g:textField class="timepicker-agenda" name="hora" id="hora" size="4" value="${localDtoAtendimento?.dataHora?.format("HH:mm")}" />
</div>

<g:if test="${localDtoAtendimento?.id}">
    <div class="fieldcontain">
        <div class="property-label">Nº Protocolo</div>
        <div class="property-value">${localDtoAtendimento.id}</div>
    </div>
</g:if>

<br>

<div class="fieldcontain">
    <label></label>
    <span id="spanSituacao" class="${localDtoAtendimento.getCor()}" style="padding: 0.5em 0.7em; display: inline-block; border-radius: 0.3em;">
        ${localDtoAtendimento.getTooltip()}
        <g:if test="${localDtoAtendimento.horarioPreenchido}">
            <input type="button" id="btnLiberar" class="speed-button-liberar" onclick="liberarClick(); return false;" title="Liberar horário"/>
        </g:if>
    </span>
</div>

