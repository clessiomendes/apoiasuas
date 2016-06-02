
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
        <div class="nav" role="navigation">
        <g:form>
            <ul>
                <li>
                    Cad <g:textField name="codigoLegado" size="3" autofocus=""/>
                </li>
                <li>
                    Dados da certidão <g:textField name="dadosCertidao" size="25"/>
                </li>
                <li>
                    Autor <g:select name="usuarioSistema" noSelection="${['':'']}" from="${ususariosDisponiveis.collect{it.username}}" keys="${ususariosDisponiveis.collect{it.id}}"/>
                </li>
                <li>
                    Situação <g:select name="situacao" noSelection="${['':'']}" from="${["Pendente","Entregue"]}" keys="${[ProcessoController.SITUACAO_PENDENTE,ProcessoController.SITUACAO_CONCLUIDO]}"/>
                </li>
                <li>
                    AR <g:textField name="numeroAR" size="10"/>
                </li>
                <li>
                    Cartório/Município <g:textField name="cartorio" size="20"/>
                </li>
                <li>
                    Data <g:textField name="dataInicio" size="7"/> a <g:textField name="dataFim" size="7"/>
                </li>
                <li><g:submitButton formaction="listExecuta" name="list" class="list" value="Procurar"/></li>
            </ul>
        </g:form>
        </div>
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
