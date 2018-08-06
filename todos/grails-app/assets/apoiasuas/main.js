function expandirFiltros(caller) {
    var $caller = $(caller);
    var $divPesquisar = $caller.closest('.pesquisar');
    var $divMaisFiltros =  $divPesquisar.find('.mais-filtros');

    $caller.hide();
    $divMaisFiltros.show();
}

/**
 * Rotina para interceptar TODAS AS CHAMADAS AJAX com erro de sessao expirada. Abre a janela de
 * login e retorna para a tela anterior SEM EXECUTAR A CHAMADA (o operador deve acionar novamente o comando)
 */
var oldAjax = $.ajax;
var newAjax = function (options) {
    var originalErrorHandler = options.error;
    var errorHandlerContext = options.context ? options.context : $;

    var customErrorHandler = function (xhr, status, error) {
        if (xhr.status === 401 || xhr.status === 403) {
            janelaModalLogin.abreJanela({url: urlLoginAjax, largura: 500});
        }
        else {
            if (originalErrorHandler) {
                originalErrorHandler.apply(errorHandlerContext, arguments);
            }
        }
    };

    if (options.error) {
        options.error = customErrorHandler;
    };

    var deferred = oldAjax.apply($, arguments).error(customErrorHandler);
    var addErrorHandler = deferred.error;
    deferred.error = function (localHandler) {
        var newLocalHandler = function (xhr) {
            if (xhr.status !== 401 && xhr.status !== 403) {
                localHandler.apply(localHandler, arguments);
            }
        };
        addErrorHandler.call(addErrorHandler, newLocalHandler);
        return deferred;
    };
    return deferred;
};
$.ajax = newAjax;

/**
 * Inicia contador para fim da sessao que, ao final, abre uma janela de login para reconectar
 */
function iniciaTimerSessaoExpirada() {
    timerSessaoExpirada = setInterval(function () {
        var tempoAtual = getCookie('expireTime');
        setCookie('expireTime', tempoAtual - intervaloVerificacaoSessaoExpirada);
        if (tempoAtual - intervaloVerificacaoSessaoExpirada <= 0) {
            janelaModalLogin.abreJanela({url: urlLoginAjax, largura: 500});
            clearInterval(timerSessaoExpirada);
        }
    }, intervaloVerificacaoSessaoExpirada * 1000);
}

function keepLoggedIn(delay) {
    timerKeepLoggedIn = setTimeout(function () {
        console.debug('mantendo conexao...'+(new Date).toTimeString());
        noOverlay = true;
        $.ajax({
            url: urlKeepLoggedIn,
            error: function (jqXHR, textStatus, errorThrown) {
                clearInterval(timerKeepLoggedIn);
            },
            success: function (data) {
                console.debug('sessao reiniciada '+(new Date).toTimeString())
                keepLoggedIn(delay);
            }
        });
    }, delay);
}

$(document).ready(function(){
    $('.novo-recurso').after($( '<img src="'+imgNovoRecurso+'" class="animmated flash"/>' ));
    $('.novo-recurso-pequeno').after($( '<img src="'+imgNovoRecurso+'" class="animmated flash"/>' ));
//    iniciaTimerSessaoExpirada();
    keepLoggedIn((tempoSessaoSegundos - 45) * 1000);
});

/**
 * Verifica se o servidor responde antes de submeter o formulario (tentando evitar que erros de servidor
 * provoquem perda de informacoes digitadas pelo operador)
 */
function submitProtegido(form, introducaoErro){
    //parametros obrigatorios
    if (typeof form === 'undefined') {
        alert("Parametro form obrigatório na funcao javascript submitProtegido(form, introducaoErro) ");
        return;
    }
    //parametros opcionais
    if (typeof introducaoErro === 'undefined')
        introducaoErro = "Servidor indisponível.";
    //teste via ajax para verificar disponibilidade do servidor antes do post do formulario
    $.ajax({
        url: urlKeepLoggedIn,
        error: function (jqXHR, textStatus, errorThrown) {
                console.debug('erro no servidor '+errorThrown);
                //sessao de usuario expirada no servidor. necessario novo login antes de submeter o formulario
                if (jqXHR.status === 406 /*not acceptable*/) {
                    janelaModalLogin.abreJanela({url: urlLoginAjax, largura: 500});
                } else {
                    //mensagens de erro quaisquer
                    Snackbar.show({pos: 'bottom-center', duration: 0,
                        backgroundColor: '#fff5f5', textColor: '#cc0000',
                        actionText: '&times;', text: introducaoErro.trim() + " " + errorThrown});
                }
            },
            success: function (data) {
                $(form).submit();
            }
    });
    return false; //em caso de chamadas vinda de botoes submit, impede que o form seja (re)submetido
};

