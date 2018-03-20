<%@ page import="org.apoiasuas.AgendaController; grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Agenda</title>

    <asset:javascript src="moment.js"/>

    <asset:stylesheet src="fullcalendar/fullcalendar.css"/>
    <asset:javascript src="fullcalendar/fullcalendar.js"/>
    <asset:javascript src="fullcalendar/locale-all.js"/>

    <asset:stylesheet src="agenda/calendario.less"/>
    <asset:javascript src="cookie.js"/>
    <asset:javascript src="jquery.timepicker/jquery.timepicker.js"/>
    <asset:stylesheet src="jquery.timepicker/jquery.timepicker.css"/>

    <asset:javascript src="agenda/calendario.js"/>
    <asset:javascript src="cidadao/procurarCidadao.js"/>
</head>

<g:javascript>
    //Variaveis globais geradas aa partir de tags grails (que não podem ser chamadas de um .js), a serem utilizadas em calendario.js
    var configuracaoAgenda = ${raw(configuracao as String)};
    var idUsuarioSistema = "${idUsuarioSistema}";
    var actionCreateCompromissoAutomatico = "${createLink(action:'createCompromissoAutomatico')}";
    var actionCreateCompromisso = "${createLink(action:'createCompromisso')}";
    var actionEditCompromisso = "${createLink(action:'editCompromisso')}";
    var actionUpdateCompromissoHorario = "${createLink(action:'updateCompromissoHorario')}";
    var actionImprimir = "${createLink(action:'imprimir')}";
    var actionObterCompromissos = "${createLink(action:'obterCompromissos')}";
    var actionConfiguracao= "${createLink(action:'configuracao')}";
    var actionCalendario = "${createLink(controller: 'agenda', action: 'calendario')}";
</g:javascript>

<body>

<div style="display: inline; text-align: center;">

    <g:form action="calendario">
        <div style="display: inline-block; margin-top: -10px">
            <div style="float: left; display: inline; margin-top: 3px">
                <input type="button" class="speed-button-config" title="configurações de exibição da agenda" onclick="configuracoes()"/>
                <input type="button" class="speed-button-atualizar" title="recarregar" onclick="atualiza()"/>
                <input type="button" class="speed-button-imprimir" title="imprimir" onclick="imprimirCompromissos();"/>
                <g:select id="selectUsuarioSistema" name="idUsuarioSistema" from="${operadores}" style="max-width: 7em"
                          value="${idUsuarioSistema}"
                          optionKey="id" class="many-to-one" noSelection="['': '(todos)']" onchange="atualiza();" />
            </div>

            <div style="float: left; display: inline; margin-top: 3px">
                <input type="button" class="speed-button-adicionar-evento" title="novo" onclick="createCompromisso();"/>
                <input type="button" class="speed-button-visao-mensal" title="visão mensal" onclick="$('#calendar').fullCalendar('changeView', 'month');"/>
                <input type="button" class="speed-button-visao-semanal" title="visão semanal" onclick="$('#calendar').fullCalendar('changeView', 'agendaWeek');"/>
                <input type="button" class="speed-button-visao-diaria" title="visão diária" onclick="$('#calendar').fullCalendar('changeView', 'agendaDay');"/>
                <input type="button" class="speed-button-visao-lista" title="visão em lista" onclick="$('#calendar').fullCalendar('changeView', 'listWeek');"/>
            </div>
        </div>
    </g:form>
</div>

<div id='calendar'></div>

<div style="display: none" id='divEscolherTipoCompromisso'>
    <input type="button" class="speed-button-atendimento" onclick="janelaModalTipoCompromisso.createAtendimento();"/>
        Atedimento Particularizado com <g:select id="selectUsuarioSistemaOpcoesCompromisso" name="idUsuarioSistema" from="${operadores}"
                          optionKey="id" class="many-to-one" noSelection="['': '']"/>
        <br>
        <input type="button" class="speed-button-compromisso" onclick="janelaModalTipoCompromisso.createOutroCompromisso();"/>
        Outros tipos de compromisso
</div>

</body>
</html>

