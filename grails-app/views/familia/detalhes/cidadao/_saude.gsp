<div class="sessao-detalhes">

    <div class="cabecalho-sessao">
        <a href="javascript:void(0)" class="left" onclick="foldSessao(this);">
            <asset:image src="usecases/saude-w.png" width="32" height="32"/>
            Saúde
            <asset:image src="down-w.png" class="imagem-fold" width="16" height="16"/>
        </a>
        <a href="javascript:void(0)" class="right" title="clique para exibir/esconder todas as sessões" onclick="foldSessaoTodos(this);">fool</a>
    </div>

    <div class="conteudo-sessao">

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.abusoDrogas">
            <label>Uso Abusivo de Álcool ou Outras Drogas<g:helpTooltip chave="help.abuso.drogas"/></label>
            <palavra-chave>vício,viciado,entorpecente,dependente,quimico</palavra-chave>
            <g:selectSimNao class="many-to-one select-sim-nao" bean="${localDtoCidadao}"
                            name="${prefixo}detalhe.abusoDrogas"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.transtornoMental">
            <label>Transtorno Mental<g:helpTooltip chave="help.transtorno.mental"/></label>
            <g:selectSimNao class="many-to-one select-sim-nao" bean="${localDtoCidadao}"
                            name="${prefixo}detalhe.transtornoMental"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.servicoSaudeMental">
            <label>Serviço(s) de Saúde Mental onde é atendido<g:helpTooltip chave="help.servico.saude.mental"/></label>
            <g:textField name="${prefixo}detalhe.servicoSaudeMental" size="30" maxlength="60" value="${localDtoCidadao.mapaDetalhes['servicoSaudeMental']}"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.doencaGrave">
            <label>Doença Grave (...)</label>
            <palavra-chave>aids,câncer,hiv</palavra-chave>
            <g:selectSimNao class="selectDoencaGrave many-to-one select-sim-nao" bean="${localDtoCidadao}"
                            name="${prefixo}detalhe.doencaGrave" onchange="vinculosCamposCidadao(this)"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.nomeDoenca" class="divNomeDoenca">
            <label>Especificar doença</label>
            <g:textField name="${prefixo}detalhe.nomeDoenca" size="20" maxlength="60" value="${localDtoCidadao.mapaDetalhes['nomeDoenca']}"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.deficiencia">
            <label>Pessoa com Deficiência (...)</label>
            <palavra-chave>cuidador,deficiente,fisico,dependente,necessidades,bpc,cego,surdo,mudo,paraplegico,tetraplegico</palavra-chave>
            <g:selectSimNao bean="${localDtoCidadao}" name="${prefixo}detalhe.deficiencia" onchange="vinculosCamposCidadao(this)"
                            class="selectDeficiencia many-to-one select-sim-nao"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.tipoDeficiencia" class="deficiencia">
            <label>Tipo de Deficiência</label>
            <palavra-chave>cego,surdo,mudo,paraplegico,tetraplegico</palavra-chave>
            <g:selectLookup bean="${localDtoCidadao}" name="${prefixo}detalhe.tipoDeficiencia" class="many-to-one"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.cuidadorPrincipal" class="deficiencia">
            <label>Cuidador(es) Princiapal(is)</label>
            <palavra-chave>reponsável,curador</palavra-chave>
            <g:textField class="txtCuidadorPrincipal" name="${prefixo}detalhe.cuidadorPrincipal" size="35" maxlength="60"
                         value="${localDtoCidadao.mapaDetalhes['cuidadorPrincipal']}"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.servicoPessoaDeficiencia">
            <label>Serviço(s) à P.C.D. onde é atendido<g:helpTooltip chave="help.servico.pessoa.deficiencia"/></label>
            <g:textField name="${prefixo}detalhe.servicoPessoaDeficiencia" size="30" maxlength="60" value="${localDtoCidadao.mapaDetalhes['servicoPessoaDeficiencia']}"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.gratuidadeTransporteDeficiente" class="deficiencia">
            <label>Acesso à gratuidade no transporte</label>
            <palavra-chave>passe livre</palavra-chave>
            <g:multiLookup bean="${localDtoCidadao}" name="${prefixo}detalhe.gratuidadeTransporteDeficiente"
                           tabela="gratuidadeTransporte" classeOpcao="opcao-multi-lookup"/>
        </g:fieldcontain>

%{--
        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.acessoRedeDeficiencia" class="deficiencia">
            <label>Acesso à rede</label>
            <g:multiLookup bean="${localDtoCidadao}" name="${prefixo}detalhe.acessoRedeDeficiencia"
                           tabela="redeDeficiencia" classeOpcao="opcao-multi-lookup"/>
        </g:fieldcontain>
--}%

%{--
        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.demandaProtese" class="deficiencia">
            <label>Necessidade não atendida de Cadeira de Rodas, Órtese ou Prótese</label>
            <g:selectSimNao bean="${localDtoCidadao}" name="${prefixo}detalhe.demandaProtese" class="many-to-one select-sim-nao"/>
        </g:fieldcontain>
--}%

        <div class="nova-linha"></div>

        <g:fieldcontain class="deficiencia">
            <label>BPC</label>
            Informar se é beneficiário do BPC na sessão "Trabalho e Renda", campo "Situação no Mercado de Trabalho".
            %{--<input type="button" class="speed-button-irpara" onclick="irPara(this, 'selectOcupacao');" title="ir para Ocupação Principal"/>--}%
        </g:fieldcontain>

    </div>

</div>
