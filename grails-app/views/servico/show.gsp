
<%@ page import="org.apoiasuas.util.StringUtils; org.apoiasuas.servico.Servico" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'servico.label', default: 'Serviço')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>

        <g:javascript>
        function submitFormEncaminhamento() {
            document.getElementById('preencherFormulario').idFormulario.value = '${formularioEncaminhamento?.id}';
            document.getElementById('preencherFormulario').idServico.value = '${servicoInstance?.id}';
            document.getElementById('preencherFormulario').submit();
        }
        </g:javascript>

    </head>
	<body>
		<a href="#show-servico" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create">Incluir novo serviço</g:link></li>
			</ul>
		</div>

		<div id="show-servico" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list servico">
			
				<g:if test="${servicoInstance?.apelido}">
				<li class="fieldcontain">
					<span id="apelido-label" class="property-label"><g:message code="servico.apelido.label" default="Nome popular" /></span>
					<span class="property-value" aria-labelledby="apelido-label"><g:fieldValue bean="${servicoInstance}" field="apelido"/></span>
				</li>
				</g:if>

                <g:if test="${servicoInstance?.descricao}">
                    <li class="fieldcontain">
                        <span id="encaminhamentoPadrao-label" class="property-label"><g:message
                                code="servico.descricao.label" default="Descrição"/></span>
                        <span class="property-value" aria-labelledby="descricao-label">
                            %{--Substitui o scape padrao do Grails por um customizado que interpreta \n como <br> --}%
                            ${raw(org.apoiasuas.util.StringUtils.toHtml(servicoInstance?.descricao))}
                        </span>
                    </li>
                </g:if>

                <g:if test="${servicoInstance?.telefones}">
                    <li class="fieldcontain">
                        <span id="telefones-label" class="property-label"><g:message code="servico.telefones.label" default="Telefone(s)" /></span>
                        <span class="property-value" aria-labelledby="telefones-label"><g:fieldValue bean="${servicoInstance}" field="telefones"/></span>
                    </li>
                </g:if>

                <g:if test="${servicoInstance?.site}">
                    <li class="fieldcontain">
                        <span id="site-label" class="property-label"><g:message code="servico.site.label" default="Site" /></span>
                        <span class="property-value" aria-labelledby="site-label">
                            <g:link target="new" url="${fieldValue(bean: servicoInstance, field: "urlSite")}">${fieldValue(bean: servicoInstance, field: "site")}</g:link>
                        </span>
                    </li>
                </g:if>

                <g:if test="${servicoInstance?.podeEncaminhar}">
                    <fieldset id="fieldsetDadosEncaminhamento" class="embedded">
                        <legend>
                            <g:message code="servico.podeEncaminhar" default="Permite encaminhamento"/>
                        </legend>

                        <g:if test="${servicoInstance?.nomeFormal}">
                            <li class="fieldcontain">
                                <span id="nomeFormal-label" class="property-label"><g:message code="servico.nomeFormal.label" default="Nome Formal" /></span>
                                <span class="property-value" aria-labelledby="nomeFormal-label"><g:fieldValue bean="${servicoInstance}" field="nomeFormal"/></span>
                            </li>
                        </g:if>

                        <g:if test="${servicoInstance?.encaminhamentoPadrao}">
                            <li class="fieldcontain">
                                <span id="encaminhamentoPadrao-label" class="property-label"><g:message
                                        code="servico.encaminhamentoPadrao.label" default="Encaminhamento Padrao"/></span>
                                <span class="property-value" aria-labelledby="encaminhamentoPadrao-label">
                                    %{--Substitui o scape padrao do Grails por um customizado que interpreta \n como <br> --}%
                                    ${raw(org.apoiasuas.util.StringUtils.toHtml(servicoInstance?.encaminhamentoPadrao))}
                                </span>
                            </li>
                        </g:if>

                        <g:if test="${servicoInstance?.endereco}">
                            <li class="fieldcontain">
                                <span id="endereco-label" class="property-label"><g:message code="servico.endereco.label" default="Endereco" /></span>

                                <span class="property-value" aria-labelledby="endereco-label"><g:fieldValue bean="${servicoInstance}" field="endereco"/></span>

                            </li>
                        </g:if>
                    </fieldset>

                    %{--Trecho que renderiza a secao de escolha do cidadao objeto do encaminhamento--}%
                    <g:if test="${formularioEncaminhamento}">
                        <fieldset id="fieldsetCidadaoEncaminhamento" class="embedded">
                            <legend>Encaminhar cidadão para o serviço</legend>
                            <g:render template="/emissaoFormulario/escolherFamilia"/>
                            <div class="fieldcontain">
                                <label>
                                    <g:actionSubmit value="Encaminhar" onclick="submitFormEncaminhamento(); return true;"/>
                                </label>
                            </div>
                        </fieldset>
                    </g:if>

                </g:if>
			</ol>

			<g:form url="[resource:servicoInstance, action:'delete']">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${servicoInstance}"><g:message code="default.button.edit.label" default="Editar" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Remover')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Confirma remoção?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
