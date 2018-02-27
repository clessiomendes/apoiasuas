<%@ page import="org.apoiasuas.CustomizacoesService" %>
<div class="sessao-detalhes">

    <div class="cabecalho-sessao">
        <a href="javascript:void(0)" class="left" onclick="foldSessao(this);">
            <asset:image src="usecases/tres-pontos-w.png" width="32" height="32"/>
            Outras informações
            <asset:image src="down-w.png" class="imagem-fold" width="16" height="16"/>
        </a>
        <a href="javascript:void(0)" class="right" title="clique para exibir/esconder todas as sessões" onclick="foldSessaoTodos(this);">fool</a>
    </div>

    <div class="conteudo-sessao">

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.centroSaude', 'error')} ">
            <label>Centro de Saúde<g:helpTooltip chave="help.UBS.domicilio"/></label>
            <g:textField name="detalhe.centroSaude" size="20" maxlength="60" value="${localDtoFamilia.mapaDetalhes['centroSaude']}"/>
        </div>

        <g:fieldcontain bean="${localDtoFamilia}" field="detalhe.equipeSaude">
            <label>Equipe de Saúde da Família</label>
            <g:textField name="detalhe.equipeSaude" size="20" maxlength="60" value="${localDtoFamilia.mapaDetalhes['equipeSaude']}"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoFamilia}" field="detalhe.cras"
                hidefrom="${[CustomizacoesService.Codigos.BELO_HORIZONTE_HAVAI_VENTOSA, CustomizacoesService.Codigos.BELO_HORIZONTE_VISTA_ALEGRE]}">
            <label>CRAS<g:helpTooltip chave="help.CRAS.domicilio"/></label>
            <g:textField name="detalhe.cras" size="20" maxlength="60" value="${localDtoFamilia.mapaDetalhes['cras']}"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.familiaQuilombola', 'error')} ">
            <label>Família Quilombola (...)</label>
            <palavra-chave>comunidade</palavra-chave>
            <g:selectSimNao id="selectQuilombola" bean="${localDtoFamilia}" name="detalhe.familiaQuilombola" class="many-to-one select-sim-nao"
                            onchange="vinculosCamposFamilia(this);"/>
        </div>

        <div id="divComunidadeQuilombola" class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.comunidadeQuilombola', 'error')} ">
            <label>Comunidade Quilombola</label>
            <g:textField name="detalhe.comunidadeQuilombola" size="20" maxlength="60" value="${localDtoFamilia.mapaDetalhes['comunidadeQuilombola']}"/>
        </div>

        <div class="nova-linha"></div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.familiaIndigena', 'error')} ">
            <label>Família Indígena (...)</label>
            <palavra-chave>povo,índio</palavra-chave>
            <g:selectSimNao id="selectIndigena" bean="${localDtoFamilia}" name="detalhe.familiaIndigena" class="many-to-one select-sim-nao"
                            onchange="vinculosCamposFamilia(this);"/>
        </div>

        <div id="divPovoIndigena" class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.povoIndigena', 'error')} ">
            <label>Povo Indígena</label>
            <palavra-chave>índio</palavra-chave>
            <g:textField name="detalhe.povoIndigena" size="20" maxlength="60" value="${localDtoFamilia.mapaDetalhes['povoIndigena']}"/>
        </div>

        <g:fieldcontain bean="${localDtoFamilia}" field="detalhe.reservaIndigena" hidefrom="${[CustomizacoesService.Codigos.BELO_HORIZONTE]}">
            <label>Reserva Indígena</label>
            <palavra-chave>índio</palavra-chave>
            <g:textField name="detalhe.reservaIndigena" size="20" maxlength="60" value="${localDtoFamilia.mapaDetalhes['reservaIndigena']}"/>
        </g:fieldcontain>

        <div class="tamanho-memo fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.informacoesComplementares', 'error')} ">
            <label>Informações complementares da família e do domicílio</label>
            <g:textArea name="detalhe.informacoesComplementares" rows="6" value="${localDtoFamilia.mapaDetalhes['informacoesComplementares']}"/>
        </div>

    </div> %{--class="conteudo-sessao"--}%

</div> %{--class="sessao-detalhes"--}%
