<%@ page import="org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.report.FormatoRelatorio; org.apoiasuas.cidadao.Familia" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
    <asset:javascript src="jqery.js"/>
</head>

%{-- TODO: Mudar código jQuery para que não só os fieldsets de primeiro nível, mas também os internos, possam colapsar http://jsfiddle.net/DbyQX/1/ --}%
<g:javascript>
    $(document).ready(function() {
        $('input.collapsable').click(function() {
            var $this = $(this);
            var parent = $this.parent().parent();
            var contents = parent.contents().not($this.parent());
            if (contents.length > 0) {
                $this.data("contents", contents.remove());
            } else {
                $this.data("contents").appendTo(parent);
            }
            return true;
        });
        $('input.collapsable').click();
        $('input.collapsable').attr('checked', false);
    });
</g:javascript>

<body>
<a href="#show-familia" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                              default="Skip to content&hellip;"/></a>
%{--
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
--}%

<div id="show-familia" class="content scaffold-show" role="main">
<h1>Guias de identidade e fotos</h1>
<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>

%{--Um único form para toda a página, de forma que múltiplos relatórios possam ser submetidos ao mesmo tempo--}%
<g:form name="formFamilia">

    <input type="hidden" id="id" name="id" value="${familiaInstance?.id}">

%{--Hidden reservado para a rotina javascript que aponta referente a qual cidadao da lista os botoes "Gerar Guia" foram pressionados    --}%
    <input type="hidden" id="idCidadaoSelecionado" name="idCidadaoSelecionado">

    <ol class="property-list familia">
    %{--CODIGO LEGADO--}%
        <g:if test="${familiaInstance?.codigoLegado}">
            <li class="fieldcontain">
                <span id="codigoLegado-label" class="property-label"><g:message code="familia.codigoLegado.label"
                                                                                default="Codigo Legado"/></span>

                <span class="property-value" aria-labelledby="codigoLegado-label">
                    <g:fieldValue bean="${familiaInstance}" field="codigoLegado"/>
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Formato:&nbsp;
                    <g:radioGroup
                            values="[FormatoFormulario.PDF.extensaoArquivo, FormatoFormulario.DOCX.extensaoArquivo, org.apoiasuas.report.FormatoFormulario.XHMTL.extensaoArquivo]"
                            labels="[FormatoFormulario.PDF.extensaoArquivo, FormatoFormulario.DOCX.extensaoArquivo, FormatoFormulario.XHMTL.extensaoArquivo]"
                            name="formatoRelatorio"
                            value="${FormatoFormulario.DOCX.extensaoArquivo}">
                        ${it.radio} ${it.label}
                    </g:radioGroup>
                </span>

            </li>
        </g:if>

        <g:hiddenField name="version" value="${familiaInstance?.version}"/>

    %{--ENDEREÇO--}%
        <fieldset class="form">
            <g:render template="formEndereco"/>
        </fieldset>

        <div class="fieldcontain">
            <label for="dataConcessao">
                <g:message code="relatorioCidadao.dataConcessao" default="Data da concessão" />
            </label>
            %{--<g:textField name="dataConcessao" value="${}"/>--}%
            %{--TODO: utilizar um componente visual de escolha de datas ou permitir digitacao livre--}%
            <g:datePicker name="dataConcessao" precision="day" value="${new Date()}"
                          noSelection="['': '']" default="${new Date()}"/>
        </div>

    %{--MEMBROS INDIVIDUAIS--}%
        <g:if test="${familiaInstance?.membros}">

            <g:each var="cidadao" in="${familiaInstance.membrosOrdemAlfabetica}" status="i">
                <li class="fieldcontain">
                    %{--
                                         <g:checkBox name="Identidade_${cidadao?.id}"/> Identidade
                                         <g:checkBox name="Foto_${cidadao?.id}"/> Foto -
                                        ${cidadao?.nomeCompleto?.encodeAsHTML()}
                    --}%
                    %{--<g:render template="formCidadao" bean="${cidadao}" var="cidadaoInstance"/>--}%
                    <g:render template="formCidadao" model="['cidadaoInstance':cidadao, 'i':i]"/>
                </li>
            </g:each>

        </g:if>

    </ol>
    <sec:ifAllGranted roles="${DefinicaoPapeis.USUARIO}">
        <fieldset class="buttons">
            <g:actionSubmit class="save" action="atualizarAlteracoes" value="Gravar alterações"/>
        </fieldset>
    </sec:ifAllGranted>
    </div>
</g:form>
</body>
</html>
