<% org.apoiasuas.importacao.ResumoImportacaoDTO resumoImportacaoDTO = resumoImportacaoDTO %>

<table>
    <thead>
    <tr>

        <td>Linha</td>
        <td>Cod</td>
        <td>Conteúdo na planilha</td> %{--TODO Disponibilizar botão expandir/retrair todos --}%
        <td>Mensagem de erro</td> %{--TODO Disponibilizar botão expandir/retrair todos --}%

    </tr>
    </thead>

    <tbody>
    %{--Lista as primeiras inconsistencias encontradas na migracao --}%
    <g:each in="${resumoImportacaoDTO.inconsistencias}" status="i" var="erro">

        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">%{--TODO Cortar a partir do centesimo erro, indicando a contagem ? --}%

            <td>${erro.numeroLinhaPlanilha}</td>
            <td>${erro.codFamilia}</td>
            <td>${erro.conteudoLinhaPlanilha}</td> %{--TODO Usar javascript para exibir o conteudo conteudoLinhaPlanilha somente se o usuario clicar em +- --}%
            <td><g:if test="${erro.familia}">Obs: Os cidadãos dessa família não foram importados<br></g:if>
                <g:each in="${erro.excecoes}" var="excecao">
                    ${excecao}<br>
                </g:each>
            </td> %{--TODO Usar javascript para exibir o conteudo das exccoes somente se o usuario clicar em +- --}%

        </tr>

    </g:each>
    </tbody>

</table>

<g:if test="${resumoImportacaoDTO.totalErros > org.apoiasuas.importacao.ResumoImportacaoDTO.MAX_ERROS_EXIBIDOS}">
    <br>... Detectados mais ${resumoImportacaoDTO.totalErros - org.apoiasuas.importacao.ResumoImportacaoDTO.MAX_ERROS_EXIBIDOS} erros que não estão sendo exibidos<br>
</g:if>
