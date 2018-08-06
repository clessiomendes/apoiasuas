//Exibe as notificacoes
$( document ).ready(function() {

try {

    if (typeof notificacoesFamilia != 'undefined' && notificacoesFamilia && notificacoesFamilia.conteudo) {
        //se for a primeira vez sendo exibida, usa um efeito de 1 segundo de duração, à partir daí, exibe imediatamente
        var showDuration = (notificacoesFamilia.numExibicoes > 0) ? 0 : 1000;

        //add a new style
        $.notify.addStyle('notificacoesFamilia', {
            html: "<div>" +
            "<div class='clearfix'>" +
            "&nbsp;" +
            "<div class='buttonsNotify'>" +
            "<button title='Fechar' class='fechar'>&times;</button>" +
            "</div>" +
            "<div class='title' data-notify-html='title'/>" +
            "</div>" +
            "</div>"
        });

        //associa o evento de fechar ao botão X
        $(document).on('click', '.notifyjs-notificacoesFamilia-base .fechar', notificacoesFamilia.limpar);

        $("#caixa-familia").notify({title: notificacoesFamilia.conteudo},
            {
                style: 'notificacoesFamilia',
                arrowSize: 10,
                showDuration: showDuration,
                hideDuration: 0,
                className: "warn",
                autoHide: false,
                clickToHide: false,
                position: "bottom right"
            });

        //Necessário sobrescrever o z-index para as notificações de família
        //$(".notifyjs-wrapper").css('z-index','1050');
        $(".notifyjs-wrapper").addClass('z-index-notificacao-familia');
    }
} catch (e) {
    console.error(e)
}

});
