<%@ page import="org.apoiasuas.cidadao.Cidadao" %>
<div class="sessao-detalhes">

    <div class="cabecalho-sessao">
        <a href="javascript:void(0)" class="left" onclick="foldSessao(this);">
            <asset:image src="usecases/internet-w.png" width="32" height="32"/>
            Redes Sociais
            <asset:image src="down-w.png" class="imagem-fold" width="16" height="16"/>
        </a>
        <a href="javascript:void(0)" class="right" title="clique para exibir/esconder todas as sessões" onclick="foldSessaoTodos(this);">fool</a>
    </div>

    <div class="conteudo-sessao">

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.email">
            <label>Email</label>
            <g:textField name="${prefixo}detalhe.email" size="60" maxlength="100"
                         value="${localDtoCidadao.mapaDetalhes['email']}"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.facebook">
            <label>Facebook</label>
            <g:textField name="${prefixo}detalhe.facebook" size="60" maxlength="100"
                         value="${localDtoCidadao.mapaDetalhes['facebook']}"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.instagram">
            <label>Instagram</label>
            <g:textField name="${prefixo}detalhe.instagram" size="60" maxlength="100"
                         value="${localDtoCidadao.mapaDetalhes['instagram']}"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.whatsapp">
            <label>Whatsapp</label>
            <g:textField name="${prefixo}detalhe.whatsapp" size="60" maxlength="100"
                         value="${localDtoCidadao.mapaDetalhes['whatsapp']}"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain class="tamanho-memo" bean="${localDtoCidadao}" field="detalhe.outrasRedesSociais">
            <label>Outras</label>
            <g:textArea name="${prefixo}detalhe.outrasRedesSociais" rows="6" value="${localDtoCidadao.mapaDetalhes['outrasRedesSociais']}"/>
        </g:fieldcontain>

    </div>

</div>