<%@ page import="org.apoiasuas.lookup.Escolaridade; org.apoiasuas.util.SimNao" %>
<div class="sessao-detalhes">

    <div class="cabecalho-sessao">
        <a href="javascript:void(0)" class="left" onclick="foldSessao(this);">
            <asset:image src="usecases/educacao-w.png" width="32" height="32"/>
            Educação
            <asset:image src="down-w.png" class="imagem-fold" width="16" height="16"/>
        </a>
        <a href="javascript:void(0)" class="right" title="clique para exibir/esconder todas as sessões" onclick="foldSessaoTodos(this);">fool</a>
    </div>

    <div class="conteudo-sessao">

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.estudando">
            <label>Estudando / Creche (...)</label>
            <palavra-chave>escola,ensino,colegio,faculdade,universidade,creche,umei,eja,supletivo</palavra-chave>
            <g:selectSimNao class="selectEstudando importante-menor-18 many-to-one select-sim-nao" bean="${localDtoCidadao}"
                            name="${prefixo}detalhe.estudando" onchange="vinculosCamposCidadao(this)"/>
        </g:fieldcontain>

        <g:fieldcontain class="estudando" bean="${localDtoCidadao}" field="detalhe.escola">
            <label>Escola ou Instituição<g:helpTooltip chave="help.nome.escola"/></label>
            <g:textField name="${prefixo}detalhe.escola" size="20" maxlength="60" value="${localDtoCidadao.mapaDetalhes['escola']}"/>
        </g:fieldcontain>

        <g:fieldcontain class="estudando" bean="${localDtoCidadao}" field="detalhe.eja">
            <label>EJA<g:helpTooltip chave="help.estudando.eja"/></label>
            <g:selectSimNao name="${prefixo}detalhe.eja" class="many-to-one select-sim-nao" bean="${localDtoCidadao}"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="analfabeto">
            <label>Analfabeto</label>
            <palavra-chave>iletrado,escrita,escrever,ler</palavra-chave>
            <g:select name="${prefixo}analfabeto" from="${[true: SimNao.SIM.descricao, false: SimNao.NAO.descricao]}"
                      value="${localDtoCidadao.analfabeto}" class="many-to-one select-sim-nao"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="escolaridade">
            <label>Escolaridade</label>
            <palavra-chave>ensino</palavra-chave>
            <g:select name="${prefixo}escolaridade" from="${org.apoiasuas.lookup.Escolaridade.asMap()}"
                      value="${localDtoCidadao.escolaridade}" class="importante many-to-one"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.anoEscolar">
            <label>Ano/Período<g:helpTooltip chave="help.ano.escolar"/></label>
            <palavra-chave>série</palavra-chave>
            <g:textField name="${prefixo}detalhe.anoEscolar" size="2" maxlength="10" value="${localDtoCidadao.mapaDetalhes['anoEscolar']}"/>
        </g:fieldcontain>

    </div>

</div>
