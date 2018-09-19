<%@ page import="org.apoiasuas.CustomizacoesService" %>
%{--
<div class="sessao-detalhes">

    <div class="cabecalho-sessao">
        <a href="javascript:void(0)" class="left" onclick="foldSessao(this);">
            <asset:image src="usecases/tres-pontos-w.png" width="32" height="32"/>
            Outras informações
            <asset:image src="down-w.png" class="imagem-fold" width="16" height="16"/>
        </a>
        <a href="javascript:void(0)" class="right" title="clique para exibir/esconder todas as sessões" onclick="foldSessaoTodos(this);">fool</a>
    </div>
--}%

    %{--<div class="conteudo-sessao">--}%

        <div class="tamanho-memo fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.informacoesComplementares', 'error')} ">
            <label>Informações complementares da família e do domicílio</label>
            <g:textArea name="detalhe.informacoesComplementares" rows="6" value="${localDtoFamilia.mapaDetalhes['informacoesComplementares']}"/>
        </div>

    %{--</div> --}%
%{--class="conteudo-sessao"--}%

%{--</div> --}%
%{--class="sessao-detalhes"--}%
