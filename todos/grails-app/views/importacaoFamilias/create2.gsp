<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Importar Cadastro de Famílias</title>
</head>

<body>
<h1><g:message code="importar.cadastro.de.familias"/></h1>
<g:message code="selecione.informacoes.correspondentes"/><br>

<g:hasErrors bean="${wrapperCabecalhos}">
    <ul class="errors" role="alert">
        <g:eachError bean="${wrapperCabecalhos}" var="error">
            <g:if test="${error in org.springframework.validation.FieldError}"/>
            <g:else>
                <li><g:message error="${error}"/></li>
            </g:else>
        </g:eachError>
    </ul>
</g:hasErrors>

<g:form>

    <fieldset class="buttons">
        <g:actionSubmit class="save" action="concluirImportacao" value="${message(code: "botao.processar")}"/>
    </fieldset>

    <table>
        <thead>
        <tr>

            <td><g:message code="coluna.na.planilha"/></td>

            <td><g:message code="correspondencia.no.sistema"/></td>

        </tr>
        </thead>

        <tbody>
        %{--Percorre todas as COLUNAS DA PLANILHA para montar os selects--}%
        %{--<g:each in="${request.mapaColunasDisponiveisImportacao.keySet()}" status="i" var="colunaImportada">--}%
        <g:each in="${wrapperCabecalhos.colunasImportadas}" status="i" var="colunaImportada">

            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td>${colunaImportada.nome}<br>
                    Ex: "${colunaImportada.conteudoExemplo}"</td>

                <td>

                    <div class="fieldcontain ${hasErrors(bean: wrapperCabecalhos, field: 'colunasImportadas[' + i + ']', 'error')}">

                    %{--
                    TODO Mudar a cor dos selects que ainda não foram relacionados a nenhum campo do banco de dados. Dica: http://codepen.io/anon/pen/FafrG
                    --}%

                        <g:select name="${'colunasImportadas[' + i + '].campoBDSelecionado'}" noSelection="${['': '']}"
                                  from="${wrapperCabecalhos.camposBDDisponiveis}"
                                  keys="${wrapperCabecalhos.camposBDDisponiveis}"
                                  value="${colunaImportada.campoBDSelecionado}"/>

                    </div>

                    %{--Campo oculto contendo o valor da COLUNA DA PLANILHA, a ser utilizado no processamento do post.
                    Não utilizamos este valor para o nome do select porque ele pode conter caracteres especiais--}%
                    <g:hiddenField name="${'colunasImportadas[' + i + '].nome'}" value="${colunaImportada.nome}"/>
                </td>

            </tr>

        </g:each>
        </tbody>

    </table>

    <fieldset class="buttons">
        <g:actionSubmit class="save" action="concluirImportacao" value="${message(code: "botao.processar")}"/>
    </fieldset>

</g:form>

</body>
</html>
