<%@ page import="org.apoiasuas.formulario.PreDefinidos; org.apoiasuas.formulario.Formulario; org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.cidadao.Familia" %>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>${dtoFormulario.nome}</title>
</head>

<body>

<g:javascript>
    /**
    * Preenche automaticamente logradouros à partir do que já está na base de dados
    */
    $(document).ready(function() {
        $('.listaLogradouros').autocomplete({
            delay: 700, minLength: 2, source: '<g:createLink controller='familia' action='obtemLogradouros'/>'
        });
    });
</g:javascript>

<div class="nav" role="navigation">
    <ul><li><g:link controller="emissaoFormulario" class="formulario" action="escolherFormulario">
            Voltar
    </g:link></li></ul>
</div>

<style>
    .icone-formulario {
        vertical-align: middle;
        width: 50px;
        height: 50px;
    }
</style>

<h1>
    <asset:image src="formulario/${dtoFormulario.formularioPreDefinido?.toString()?.toLowerCase()}.png" class="icone-formulario" />
    <span style="vertical-align: middle">${dtoFormulario.nome}</span>
</h1>

<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>


<g:each in="${dtoFormulario.campos.sort { it.id } .findAll { it.mensagemErro }}" var="campo">
    <ul class="errors" role="alert">
        %{--<li><g:message error="${mensagemErro}"/></li>--}%
        <li><g:message error="${campo.mensagemErro}"/></li>
    </ul>
</g:each>

<g:form action="imprimirFormulario" elementId="formEmissaoFormulario">

    <input type="hidden" id="cidadao.id" name="cidadao.id" value="${dtoFormulario?.cidadao?.id}">
    <input type="hidden" id="familia.id" name="familia.id" value="${dtoFormulario?.familia?.id}">
    <input type="hidden" id="idFormulario" name="idFormulario" value="${dtoFormulario?.id}">
    <input type="hidden" id="formularioEmitido.id" name="formularioEmitido.id" value="${dtoFormulario?.formularioEmitido?.id}">

    <ol class="property-list">

        <g:if test="${templateCamposCustomizados}">%{-- renderiza um template customizado para o formulario sendo editado --}%
            <g:render template="${templateCamposCustomizados}" model="${['dtoFormulario', dtoFormulario]}"/>
        </g:if>
        <g:else>
            <g:agrupaCampos lista="${dtoFormulario.getCamposOrdenados(true)}" campoGrupo="grupo" status="i" var="campo">
                    <g:divCampoFormulario campoFormulario="${campo}" focoInicial="${i == 1}"/>
            </g:agrupaCampos>
        </g:else>

    </ol>

    <fieldset class="buttons sticky-footer">
        %{--Varias opcoes de modelo: abre para escolha do operador--}%
        <g:if test="${dtoFormulario.modelos.size() > 1}">
            <span class="label-campos" style="margin-left: 1em">Modelo</span>
            <g:select style="max-width: 20em" id="idModelo" name="idModelo" from="${dtoFormulario.modelos.sort{it.id}}" optionKey="id"
                          optionValue="descricao" value="${idModelo ?: dtoFormulario.modeloPadrao.id}" class="many-to-one" forcarEscolha="true"/>
        </g:if>
        %{--modelo unico:--}%
        <g:else>
            <input type="hidden" id="idModelo" name="idModelo" value="${idModelo ?: dtoFormulario.modeloPadrao.id}">
        </g:else>
        <input type="button" class="print" value="Gerar formulário" onclick="submitProtegido(this.form)"/>
    </fieldset>
    </div>
</g:form>
</body>
</html>
