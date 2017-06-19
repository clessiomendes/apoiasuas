//= require especificos/calendar/moment.js
//= require especificos/calendar/fullcalendar.js
//= require especificos/calendar/locale-all.js

function inicializaCalendario($divCalendario, funcaoCreateCompromisso, funcaoCreateCompromissoAutomatico, funcaoUpdateCompromisso, urlObterCompromissos, idUsuarioSistema) {
    $divCalendario.fullCalendar({
        events: {
            url: urlObterCompromissos,
            type: 'POST',
            data: {
                idUsuarioSistema: idUsuarioSistema
            },
            error: function() {
                alert('Erro obtendo compromissos do servidor');
            }
        },
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
        header: {
            left: 'prev,next today',
            center: 'title',
            right: 'month,agendaWeek,listWeek,agendaDay'
        },
        //defaultDate: '2017-05-12',
        navLinks: true, // can click day/week names to navigate views
        selectable: true,
        selectHelper: true,
        //evento acionado para criação de novos compromissos
        select: function(start, end, jsEvent, view) {
            if (jsEvent.shiftKey) {
                funcaoCreateCompromissoAutomatico(start, end)
            } else
                funcaoCreateCompromisso(start, end);
            $divCalendario.fullCalendar('unselect');
        },
        //evento acionado quando um evento é movido de lugar no calendario, sinalizando uma alteração
        eventResize: function( event, delta, revertFunc, jsEvent, ui, view ) {
            funcaoUpdateCompromisso(event);
        },
        //evento acionado quando um evento é redimencionado no calendario, sinalizando uma alteração
        eventDrop: function( event, delta, revertFunc, jsEvent, ui, view ) {
            funcaoUpdateCompromisso(event)
        },
        editable: true,
        eventLimit: true // allow "more" link when too many events
    })
}

