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
        </style>
</head>

<body>

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
                <g:link class="marrom" controller="formulario" action="reinicializarFormulariosPreDefinidos">Reinstalar formulários pré-definidos</g:link>
                <g:link class="verde_oliva" controller="importacaoFamilias"
                        action="list">Importação de famílias</g:link>
                <g:link class="laranja" controller="usuarioSistema" action="list">Operadores do sistema</g:link>
            </sec:ifAnyGranted>
            <g:link class="verde_agua" controller="inicio" action="status">Status do sistema</g:link>
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

</div>

</body>
</html>
