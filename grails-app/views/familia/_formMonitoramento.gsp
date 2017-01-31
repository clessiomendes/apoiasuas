<%
	org.apoiasuas.cidadao.Monitoramento localDtoMonitoramento = monitoramentoInstance;
%>
<g:hiddenField name="familia.id" value="${localDtoMonitoramento.familia.id}"/>

<script>
	$(function() {
        $("#checkEfetivado").change(function() {
            if (this.checked)
                $("#divDataEfetivada").show()
            else
                $("#divDataEfetivada").hide()
        });
        $("#checkEfetivado").change();
	});
</script>

<div class="fieldcontain ${hasErrors(bean: localDtoMonitoramento, field: 'memo', 'error')} ">
	<label for="memo">
        Descrição <span class="required-indicator">*</span>
        <g:helpTooltip chave="help.descricao.monitoramento" />
    </label>
    <g:textArea name="memo" value="${localDtoMonitoramento?.memo}" rows="3" cols="40" autofocus=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoMonitoramento, field: 'responsavel', 'error')} ">
	<label for="responsavel">
        Técnico <nobr>responsável
        <span class="required-indicator">*</span>
        <g:helpTooltip chave="help.responsavel.monitoramento" />
        </nobr>
    </label>
	<g:select id="responsavel" name="responsavel.id" from="${operadores}" optionKey="id" value="${localDtoMonitoramento?.responsavel?.id}" class="many-to-one" noSelection="['': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoMonitoramento, field: 'dataCriacao', 'error')} ">
    <label>Data de <nobr>criação
        <span class="required-indicator">*</span>
        <g:helpTooltip chave="help.data.criacao.monitoramento" />
    </nobr>
    </label>
    <g:textField class="dateMask" name="dataCriacao" size="10" maxlength="10" value="${localDtoMonitoramento?.dataCriacao?.format("dd/MM/yyyy")}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoMonitoramento, field: 'dataPrevista', 'error')} ">
    <label>Próximo <bobr>monitoramento
        <g:helpTooltip chave="help.data.prevista.monitoramento" />
    </bobr>
    </label>
    <g:textField class="dateMask" name="dataPrevista" size="10" maxlength="10" value="${localDtoMonitoramento?.dataPrevista?.format("dd/MM/yyyy")}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoMonitoramento, field: 'efetivado', 'error')} ">
    <label>
        <g:helpTooltip chave="help.efetivado.monitoramento" />
    </label>
    <g:checkBox id="checkEfetivado" name="efetivado" value="${localDtoMonitoramento?.efetivado}" /> evetivado
%{--    <g:textField class="dateMask" name="dateCreated" size="10" maxlength="10" value="${localDtoMonitoramento?.dateCreated?.format("dd/MM/yyyy")}"/>--}%
</div>

<div id="divDataEfetivada" class="fieldcontain ${hasErrors(bean: localDtoMonitoramento, field: 'dataEfetivada', 'error')} ">
    <label>Data da <nobr> efetivação
        <g:helpTooltip chave="help.data.efetivacao.monitoramento" />
        </nobr>
    </label>
    <g:textField class="dateMask" name="dataEfetivada" size="10" maxlength="10" value="${localDtoMonitoramento?.dataEfetivada?.format("dd/MM/yyyy")}"/>
</div>
