<%@ page import="org.apoiasuas.cidadao.FamiliaController" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="emissao.formularios"/></title>
    <asset:stylesheet src="emissaoFormulario/emissao-formulario.less"/>
</head>


<body>

<g:render template="/baixarArquivo"/>

<div id="edit-fool" class="content scaffold-edit" role="main">
    <h1>
        Deseja atualizar o cadastro
        ${cidadaoSelecionado ?
                'de "'+cidadaoSelecionado.nomeCompleto+'"' :
                'da família "cad '+familiaSelecionada.cad+'"'}
        com os dados fornecidos no formulário?
    </h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <g:form class="form">
        <fieldset class="form">
            <g:hiddenField name="formularioCommand.id" value="${formulario.id}"/>
            <g:hiddenField name="formularioCommand.idFormularioEmitido" value="${formulario.formularioEmitido.id}"/>
            <g:hiddenField name="formularioCommand.idCidadao" value="${formulario.cidadao?.id}"/>
            <g:hiddenField name="formularioCommand.idFamilia" value="${formulario.familia?.id}"/>

%{--
            <div class="fieldcontain">
                Deseja atualizar o cadastro
                ${formulario.cidadao ? 'de "'+formulario.cidadao.nomeCompleto+'"' : 'da família de "'+ formulario.familia.referencia.nomeCompleto+'"'}
                com os dados fornecidos no formulário?
            </div>
--}%
            <g:set var="ordCampos" value="${0}"/>
            <g:each in="${camposAfetados.groupBy { it.campoFormulario.origem.descricao }}" var="camposAfetadosNoTipo">
                <g:if test="${camposAfetadosNoTipo.key}"><fieldset class="embedded"><legend>${camposAfetadosNoTipo.key}</legend></g:if>

                    <g:each in="${camposAfetadosNoTipo.value}" var="campoAfetado">
                        <div class="fieldcontain" style="display: block">
                            <label>${campoAfetado.campoFormulario.descricao}</label>
                            <g:checkBox name="formularioCommand.campos[${ordCampos}].id" checked="true" value="${campoAfetado.campoFormulario.id}"/>
                            <g:hiddenField name="formularioCommand.campos[${ordCampos}].novoConteudo" value="${campoAfetado.novoContedudo}"/>
        %{--${campoAfetado.campoFormulario.origem}.${campoAfetado.nomeCampoPersistente} (${campoAfetado.campoFormulario.descricao}): ${campoAfetado.conteudoAnterior ?: '(vazio)'} => ${campoAfetado.novoContedudo ?: '(vazio)'}--}%

                            de <span class="property-value" style="display: inline;">${campoAfetado.conteudoAnterior ?: '(vazio)'}</span>
                            para <span class="property-value" style="display: inline;">${campoAfetado.novoContedudo ?: '(vazio)'}</span>
                        </div>
                        <g:set var="ordCampos" value="${ordCampos+1}"/>
                    </g:each>
                <g:if test="${camposAfetadosNoTipo.key}"></fieldset></g:if>
            </g:each>

            <div class="fieldcontain">
                obs: desmarque as informações que não quiser atualizar ou escolha "Não Atualizar" para descartar todas
            </div>
        </fieldset>

        <fieldset class="buttons">
            <g:actionSubmit action="gravarAlteracoes" class="save" value="Atualizar Cadastro"/>
            <g:actionSubmit action="cancelarAlteracoes"  class="cancel" value="Não Atualizar"/>
        </fieldset>

    </g:form>

</div>

</body>
</html>
