<%@ page import="org.apoiasuas.InicioController; org.apoiasuas.redeSocioAssistencial.RecursosServico; org.apoiasuas.importacao.ImportacaoFamiliasController; org.apoiasuas.seguranca.DefinicaoPapeis" %>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Apoia SUAS</title>
    <asset:stylesheet src="inicio/menu.less"/>
    <asset:javascript src="cidadao/procurarCidadao.js"/>
    %{--<asset:stylesheet src="animate.css"/>--}%
</head>

<body>

<div id="page-body" style="padding: 0 10px" role="main">

    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <div>
        <label for="nomeOuCad" class="label-menu-procurar">
            Nome ou cadastro de <nobr>usuário <g:helpTooltip chave="buscaUsuario.help"/></nobr>
        </label>
        <nobr>
            <g:textField name="nomeOuCad" id="inputNomeOuCad" autofocus="" size="50" class="input-menu-procurar"
                         onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/>
            <g:link class="input-menu-procurar" onclick="linkProcurarCidadao(this, '${createLink(controller: 'cidadao', action: 'procurarCidadao')}');">
                <input id="btnProcurarCidadao" type="button" class="speed-button-procurar"/>
            </g:link>
        </nobr>
        <div style="clear: both"></div>
    </div>

    <div style="margin-top: 10px">
        <label for="palavraChave" class="label-menu-procurar">
            Procurar no <nobr>ApoiaCRAS <g:helpTooltip chave="buscaCentralizada.help"/></nobr>
        </label>
        <nobr><g:form action="list" controller="buscaCentralizada">
            <g:textField name="palavraChave" size="50" class="input-menu-procurar" onfocus="if(this.value == 'ex: jovem aprendiz') { this.value = ''; }" value="ex: jovem aprendiz"/>
            <g:submitButton name="list" class="input-menu-procurar speed-button-procurar" value=""/>
        </g:form></nobr>
        <div style="clear: both"></div>
    </div>

    %{--<g:render template="anuncioRedeSocioAssistencial"/>--}%

    <script>
        $(document).ready(function(){
            var imgNovoRecurso = '${assetPath(src: 'novo-recurso-menu.png')}'
            $('.novo-recurso-menu').append($( '<img src="'+imgNovoRecurso+'" class="animmated flash"/>' ));
        });
    </script>

    <div id="menu">

            <g:linkMenu title="Formulários emitidos on-line com preenchimento automático à partir do banco de dados de cidadãos (quando disponíveis)"
                        imagem="usecases/formulario-w.png"
                        class="verde_oliva" controller="emissaoFormulario" action="escolherFormulario">Emissão de Formulários</g:linkMenu>
            <g:linkMenu title="Banco de dados de famílias cadastradas em ${sec.loggedInUserInfo(field:'servicoSistemaSessaoCorrente.nome')}"
                        imagem="usecases/procurar-usuario-w.png"
                        class="laranja" controller="cidadao" action="procurarCidadao">Pesquisa de Usuários</g:linkMenu>
            <g:linkMenu title="Cadastra uma nova família no banco de dados"
                        imagem="usecases/cadastrar-familia-w.png"
                        class="rosa ${InicioController.novoRecurso("31/03/2018","novo-recurso-menu")}" controller="familiaDetalhado" action="create">Cadastrar família</g:linkMenu>
            <g:linkMenu title="Serviços, programas, projetos e ações disponíveis na rede sócio-assistencial"
                        imagem="usecases/rede-socio-assistencial-w.png"
                        class="verde_agua" controller="servico">Rede sócio-assistencial</g:linkMenu>
            <g:linkMenu title="Agenda de atendimentos e demais compromissos"
                        imagem="usecases/agenda-w.png"
                        class="lilas" controller="agenda" action="calendario">Agenda</g:linkMenu>
            <g:linkMenu title="Links para sites externos ou documentos, formulários, planilhas, etc salvos no sistema para consulta posterior"
                        imagem="usecases/link-w.png"
                        class="azul" controller="link" action="exibeLinks">Links e documentos</g:linkMenu>
            <g:linkMenu title="Geração de planilhas com a relação de famílias ou membros de acordo com diferentes critérios (idade, técnico de referência, programa de que participa, etc)"
                        imagem="usecases/listagens-w.png"
                        class="magenta" controller="emissaoRelatorio" action="definirListagem">Listagens</g:linkMenu>
            <g:linkMenu title="Consultar a situação de pedidos de certidão emitidos anteriormente (ou registrar manualmente um pedido feito fora do sistema)"
                        imagem="usecases/pedidos-certidao-w.png"
                        acessoServico="${RecursosServico.PEDIDOS_CERTIDAO}"
                        class="marrom" controller="pedidoCertidaoProcesso" action="preList">Gestão de Pedidos de Certidão</g:linkMenu>
            <sec:ifAnyGranted roles="${DefinicaoPapeis.STR_TECNICO}">
                <g:linkMenu title="Registrar um acompanhamento e emitir o Plano de Acompanhamento Familiar"
                            acessoServico="${RecursosServico.PLANO_ACOMPANHAMENTO}"
                            imagem="usecases/acompanhamento-w.png"
                            class="lilas" controller="familia" action="selecionarAcompanhamento">Acompanhamento familiar</g:linkMenu>
                <g:linkMenu title="Permite ao técnico gerenciar as famílias de quem é referência, monitoramentos das intervenções com as famílias (acompanhadas ou não), pedidos de certidão, etc"
                            imagem="usecases/gestao-tecnica-w.png"
                            class="rosa" controller="gestaoTecnica" action="inicial">Gestão técnica</g:linkMenu>
            </sec:ifAnyGranted>
            <g:linkMenu title="Informações técnicas do sistema"
                        class="verde_oliva" controller="inicio" action="status">Status do sistema</g:linkMenu>
            <g:linkMenu title="Alterar suas informações como nome, matrícula, senha, etc"
                        class="laranja" controller="usuarioSistema" action="alteraPerfil" id="${sec.loggedInUserInfo(field:'id')}">Perfil e senha</g:linkMenu>

            %{--TODO: Criar perfil usuario avancado--}%

            <sec:ifAnyGranted roles="${DefinicaoPapeis.STR_SUPER_USER}">
                <g:linkMenu title="Alterações no mecanismo de emissão automática de formulários"
                            class="azul" controller="formulario" action="list">Configuração de formulários</g:linkMenu>
                <g:linkMenu title="Definição das áreas geográficas de atuação dos serviços, programas, para compartilhamento de links, etc"
                            class="beje" controller="abrangenciaTerritorial">Territórios, Regionais e Entes federativos</g:linkMenu>
                <g:linkMenu title="Importação de planilhas de banco de dados do cadastro de famílias do seu serviço para uso no sistema"
                            class="verde_oliva" controller="importacaoFamilias" action="list">Importação de famílias</g:linkMenu>
                <g:linkMenu title="Criação e modificação dos usuários (operadores) do sistema, respectivos perfis e serviços a que estão vinculados"
                            class="laranja" controller="usuarioSistema" action="list">Operadores do sistema</g:linkMenu>
                <g:linkMenu title="Criação e modificação dos serviços habilitados a usarem o sistema"
                            class="verde_agua" controller="servicoSistema" action="list">Serviços utilizando o sistema</g:linkMenu>
                <g:linkMenu title="Criação e modificação de programas, vulnerabilidades, ações e outros marcadores disponíveis para serem aplicados às famílias"
                            class="rosa" controller="marcador" action="list">Programas, Vulnerabilidades, etc</g:linkMenu>
            </sec:ifAnyGranted>
            <sec:ifAnyGranted roles="${DefinicaoPapeis.STR_USUARIO}">
                <sec:ifNotGranted roles="${DefinicaoPapeis.STR_SUPER_USER}">
                    <g:linkMenu title="Alteração das informações institucionais do seu serviço, como nome, endereço, etc" class="verde_agua" controller="servicoSistema" action="editCurrent">Configurações do serviço</g:linkMenu>
                </sec:ifNotGranted>
            </sec:ifAnyGranted>

            <br style="clear: both"/>
            <br style="clear: both"/>
    </div>

    <div style="font-weight: bold; text-align: center; ${ImportacaoFamiliasController.getDataUltimaImportacao(session)?.atrasada ? 'color:red;' : ''}">
        <p>Última importação do cadastro de cidadãos: <g:formatDate format="dd/MM/yyyy HH:mm" date="${ImportacaoFamiliasController.getDataUltimaImportacao(session)?.valor}"/></p>
    </div>

</div>

</body>
</html>
