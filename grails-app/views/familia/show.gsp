<%@ page import="org.apoiasuas.InicioController; org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.cidadao.Familia" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
		<asset:javascript src="familia/telefone/formTelefones.js"/>
		<asset:stylesheet src="familia/telefone/formTelefones.less"/>
	</head>
	<body>

		<div class="nav" role="navigation">
			<ul>
                <li><g:link class="search" controller="cidadao" action="procurarCidadao"
							title="Procurar outra família/usuário"><g:message message="Procurar"/></g:link></li>
				<li><g:link class="edit ${InicioController.novoRecurso("31/03/2018")}" controller="familiaDetalhado" action="edit" id="${familiaInstance.id}"
							title="Ver (ou alterar) o cadastro familiar completo">Cadastro completo</g:link></li>
				<li><g:link class="formulario" controller="emissaoFormulario" action="escolherFormulario"
							title="Preencher um formulário para um membro desta família">Emitir formulário</g:link></li>
				<li><g:link class="edit" controller="familia" action="editMarcadoresApenas" id="${familiaInstance.id}"
							title="Definir programas, vulnerabilidades e ações para esta família">Alterar indicadores</g:link></li>
				<li><g:link class="acompanhamento" controller="familia" action="editAcompanhamentoFamilia" id="${familiaInstance.id}"
							title="Alterar os dados de acompanhamento desta família">Acompanhamento</g:link></li>
				<li><g:link class="atendimento" controller="agenda" action="calendario"
							title="Abre a agenda de atendimentos para escolha de um horário">Agendar atendimento</g:link></li>
				<li><g:link class="list" controller="emissaoFormulario" action="listarFormulariosEmitidosFamilia" params="[idFamilia: familiaInstance.id]"
							title="Permite ver todos os formulários já emitidos para esta família">Formulários emitidos</g:link></li>
			</ul>
		</div>

	<div id="show-familia" class="content scaffold-show" role="main">
		<h1><g:message code="default.show.label" args="[entityName]" /></h1>

		<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
		</g:if>

		%{--Exibe pedidos de certidão pendentes--}%
		<g:if test="${pedidosCertidaoPendentes}">
			%{--Aguardando pedido de certidão:--}%
			<ul class="errors" role="alert">
			<g:each in="${pedidosCertidaoPendentes}" var="pedido">
				<li><g:link controller="pedidoCertidaoProcesso" action="mostraProcesso" id="${pedido.id}">Pedido de certidão em <g:formatDate date="${pedido.inicio}" />, situação: ${pedido.situacaoAtual}</g:link></li>
			</g:each>
			</ul>
		</g:if>

		<g:tabs id="tabs" style="margin: 5px;">
			<g:tab id="tabShowFamilia" titulo="família" template="tabShowFamilia"/>
			<g:tab id="tabShowTelefones" titulo="telefones" template="telefone/tabTelefones"/>
			<g:tab id="tabShowAtendimentos" titulo="atendimentos" template="tabAtendimentos"/>
			<g:tab id="tabMonitoramento" titulo="monitoramentos" template="monitoramento/tabMonitoramentos" roles="${DefinicaoPapeis.STR_TECNICO}"/>
		</g:tabs>

	</div>
	</body>
</html>
