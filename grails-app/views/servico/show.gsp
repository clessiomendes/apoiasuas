<%@ page import="org.apoiasuas.util.StringUtils; org.apoiasuas.redeSocioAssistencial.Servico" %>
<%
    Servico localDtoServico = servicoInstance
%>

<!DOCTYPE html>
<html>
	<head>
        <g:set var="entityName" value="${message(code: 'servico.label', default: 'Serviço')}" />
        <title>Rede intersetorial e socioassistencial</title>
        <asset:stylesheet src="servico/servico.less"/>
        <asset:javascript src="servico/servico.js"/>
		<meta name="layout" content="main">
    </head>

	<body>
		<a href="#show-servico" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <h1>Exibir - Rede intersetorial e socioassistencial<span class="hide-on-mobile"> (serviços, benefícios, programas e projetos)</span></h1>

		<div class="nav" role="navigation">
			<ul>
				<li><g:link class="search" action="list">Procurar</g:link></li>
				<li><g:link class="create" action="create">Incluir novo</g:link></li>
				<li><g:link class="print" action="download" id="${localDtoServico.id}">Imprimir</g:link></li>
				<li><g:link class="edit" action="edit" resource="${localDtoServico}">Editar</g:link></li>
                <li><g:link class="delete" action="delete" id="${localDtoServico.id}"
                            onclick="return confirm('Confirma remoção deste serviço?');"
                            title="Exclui o serviço do sistema">Remover</g:link></li>
                <li><g:link class="clone" action="clone" resource="${localDtoServico}">Clonar</g:link></li>
			</ul>
		</div>

		<div id="show-servico" class="content scaffold-show" role="main">
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list servico">

                %{--Trecho que renderiza a secao de escolha do cidadao objeto do encaminhamento--}%
                <g:if test="${formularioEncaminhamento}">
                    <fieldset id="fieldsetCidadaoEncaminhamento" class="embedded">
                        <legend>Encaminhar cidadão para o serviço</legend>
                        <g:render template="/emissaoFormulario/escolherFamilia"/>
                        <div class="fieldcontain quebra-linha">
                            <input type="button" value="Encaminhar" class="encaminhar"
                                   onclick="submitFormEncaminhamento('${formularioEncaminhamento?.id}', '${localDtoServico?.id}'); return true;"/>
                        </div>
                    </fieldset>
                </g:if>

                <li class="fieldcontain">
                    <img id="imgServico" class="imagem-servico" src="${imagemServico ?: assetPath([src: 'servico/sem-imagem.png'])}">
                </li>

                <div class="direita-imagem">
                    <g:campoExibicao conteudo="${localDtoServico?.apelido}" titulo="Nome popular" classeCss="linha-inteira"/>
                    <g:campoExibicao conteudo="${localDtoServico?.nomeFormal}" titulo="Nome formal" classeCss="linha-inteira"/>
                    <div>
                        <asset:image src="check-gray.png" class="checked"/>
                        <g:campoExibicao conteudo="" titulo="Informações de" >
                            <g:formatDate date="${localDtoServico?.ultimaVerificacao}"/>
                        </g:campoExibicao>
                        <g:campoExibicao conteudo="Serviço ${localDtoServico.habilitado ? "habilitado" : "desabilitado"}"/>
                    </div>
                </div>

                <g:campoExibicao conteudo="${localDtoServico?.descricao}" titulo="Descrição detalhada" quebraLinha="true"/>
                <g:campoExibicao conteudo="${localDtoServico?.publico}" titulo="Restrição do público atendido" quebraLinha="true"/>
                <g:campoExibicao conteudo="${localDtoServico?.documentos}" titulo="Documentos necessários" quebraLinha="true"/>
                <g:campoExibicao conteudo="${localDtoServico?.enderecos}" titulo="Endereços e horários de atendimento" quebraLinha="true"/>
                <g:campoExibicao conteudo="${localDtoServico?.fluxo}" titulo="Fluxo do atendimento" quebraLinha="true"/>

                <g:if test="${localDtoServico?.site}">
                    <g:campoExibicao titulo="Site na internet" quebraLinha="true">
                        <g:link target="new" url="${fieldValue(bean: localDtoServico, field: "urlSite")}">${fieldValue(bean: localDtoServico, field: "site")}</g:link>
                    </g:campoExibicao>
                </g:if>

                <g:campoExibicao conteudo="${localDtoServico?.telefones}" titulo="Telefones de atendimento ao público" quebraLinha="true"/>
                <g:campoExibicao conteudo="${localDtoServico?.contatosInternos}" titulo="Contatos internos" quebraLinha="true"/>
                <g:campoExibicao conteudo="${localDtoServico.podeEncaminhar ? "Permite" : "Não permite"} encaminhamentos" titulo="" quebraLinha="true"/>
                <g:if test="${localDtoServico?.podeEncaminhar}">
                    <g:campoExibicao conteudo="${localDtoServico.encaminhamentoPadrao}" titulo="Encaminhamento padrão" quebraLinha="true"/>
                </g:if>

                <g:campoExibicao titulo="Território atendido" quebraLinha="true">
                    <g:render template="/abrangenciaTerritorial"/>
                </g:campoExibicao>
			</ol>
		</div>
	</body>
</html>
