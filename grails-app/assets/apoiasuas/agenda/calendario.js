var janelaModal = new JanelaModalAjax();
var janelaModalTipoCompromisso = new JanelaModalAjax();
var compromissoAntesMudancaHorario = null;

$(document).ready(function() {
    //detectar celular
    var isMobile = false;
    try { isMobile = window.matchMedia("only screen and (max-width: 700px)").matches; }
    catch(e) { console.error('window.matchMedia() não suportado') };

    var cookieCalendario = cookie.get("calendario") ? JSON.parse(cookie.get("calendario")) :
            { //Configuração inicial ou default da agenda
                defaultView: "agendaWeek", start: moment().format()
            };

    //Inicialização do calendário
    $divCalendario = $('#calendar');
    $('#selectUsuarioSistema').val(idUsuarioSistema);

    $divCalendario.fullCalendar({
        events: getEvents(),
        firstDay: configuracaoAgenda.firstDay,
        minTime: configuracaoAgenda.minTime,
        maxTime: configuracaoAgenda.maxTime,
        aspectRatio: "auto",
        //defaultView: "agendaTwoDay",
        defaultView: isMobile ? "listDay" : cookieCalendario.defaultView,
        slotLabelFormat: "HH:mm",
        locale: 'pt',
        allDaySlot: false,
        weekends: configuracaoAgenda.weekends,
        header: {
            left: 'prev',
            center: 'title',
            right: 'next',
        },
        defaultDate: isMobile ? null /*data atual*/ : moment(cookieCalendario.start) /*ultima data consultada*/,
        navLinks: true, // can click day/week names to navigate views
        selectable: isMobile ? false : true,
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
        editable: isMobile ? false : true,
        eventRender: eventRender,
        eventLimit: false, // allow "more" link when too many events
        eventDataTransform: injetaMetodos,
        eventTextColor: "black",
        viewRender: function( view ) {     //Evento acionado após cada mudança na página para gravar em um cookie a visão atual
            cookie.set("calendario", JSON.stringify({
                    defaultView: view.name, start: view.intervalStart.format()
            }));
        },
        eventRenderWait: 100
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
    if (! start)
        start = moment($('#calendar').fullCalendar('getDate').format()+"T"+configuracaoAgenda.minTime);
    var inicioStr = start ? start.format() : "";
    var fimStr = end ? end.format() : ""
    var $usuarioSelecionadoModal = $('#selectUsuarioSistemaOpcoesCompromisso')
    var $usuarioSelecionadoTelaMae = $('#selectUsuarioSistema')

    if ($usuarioSelecionadoTelaMae.val()) //Força que o tecnico selecionado na tela mae (se houver) sobrescreva o tecnico previamente selecionado na tela modal
        $usuarioSelecionadoModal.val($usuarioSelecionadoTelaMae.val()) //copia valor ao select no topo da tela para o select que aparecera na janela modal

    janelaModalTipoCompromisso.createAtendimento = function() {
        if (! $usuarioSelecionadoModal.val()) {
            alert("Escolha um técnico para o atendimento!")
            return;
        }
        //duração de uma hora (TODO: permitir que esse tempo seja configuravel)
        fimStr = start ? start.add(1,"h").format() : ""
        var urlCreate = actionCreateCompromissoAutomatico+"?idUsuarioSistema="+$usuarioSelecionadoModal.val()
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
                refreshEvento(data);
            }
        });
        $('#calendar').fullCalendar('unselect');
        janelaModalTipoCompromisso.confirmada();
    }

    janelaModalTipoCompromisso.createOutroCompromisso = function() {
        janelaModal.abreJanela({titulo: "Criar novo Compromisso",
                url: actionCreateCompromisso+"?idUsuarioSistema="+$usuarioSelecionadoTelaMae.val()+"&inicio="+inicioStr+"&fim="+fimStr/*, largura: 800*/});
        $('#calendar').fullCalendar('unselect');
        janelaModalTipoCompromisso.confirmada();
    }

    if (jsEvent && jsEvent.shiftKey && $usuarioSelecionadoTelaMae.val()) //compromisso automatico - do tipo Atendimento
        janelaModalTipoCompromisso.createAtendimento();
    else
        janelaModalTipoCompromisso.abreJanela({titulo: "Agenda", element: $('#divEscolherTipoCompromisso')/*, largura: 550*/});
}

function abreCompromisso( event, delta, revertFunc, jsEvent, ui, view ) {
     var tituloJanela = event.tipoAtendimento ? "Alterar Atendimento" : "Alterar Compromisso";
     janelaModal.abreJanela({titulo: tituloJanela, url: actionEditCompromisso+"?idCompromisso="+event.id/*, largura: 800*/});
}

/**
* Chamado tanto no resize quanto no move do compromisso, para atualizar a nova informacao no banco de dados
*/
function updateHorarioCompromisso( event, delta, revertFunc, jsEvent, ui, view) {

    if (! jsEvent.shiftKey) { //exige a tecla shift
        alert("Compromisso não alterado! É necessário manter a tecla 'shift' pressionada antes de movê-lo ou redimensioná-lo!");
        revertFunc();
        return;
    }

    var strStart = event.start ? event.start.format() : "";
    var strEnd = event.end ? event.end.format() : "";
    var urlUpdate = actionUpdateCompromissoHorario+"?idCompromisso="+event.id
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
            refreshEvento(data, {pos: 'bottom-center', duration: 0, 
                showSecondButton: true, secondButtonText: 'desfazer',
                backgroundColor: '#e3f3ff', textColor: '#006dba',
                //secondButtonTextColor: '#cc0000',
                //actionTextColor: '#006dba', actionText: '&times;',
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
    var urlUpdate = actionUpdateCompromissoHorario+"?idCompromisso="+compromissoAntesMudancaHorario.id
                +"&start="+strStart+"&end="+strEnd;
    //Executa a chamada ajax para autalizar o compromisso no banco de dados
    $.ajax({ url: urlUpdate,
        success: function(data) {
            refreshEvento(data);
            $(element).css('opacity', 0);
        }
    });
}

//var snackbarMudancaHorario = {}
//var snackbarConflito = {}

/**
 * Reflete o compromisso, após gravação, no componente de calendario, para ser exibido ao operador
 * snackbarOptions - uma mensagem pre-definida a ser apresentada na parte de baixo da tela
 */
function refreshEvento(compromisso, snackbarOptions){
    var $calendar = $('#calendar')
    $calendar.fullCalendar('removeEvents', compromisso.id)
    var $selectOperadores = $('#selectUsuarioSistema')
    //Situacoes em que o evento deve ser exibido:
    if ($selectOperadores.val() == '' || //nenhum operador filtrado na tela
                compromisso.idsParticipantes.length == 0 ||  //o compromisso nao tem nenhum participante, exibir para todos os operadores
                $.inArray(parseInt($selectOperadores.val()), compromisso.idsParticipantes) >= 0) { //verificar se o operador filtrado na tela e um dos participantes do compromisso

        $calendar.fullCalendar('renderEvent', compromisso);

        if (compromisso.mensagem) {
            if (snackbarOptions) {
                snackbarOptions.text = compromisso.mensagem + '<br>'  + snackbarOptions.text;
            } else {
                snackbarOptions = {pos: 'bottom-center', duration: 0,
                    actionText: '&times;', text: compromisso.mensagem};
            }
            snackbarOptions.backgroundColor = '#fff5f5'; //rosa
            snackbarOptions.textColor = '#cc0000';//vermelho
        }
        if (snackbarOptions)
            Snackbar.show(snackbarOptions);
    }
}

function imprimirCompromissos() {
    //janelaModal.abreJanela("escolha...",null,$('#divEscolherTipoCompromisso').html());

    var view = $('#calendar').fullCalendar('getView');
    //alert(view.name + " " + view.title + " " + view.start.format() + " " + view.end.format() + " ")
    var url = actionImprimir+"?idUsuarioSistema="+$('#selectUsuarioSistema').val()+"&start="+view.start.format()+"&end="+view.end.format();
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

function hoje() {
    //$('#calendar').fullCalendar('removeEventSources' )
    //var source = getEvents();
    //$('#calendar').fullCalendar('addEventSource', source )
}

/**
 * Monta request para obter compromissos do servidor
 */
function getEvents() {
    var result = {
            url: actionObterCompromissos,
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
     janelaModal.abreJanela({titulo: "Configurações de exibição da agenda", /*largura: 600,*/ url: actionConfiguracao,
            refreshFunction: function() {
                //atualiza a exibição do calendário
                $selectUsuarioSistema = $("#selectUsuarioSistema");
                if ($selectUsuarioSistema.val())
                    actionCalendario += '?idUsuarioSistema='+$selectUsuarioSistema.val();
                window.location = actionCalendario;
            }
     });
}

function eventRender(event, element) {
    $(element).attr("title", event.title);
}

function abreEstatisticaAtendimentos() {
    inicioStr = $('#calendar').fullCalendar('getView').intervalStart.format();
    fimStr = $('#calendar').fullCalendar('getView').intervalEnd.format();
//    console.log(inicioStr);
//    console.log(fimStr);
    janelaModal.abreJanela({titulo: "Total de atendimentos por técnico",
            url: actionEstatisticaAtendimentos+"?inicio="+inicioStr+"&fim="+fimStr, largura: 400});
}
