<div class="sessao-detalhes">

    <div class="cabecalho-sessao">
        <a href="javascript:void(0)" class="left" onclick="foldSessao(this);">
            <asset:image src="usecases/acompanhamento-w.png" width="32" height="32"/>
            Vulnerabilidades específicas
            <asset:image src="down-w.png" class="imagem-fold" width="16" height="16"/>
        </a>
        <a href="javascript:void(0)" class="right" title="clique para exibir/esconder todas as sessões" onclick="foldSessaoTodos(this);">fool</a>
    </div>

    <div class="conteudo-sessao">

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.migrante">
            <label>Migrante<g:helpTooltip chave="help.cidadao.migrante"/></label>
            <g:selectSimNao bean="${localDtoCidadao}" name="${prefixo}detalhe.migrante" class="many-to-one select-sim-nao"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.institucionalizado">
            <label>em Acolhimento Institucional (...)<g:helpTooltip chave="help.acolhimento.institucional"/></label>
            <palavra-chave>institucionalização,albergue,albergado,orfanato,lar,ILPI,instituição de longa permanência para idosos</palavra-chave>
            <g:selectSimNao bean="${localDtoCidadao}" name="${prefixo}detalhe.institucionalizado" onchange="vinculosCamposCidadao(this)"
                            class="selectInstitucionalizado many-to-one select-sim-nao"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.instituicaoAcolhimento" class="divInstituicaoAcolhedora">
            <label>Instituição Acolhedora</label>
            <palavra-chave>institucionalização,albergue,albergado,orfanato,lar,ILPI,instituição de longa permanência para idosos</palavra-chave>
            <g:textField name="${prefixo}detalhe.instituicaoAcolhimento" size="20" maxlength="60"
                         value="${localDtoCidadao.mapaDetalhes['instituicaoAcolhimento']}"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.sistemaPrisional">
            <label>Recluso / Egresso do Sistema Prisional<g:helpTooltip chave="help.sistema.prisional"/></label>
            <palavra-chave>preso,presidiário,cadeia,carcerário</palavra-chave>
            <g:selectSimNao bean="${localDtoCidadao}" name="${prefixo}detalhe.sistemaPrisional"
                            class="many-to-one select-sim-nao"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.medidaSocioEducativa">
            <label>Cumprindo / Histórico de Medida Sócio-educativa<g:helpTooltip chave="help.medida.socio.educativa"/></label>
            <palavra-chave>liberdade assistida, serviço comunitário,PSC,acautelado,internação,semiliberdade</palavra-chave>
            <g:selectSimNao bean="${localDtoCidadao}" name="${prefixo}detalhe.medidaSocioEducativa"
                            class="many-to-one select-sim-nao"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.situacaoRua">
            <label>em Situação de Rua (...)</label>
            <palavra-chave>morador de rua,sem teto</palavra-chave>
            <g:selectSimNao bean="${localDtoCidadao}" name="${prefixo}detalhe.situacaoRua" onchange="vinculosCamposCidadao(this)"
                            class="selecSituacaoRua many-to-one select-sim-nao"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.ondeDorme" class="situacao-rua">
            <label>Onde Dorme<g:helpTooltip chave="help.onde.dorme"/></label>
            <g:textField name="${prefixo}detalhe.ondeDorme" size="20" maxlength="60"
                         value="${localDtoCidadao.mapaDetalhes['ondeDorme']}"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.tempoRua" class="situacao-rua">
            <label>Tempo em Situação de Rua</label>
            <g:textField name="${prefixo}detalhe.tempoRua" size="20" maxlength="60"
                         value="${localDtoCidadao.mapaDetalhes['tempoRua']}"/>
        </g:fieldcontain>

%{--
        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.motivoSituacaoRua" class="situacao-rua">
            <label>Motivos</label>
            <g:checkBox name="XXX"/> Perda de Moradia <g:checkBox name="XXX"/> Álcool ou outras drogas <g:checkBox name="XXX"/> Perda de Moradia <g:checkBox name="XXX"/> Álcool ou outras drogas <g:checkBox name="XXX"/> Perda de Moradia <g:checkBox name="XXX"/> Álcool ou outras drogas
        </g:fieldcontain>
--}%

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.acessoRedePopRua" class="situacao-rua">
            <label>Acesso à Rede</label>
            <g:multiLookup bean="${localDtoCidadao}" name="${prefixo}detalhe.acessoRedePopRua"
                           tabela="redePopRua" classeOpcao="opcao-multi-lookup"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.contatoFamiliar" class="situacao-rua">
            <label>
                Mantém Contato com Familiares com Domicílio
                <g:helpTooltip chave="help.contato.familiares"/>
            </label>
            <g:textField name="${prefixo}detalhe.contatoFamiliar" size="40" maxlength="80"
                         value="${localDtoCidadao.mapaDetalhes['contatoFamiliar']}"/>
        </g:fieldcontain>

    </div>

</div>
