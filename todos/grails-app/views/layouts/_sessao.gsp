<div class="sessao-detalhes">
    <div class="cabecalho-sessao">
        <a href="javascript:void(0)" class="left" onclick="foldSessao(this);">
            <asset:image src="${icone}" width="32" height="32"/>
            ${titulo}
            <asset:image src="down-w.png" class="imagem-fold" width="16" height="16"/>
        </a>
        <a href="javascript:void(0)" class="right" title="clique para exibir/esconder todas as sessões" onclick="foldSessaoTodos(this);">fool</a>
    </div>

    <div class="conteudo-sessao">
        ${raw(body())}
    </div>
</div>

