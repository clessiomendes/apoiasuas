<%@ page import="org.apoiasuas.cidadao.Cidadao" %>
<div class="sessao-detalhes">

    <div class="cabecalho-sessao">
        <a href="javascript:void(0)" class="left" onclick="foldSessao(this);">
            <asset:image src="usecases/tres-pontos-w.png" width="32" height="32"/>
            Outras Informações do membro familiar
            <asset:image src="down-w.png" class="imagem-fold" width="16" height="16"/>
        </a>
        <a href="javascript:void(0)" class="right" title="clique para exibir/esconder todas as sessões" onclick="foldSessaoTodos(this);">fool</a>
    </div>

    <div class="conteudo-sessao">

        <g:fieldcontain bean="${localDtoCidadao}" field="naturalidade">
            <label>Naturalidade - UF</label>
            <g:textField name="${prefixo}naturalidade" class="destinoSugestao1" size="30" maxlength="60" value="${localDtoCidadao?.naturalidade}"/>
            <g:textField name="${prefixo}UFNaturalidade" class="destinoSugestao2" size="2" maxlength="2" value="${localDtoCidadao?.UFNaturalidade}"/>
            <span class="sugestaoPreenchimento">
                <input type="button" class="speed-button-sugestao" onclick="clickSugestao(this);" title="preencher com informação padrão"/>
                <span class="origemSugestao1">${municipioLogado}</span> - <span class="origemSugestao2">${UFLogada}</span>
            </span>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.${Cidadao.CODIGO_NACIONALIDADE}">
            <label>Nacionalidade</label>
            <g:textField class="destinoSugestao1" name="${prefixo}detalhe.${Cidadao.CODIGO_NACIONALIDADE}" size="10" maxlength="30"
                         value="${localDtoCidadao.mapaDetalhes[Cidadao.CODIGO_NACIONALIDADE]}"/>
            <span class="sugestaoPreenchimento">
                <input type="button" class="speed-button-sugestao" onclick="clickSugestao(this);" title="preencher com informação padrão"/>
                <span class="origemSugestao1">brasileira</span>
            </span>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="sexo">
            <label>Sexo</label>
            <g:select name="${prefixo}sexo" from="${org.apoiasuas.lookup.Sexo.asMap()}"
                      value="${localDtoCidadao.sexo}" class="importante many-to-one"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.orientacaoSexual">
            <label>Identidade de Gênero / Orientação Sexual (...)</label>
            <g:selectLookup class="selectIdentidadeSexual many-to-one" bean="${localDtoCidadao}" name="${prefixo}detalhe.orientacaoSexual"
                            onchange="vinculosCamposCidadao(this)"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.outraOrientacao" class="divOutraIdentidadeSexual">
            <label>Especificar identidade / orientação</label>
            <g:textField name="${prefixo}detalhe.outraOrientacao" size="20" maxlength="40"
                         value="${localDtoCidadao.mapaDetalhes['outraOrientacao']}"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.corRaca">
            <label>Raça / Cor</label>
            <g:selectLookup class="importante many-to-one" bean="${localDtoCidadao}" name="${prefixo}detalhe.corRaca"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.estadoCivil">
            <label>Estado Civil</label>
            <g:selectLookup class="many-to-one" bean="${localDtoCidadao}" name="${prefixo}detalhe.estadoCivil"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain class="tamanho-memo" bean="${localDtoCidadao}" field="detalhe.informacoesComplementares">
            <label>Informações complementares do membro familiar</label>
            <g:textArea name="${prefixo}detalhe.informacoesComplementares" rows="6" value="${localDtoCidadao.mapaDetalhes['informacoesComplementares']}"/>
        </g:fieldcontain>

    </div>

</div>