<%@ page import="org.apoiasuas.cidadao.FamiliaController" %>

<g:if test="${FamiliaController.getNotificacao(session)}">
    <g:javascript>

        //Exibe as notificacoes
        $( document ).ready(function() {
            //Busca na sessao a(s) mensagens a serem exibidas
            var conteudo = '${raw(FamiliaController.getNotificacao(session))}';

            if (conteudo) {
                //Busca na sessao o numero de vezes que a notificacao ja foi exibida
                var numExibicoes = ${FamiliaController.getNumeroExibicoesNotificacao(session)};
                //se for a primeira vez sendo exibida, usa um efeito de 1 segundo de duração, à partir daí, exibe imediatamente
                var showDuration = (numExibicoes > 0) ? 0 : 1000;

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
                    noOverlay = true;
                    ${remoteFunction(controller: 'familia', action: 'limparNotificacoes', method: "post")};
                    $(this).trigger('notify-hide');
                });

                $("#caixa-familia").notify({title: conteudo},
                        {style: 'notificacoesFamilia', arrowSize: 10, showDuration: showDuration, hideDuration: 0, className: "warn", autoHide: false, clickToHide: false, position:"bottom right" });

                //Necessário sobrescrever o z-index para as notificações de família
                //$(".notifyjs-wrapper").css('z-index','1050');
                $(".notifyjs-wrapper").addClass('z-index-notificacao-familia');
            }
        });

    </g:javascript>
</g:if>