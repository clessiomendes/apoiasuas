<%@ page import="org.apoiasuas.seguranca.DefinicaoPapeis" %>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Apoia SUAS</title>
    <asset:stylesheet src="metro.css"/>


        <style type="text/css" media="screen">

#controller-list {
    float: left;
    margin-left: 20px;
    list-style-type: none;
}

#controller-list ul {
    list-style-position: inside;
}

#controller-list li {
    line-height: 1.3;
    list-style-position: inside;
    margin: 0.25em 0;
}

%{--
        #status {
            background-color: #eee;
            border: .2em solid #fff;
            margin: 2em 2em 1em;
            padding: 1em;
            width: 12em;
            float: left;
            -moz-box-shadow: 0px 0px 1.25em #ccc;
            -webkit-box-shadow: 0px 0px 1.25em #ccc;
            box-shadow: 0px 0px 1.25em #ccc;
            -moz-border-radius: 0.6em;
            -webkit-border-radius: 0.6em;
            border-radius: 0.6em;
        }

        .ie6 #status {
            display: inline; /* float double margin fix http://www.positioniseverything.net/explorer/doubled-margin.html */
        }

        #status ul {
            font-size: 0.9em;
            list-style-type: none;
            margin-bottom: 0.6em;
            padding: 0;
        }

        #status li {
            line-height: 1.3;
        }

        #status h1 {
            text-transform: uppercase;
            font-size: 1.1em;
            margin: 0 0 0.3em;
        }

        #page-body {
            margin: 2em 1em 1.25em 18em;
        }

        h2 {
            margin-top: 1em;
            margin-bottom: 0.3em;
            font-size: 1em;
        }

        p {
            line-height: 1.5;
            margin: 0.25em 0;
        }

        @media screen and (max-width: 480px) {
            #status {
                display: none;
            }

            #page-body {
                margin: 0 1em 1em;
            }

            #page-body h1 {
                margin-top: 0;
            }
        }
    --}%
        </style>
</head>

<body>
%{--
        <div id="status" role="complementary">

            <h1>Application Status</h1>
            <ul>
                <li>App version: <g:meta name="app.version"/></li>
                <li>Grails version: <g:meta name="app.grails.version"/></li>
                <li>Groovy version: ${GroovySystem.getVersion()}</li>
                <li>JVM version: ${System.getProperty('java.version')}</li>
                <li>Reloading active: ${grails.util.Environment.reloadingAgentEnabled}</li>
                <li>Controllers: ${grailsApplication.controllerClasses.size()}</li>
                <li>Domains: ${grailsApplication.domainClasses.size()}</li>
                <li>Services: ${grailsApplication.serviceClasses.size()}</li>
                <li>Tag Libraries: ${grailsApplication.tagLibClasses.size()}</li>
            </ul>
            <h1>Installed Plugins</h1>
            <ul>
                <g:each var="plugin" in="${applicationContext.getBean('pluginManager').allPlugins}">
                    <li>${plugin.name} - ${plugin.version}</li>
                </g:each>
            </ul>
    </div>
--}%

<div id="page-body" role="main">
    <div class="wrap">
        <div id="menu">

            <g:link class="verde_oliva" controller="emissaoFormulario" action="escolherFamilia">
                Emissão de Formulários
            %{--<br><br><asset:image src="apple-touch-icon.png"/>--}%
            </g:link>
            <g:link class="laranja" controller="cidadao" action="procurarCidadao">Pesquisa de Usuários</g:link>
            <g:link class="verde_agua" controller="servico">Rede sócio-assistencial</g:link>
            <g:link class="azul" controller="link" action="exibeLinks">Atalhos Externos</g:link>
            <sec:ifAnyGranted roles="${DefinicaoPapeis.SUPER_USER}">
                <g:link class="magenta" controller="formulario" action="list">Configuração de formulários</g:link>
                <g:link class="marrom" controller="formulario"
                        action="reinicializarFormulariosPreDefinidos">Reinstalar formulários pré-definidos</g:link>
                <g:link class="verde_oliva" controller="importacaoFamilias"
                        action="list">Importação de famílias</g:link>
                <g:link class="laranja" controller="usuarioSistema" action="list">Operadores do sistema</g:link>
            </sec:ifAnyGranted>
        </div>
    </div>
    <div id="controller-list" role="navigation">
<sec:ifAnyGranted roles="${DefinicaoPapeis.SUPER_USER}">
        <h1>Outros controllers:</h1>
        <g:each var="c" in="${request.outrasOpcoes.sort { it.fullName }}">
            <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.fullName}</g:link></li>
        </g:each>
</sec:ifAnyGranted>
    </div>

    %{--

        <h1>Menu inicial</h1>

        <div id="controller-list" role="navigation">
            <ul>
                <g:linkMenu controller="emissaoFormulario" action="escolherFamilia">Emissão de Formulários</g:linkMenu>
                <g:linkMenu controller="cidadao" action="procurarCidadao">Pesquisa de Usuários</g:linkMenu>
                <g:linkMenu controller="servico">Rede sócio-assistencial</g:linkMenu>
                <g:linkMenu controller="link" action="exibeLinks">Atalhos Externos</g:linkMenu>
                <sec:ifAnyGranted roles="${DefinicaoPapeis.SUPER_USER}">
                    <g:linkMenu controller="formulario" action="list">Configuração de formulários</g:linkMenu>
                    <g:linkMenu controller="formulario"
                                action="reinicializarFormulariosPreDefinidos">Reinstalar formulários pré-definidos</g:linkMenu>
                    <g:linkMenu controller="importacaoFamilias" action="list">Importação de famílias</g:linkMenu>
                    <g:linkMenu controller="usuarioSistema" action="list">Operadores do sistema</g:linkMenu>
                    <h1>Outros controllers:</h1>
                    <g:each var="c" in="${request.outrasOpcoes.sort { it.fullName }}">
                        <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.fullName}</g:link></li>
                    </g:each>
                </sec:ifAnyGranted>
            </ul>
        </div>
    <br>
    --}%
</div>

</body>
</html>
