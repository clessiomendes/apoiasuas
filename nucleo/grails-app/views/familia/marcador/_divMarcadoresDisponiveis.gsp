%{--
Template de exibição dos marcadores disponiveis. Usado diretamente na montagem da tela inicial e, em seguida, via ajax, para atualizar os marcadores
disponiveis a cada nova inclusao de marcador.

Passar como parametros do modelo:
- marcadoresDisponiveis (ex:programasDisponiveis) lista cujo tipo implementa Marcador (ex: Programa) contendo todos os marcadores a exibir
- label (ex:programasDisponiveis) para ser usado como parte do nome nos diferentes elementos necessarios para construir o componente
- nomeDiv (ex:divEditPrograma) nome do elemento div que contem o componente de janela modal de edicao dos detalhes do marcador nesta familia
- classeMarcador (ex:marcadores-programa) classe CSS para colorir os marcadores exibidos
--}%
<g:each in="${marcadoresDisponiveis}" var="marcadordisp" status="countMarcadores">
    <span class="${classeMarcador} clonarEmNovoMarcador">
        <% org.apoiasuas.marcador.Marcador marcador = marcadordisp; %>
        <g:checkBox onchange="if (this.checked) document.getElementById('link${label}[${countMarcadores}]').click();"
                    class="check-marcadores" name="${label}[${countMarcadores}].habilitado"
                    value="${marcador.selecionado}"/>
        %{--onclick - abrir a janela modal de edição correspondente à partir do click no nome do marcador--}%
        &nbsp;<a class="descricao-marcadores" id="link${label}[${countMarcadores}]" href="javascript:void(0);"
           onclick="${nomeDiv}.janelaEditMarcador(this.parentElement);">${marcador.descricao}</a>
        <g:hiddenField name="${label}[${countMarcadores}].id" value="${marcador.id}"/>
        <g:hiddenField class='observacao-marcadores' name="${label}[${countMarcadores}].observacao"
                       value="${marcador.observacao}"/>
        <g:hiddenField class='tecnico-marcadores' name="${label}[${countMarcadores}].tecnico"
                       value="${marcador.tecnico?.id}"/>
    </span>
</g:each>
