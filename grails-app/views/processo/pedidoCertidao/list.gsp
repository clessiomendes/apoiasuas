
<%@ page import="org.apoiasuas.processo.ProcessoController; org.apoiasuas.Link" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
        <g:set var="entityName" value="Pedidos de Certidão" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>

	<body>
		<a href="#list-task" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

        <h1>Pesquisar Pedidos de Certidão</h1>

        <g:form class="pesquisar">
            <span class="campo">
                <span class="titulo">Nome na certidão</span>
                <g:textField name="dadosCertidao" />
            </span>

            <span class="campo">
                <span class="titulo">Cad</span>
                <g:textField name="cad" style="width:5em" autofocus=""/>
            </span>

            <span class="mais-filtros">
                <br>
                <span class="campo">
                    <span class="titulo">Autor</span>
                    <g:select name="usuarioSistema" noSelection="${['':'']}" from="${ususariosDisponiveis.collect{it.username}}" keys="${ususariosDisponiveis.collect{it.id}}"/>
                </span>

                <span class="campo">
                    <span class="titulo">Situação</span>
                    <g:select name="situacao" noSelection="${['':'']}" from="${["Pendente","Entregue"]}" keys="${[ProcessoController.SITUACAO_PENDENTE,ProcessoController.SITUACAO_CONCLUIDO]}"/>
                </span>

                <span class="campo">
                    <span class="titulo">AR</span>
                    <g:textField name="numeroAR" />
                </span>

                <span class="campo">
                    <span class="titulo">Cartório/Município</span>
                    <g:textField name="cartorio" />
                </span>

                <span class="campo">
                    <span class="titulo">Data</span>
                    <g:textField name="dataInicio" style="width:6em" /> a <g:textField name="dataFim" style="width:6em" />
                </span>
            </span>

            <g:submitButton formaction="list" name="list" id="search" class="search" value="Procurar"/>
            <div class="expandir"><asset:image src="slidedown.png" onclick="expandirFiltros(this);"/></div>
        </g:form>

%{--
        <g:form>
            <table class="parametrosPesquisa">
                <tr>
                    <td>
                        <div>
                            <nobr>Cad <g:textField name="cad" size="3" autofocus=""/></nobr>
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
                            <input type="button" class="speed-button-expandir" title="mais opções" onclick="expandePesquisa();"/>
                            <g:submitButton formaction="list" name="list" id="search" class="search" value="Procurar"/>
                        </div>
                    </td>
                </tr>
            </table>
        </g:form>
--}%

		<div id="list-task" class="content scaffold-list" role="main">
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
            <table class="tabelaListagem">
			<thead>
					<tr>
                        <g:sortableColumn property="criacao" title="Data" />
						<g:sortableColumn class="hide-on-mobile" property="usuarioSistema" title="Autor" />
                        <g:sortableColumn property="situacao" title="Situação atual" />  %{--Descricao da tarefa--}%
                        <g:sortableColumn class="hide-on-mobile" property="cad" title="Cad" />
                        <g:sortableColumn property="dadosCertidao" title="Dados do Pedido" />
                        <g:sortableColumn class="hide-on-mobile" property="cartorio" title="Cartório" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${processos}" status="i" var="processo">
                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td><g:formatDate format="dd/MM/yyyy HH:mm" date="${processo.inicio}"/></td>
                        <td class="hide-on-mobile" >${processo.operadorResponsavel?.username}</td>
                        <td><g:link action="mostraProcesso" id="${processo.id}">${processo.situacaoAtual}</g:link></td>
                        <td class="hide-on-mobile" >${processo.familia?.cad}</td>
                        <td>${processo.dadosCertidao}</td>
                        <td class="hide-on-mobile" >${processo.cartorio}</td>
					</tr>
				</g:each>
				</tbody>
			</table>
            <g:if test="${processos.size() >= org.apoiasuas.processo.ProcessoDTO.MAX_PAGINACAO}">
                <ul class="errors" role="alert"> <li>
                        - Atenção! Apenas os ${org.apoiasuas.processo.ProcessoDTO.MAX_PAGINACAO} primeiros pedidos foram exibidos.
                        Restrinja mais a sua pesquisa para ver os demais.
                </li> </ul>
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
