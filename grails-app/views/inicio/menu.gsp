<%@ page import="org.apoiasuas.importacao.ImportacaoFamiliasController; org.apoiasuas.seguranca.DefinicaoPapeis" %>

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

    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <div id="filtros">
        <table style="border-top: 0; margin-bottom: 0;">
            <tr>
                <td style="width: 14em;">Nome ou cadastro de usuário:</td>
                <td><g:form action="procurarCidadaoExecuta" controller="cidadao"> <g:textField name="nomeOuCodigoLegado" size="50" autofocus=""/>
                    <g:submitButton name="procurar" value="Procurar"/></g:form></td>
            </tr>
            <tr>
                <td>Procurar no ApoiaCRAS:</td>
                <td><g:form action="list" controller="buscaCentralizada"> <g:textField name="palavraChave" size="50" autofocus="" value="${filtro?.nome}"/>
                    <g:submitButton name="list" class="list" value="Procurar"/></g:form></td>
            </tr>
        </table>
    </div>

%{--
    <div filtro-cidadao>
        <g:form action="procurarCidadao">
            <table>
                <tr>
                    <td>Nome ou cadastro de usuário: <g:textField name="nomeOuCodigoLegado" size="25" autofocus=""/></td>
                    <td><g:submitButton name="procurar" value="Procurar"/></td>
                </tr>
                <tr>
                    <td>Rede sócio-assistencial:<g:textField name="palavraChave" size="20" autofocus="" value="${filtro?.nome}"/></td>
                    <td><g:submitButton name="list" class="list" value="Procurar"/></td>
                </tr>
            </table>
        </g:form>
    </div>

    <div id="filtro-servico">
        <g:form action="list">
            <table>
                <tr>
                    <td>Rede sócio-assistencial:<g:textField name="palavraChave" size="20" autofocus="" value="${filtro?.nome}"/></td>
                    <td><g:submitButton name="list" class="list" value="Procurar"/></td>
                </tr>
            </table>
        </g:form>
    </div>
--}%

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
            <g:link class="marrom" controller="pedidoCertidaoProcesso" action="preList">Pedidos de Certidão</g:link>
            <g:link class="lilas" controller="inicio" action="status">Status do sistema</g:link>
            <g:link class="rosa" controller="usuarioSistema" action="alteraPerfil" id="${sec.loggedInUserInfo(field:'id')}">Perfil e senha</g:link>

            %{--TODO: Crirar perfil usuario avancado--}%

            <sec:ifAnyGranted roles="${DefinicaoPapeis.STR_SUPER_USER}">
                <g:link class="rosa" controller="formulario" action="list">Configuração de formulários</g:link>
                <g:link class="beje" controller="abrangenciaTerritorial">Territórios</g:link>
                <g:link class="verde_oliva" controller="importacaoFamilias"
                        action="list">Importação de famílias</g:link>
                <g:link class="laranja" controller="usuarioSistema" action="list">Operadores do sistema</g:link>
            </sec:ifAnyGranted>
            <sec:ifAnyGranted roles="${DefinicaoPapeis.STR_SUPER_USER}">
                <g:link class="verde_agua" controller="servicoSistema" action="list">Serviços utilizando o sistema</g:link>
            </sec:ifAnyGranted>
            <sec:ifAnyGranted roles="${DefinicaoPapeis.STR_USUARIO}">
                <sec:ifNotGranted roles="${DefinicaoPapeis.STR_SUPER_USER}">
                    <g:link class="verde_agua" controller="servicoSistema" action="editCurrent">Configurações do serviço</g:link>
                </sec:ifNotGranted>
            </sec:ifAnyGranted>


            <br style="clear: both"/>
            <br style="clear: both"/>
        </div>
    </div>

    <div style="font-weight: bold; text-align: center; ${ImportacaoFamiliasController.getDataUltimaImportacao(session)?.atrasada ? 'color:red;' : ''}">
        <p>Última importação do cadastro de cidadãos: <g:formatDate format="dd/MM/yyyy HH:mm" date="${ImportacaoFamiliasController.getDataUltimaImportacao(session)?.valor}"/></p>
    </div>

</div>

</body>
</html>
