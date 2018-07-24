<%@ page import="org.apoiasuas.CustomizacoesService" %>
<div class="sessao-detalhes">

    <div class="cabecalho-sessao">
        <a href="javascript:void(0)" class="left" onclick="foldSessao(this);">
            <asset:image src="usecases/domicilio-w.png" width="32" height="32"/>
            Infraestrutura
            <asset:image src="down-w.png" class="imagem-fold" width="16" height="16"/>
        </a>
        <a href="javascript:void(0)" class="right" title="clique para exibir/esconder todas as sessões" onclick="foldSessaoTodos(this);">fool</a>
    </div>

    <div class="conteudo-sessao">

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.riscoGeologico', 'error')} ">
            <label>Risco Geológico<g:helpTooltip chave="help.risco.geologico"/></label>
            <g:selectSimNao bean="${localDtoFamilia}" name="detalhe.riscoGeologico" class="many-to-one select-sim-nao"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.riscoConstrutivo', 'error')} ">
            <label>Risco Construtivo<g:helpTooltip chave="help.risco.construtivo"/></label>
            <g:selectSimNao bean="${localDtoFamilia}" name="detalhe.riscoConstrutivo" class="many-to-one select-sim-nao"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.tipoConstrucao', 'error')} ">
            <label>Tipo de construção<g:helpTooltip chave="help.tipo.construcao"/></label>
            <g:selectLookup bean="${localDtoFamilia}" name="detalhe.tipoConstrucao" class="many-to-one"/>
            <span style="font-size: .7em">mar/<br>2018</span>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.tipoEletricidade', 'error')} ">
            <label>Eletricidade</label>
            <g:selectLookup bean="${localDtoFamilia}" name="detalhe.tipoEletricidade" class="many-to-one"/>
            %{--<g:selectSimNao bean="${localDtoFamilia}" name="detalhe.eletricidade" class="many-to-one select-sim-nao"/>--}%
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.coletaLixo', 'error')} ">
            <label>Coleta de Lixo<g:helpTooltip chave="help.coleta.lixo"/></label>
            <g:selectSimNao bean="${localDtoFamilia}" name="detalhe.coletaLixo" class="many-to-one select-sim-nao"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.aguaTratada', 'error')} ">
            <label>Água Tratada<g:helpTooltip chave="help.agua.tratada"/></label>
            <g:selectSimNao bean="${localDtoFamilia}" name="detalhe.aguaTratada" class="many-to-one select-sim-nao"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.redeEsgoto', 'error')} ">
            <label>Rede de Esgoto<g:helpTooltip chave="help.rede.esgoto"/></label>
            <g:selectSimNao bean="${localDtoFamilia}" name="detalhe.redeEsgoto" class="many-to-one select-sim-nao"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.ruaPavimentada', 'error')} ">
            <label>Rua Pavimentada<g:helpTooltip chave="help.rua.pavimentada"/></label>
            <g:selectSimNao bean="${localDtoFamilia}" name="detalhe.ruaPavimentada" class="many-to-one select-sim-nao"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.banheiro', 'error')} ">
            <label>Banheiro</label>
            <g:selectSimNao bean="${localDtoFamilia}" name="detalhe.banheiro" class="many-to-one select-sim-nao"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.pisoTerra', 'error')} ">
            <label>Piso em Terra<g:helpTooltip chave="help.piso.terra"/></label>
            <g:selectSimNao bean="${localDtoFamilia}" name="detalhe.pisoTerra" class="many-to-one select-sim-nao"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.numeroComodos', 'error')} ">
            <label>Nº cômodos</label>
            <g:textField class="integerMask" name="detalhe.numeroComodos" size="2" value="${localDtoFamilia.mapaDetalhes['numeroComodos']}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.numeroQuartos', 'error')} ">
            <label>Nº quartos</label>
            <g:textField class="integerMask" name="detalhe.numeroQuartos" size="2" value="${localDtoFamilia.mapaDetalhes['numeroQuartos']}"/>
        </div>

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoFamilia}" field="detalhe.zonaResidencia"
                hidefrom="${[CustomizacoesService.Codigos.BELO_HORIZONTE_HAVAI_VENTOSA]}">
            <label>Zona Urbana</label>
            <g:selectLookup bean="${localDtoFamilia}" name="detalhe.zonaResidencia" class="many-to-one"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoFamilia}" field="detalhe.propriedadeMoradia">
            <label>Moradia</label>
            <g:selectLookup bean="${localDtoFamilia}" name="detalhe.propriedadeMoradia" class="many-to-one"/>
        </g:fieldcontain>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.areaOcupacao', 'error')} ">
            <label>
                Área de Ocupação (...)<g:helpTooltip chave="help.area.ocupacao"/>
                <palavra-chave>invasão,assentamento</palavra-chave>
            </label>
            <g:selectSimNao id="selectOcupacao" bean="${localDtoFamilia}" name="detalhe.areaOcupacao" onchange="vinculosCamposFamilia(this);"
                            class="many-to-one select-sim-nao"/>
        </div>

        <div id="divNomeOcupacao" class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.nomeOcupacao', 'error')} ">
            <label>
                Nome da Ocupação
                <palavra-chave>invasão</palavra-chave>
            </label>
            <g:textField name="detalhe.nomeOcupacao" size="20" maxlength="60" value="${localDtoFamilia.mapaDetalhes['nomeOcupacao']}"/>
        </div>

    </div> %{--class="conteudo-sessao"--}%

</div> %{--class="sessao-detalhes"--}%
