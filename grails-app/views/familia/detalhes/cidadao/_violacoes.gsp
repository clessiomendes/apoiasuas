<div class="sessao-detalhes">

    <div class="cabecalho-sessao">
        <a href="javascript:void(0)" class="left" onclick="foldSessao(this);">
            <asset:image src="usecases/pare-mao-w.png" width="32" height="32"/>
            Violações
            <asset:image src="down-w.png" class="imagem-fold" width="16" height="16"/>
        </a>
        <a href="javascript:void(0)" class="right" title="clique para exibir/esconder todas as sessões" onclick="foldSessaoTodos(this);">fool</a>
    </div>

    <div class="conteudo-sessao">

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.violacao">
            <label>Pessoa vítima de (...)</label>
            <g:multiLookup bean="${localDtoCidadao}" name="${prefixo}detalhe.violacao"
                           help-tooltip="help.lookup.violacao" classeOpcao="opcao-multi-lookup"
                           classeCheckbox="opcaoViolacao" onchange="vinculosCamposCidadao(this);" />
        </g:fieldcontain>

        <g:fieldcontain class="divDetalheViolacao tamanho-memo" bean="${localDtoCidadao}" field="detalhe.detalhesViolacao">
            <label>
                Mais informações sobre a(s) violação(ões) <g:helpTooltip chave="help.detalhes.violacao"/>
            </label>
            <g:textArea name="${prefixo}detalhe.detalhesViolacao" rows="4"
                        value="${localDtoCidadao.mapaDetalhes['detalhesViolacao']}"/>
        </g:fieldcontain>

    </div>

</div>
