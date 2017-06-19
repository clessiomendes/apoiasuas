<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Agenda</title>
    <asset:stylesheet src="especificos/calendar/fullcalendar"/>
    <asset:stylesheet src="especificos/calendar/calendario.less"/>
    <asset:javascript src="especificos/calendar/moment.js"/>
    <asset:javascript src="especificos/calendar/fullcalendar.js"/>
    <asset:javascript src="especificos/calendar/locale-all.js"/>
</head>

<g:javascript>
    var janelaModal = new JanelaModalAjax(null, 800);

    $(document).ready(function() {
        //Inicialização do calendário
        $divCalendario = $('#calendar');
        %{--selectUsuarioSistema = "${idUsuarioSistema}";--}%
        $divCalendario.fullCalendar({
            events: getEvents(),
            firstDay: 3/*quarta-feira*/, //TODO tornar um parâmetro configurável
            minTime: "08:00:00", //TODO tornar um parâmetro configurável
            maxTime: "18:00:00", //TODO tornar um parâmetro configurável
            aspectRatio: "auto",
            defaultView: "agendaWeek",
            slotLabelFormat: "HH:mm",
            locale: 'pt',
            allDaySlot: false,
            weekends: false, //TODO tornar um parâmetro configurável
            //dayClick: function() {            },
            //header: false,
            header: {
                //left: 'prev,next today',
                left: '',
                center: 'title',
                right: '',
                //right: 'month,agendaWeek,listWeek,agendaDay'
            },
            //defaultDate: '2017-05-12',
            navLinks: true, // can click day/week names to navigate views
            selectable: true,
            selectHelper: true,
            //evento acionado para criação de novos compromissos
            select: createCompromisso,
            //evento acionado quando um evento é movido de lugar no calendario, sinalizando uma alteração
            eventResize: updateHorarioCompromisso,
            //evento acionado quando um evento é redimencionado no calendario, sinalizando uma alteração
            eventDrop: updateHorarioCompromisso,
            //clique num evento
            eventClick: abreCompromisso,
            editable: true,
            eventRender: function(event, element) {
                $(element).attr("title", event.title);
            },
            eventLimit: true, // allow "more" link when too many events
            //eventRender: verificaConflitos,
            //eventAfterAllRender: verificaConflitos2,
            eventDataTransform: injetaMetodos,
            eventTextColor: "black"
        })
    });

    /**
     * Função acionada pelo evento on select do fullCalendar que será usado para disparar a criação de um novo compromisso
     * que pode ser automatico (se o shift estiver pressionado) ou via janela popup.
     */
    function createCompromisso(start, end, jsEvent, view) {
        var inicioStr = start ? start.format() : ""
        var fimStr = end ? end.format() : ""
        var usuarioSistemaSelecionado = $('#selectUsuarioSistema').val()

        if (jsEvent.shiftKey) { //compromisso automatico - do tipo Atendimento
            if (! usuarioSistemaSelecionado) {
                alert("É necessário escolher um técnico antes de definir um horário de atendimento");
                $divCalendario.fullCalendar('unselect');
                return;
            }
            //duração de uma hora (TODO: permitir que esse tempo seja configuravel)
            fimStr = start ? start.add(1,"h").format() : ""
            var urlCreate = "${createLink(action:'createCompromissoAutomatico')}?idUsuarioSistema="+usuarioSistemaSelecionado
                        +"&inicio="+inicioStr+"&fim="+fimStr;
            //Executa a chamada ajax para autalizar o compromisso no banco de dados
            $.ajax({
                url: urlCreate,
                dataType: "json",
                error: function( jqXHR, textStatus, errorThrown ) {
                    //TODO porque a janela modal não está conseguindo exibir o conteúdo HTML retornado com o erro do request?
                    //janelaModal.abreJanela("Erro gravando alteração do compromisso ["+event.title+"] no banco de dados : "+textStatus+", "+errorThrown,
                    //    null, jqXHR.responseText);
                    alert("Erro criando atendimento no banco de dados : "+textStatus+", "+errorThrown);
                },
                success: function(data) {
                    $divCalendario.fullCalendar('renderEvent', data);
                }
            });
        } else { //cria compromisso genérico, abrindo a tela para entrada de dados do operador
            janelaModal.abreJanela("Criar novo Compromisso", "${createLink(action:'createCompromisso')}?idUsuarioSistema="+
                    usuarioSistemaSelecionado+"&inicio="+inicioStr+"&fim="+fimStr);
        }
        $('#calendar').fullCalendar('unselect');
    };

%{--
    function criaCompromissoGenerico() {
        janelaModal.abreJanela("Criar novo Compromisso", "${createLink(action:'createCompromisso',
                params: [idUsuarioSistema: idUsuarioSistema])}&inicio="+inicioStr+"&fim="+fimStr);
    }
--}%

    function abreCompromisso( event, delta, revertFunc, jsEvent, ui, view ) {
        var tituloJanela = event.tipoAtendimento ? "Alterar Atendimento" : "Alterar Compromisso";
 /*       var url = "${createLink(action:'editCompromisso')}?idCompromisso="+event.id
        if (event.tipoAtendimento) {
            alert("idCidadao"+idCidadao+" idFamilia"+idFamilia)
            if (idCidadao)
                url += "&idCidadao="+idCidadao
            if (idFamilia)
                url += "&idFamilia="+idFamilia
        }
        janelaModal.abreJanela(tituloJanela, url);
*/
        janelaModal.abreJanela(tituloJanela, "${createLink(action:'editCompromisso')}?idCompromisso="+event.id);
    }

    function updateHorarioCompromisso( event, delta, revertFunc, jsEvent, ui, view) {
        var strStart = event.start ? event.start.format() : "";
        var strEnd = event.end ? event.end.format() : "";
        var urlUpdate = "${createLink(action:'updateCompromissoHorario')}?idCompromisso="+event.id
                    +"&start="+strStart+"&end="+strEnd;
        //Executa a chamada ajax para autalizar o compromisso no banco de dados
        $.ajax({
            url: urlUpdate,
            error: function( jqXHR, textStatus, errorThrown ) {
                //TODO porque a janela modal não está conseguindo exibir o conteúdo HTML retornado com o erro do request?
                //janelaModal.abreJanela("Erro gravando alteração do compromisso ["+event.title+"] no banco de dados : "+textStatus+", "+errorThrown,
                //    null, jqXHR.responseText);
                alert("Erro gravando alteração do compromisso ["+event.title+"] no banco de dados : "+textStatus+", "+errorThrown);
            },
            //como sinalizar sucesso?
            success: function(data) { }
        });
    };

    function imprimirCompromissos() {
        //janelaModal.abreJanela("escolha...",null,$('#divEscolherTipoCompromisso').html());

        var view = $('#calendar').fullCalendar('getView');
        //alert(view.name + " " + view.title + " " + view.start.format() + " " + view.end.format() + " ")
        var url = "${createLink(action:'imprimir')}?idUsuarioSistema="+$('#selectUsuarioSistema').val()+"&start="+view.start.format()+"&end="+view.end.format();
        window.open(url);
    }

    /*
     * Reflete o compromisso, após gravação, no componente de calendario, para ser exibido ao operador
     */
    function sucessoSave(data) {
        janelaModal.confirmada(); //fecha a janela modal
        var $calendar = $('#calendar')
        $calendar.fullCalendar('removeEvents', data.id)
        var $selectOperadores = $('#selectUsuarioSistema')
/*
        $('#divAgendandoAtendimento').slideUp();
        if (data.nome) {
            idCidadao = null;
            idFamilia = null;
        }
*/
        if ($selectOperadores.val() == '' || $selectOperadores.val() == data.idResponsavel)
            $calendar.fullCalendar('renderEvent', data);
    }

    /*
     * Reflete a exclusao do compromisso no componente de calendario, para ser ocultado do operador
     */
    function sucessoDelete(data) {
        janelaModal.confirmada(); //fecha a janela modal
        $('#calendar').fullCalendar('removeEvents', data.id)
    }

/*
    function verificaConflitos(compromisso1, element, view) {
        console.log("testando evento "+compromisso1.toString())
        $('#calendar').fullCalendar('clientEvents').forEach(function(compromisso2) {
            if (compromisso1.id != compromisso2.id && compromisso1.idResponsavel && compromisso2.idResponsavel
                            && compromisso1.idResponsavel == compromisso2.idResponsavel) {
                if (compromisso1.start.isBefore(compromisso2.end) && compromisso1.end.isAfter(compromisso2.start)) {
                    //compromisso1.color = "red";
                    //compromisso2.color = "red";
                    console.log("Match: "+compromisso1.toString()+" e "+compromisso2.toString())
                    //$('#calendar').fullCalendar('updateEvents', [compromisso1, compromisso2])
                    element.color = "red";
                }
            }
        });
    }

    function verificaConflitos2(view) {
        //console.log("testando evento "+compromisso1.toString())
		$('#calendar').fullCalendar('clientEvents').forEach(function(compromisso1) {
            $('#calendar').fullCalendar('clientEvents').forEach(function(compromisso2) {
                if (compromisso1.id != compromisso2.id && compromisso1.idResponsavel && compromisso2.idResponsavel
                                && compromisso1.idResponsavel == compromisso2.idResponsavel) {
                    if (compromisso1.start.isBefore(compromisso2.end) && compromisso1.end.isAfter(compromisso2.start)) {
                        compromisso1.color = "red";
                        compromisso2.color = "red";
                        console.log("Match: "+compromisso1.toString()+" e "+compromisso2.toString())
		                //$('#calendar').fullCalendar('updateEvents', [compromisso1, compromisso2])
                        //element.color = "red";
                    }
                }
            });
        });
    }
*/

    function injetaMetodos(eventData ) {
        eventData.toString = function() {
            return this.id+", "+this.start.format("D/M")+"["+this.start.format("H:mm")+"-"+this.end.format("H:mm")+"], "+this.title;
        }
        return eventData;
    }

    function atualiza() {
        $('#calendar').fullCalendar('removeEventSources' )
        var source = getEvents();
        $('#calendar').fullCalendar('addEventSource', source )
    }

    /**
     * Monta request para obter compromissos do servidor
     */
    function getEvents() {
        var urlObterCompromissos = "${createLink(action:'obterCompromissos')}";
        return {
                url: urlObterCompromissos,
                type: 'POST',
                data: { idUsuarioSistema: $('#selectUsuarioSistema').val() },
                error: function() {
                    alert('Erro obtendo agenda do servidor');
                }
            }
    }

</g:javascript>

<body>

<div style="display: inline;">
    %{--<div id="divAgendandoAtendimento" style="text-align: center; margin: 5px">Agendando atendimento para <b>CRISTIANE DA SILVA (CAD 513)</b></div>--}%
<g:form action="calendario">
        <div style="float: left; display: inline">
            <input type="button" class="speed-button-voltar" title="período anterior" onclick="$('#calendar').fullCalendar('prev');"/>
            <input type="button" class="speed-button-atualizar" title="recarregar" onclick="atualiza()"/>
            <input type="button" class="speed-button-imprimir" title="imprimir" onclick="imprimirCompromissos();"/>
            Responsável <g:select id="selectUsuarioSistema" name="idUsuarioSistema" from="${operadores}"
                      optionKey="id" class="many-to-one" noSelection="['': '(todos)']" onchange="atualiza();" />
        </div>

        <div style="float: right; display: inline">
            <input type="button" class="speed-button-visao-mensal" title="visão mensal" onclick="$('#calendar').fullCalendar('changeView', 'month');"/>
            <input type="button" class="speed-button-visao-semanal" title="visão semanal" onclick="$('#calendar').fullCalendar('changeView', 'agendaWeek');"/>
            <input type="button" class="speed-button-visao-diaria" title="visão diária" onclick="$('#calendar').fullCalendar('changeView', 'agendaDay');"/>
            <input type="button" class="speed-button-visao-lista" title="visão em lista" onclick="$('#calendar').fullCalendar('changeView', 'listMonth');"/>
            <input type="button" class="speed-button-avancar" title="próximo período" onclick="$('#calendar').fullCalendar('next')"/>
        </div>
</g:form>
</div>

<div id='calendar'></div>

%{--
<div id='divEscolherTipoCompromisso'>Esolhendo...
    <input type="button" value="atendimento particularizado" onclick="alert('parti');"/>
    <input type="button" value="outros tipos de compromisso" onclick="alert('parti');"/>
</div>
--}%

</body>
</html>