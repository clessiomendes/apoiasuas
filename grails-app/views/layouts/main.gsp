<%@ page import="org.apoiasuas.util.AmbienteExecucao; org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.cidadao.CidadaoController; org.apoiasuas.cidadao.Cidadao; org.apoiasuas.cidadao.Familia; org.apoiasuas.AncestralController" %>
<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle default="Apoia CRAS"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
    <link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
    <link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
    <asset:link rel="manifest" href="manifest.json"/>
    <asset:stylesheet src="application.css"/>
    <asset:javascript src="application.js"/>
    <g:if env="development">
        <asset:stylesheet src="development-utils.less"/>
    </g:if>

    <g:layoutHead/>
    <r:layoutResources/>

</head>

<script>
    //variaveis globais para serem usadas pelo javascript sessao.js
    var imgVerMais = '${assetPath(src: 'down-w.png')}';
    var imgVerMenos = '${assetPath(src: 'up-w.png')}';
    var imgVerMaisTodos = 'url(${assetPath(src: 'double-down-w.png')})';
    var imgVerMenosTodos = 'url(${assetPath(src: 'double-up-w.png')})';

    var notificacoesFamilia = {
        //Busca na sessao a(s) mensagens a serem exibidas
        conteudo: '${raw(FamiliaController.getNotificacao(session))}',
        //Busca na sessao o numero de vezes que a notificacao ja foi exibida
        numExibicoes: ${FamiliaController.getNumeroExibicoesNotificacao(session) ?: 0},
        //chamada ajax para limpar na sessão a notificação exibida atualmente
        limpar: function () {
            ${remoteFunction(controller: 'familia', action: 'limparNotificacoes', method: "post",
                    //antes: desabilitar a animação de "aguardando submissão de ajax responder"
                    before: "noOverlay = true",
                    //após: esconder o balão de notificação
                    after: "jQuery(this).trigger('notify-hide')")};
        }
    };

    var intervaloVerificacaoSessaoExpirada = 2 /*segundos*/;
    var tempoSessaoSegundos = ${session.maxInactiveInterval}
    var janelaModalLogin = new JanelaModalAjax();
    var timerSessaoExpirada
    var timerKeepLoggedIn
    var urlLoginAjax = "${createLink(controller: 'LoginApoiaSuas', action: 'loginAjax')}";
    var urlKeepLoggedIn = "${createLink(controller: 'LoginApoiaSuas', action: 'keepLoggedIn')}";
    var imgNovoRecurso = '${assetPath(src: 'novo-recurso.png')}';

    //caminho para imagens a serem utilizadas em qualquer pagina
    window.grailsSupport = {
        iconeCalendario : "${assetPath(src: 'calendario.png')}"
    };

</script>

<body>

    %{--   Muda a cor do banner de acordo com o ambiente:   --}%
    %{--<div role="banner" class = "${AmbienteExecucao.isProducao() || AmbienteExecucao.isDemonstracao() ? "banner-producao" : AmbienteExecucao.isValidacao() ? "banner-validacao" : "banner-desenvolvimento"}"}>--}%
    <div role="banner" class = "banner-producao"}>
        <table id="cabecalho"><tr>
            <td>
                <a id="textoBanner" href="${createLink(controller: "inicio", action: "menu")}"><asset:image id="imgLogo" src="suas.png" alt="Apoia CRAS"/>
                        <h1>APOIA CRAS</h1>
                        <h2><sec:loggedInUserInfo field="servicoSistemaSessaoCorrente.nome"/></h2>
                </a>
            </td><td> %{--  Exibe a última família / cidadão selecionado, se houver:   --}%
                <%
                    Familia ultimaFamilia = org.apoiasuas.cidadao.FamiliaController.getUltimaFamilia(session)
                    Cidadao ultimoCidadao = org.apoiasuas.cidadao.CidadaoController.getUltimoCidadao(session)
                %>
                <g:if test="${ultimaFamilia != null}">
                    <div id="caixa-familia">
                        <g:link controller="familia" action="show" id="${ultimaFamilia.id}">Cad ${ultimaFamilia.cad}</g:link>
                        <br>
                        <g:if test="${ultimoCidadao != null}">
                            <g:link controller="cidadao" action="show" id="${ultimoCidadao.id}">${ultimoCidadao.nomeCompleto}</g:link>
                        </g:if>
                        &nbsp;
                    </div>
                </g:if>
            </td>
        </tr></table>
    </div>


    <g:layoutBody/>

    <div class="footerProd">
    %{--<div class=${org.apoiasuas.util.AmbienteExecucao.isProducao() ? "footerProd" : org.apoiasuas.util.AmbienteExecucao.isValidacao() ? "footerValid" : "footerLocal"} role="contentinfo">--}%
        <form name="logout" method="POST" action="${createLink(controller: 'logout')}">
            <sec:ifLoggedIn>
                <asset:image src="operador.png" alt="Operador" height="20" width="20"/> <sec:loggedInUserInfo field="username"/>
                <g:link controller="logout" style="color: black">(sair)</g:link>
            </sec:ifLoggedIn>
            <span style="color: black; float: right" >
                Créditos:
                <a style="color: black" target="_blank" href="http://icons8.com">Icons8</a>
                <a style="color: black" target="_blank" href="http://grails.org">Grails</a>
            </span>
        </form>
    </div>
    %{--<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>--}%
    <r:layoutResources/>
</body>
</html>
