<%@ page import="org.apoiasuas.AgendaController; grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Agenda</title>
    <asset:stylesheet src="especificos/calendar/fullcalendar"/>
    <asset:stylesheet src="especificos/calendar/calendario.less"/>
    <asset:javascript src="especificos/calendar/moment.js"/>
    <asset:javascript src="especificos/calendar/fullcalendar.js"/>
    <asset:javascript src="especificos/calendar/locale-all.js"/>
    <asset:javascript src="especificos/cookie.js"/>
    <asset:javascript src="especificos/jquery.timepicker.js"/>
    <asset:stylesheet src="especificos/jquery.timepicker.css"/>

    <asset:javascript src="especificos/procurarCidadao.js"/>
</head>

<g:javascript>
    var janelaModal = new JanelaModalAjax();
    var janelaModalTipoCompromisso = new JanelaModalAjax();
    var configuracaoAgenda = ${raw(configuracao as String)};

        %{--var minhaUrl = "${createLink(action:'gravaConfiguracaoAgenda')}";--}%
%{--
        $.ajax({
            type: 'POST',
            url: "${createLink(action:'gravaConfiguracaoAgenda')}",
            data: JSON.stringify(minhaView),
            contentType: "application/json",
            dataType: 'json'
        });
--}%

    $(document).ready(function() {
        var cookieCalendario = cookie.get("calendario") ? JSON.parse(cookie.get("calendario")) :
                { //Configuração inicial ou default da agenda
                    defaultView: "agendaWeek", start: moment().format()
                };

        //Inicialização do calendário
        $divCalendario = $('#calendar');
        $('#selectUsuarioSistema').val("${idUsuarioSistema}");

        $divCalendario.fullCalendar({
            events: getEvents(configuracaoAgenda.mostrarAtendimentos, configuracaoAgenda.mostrarOutrosCompromissos),
            firstDay: configuracaoAgenda.firstDay,
            minTime: configuracaoAgenda.minTime,
            maxTime: configuracaoAgenda.maxTime,
            aspectRatio: "auto",
            defaultView: cookieCalendario.defaultView,
            slotLabelFormat: "HH:mm",
            locale: 'pt',
            allDaySlot: false,
            weekends: configuracaoAgenda.weekends,
            header: {
                //left: 'prev,next today',
                left: '',
                center: 'title',
                right: '',
                //right: 'month,agendaWeek,listWeek,agendaDay'
            },
            defaultDate: moment(cookieCalendario.start),
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
            eventLimit: false, // allow "more" link when too many events
            //eventRender: verificaConflitos,
            //eventAfterAllRender: verificaConflitos2,
            eventDataTransform: injetaMetodos,
            eventTextColor: "black",
            viewRender: function( view ) {     //Evento acionado após cada mudança na página para gravar em um cookie a visão atual
                cookie.set("calendario", JSON.stringify({
                        defaultView: view.name, start: view.intervalStart.format()
                }));
            }

        })
    });

    /**
      * Função que dispara o processo de criação de um novo compromisso (ou atendimento). Por padrão, abre uma tela primeiro
      * para escolha do tipo de compromisso. Se o operador mantiver o shift apertado no momento do click (e se houver um
      * tecnico previamente escolhido na tela), sera criado um ATENDIMENTO automaticamente, sem a necessidade de informar mais
      * nada. A tela intermediária está definida em divEscolherTipoCompromisso e contém botões que, ao serem clicados, acionam
      * os eventos definidos no corpo desta funcao e adicionados dinamicamente à janela modal, específica para esta seleção.
      */
    function createCompromisso(start, end, jsEvent, view) {
        var inicioStr = start ? start.format() : ""
        var fimStr = end ? end.format() : ""
        var $usuarioSelecionadoModal = $('#selectUsuarioSistemaOpcoesCompromisso')
        var $usuarioSelecionadoTelaMae = $('#selectUsuarioSistema')
        //$usuarioSelecionado.attr("value", $('#selectUsuarioSistema').val());

        //Para conseguirmos copiar o valor de um input select via jQuery, é necessário alterar o HTML que marca o item selecionado, e nao simplesmente só atribur um valor via .val()
        //$usuarioSelecionado.find(":selected").removeAttr('selected'); //remove do HTML valores selecionados anteriormente
        //$usuarioSelecionado.val($('#selectUsuarioSistema').val()) //atribui um novo valor ao select
        //$usuarioSelecionado.find(":selected").attr("selected", "selected"); //transforma o valor atribuido em um codigo HTML que marca o item como selecionado

        if ($usuarioSelecionadoTelaMae.val()) //Força que o tecnico selecionado na tela mae (se houver) sobrescreva o tecnico previamente selecionado na tela modal
            $usuarioSelecionadoModal.val($usuarioSelecionadoTelaMae.val()) //copia valor ao select no topo da tela para o select que aparecera na janela modal

        janelaModalTipoCompromisso.createAtendimento = function() {
            if (! $usuarioSelecionadoModal.val()) {
                alert("Escolha um técnico para o atendimento!")
                return;
            }
            //duração de uma hora (TODO: permitir que esse tempo seja configuravel)
            fimStr = start ? start.add(1,"h").format() : ""
            var urlCreate = "${createLink(action:'createCompromissoAutomatico')}?idUsuarioSistema="+$usuarioSelecionadoModal.val()
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
            $('#calendar').fullCalendar('unselect');
            janelaModalTipoCompromisso.confirmada();
        }

        janelaModalTipoCompromisso.createOutroCompromisso = function() {
            janelaModal.abreJanela({titulo: "Criar novo Compromisso",
                    url: "${createLink(action:'createCompromisso')}?idUsuarioSistema="+$usuarioSelecionadoTelaMae.val()+"&inicio="+inicioStr+"&fim="+fimStr,
                    largura: 800});
            $('#calendar').fullCalendar('unselect');
            janelaModalTipoCompromisso.confirmada();
        }

        if (jsEvent.shiftKey && $usuarioSelecionadoTelaMae.val()) //compromisso automatico - do tipo Atendimento
            janelaModalTipoCompromisso.createAtendimento();
        else
            janelaModalTipoCompromisso.abreJanela({titulo: "Agenda", element: $('#divEscolherTipoCompromisso'), largura: 550});
    }

    function abreCompromisso( event, delta, revertFunc, jsEvent, ui, view ) {
         var tituloJanela = event.tipoAtendimento ? "Alterar Atendimento" : "Alterar Compromisso";
         janelaModal.abreJanela({titulo: tituloJanela, url: "${createLink(action:'editCompromisso')}?idCompromisso="+event.id, largura: 800});
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
            success: function(data) { refreshEvento(data) }
        });
    };

    /*
     * Reflete o compromisso, após gravação, no componente de calendario, para ser exibido ao operador
     */
    function refreshEvento(compromisso){
        var $calendar = $('#calendar')
        $calendar.fullCalendar('removeEvents', compromisso.id)
        var $selectOperadores = $('#selectUsuarioSistema')
        if ($selectOperadores.val() == '' || $selectOperadores.val() == compromisso.idResponsavel)
            $calendar.fullCalendar('renderEvent', compromisso);
    }

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
        refreshEvento(data);
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
                data: { idUsuarioSistema: $('#selectUsuarioSistema').val(),
                        mostrarAtendimentos: configuracaoAgenda.atendimentos,
                        mostrarOutrosCompromissos: configuracaoAgenda.outrosCompromissos
                },
                error: function() {
                    alert('Erro obtendo agenda do servidor');
                }
        }
    }

    function configuracoes() {
         janelaModal.abreJanela({titulo: "Configurações de exibição da agenda", largura: 600, url: "${createLink(action:'configuracao')}",
                refreshFunction: function() {
                    //atualiza a exibição do calendário
                    $selectUsuarioSistema = $("#selectUsuarioSistema");
                    var destino = "${createLink(controller: 'agenda', action: 'calendario')}"
                    if ($selectUsuarioSistema.val())
                        destino += '?idUsuarioSistema='+$selectUsuarioSistema.val();
                    window.location = destino;
                }
         });
    }


</g:javascript>

<body>

<div style="display: inline;">
    %{--<div id="divAgendandoAtendimento" style="text-align: center; margin: 5px">Agendando atendimento para <b>CRISTIANE DA SILVA (CAD 513)</b></div>--}%
<g:form action="calendario">
        <div style="float: left; display: inline">
            <input type="button" class="speed-button-voltar" title="período anterior" onclick="$('#calendar').fullCalendar('prev');"/>
            <input type="button" class="speed-button-config" title="configurações de exibição da agenda" onclick="configuracoes()"/>
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

