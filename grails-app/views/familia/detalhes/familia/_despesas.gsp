<div class="sessao-detalhes">

    <div class="cabecalho-sessao">
        <a href="javascript:void(0)" class="left" onclick="foldSessao(this);">
            <asset:image src="usecases/despesas-w.png" width="32" height="32"/>
            Despesa mensal com
            <asset:image src="down-w.png" class="imagem-fold" width="16" height="16"/>
        </a>
        <a href="javascript:void(0)" class="right" title="clique para exibir/esconder todas as sessões" onclick="foldSessaoTodos(this);">fool</a>
    </div>

    <div class="conteudo-sessao">

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.despesaAluguel', 'error')} ">
            <label>Aluguel R$</label>
            <g:textField name="detalhe.despesaAluguel" class="despesa integerMask" size="4" value="${localDtoFamilia.mapaDetalhes['despesaAluguel']}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.despesaAgua', 'error')} ">
            <label>Água R$</label>
            <g:textField name="detalhe.despesaAgua" class="despesa integerMask" size="4" value="${localDtoFamilia.mapaDetalhes['despesaAgua']}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.despesaGas', 'error')} ">
            <label>Gás R$</label>
            <g:textField name="detalhe.despesaGas" class="despesa integerMask" size="4" value="${localDtoFamilia.mapaDetalhes['despesaGas']}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.despesaEnergia', 'error')} ">
            <label>Energia Elétrica R$</label>
            <g:textField name="detalhe.despesaEnergia" class="despesa integerMask" size="4" value="${localDtoFamilia.mapaDetalhes['despesaEnergia']}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.despesaTransporte', 'error')} ">
            <label>Transporte R$</label>
            <g:textField name="detalhe.despesaTransporte" class="despesa integerMask" size="4" value="${localDtoFamilia.mapaDetalhes['despesaTransporte']}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.despesaMedicamentos', 'error')} ">
            <label>Medicamentos R$</label>
            <g:textField name="detalhe.despesaMedicamentos" class="despesa integerMask" size="4" value="${localDtoFamilia.mapaDetalhes['despesaMedicamentos']}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.despesaSupermercado', 'error')} ">
            <label>Alimentação, higiene e limpeza R$</label>
            <g:textField name="detalhe.despesaSupermercado" class="despesa integerMask" size="4" value="${localDtoFamilia.mapaDetalhes['despesaSupermercado']}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.despesaOutras', 'error')} ">
            <label>Outras R$</label>
            <g:textField name="detalhe.despesaOutras" class="despesa integerMask" size="4" value="${localDtoFamilia.mapaDetalhes['despesaOutras']}"/>
        </div>

        <div class="nova-linha"></div>

        <div class="fieldcontain">
            <label>Despesa Total R$</label>
            <span id="spanTotalDespesas"></span>
        </div>

        <div class="fieldcontain">
            <label>Renda Total R$</label>
            <span id="spanTotalRenda"></span>
        </div>

    </div> %{--class="conteudo-sessao"--}%

</div> %{--class="sessao-detalhes"--}%
