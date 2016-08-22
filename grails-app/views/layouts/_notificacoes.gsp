<%@ page import="org.apoiasuas.cidadao.FamiliaController" %>

<g:if test="${FamiliaController.getNotificacao(session)}">
    <g:javascript>

        //Exibe as notificacoes
        $( document ).ready(function() {
            //$.notify("Hello Box", { className: 'warn', autoHide: false, clickToHide: false, position:"right" });
            //    $.notify("I'm over here !");
            //        $.notify("Hello Box lalala lalala ototot ytytytytyHello Box lalala lalala ototot ytytytyty", { style: 'happyblue', showDuration: 1000, className: "warn", autoHide: false, position:"top center" });
            //$("#caixa-familia").
            var numExibicoes = ${FamiliaController.getNumeroExibicoesNotificacao(session)}; //Busca na sessao o numero de vezes que a notificacao ja foi exibida
//            alert(numExibicoes);
            var showDuration = (numExibicoes > 0) ? 0 : 1000;
            var conteudo = '${raw(FamiliaController.getNotificacao(session))}'; //Busca na sessao a(s) mensagens a serem exibidas
//            alert(conteudo);
//            $("#caixa-familia").notify({title: "Família elegível a SCFV 0 a 3 <a href='http://www.uol.com.br'>encaminhar</a><br>Família elegível a SCFV > 60 <a href='http://www.uol.com.br'>encaminhar</a>", button: 'Confirm'},
            $("#caixa-familia").notify({title: conteudo},
                    {style: 'notificacoesFamilia', arrowSize: 10, showDuration: showDuration, hideDuration: 0, className: "warn", autoHide: false, clickToHide: false, position:"bottom right" });
        });

        //add a new style
        $.notify.addStyle('notificacoesFamilia', {
            html:
            "<div>" +
            "<div class='clearfix'>" +
            "&nbsp;" +
            "<div class='buttonsNotify'>" +
            "<button title='Fechar' class='fechar'>X</button>" +
            "</div>" +
            "<div class='title' data-notify-html='title'/>" +
            "</div>" +
            "</div>"
        });

        //listen for click events from this style
        $(document).on('click', '.notifyjs-notificacoesFamilia-base .fechar', function() {
            ${remoteFunction(controller: 'familia', action: 'limparNotificacoes', method: "post")};
            $(this).trigger('notify-hide');
        });

    </g:javascript>
</g:if>