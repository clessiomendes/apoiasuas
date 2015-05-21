<%@ page import="org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.report.FormatoRelatorio; org.apoiasuas.cidadao.Familia" %>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Formulário para Identidade</title>
</head>

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
<h1>Formulário para Identidade</h1>
<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>

<g:form name="formFamilia">

    <input type="hidden" id="idCidadao" name="idCidadao" value="${dtoCidadao?.id}">

    <ol class="property-list familia">
        %{--CODIGO LEGADO--}%
        <li class="fieldcontain">
            <span id="codigoLegado-label" class="property-label">
                <g:message code="familia.codigoLegado.label" default="Codigo Legado"/>
            </span>

%{--
            <span class="property-value" aria-labelledby="codigoLegado-label">
                <g:fieldValue bean="${dtoCidadao?.familia}" field="codigoLegado"/>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Formato:&nbsp;
                <g:radioGroup
                        values="[FormatoFormulario.PDF.extensaoArquivo, FormatoFormulario.DOCX.extensaoArquivo, FormatoFormulario.XHMTL.extensaoArquivo]"
                        labels="[FormatoFormulario.PDF.extensaoArquivo, FormatoFormulario.DOCX.extensaoArquivo, FormatoFormulario.XHMTL.extensaoArquivo]"
                        name="formatoRelatorio"
                        value="${FormatoFormulario.DOCX.extensaoArquivo}">
                    ${it.radio} ${it.label}
                </g:radioGroup>
            </span>
--}%

        </li>

        %{--ENDEREÇO--}%
        <g:render template="apagar/formEnderecoFormulario" model="${[dtoFamilia: dtoFamilia, dtoFormulario: dtoFormulario]}"/>

        %{--MEMBROS INDIVIDUAIS--}%
        <g:render template="apagar/formCidadaoFormulario" model="${[dtoCidadao: dtoCidadao, dtoFormulario: dtoFormulario]}"/>

        <div class="fieldcontain">
            <label for="dataConcessao">
                <g:message code="relatorioCidadao.dataConcessao" default="Data da concessão"/>
            </label>
            %{--<g:textField name="dataConcessao" value="${}"/>--}%
            %{--TODO: utilizar um componente visual de escolha de datas ou permitir digitacao livre--}%
            <g:datePicker name="dataConcessao" precision="day" value="${new Date()}"
                          noSelection="['': '']" default="${new Date()}"/>
        </div>

    </ol>

    <sec:ifAllGranted roles="${DefinicaoPapeis.USUARIO}">
        <fieldset class="buttons">
            %{-- TODO:Permitir gravar alteracoes dos dados cadastrais (se possivel, junto com a submissao da impressao)
                        <g:actionSubmit class="save" action="atualizarAlteracoes" value="Gravar alterações"/>
            --}%
            <g:actionSubmit class="save" action="imprimirGuiaIdentidade" value="Gerar formulário"/>
        </fieldset>
    </sec:ifAllGranted>
    </div>
</g:form>
</body>
</html>
