<%@ page import="org.apoiasuas.util.SimNao" %>
<div class="sessao-detalhes">

    <div class="cabecalho-sessao">
        <a href="javascript:void(0)" class="left" onclick="foldSessao(this);">
            <asset:image src="usecases/acompanhamento-w.png" width="32" height="32" />
            Público Prioritário
            <asset:image src="down-w.png" class="imagem-fold" width="16" height="16" />
        </a>
        <a href="javascript:void(0)" class="right" title="clique para exibir/esconder todas as sessões" onclick="foldSessaoTodos(this);">fool</a>
    </div>

    <div class="conteudo-sessao">

        <g:fieldcontain bean="${localDtoFamilia}" field="bolsaFamilia">
            <label>Beneficiária Bolsa Família</label>
            <palavra-chave>pbf</palavra-chave>
            <g:select name="bolsaFamilia" id="selectBolsaFamilia" from="${[true: SimNao.SIM.descricao, false: SimNao.NAO.descricao]}"
                      noSelection="['':'não sabe']" class="importante many-to-one select-sim-nao"
                      value="${localDtoFamilia.bolsaFamilia}" onchange="vinculosCamposFamilia(this)"/>
            <span>mar/2018</span>
        </g:fieldcontain>

        <g:fieldcontain class="pbf-sim" bean="${localDtoFamilia}" field="detalhe.descumprimentoPBF">
            <label>em Descumprimento no PBF</label>
            <palavra-chave>condicionalidade,bolsa familia</palavra-chave>
            <g:selectSimNao class="many-to-one select-sim-nao" bean="${localDtoFamilia}"
                            name="detalhe.descumprimentoPBF"/>
        </g:fieldcontain>

        <g:fieldcontain class="pbf-nao" bean="${localDtoFamilia}" field="exBolsaFamilia">
            <label>já participou do Bolsa Família</label>
            <g:select name="exBolsaFamilia" from="${[true: SimNao.SIM.descricao, false: SimNao.NAO.descricao]}"
                      value="${localDtoFamilia.exBolsaFamilia}" class="many-to-one select-sim-nao"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoFamilia}" field="bpc">
            <label>Beneficiária BPC</label>
            <palavra-chave>benefício de prestação continuada</palavra-chave>
            <g:select name="bpc" from="${[true: SimNao.SIM.descricao, false: SimNao.NAO.descricao]}"
                      value="${localDtoFamilia.bpc}" class="many-to-one select-sim-nao"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoFamilia}" field="detalhe.contraReferencia">
            <label>Contra-referência da PSE</label>
            <palavra-chave>proteção social especial</palavra-chave>
            <g:selectSimNao class="many-to-one select-sim-nao" bean="${localDtoFamilia}"
                            name="detalhe.contraReferencia"/>
        </g:fieldcontain>

    </div> %{--class="conteudo-sessao"--}%

</div> %{--class="sessao-detalhes"--}%
