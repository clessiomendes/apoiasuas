<div class="sessao-detalhes">

    <div class="cabecalho-sessao">
        <a href="javascript:void(0)" class="left" onclick="foldSessao(this);">
            <asset:image src="usecases/documento-w.png" width="32" height="32"/>
            Documentos
            <asset:image src="down-w.png" class="imagem-fold" width="16" height="16"/>
        </a>
        <a href="javascript:void(0)" class="right" title="clique para exibir/esconder todas as sessões" onclick="foldSessaoTodos(this);">fool</a>
    </div>

    <div class="conteudo-sessao">

        <g:fieldcontain bean="${localDtoCidadao}" field="identidade">
            <label>Identidade</label>
            <g:textField name="${prefixo}identidade" class="integerMask importante" size="15" maxlength="20" value="${localDtoCidadao?.identidade}"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.emissorIdentidade">
            <label>Órgão Emissor - UF</label>
            <g:textField name="${prefixo}detalhe.emissorIdentidade" class="destinoSugestao1" size="5" maxlength="20"
                         value="${localDtoCidadao.mapaDetalhes['emissorIdentidade']}"/>
            <g:textField name="${prefixo}detalhe.ufEmissorIdentidade" class="destinoSugestao2" size="2" maxlength="2"
                         value="${localDtoCidadao.mapaDetalhes['ufEmissorIdentidade']}"/>
            <span class="sugestaoPreenchimento">
                <input type="button" class="speed-button-sugestao" onclick="clickSugestao(this);" title="preencher com informação padrão"/>
                <span class="origemSugestao1">PC</span> - <span class="origemSugestao2">${UFLogada}</span>
            </span>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <g:fieldcontain bean="${localDtoCidadao}" field="nis">
            <label>NIS, PIS ou PASEP</label>
            <g:textField name="${prefixo}nis" class="integerMask" size="15" maxlength="20" value="${localDtoCidadao?.nis}"/>
        </g:fieldcontain>

        <g:fieldcontain bean="${localDtoCidadao}" field="cpf">
            <label>CPF</label>
            <g:textField name="${prefixo}cpf" class="cpfMask" size="15" maxlength="20" value="${localDtoCidadao?.cpf}"/>
        </g:fieldcontain>

        <div class="nova-linha"></div>

        <fieldset class="embedded"><legend>Certidão</legend>

            <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.tipoCertidao" style="min-width: inherit">
                <label>Tipo</label>
                <g:selectLookup bean="${localDtoCidadao}" name="${prefixo}detalhe.tipoCertidao" class="many-to-one"/>
            </g:fieldcontain>

            <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.livroCertidao" style="min-width: inherit">
                <label>Livro</label>
                <g:textField name="${prefixo}detalhe.livroCertidao" size="3" maxlength="10" value="${localDtoCidadao.mapaDetalhes['livroCertidao']}"/>
            </g:fieldcontain>

            <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.folhaCertidao" style="min-width: inherit">
                <label>Folha</label>
                <g:textField name="${prefixo}detalhe.folhaCertidao" size="3" maxlength="10" value="${localDtoCidadao.mapaDetalhes['folhaCertidao']}"/>
            </g:fieldcontain>

            <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.termoCertidao" style="min-width: inherit">
                <label>Termo</label>
                <g:textField name="${prefixo}detalhe.termoCertidao" size="3" maxlength="10" value="${localDtoCidadao.mapaDetalhes['termoCertidao']}"/>
            </g:fieldcontain>

            <div class="nova-linha"></div>

            <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.cartorioCertidao">
                <label>Cartório</label>
                <g:textField name="${prefixo}detalhe.cartorioCertidao" size="30" maxlength="60" value="${localDtoCidadao.mapaDetalhes['cartorioCertidao']}"/>
            </g:fieldcontain>

            <div class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'municipioCertidao', 'error')} ">
                <label>Município de registro - UF</label>
                <g:textField name="${prefixo}detalhe.municipioCertidao" class="destinoSugestao1" size="30" maxlength="60"
                             value="${localDtoCidadao.mapaDetalhes['municipioCertidao']}"/>
                <g:textField name="${prefixo}detalhe.ufCertidao" class="destinoSugestao2" size="2" maxlength="2"
                             value="${localDtoCidadao.mapaDetalhes['ufCertidao']}"/>
                <span class="sugestaoPreenchimento">
                    <input type="button" class="speed-button-sugestao" onclick="clickSugestao(this);" title="preencher com informação padrão"/>
                    <span class="origemSugestao1">${municipioLogado}</span> - <span class="origemSugestao2">${UFLogada}</span>
                </span>
            </div>

        </fieldset>

        %{--
        <fieldset class="embedded"><legend>Carteira de Trabalho</legend>

            <div class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'XXX', 'error')} ">
                <label>Número</label>
                <g:textField name="XXX" size="5" maxlength="10" value=""/>
            </div>

            <div class="fieldcontain ${hasErrors(bean: localDtoCidadao, field: 'XXX', 'error')} ">
                <label>Série</label>
                <g:textField name="XXX" size="5" maxlength="10" value=""/>
            </div>

        </fieldset>
        --}%

        <fieldset class="embedded"><legend>Título de Eleitor</legend>

            <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.numeroTituloEleitor">
                <label>Número</label>
                <g:textField name="${prefixo}detalhe.numeroTituloEleitor" size="15" maxlength="20" value="${localDtoCidadao.mapaDetalhes['numeroTituloEleitor']}"/>
            </g:fieldcontain>

            <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.zonaTituloEleitor">
                <label>Zona</label>
                <g:textField name="${prefixo}detalhe.zonaTituloEleitor" size="3" maxlength="10" value="${localDtoCidadao.mapaDetalhes['zonaTituloEleitor']}"/>
            </g:fieldcontain>

            <g:fieldcontain bean="${localDtoCidadao}" field="detalhe.secaoTituloEleitor">
                <label>Seção</label>
                <g:textField name="${prefixo}detalhe.secaoTituloEleitor" size="3" maxlength="10" value="${localDtoCidadao.mapaDetalhes['secaoTituloEleitor']}"/>
            </g:fieldcontain>

        </fieldset>

    </div>

</div>
