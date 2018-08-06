<%@ page import="org.apoiasuas.util.SimNao" %>
<div class="sessao-detalhes">

    <div class="cabecalho-sessao">
        <a href="javascript:void(0)" class="left" onclick="foldSessao(this);">
            <asset:image src="usecases/trabalho-w.png" width="32" height="32"/>
            Trabalho e Renda
            <asset:image src="down-w.png" class="imagem-fold" width="16" height="16"/>
        </a>
        <a href="javascript:void(0)" class="right" title="clique para exibir/esconder todas as sessões" onclick="foldSessaoTodos(this);">fool</a>
    </div>

    <div class="conteudo-sessao">

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.situacaoTrabalho" style="min-width: inherit">
            <label>Situação no Mercado de Trabalho</label>
            <palavra-chave>emprego,ocupação,desemprego,desempregado,aposentadoria,pensão</palavra-chave>
            <g:selectLookup bean="${localDtoCidadao}" name="${prefixo}detalhe.situacaoTrabalho" class="importante-maior-18 many-to-one"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.rendaMensal">
            <label>Renda mensal (R$)<g:helpTooltip chave="help.renda.mensal"/></label>
            <palavra-chave>salário,benefício,aposentadoria,pensão,dinheiro</palavra-chave>
            <g:textField class="txtRendaMensal integerMask" name="${prefixo}detalhe.rendaMensal" size="5" maxlength="10"
                         value="${localDtoCidadao.mapaDetalhes['rendaMensal']}"/>
            %{-- campo booleano para detalhe.semRenda (informar o tipo em um hidden e tratar o valor aa partir do enum SimNao) --}%
            <input type="hidden" name="${prefixo}detalhe.semRenda_tipo" value="BOOLEAN" id="${prefixo}detalhe.semRenda_tipo">
            <g:checkBox class="checkSemRenda" name="${prefixo}detalhe.semRenda"
                        onclick="vinculosCamposCidadao(this)" value="${SimNao.SIM}"
                        checked="${localDtoCidadao?.mapaDetalhes['semRenda']?.asBoolean()}"/> sem renda
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.ocupacao">
            <label>Ocupação atual</label>
            <g:textField name="${prefixo}detalhe.ocupacao" size="20" maxlength="60" value="${localDtoCidadao.mapaDetalhes['ocupacao']}"/>
        </g:fieldcontain>

    </div>

</div>
