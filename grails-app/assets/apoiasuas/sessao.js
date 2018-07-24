/**
 * mostra ou esconde uma sessao especifica
 */
function foldSessao(elementoSessao) {
    var $mae = $(elementoSessao).closest('.sessao-detalhes');

    //todos os filhos imediatos de sessao-detalhes que nao sao cabecalho-sessao
    //var $conteudoSessao = $mae.children(':not(.cabecalho-sessao)');
    var $conteudoSessao = $mae.children('.conteudo-sessao');
    //determina se a sessao estava visivel antes do cabecalho dela ser clicado
    var visiveis = $conteudoSessao.is(':visible')

    //troca o icone de seta pra baixo/cima, tanto icone simples (img) quanto do icone duplo (a background-image)
    var $imagem = $mae.find('.imagem-fold');
    var $link = $mae.find('.right');
    $imagem.attr('src', visiveis ? imgVerMais : imgVerMenos);
    $link.css('background-image', visiveis ? imgVerMaisTodos : imgVerMenosTodos);

    //mostra ou esconde a sessao
    $conteudoSessao.toggle(500);
}

/**
 * mostra ou esconde todas as sessoes
 */
function foldSessaoTodos(elementoSessao) {
    var $mae = $(elementoSessao).closest('.sessao-detalhes');

    //todos os filhos imediatos de sessao-detalhes que nao sao cabecalho-sessao
    var $irmaos = $mae.children(':not(.cabecalho-sessao)');
    var visiveis = $($irmaos[0]).is(':visible')

    //troca todos os icones de seta pra baixo/cima EM TODOS OS CABEÇALHOS, tanto icone simples (img) quanto do icone duplo (a background-image)
    var $imagens = $('.sessao-detalhes .imagem-fold');
    var $links = $('.sessao-detalhes .right');
    $imagens.attr('src', visiveis ? imgVerMais : imgVerMenos);
    $links.css('background-image', visiveis ? imgVerMaisTodos : imgVerMenosTodos);

    //mostra ou esconde todas as sessoes
    if (visiveis)
        $('.sessao-detalhes > :not(.cabecalho-sessao)').hide(500)
    else
        $('.sessao-detalhes > :not(.cabecalho-sessao)').show(500);
}
