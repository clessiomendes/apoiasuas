<%@ page import="org.apoiasuas.importacao.ImportacaoFamiliasController; org.apoiasuas.seguranca.DefinicaoPapeis" %>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Apoia SUAS</title>
    <asset:stylesheet src="metro.css"/>
</head>

<body>

<div id="page-body" role="main">

    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <fieldset class="buttons">
        <table style="border-top: 0; margin-bottom: 0;">
            <tr>
                <td style="width: 14em;">Nome ou cadastro de usuário:</td>
                <td><g:form action="procurarCidadaoExecuta" controller="cidadao"> <g:textField name="nomeOuCodigoLegado" size="50" autofocus=""/>
                    <g:submitButton name="procurar" class="search" value="Procurar"/></g:form></td>
            </tr>
            <tr>
                <td>Procurar no ApoiaCRAS:</td>
                <td><g:form action="list" controller="buscaCentralizada">
                    <g:textField name="palavraChave" size="50" onfocus="if(this.value == 'ex: jovem aprendiz') { this.value = ''; }" value="ex: jovem aprendiz"/>
                    <g:submitButton name="list" class="search" value="Procurar"/>
                </g:form></td>
            </tr>
        </table>
    </fieldset>

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
    <g:render template="anuncioRedeSocioAssistencial"/>

    <div class="wrap">
    <div id="menu">
            <g:link title="Formulários emitidos on-line com preenchimento automático à partir do banco de dados de cidadãos (quando disponíveis)" class="verde_oliva" controller="emissaoFormulario" action="escolherFamilia">Emissão de Formulários</g:link>
            <g:link title="Banco de dados de famílias cadastradas em ${sec.loggedInUserInfo(field:'servicoSistemaSessaoCorrente.nome')}" class="laranja" controller="cidadao" action="procurarCidadao">Pesquisa de Usuários</g:link>
            <g:link title="Serviços, programas, projetos e ações disponíveis na rede sócio-assistencial" class="verde_agua" controller="servico">Rede sócio-assistencial</g:link>
            <g:link title="Links para sites externos ou documentos, formulários, planilhas, etc salvos no sistema para consulta posterior" class="azul" controller="link" action="exibeLinks">Links e documentos</g:link>
            <g:link title="Geração de planilhas com a relação de famílias ou membros de acordo com diferentes critérios (idade, técnico de referência, programa de que participa, etc)" class="magenta" controller="emissaoRelatorio" action="definirListagem">Listagens</g:link>
            <g:link title="Consultar a situação de pedidos de certidão emitidos anteriormente (ou registrar manualmente um pedido feito fora do sistema)" class="marrom" controller="pedidoCertidaoProcesso" action="preList">Gestão de Pedidos de Certidão</g:link>
            <g:link title="Informações técnicas do sistema" class="lilas" controller="inicio" action="status">Status do sistema</g:link>
            <g:link title="Alterar suas informações como nome, matrícula, senha, etc" class="rosa" controller="usuarioSistema" action="alteraPerfil" id="${sec.loggedInUserInfo(field:'id')}">Perfil e senha</g:link>

            %{--TODO: Crirar perfil usuario avancado--}%

            <sec:ifAnyGranted roles="${DefinicaoPapeis.STR_SUPER_USER}">
                <g:link class="rosa" controller="formulario" action="list">Configuração de formulários</g:link>
                <g:link title="Definição das áreas geográficas de atuação dos serviços, programas, para compartilhamento de links, etc" class="beje" controller="abrangenciaTerritorial">Territórios, Regionais e Entes federativos</g:link>
                <g:link title="Importação de planilhas de banco de dados do cadastro de famílias do seu serviço para uso no sistema" class="verde_oliva" controller="importacaoFamilias" action="list">Importação de famílias</g:link>
                <g:link title="Criação e modificação dos usuários (operadores) do sistema, respectivos perfis e serviços a que estão vinculados" class="laranja" controller="usuarioSistema" action="list">Operadores do sistema</g:link>
                <g:link title="Criação e modificação dos serviços habilitados a usarem o sistema" class="verde_agua" controller="servicoSistema" action="list">Serviços utilizando o sistema</g:link>
            </sec:ifAnyGranted>
            <sec:ifAnyGranted roles="${DefinicaoPapeis.STR_USUARIO}">
                <sec:ifNotGranted roles="${DefinicaoPapeis.STR_SUPER_USER}">
                    <g:link title="Alteração das informações institucionais do seu serviço, como nome, endereço, etc" class="verde_agua" controller="servicoSistema" action="editCurrent">Configurações do serviço</g:link>
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
