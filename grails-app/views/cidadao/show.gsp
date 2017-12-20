
<%@ page import="org.apoiasuas.cidadao.CidadaoController; org.apoiasuas.cidadao.Cidadao" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'cidadao.label', default: 'Cidadao')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-cidadao" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="list" action="procurarCidadao"><g:message message="Procurar"/></g:link></li>
			</ul>
		</div>
		<div id="show-cidadao" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list cidadao">

                <g:if test="${cidadaoInstance?.nomeCompleto}">
                    <li class="fieldcontain">
                        <span id="nomeCompleto-label" class="property-label"><g:message code="cidadao.nomeCompleto.label" default="Nome Completo" /></span>
                        <span class="property-value" aria-labelledby="nomeCompleto-label"><g:fieldValue bean="${cidadaoInstance}" field="nomeCompleto"/></span>
                    </li>
                </g:if>

                <g:if test="${cidadaoInstance?.parentescoReferencia}">
                    <li class="fieldcontain">
                        <g:if test="${cidadaoInstance?.referencia}">
                            <span class="property-label"></span>
                            <span class="property-value">(referência familiar)</span>
                        </g:if>
                        <g:else>
                            <span id="parentescoReferencia-label" class="property-label"><g:message code="cidadao.parentescoReferencia.label" default="Parentesco Referencia" /></span>
                            <span class="property-value" aria-labelledby="parentescoReferencia-label"><g:fieldValue bean="${cidadaoInstance}" field="parentescoReferencia"/></span>
                        </g:else>
                    </li>
                </g:if>

                <br>

                <g:if test="${cidadaoInstance?.familia}">
                    <li class="fieldcontain">
                        <span id="familia-label" class="property-label"><g:message code="cidadao.familia.label" default="Familia" /></span>
                            <span class="property-value" aria-labelledby="familia-label"><g:link controller="familia" action="show" id="${cidadaoInstance?.familia?.id}">${cidadaoInstance?.familia?.montaDescricao().encodeAsHTML()}</g:link></span>
                    </li>
                    <br>
				</g:if>

                <g:if test="${cidadaoInstance?.nomeMae}">
                    <li class="fieldcontain">
                        <span id="nomeMae-label" class="property-label"><g:message code="cidadao.nomeMae.label" default="Nome da Mãe" /></span>
                        <span class="property-value" aria-labelledby="nomeMae-label"><g:fieldValue bean="${cidadaoInstance}" field="nomeMae"/></span>
                    </li>
                </g:if>

                <g:if test="${cidadaoInstance?.nomePai}">
                    <li class="fieldcontain">
                        <span id="nomePai-label" class="property-label"><g:message code="cidadao.nomePai.label" default="Nome do Pai" /></span>
                        <span class="property-value" aria-labelledby="nomePai-label"><g:fieldValue bean="${cidadaoInstance}" field="nomePai"/></span>
                    </li>
                </g:if>

                <br>

                <g:if test="${cidadaoInstance?.dataNascimento}">
                    <li class="fieldcontain">
                        <span id="dataNascimento-label" class="property-label"><g:message code="cidadao.dataNascimento.label" default="Data Nascimento" /></span>
                        <span class="property-value" aria-labelledby="dataNascimento-label"><g:formatDate date="${cidadaoInstance?.dataNascimento}" /> ${cidadaoInstance?.idade ? "("+cidadaoInstance?.idade+" anos)" : ""} </span>
                    </li>
                </g:if>

                <g:if test="${cidadaoInstance?.naturalidade}">
                    <li class="fieldcontain">
                        <span id="naturalidade-label" class="property-label"><g:message code="cidadao.naturalidade.label" default="Naturalidade" /></span>
                        <span class="property-value" aria-labelledby="naturalidade-label">
                            <g:fieldValue bean="${cidadaoInstance}" field="naturalidade"/>
                            <g:fieldValue bean="${cidadaoInstance}" field="UFNaturalidade"/>
                        </span>
                    </li>
                </g:if>

                <br>

                <g:if test="${cidadaoInstance?.estadoCivil}">
                    <li class="fieldcontain">
                        <span id="estadoCivil-label" class="property-label"><g:message code="cidadao.estadoCivil.label" default="Estado Civil" /></span>
                        <span class="property-value" aria-labelledby="estadoCivil-label"><g:fieldValue bean="${cidadaoInstance}" field="estadoCivil"/></span>
                    </li>
                </g:if>

                <g:if test="${CidadaoController.getDocumentos(cidadaoInstance)}">
                    <li class="fieldcontain">
                        <span id="nis-label" class="property-label"><g:message code="cidadao.documentos.label" default="Documentos" /></span>
                        <g:each in="${CidadaoController.getDocumentos(cidadaoInstance)}" var="documento">
                            <span class="property-value" aria-labelledby="documento-label">${documento}</span>
                        </g:each>
                    </li>
                </g:if>

            </ol>
            <fieldset class="buttons">
                <g:link class="add" controller="emissaoFormulario" action="escolherFamilia">Emitir formulário</g:link>
                <g:link class="list" controller="emissaoFormulario" action="listarFormulariosEmitidosCidadao" params="[idCidadao: cidadaoInstance.id]" >Formulários emitidos</g:link>
                <g:link class="edit" action="edit" resource="${cidadaoInstance}">Alterar dados</g:link>
                %{--Só permite remover se não for a ÚNICA referência--}%
                <g:if test="${podeExcluir}">
                    <g:link class="export" action="desabilitar" resource="${cidadaoInstance}"
                            title="Remover este membro do grupo familiar de ${cidadaoInstance.familia.referencia?.nomeCompleto}"
                            onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Deseja remover este membro da família? (obs: se necessario, ele poderá ser reintegrado no futuro)')}');">Remover</g:link>
                </g:if>
                <g:if test="${! cidadaoInstance.habilitado}">
                    <g:link class="import" action="reabilitar" title="Reintegrar este cidadão ao grupo familiar de ${cidadaoInstance.familia.referencia.nomeCompleto}" resource="${cidadaoInstance}">Reintegrar</g:link>
                </g:if>
                <g:link class="atendimento" controller="agenda" action="calendario">Atendimento</g:link>
            </fieldset>
		</div>
	</body>
</html>
