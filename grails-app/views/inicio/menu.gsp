<%@ page import="org.apoiasuas.seguranca.DefinicaoPapeis" %>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Apoia SUAS</title>
    <asset:stylesheet src="metro.css"/>
    <style type="text/css" media="screen">
    #controller-list {
        /*float: left;*/
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
            <g:link class="magenta" controller="emissaoRelatorio" action="definirListagem">Listagens</g:link>
            <g:link class="marrom" controller="inicio" action="status">Status do sistema</g:link>
            <g:link class="lilas" controller="usuarioSistema" action="alteraPerfil" id="${sec.loggedInUserInfo(field:'id')}">Perfil e senha</g:link>
            <sec:ifAnyGranted roles="${DefinicaoPapeis.SUPER_USER}">
                <g:link class="rosa" controller="formulario" action="list">Configuração de formulários</g:link>
                <g:link class="beje" controller="formulario" action="reinicializarFormulariosPreDefinidos">Reinstalar formulários pré-definidos</g:link>
                <g:link class="verde_oliva" controller="importacaoFamilias"
                        action="list">Importação de famílias</g:link>
                <g:link class="laranja" controller="usuarioSistema" action="list">Operadores do sistema</g:link>
                <g:link class="verde_agua" controller="configuracao">Configurações</g:link>
            </sec:ifAnyGranted>
            <br style="clear: both"/>
            <br style="clear: both"/>
        </div>
    </div>

    <div style="font-weight: bold; text-align: center; ${session.ultimaImportacao?.atrasada ? 'color:red;' : ''}">
        <p>Última importação do cadastro de cidadãos: <g:formatDate format="dd/MM/yyyy HH:mm" date="${session.ultimaImportacao?.data}"/></p>
    </div>

</div>

</body>
</html>
