
<%@ page import="org.apoiasuas.processo.ProcessoController; org.apoiasuas.Link" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
        <g:set var="entityName" value="Pedidos de Certidão" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.1/jquery.min.js"></script>
        <style type="text/css">
        table.search {margin: 0;}
        table.search tr:hover { background: none; }
        table.search td { padding: 0; }
        table.search div { text-indent: 0; }
        </style>

	</head>

<g:javascript>
    function expandePesquisa() {
        $("#expansivel").slideDown(1000);
        $("#slidedown").hide();
        return false;
    };
</g:javascript>

	<body>
		<a href="#list-task" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <g:form>
            <fieldset class="buttons">
            <table class="search">
                <tr>
                    <td>
                        <div>
                            <nobr>Cad <g:textField name="codigoLegado" size="3" autofocus=""/></nobr>
                            <nobr>Nome na certidão <g:textField name="dadosCertidao" size="25"/></nobr>
                            <nobr>Autor <g:select name="usuarioSistema" noSelection="${['':'']}" from="${ususariosDisponiveis.collect{it.username}}" keys="${ususariosDisponiveis.collect{it.id}}"/></nobr>
                        </div>
                        <div id="expansivel" style="display: none">
                            <nobr>Situação <g:select name="situacao" noSelection="${['':'']}" from="${["Pendente","Entregue"]}" keys="${[ProcessoController.SITUACAO_PENDENTE,ProcessoController.SITUACAO_CONCLUIDO]}"/></nobr>
                            <nobr>AR <g:textField name="numeroAR" size="10"/></nobr>
                            <nobr>Cartório/Município <g:textField name="cartorio" size="20"/></nobr>
                            <nobr>Data <g:textField name="dataInicio" size="7"/> a <g:textField name="dataFim" size="7"/></nobr>
                        </div>
                    </td>
                    <td>
                        <div>
                            <input type="button" id="slidedown" class="slidedown" value="." title="mais opções" onclick="expandePesquisa();"/>
                            <g:submitButton formaction="list" name="list" id="search" class="search" value="Procurar"/>
                        </div>
                    </td>
                </tr>
            </table>
            </fieldset>
        </g:form>
		<div id="list-task" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
                        <g:sortableColumn  property="criacao" title="Data" />
						<g:sortableColumn property="usuarioSistema" title="Autor" />
                        <g:sortableColumn property="situacao" title="Situação atual" />  %{--Descricao da tarefa--}%
                        <g:sortableColumn property="cad" title="Cad" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${processos}" status="i" var="processo">
                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td><g:formatDate format="dd/MM/yyyy HH:mm" date="${processo.inicio}"/></td>
                        <td>${processo.operadorResponsavel?.username}</td>
                        <td><g:link action="mostraProcesso" id="${processo.id}">${processo.situacaoAtual}</g:link></td>
                        <td>${processo.familia?.codigoLegado}</td>
					</tr>
				</g:each>
				</tbody>
			</table>
            <g:if test="${processos.size() >= org.apoiasuas.processo.ProcessoDTO.MAX_PAGINACAO}">
                - Atenção! Apenas os ${org.apoiasuas.processo.ProcessoDTO.MAX_PAGINACAO} primeiros pedidos foram exibidos.
                Restrinja mais a sua pesquisa para ver os demais.
            </g:if>
		</div>
        <sec:ifAnyGranted roles="${org.apoiasuas.seguranca.DefinicaoPapeis.STR_USUARIO}">
        <div class="nav" role="navigation">
            <ul style="margin-bottom: 0.5em">
                    <li><g:link class="create" action="create">Novo pedido</g:link></li>
            </ul>
        </div>
        </sec:ifAnyGranted>
        </body>
    </html>
