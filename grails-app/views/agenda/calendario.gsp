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

    %{--<asset:javascript src="especificos/loadingoverlay.js"/>--}%
    <asset:javascript src="especificos/procurarCidadao.js"/>
</head>

<g:javascript>
    var janelaModal = new JanelaModalAjax();
    var janelaModalTipoCompromisso = new JanelaModalAjax();
    var configuracaoAgenda = ${raw(configuracao as String)};
    var compromissoAntesMudancaHorario = null;
    var miliInicial = Date.now()

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
            events: getEvents(),
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
                left: 'prev',
                center: 'title',
                right: 'next',
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
            //evento acionado ANTES de mover um evento pelo calendario
            eventDragStart: guardaEstadoCompromisso,
            //evento acionado ANTES de redimencionar um evento no calendario
            eventResizeStart: guardaEstadoCompromisso,
            //clique num evento
            eventClick: abreCompromisso,
            editable: true,
            eventRender: function(event, element) {
                $(element).attr("title", event.title);
            },
            eventLimit: false, // allow "more" link when too many events
            //eventAfterRender: verificaConflitos,
            //eventAfterAllRender: verificaConflitos2,
            eventDataTransform: injetaMetodos,
            eventTextColor: "black",
            viewRender: function( view ) {     //Evento acionado após cada mudança na página para gravar em um cookie a visão atual
                cookie.set("calendario", JSON.stringify({
                        defaultView: view.name, start: view.intervalStart.format()
                }));
                $('#titulo').text($divCalendario.fullCalendar('getView').title);
                //alert($divCalendario.fullCalendar('getView').title);
            },
            eventRenderWait: 100,
            loading: loading,
            eventAfterAllRender: eventAfterAllRender
        })

        //$('.fc-toolbar').css('display','none')
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
                    verificaConflitos(data);
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

    /**
    * Chamado tanto no resize quanto no move do compromisso, para atualizar a nova informacao no banco de dados
    */
    function updateHorarioCompromisso( event, delta, revertFunc, jsEvent, ui, view) {
/*
        if (! confirm("Confirma alteração de '"+event.title+"' ?")) {
            revertFunc();
            return;
        }
*/
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
            success: function(data) {
                refreshEvento(data);
                //Snackbar.show({text: 'Horário de compromisso alterado'});
                Snackbar.show({pos: 'bottom-center', duration: 0, customClass: 'snackbar-agenda',
                    showSecondButton: true, secondButtonText: 'desfazer', secondButtonTextColor: 'red',
                    backgroundColor: '#e3f3ff', textColor: '#006dba',
                    actionTextColor: '#006dba', actionText: 'X',
                    onSecondButtonClick: desfazUpdateHorarioCompromisso,
                    text: 'Horário alterado: '+event.start.format('D/M H:mm')+' às '+event.end.format('H:mm')});
            }
        });
    };

    /**
     * Guarda as informações do compromisso ANTES dele ser movido ou redimencionado. (para ser possível desfazer a operação)
     * IMPORTANTE: Somente as informações do último evento são guardadas.
     */
    function guardaEstadoCompromisso(event, jsEvent, ui, view) {
        compromissoAntesMudancaHorario = Object.assign({}, event);
    }

    /**
    * Caso o operador clique em "desfazer", após uma mudança de horário do compromisso diretamente no calendário (drag/move)
    * @param element O div snackbar de onde o click se originou
    */
    function desfazUpdateHorarioCompromisso(element) {
        //alert('voltando para '+compromissoAntesMudancaHorario.start.format('D/M H:mm'));
        var strStart = compromissoAntesMudancaHorario.start ? compromissoAntesMudancaHorario.start.format() : "";
        var strEnd = compromissoAntesMudancaHorario.end ? compromissoAntesMudancaHorario.end.format() : "";
        var urlUpdate = "${createLink(action:'updateCompromissoHorario')}?idCompromisso="+compromissoAntesMudancaHorario.id
                    +"&start="+strStart+"&end="+strEnd;
        //Executa a chamada ajax para autalizar o compromisso no banco de dados
        $.ajax({ url: urlUpdate,
            success: function(data) {
                refreshEvento(data);
                $(element).css('opacity', 0);
            }
        });
    }

    /**
     * Reflete o compromisso, após gravação, no componente de calendario, para ser exibido ao operador
     */
    function refreshEvento(compromisso){
        var $calendar = $('#calendar')
        $calendar.fullCalendar('removeEvents', compromisso.id)
        var $selectOperadores = $('#selectUsuarioSistema')
        //Situacoes em que o evento deve ser exibido:
        if ($selectOperadores.val() == '' || //nenhum operador filtrado na tela
                    compromisso.idsParticipantes.length == 0 ||  //o compromisso nao tem nenhum participante, exibir para todos os operadores
                    $.inArray(parseInt($selectOperadores.val()), compromisso.idsParticipantes) >= 0) { //verificar se o operador filtrado na tela e um dos participantes do compromisso
            $calendar.fullCalendar('renderEvent', compromisso);
            verificaConflitos(compromisso);
        }

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

    /**
    * Verifica se o compromisso em questao confilta com algum dos outros compromissos.
    * 1) Verifica se o intervalo deste compromisso intersecciona o intervalo de algum outro compromisso.
    * 2) Olha cada participante do compromisso, ve se ele ja esta presente em algum outro compromisso.
    * 3) Exibe mensagem de alerta de cada participante no conflito de horarios
    */
    function verificaConflitos(evento) {
        var compromisso1 = $('#calendar').fullCalendar('clientEvents', evento.id)[0] //retorna o primeiro elemento, embora a lista seja de apenas um elemento
        console.log("testando evento "+compromisso1.title)

/*
        Snackbar.show({pos: 'bottom-center', duration: 0, customClass: 'snackbar-agenda',
                showSecondButton: true, secondButtonText: 'desfazer', secondButtonTextColor: 'red',
                backgroundColor: '#e3f3ff', textColor: '#006dba',
                actionTextColor: '#006dba', actionText: 'X',
                onSecondButtonClick: desfazUpdateHorarioCompromisso,
                text: compromisso1.toString()});
*/

        $('#calendar').fullCalendar('clientEvents').forEach(function(compromisso2) {
            if (compromisso1.id != compromisso2.id) {
                console.log("Testa: "+compromisso1.title+" e "+compromisso2.title)
                //if (compromisso1.idResponsavel == compromisso2.idResponsavel) {
                if (compromisso1.start.isBefore(compromisso2.end) && compromisso1.end.isAfter(compromisso2.start)) {
                    //compromisso1.color = "red";
                    //compromisso2.color = "red";
                        console.log("Match: "+compromisso1.title+" e "+compromisso2.title)
                    //$('#calendar').fullCalendar('updateEvents', [compromisso1, compromisso2])
                    //element.color = "red";
                    }
                //}
            }
        });
/*
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
*/
    }
/*
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
        tempoDecorrido("antes removeEventSources")
        $('#calendar').fullCalendar('removeEventSources' )
        tempoDecorrido("antes getEvents")
        var source = getEvents();
        tempoDecorrido("antes addEventSource")
        $('#calendar').fullCalendar('addEventSource', source )
        tempoDecorrido("após addEventSource")
    }

    /**
     * Monta request para obter compromissos do servidor
     */
    function getEvents() {
        var urlObterCompromissos = "${createLink(action:'obterCompromissos')}";
        var result = {
                url: urlObterCompromissos,
                type: 'POST',
                data: { idUsuarioSistema: $('#selectUsuarioSistema').val(),
                        mostrarAtendimentos: configuracaoAgenda.atendimentos,
                        mostrarOutrosCompromissos: configuracaoAgenda.outrosCompromissos
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    if (jqXHR.status == 401) //não logado
                        alert('Sessão expirada por inatividade. Favor fazer novo login.');
                    else
                        alert(jqXHR.responseText);
                }
        }
        return result
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

    function loading(isLoading, view) {
        if (isLoading)
            console.time("tempo AJAX")
        else
            console.timeEnd("tempo AJAX");
        if (isLoading)
            tempoDecorrido("antes AJAX")
        else
            tempoDecorrido("depois AJAX")
    }

    function eventAfterAllRender() {
        tempoDecorrido("depois eventAfterAllRender")
    }

    function tempoDecorrido(mensagem) {
        console.log(mensagem + " " + (Date.now() - miliInicial) + " ms");
    }

    /**
     * This monitors all AJAX calls that have an error response. If a user's
     * session has expired, then the system will return a 401 status,
     * "Unauthorized", which will trigger this listener and so prompt the user if
     * they'd like to be redirected to the login page.
     */
/*
    $(document).ajaxError(function(event, jqxhr, settings, exception) {
        if (jqxhr.status = 401) {
        //if (exception == 'Unauthorized') {
            alert("Favor efetuar novo login antes de prosseguir")
            window.location = location.pathname;
        }
    });
*/

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
                          optionKey="id" class="many-to-one" noSelection="['': '(todos)']" onchange="atualiza();" />
            </div>

            <div style="float: left; display: inline; margin-top: 3px">
                <input type="button" class="speed-button-visao-mensal" title="visão mensal" onclick="$('#calendar').fullCalendar('changeView', 'month');"/>
                <input type="button" class="speed-button-visao-semanal" title="visão semanal" onclick="$('#calendar').fullCalendar('changeView', 'agendaWeek');"/>
                <input type="button" class="speed-button-visao-diaria" title="visão diária" onclick="$('#calendar').fullCalendar('changeView', 'agendaDay');"/>
                <input type="button" class="speed-button-visao-lista" title="visão em lista" onclick="$('#calendar').fullCalendar('changeView', 'listMonth');"/>
            </div>
        </div>
    </g:form>
</div>

<div id='calendar' style="display: inline-block"></div>

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

