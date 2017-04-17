<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance
    org.apoiasuas.cidadao.Endereco enderecoInstance = localDtoFamilia.endereco
    org.apoiasuas.cidadao.AcompanhamentoFamiliar localDtoAcompanhamentoFamiliar = localDtoFamilia.acompanhamentoFamiliar
%>

<div class="fieldcontain ${hasErrors(bean: localDtoAcompanhamentoFamiliar, field: 'dataInicio', 'error')} ">
    <label for="acompanhamentoFamiliar.dataInicio">Data de início</label>
    <g:textField class="dateMask" name="acompanhamentoFamiliar.dataInicio" size="10" maxlength="10" value="${localDtoAcompanhamentoFamiliar?.dataInicio?.format("dd/MM/yyyy")}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoAcompanhamentoFamiliar, field: 'dataFim', 'error')} ">
    <label for="acompanhamentoFamiliar.dataFim">Data de <nobr>encerramento
        <g:helpTooltip chave="help.data.fim.acompanhamento" />
    </nobr>
    </label>
    <g:textField class="dateMask" name="acompanhamentoFamiliar.dataFim" size="10" maxlength="10" value="${localDtoAcompanhamentoFamiliar?.dataFim?.format("dd/MM/yyyy")}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoAcompanhamentoFamiliar, field: 'analiseTecnica', 'error')} ">
    <label for="acompanhamentoFamiliar.analiseTecnica">
        Análise técnica
        <g:helpTooltip chave="help.analise.tecnica.acompanhamento" />
    </label>
    <g:textArea name="acompanhamentoFamiliar.analiseTecnica" value="${localDtoAcompanhamentoFamiliar?.analiseTecnica}" rows="6" cols="60"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoAcompanhamentoFamiliar, field: 'resultados', 'error')} ">
    <label for="acompanhamentoFamiliar.resultados">
        Resultados e aquisições
        <g:helpTooltip chave="help.resultados.acompanhamento" />
    </label>
    <g:textArea name="acompanhamentoFamiliar.resultados" value="${localDtoAcompanhamentoFamiliar?.resultados }" rows="6" cols="60"/>
</div>

