/**
 * Abre uma janela modal com o título e a url passados
 * @param titulo exibido na barra de topo da janela
 * @param url montada com a action e a view necessarias para recuperar o html contendo um div a ser exibido no conteudo da janela
 */
function abreJanela(titulo, url, onClose) {
    var $janelaMonitoramento = $("#janelaMonitoramento");
    //exibe uma janela modal inicialmente vazia enquanto aguarda a resposta do servidor
    $janelaMonitoramento.html('<asset:image src="loading.gif"/> carregando...').dialog({
        position:  {my: "center", at: "center", of: window},
        resizable: false,
        modal: true,
        title: titulo,
        width: $(window).width() > 700 ? 700 : 'auto',
        //width: 700,
        close: function() {
            if (onClose)
                onClose();
        }
    }).dialog('open');

    //Executa a chamada ajax para preencher a janela com o resultado retornado do servidor
    $.ajax({
        url: url,
        error: function( jqXHR, textStatus, errorThrown ) {
            $janelaMonitoramento.html(jqXHR.responseText).dialog({position: ['center']}).dialog('open');
        },
        success: function(data) {
            $janelaMonitoramento.html(data).dialog({position: ['center']}).dialog('open');
        }
    });
}

function fechaJanela() {
    $('#janelaMonitoramento').dialog('close');
}
