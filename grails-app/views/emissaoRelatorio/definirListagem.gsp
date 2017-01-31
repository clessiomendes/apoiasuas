<%@ page import="org.apoiasuas.marcador.Programa; org.apoiasuas.programa.Programa; org.apoiasuas.util.StringUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Listagens</title>
</head>

<body>

<div id="edit-fool" class="content scaffold-edit" role="main">
    <h1>Emissão de Listagens</h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <g:hasErrors bean="${definicaoListagem}">
        <ul class="errors" role="alert">
            <g:eachError bean="${definicaoListagem}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>

    %{--<g:form action="downloadListagem">--}%
    <g:form>
        <fieldset class="form">

            %{--Listar--}%
            <fieldset class="embedded" style="padding: 10px"><legend class="collapsable" style="cursor:pointer;">Listar:</legend>
                <g:radio name="membros" value="" checked="true"/> apenas a referência <br>
                <g:radio name="membros" value="true"/> todos os membros
            </fieldset>

            %{--Programas--}%
            <fieldset class="embedded" style="padding: 10px"><legend class="collapsable" style="cursor:pointer;">Restringir aos programas:</legend>
                <g:each in="${programasDisponiveis}" var="progdisp" status="i">
                    <% org.apoiasuas.marcador.Programa programaDisponivel = progdisp %>
                    <g:checkBox name="programasdisponiveis[${i}].selected" value="${programaDisponivel.selected}"/>
                    <g:hiddenField name="programasdisponiveis[${i}].id" value="${programaDisponivel.id}"/>
                    ${programaDisponivel.nome ?: programaDisponivel.sigla} <br>
                </g:each>
            </fieldset>

            %{--Filtrar--}%
            <fieldset class="embedded" style="padding: 10px"><legend class="collapsable"
                                                                     style="cursor:pointer;">Filtrar:</legend>
                Técnico de referência:
                <g:select name="tecnicoReferencia" from="${operadores.entrySet()}" optionKey="key" optionValue="value" noSelection="['-1': '']"/>
                <br><br>
                Idade de
                <div style="display: inline" class="fieldcontain ${hasErrors(bean: definicaoListagem, field: 'idadeInicial', 'error')} ">
                    <g:textField name="idadeInicial" size="1"/> a
                </div>
                <div style="display: inline" class="fieldcontain ${hasErrors(bean: definicaoListagem, field: 'idadeFinal', 'error')} ">
                    <g:textField name="idadeFinal" size="1"/> anos
                </div>
            </fieldset>

        </fieldset>%{--class="form"--}%

        <fieldset class="buttons">
            %{--<g:submitButton name="list" class="edit" value="Gerar listagem" />--}%
            <g:actionSubmit value="Download de planilha" action="downloadListagem"/>
            <g:actionSubmit value="Exibir na tela" action="exibeListagem"/>
        </fieldset>

    </g:form>

</div>

</body>
</html>
