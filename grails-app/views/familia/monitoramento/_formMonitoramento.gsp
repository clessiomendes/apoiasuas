<%
	org.apoiasuas.cidadao.Monitoramento localDtoMonitoramento = monitoramentoInstance;
%>
<g:hiddenField name="familia.id" value="${localDtoMonitoramento.familia.id}"/>

<style>
.proximoMonitoramento {
    padding: 0;
    width: 28px;
    height: 25px;
}
</style>

<asset:javascript src="especificos/datepicker-pt-BR.js"/>

<script>
	$(function() {
        //evento onCheck para o checkbox "efetivado"
        $("#checkEfetivado").change(function() {
            var $dataEfetivada = $("#dataEfetivada");
            if (this.checked) {
                $("#divSuspenso").hide();
                $("#divDataEfetivada").show();
                //preenche automaticamente a data de efetivacao como a atual
                if (! $dataEfetivada.val())
                    $dataEfetivada.val(dateToStr(new Date()));
            } else {
                $("#divSuspenso").show();
                $("#divDataEfetivada").hide()
            }
        });
        $("#checkEfetivado").change();

        $.datepicker.setDefaults(
                $.extend(
                        {'dateFormat':'dd/MM/yyyy'},
                        $.datepicker.regional['pt-BR']
                )
        );

        $( ".datepicker" ).datepicker({
            showOn: "button",
            buttonImage: "${assetPath(src: 'calendario.png')}",
//            dateFormat: "dd/MM/yyyy",
            buttonImageOnly: true,
            buttonText: "Select date"
        });
	});

    function dateToStr(data) {
        return data.getDate()+"/"+(data.getMonth()+1)+"/"+data.getFullYear();
    }
    /**
    * Soma X dias à data de criação e preenche o campo de data prevista
    * @param dias
    */
    function calculaDataPrevista(dias) {
//        var dataCriacaoStr = $("#dataCriacao").val();
//        if (dataCriacaoStr) {
//            var parts = dataCriacaoStr.split('/');
            var dataPrevista = new Date();
//            var dataCriacaoDt = new Date(parts[2], parts[1]-1, parts[0]);
//            dataCriacaoDt.setDate(dataCriacaoDt.getDate()+dias);
            dataPrevista.setDate(dataPrevista.getDate()+dias);
//            if (dataCriacaoDt) {
//                $("#dataPrevista").val(dateToStr(dataCriacaoDt));
                $("#dataPrevista").val(dateToStr(dataPrevista));
//            }
//        }
    }
</script>

<div class="fieldcontain ${hasErrors(bean: localDtoMonitoramento, field: 'memo', 'error')} ">
	<label for="memo">
        Descrição<span class="required-indicator">*</span>
        <g:helpTooltip chave="help.descricao.monitoramento" />
    </label>
    <g:textArea name="memo" value="${localDtoMonitoramento?.memo}" rows="3" cols="40" autofocus=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoMonitoramento, field: 'responsavel', 'error')} ">
	<label for="responsavel">
        Técnico <nobr>responsável<span class="required-indicator">*</span>
            <g:helpTooltip chave="help.responsavel.monitoramento" />
        </nobr>
    </label>
	<g:select id="responsavel" name="responsavel.id" from="${operadores}" optionKey="id" value="${localDtoMonitoramento?.responsavel?.id}" class="many-to-one" noSelection="['': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoMonitoramento, field: 'dataCriacao', 'error')} ">
    <label>Data de <nobr>criação<span class="required-indicator">*</span>
            <g:helpTooltip chave="help.data.criacao.monitoramento" />
        </nobr>
    </label>
    <g:textField class="dateMask" name="dataCriacao" id="dataCriacao" size="10" maxlength="10" value="${localDtoMonitoramento?.dataCriacao?.format("dd/MM/yyyy")}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoMonitoramento, field: 'dataPrevista', 'error')} ">
    <label>Próximo <nobr>monitoramento
            <g:helpTooltip chave="help.data.prevista.monitoramento" />
        </nobr>
    </label>
    <input type="button" style="font-size: 0.7em" class="proximoMonitoramento" title="calcula mais 7 dias à partir de hoje"value="+7" onclick="calculaDataPrevista(7);"/>
    <input type="button" style="font-size: 0.7em" class="proximoMonitoramento" title="calcula mais 30 dias à partir de hoje" value="+30" onclick="calculaDataPrevista(30);"/>
    <g:textField class="dateMask datepicker" name="dataPrevista" id="dataPrevista" size="10" maxlength="10" value="${localDtoMonitoramento?.dataPrevista?.format("dd/MM/yyyy")}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: localDtoMonitoramento, field: 'prioritario', 'error')} ">
    <label>
    </label>
    <g:checkBox id="checkPrioritario" name="prioritario" value="${localDtoMonitoramento?.prioritario}" /> prioritário
    <g:helpTooltip chave="help.prioritario.monitoramento" />
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoMonitoramento, field: 'efetivado', 'error')} ">
    <label>
    </label>
    <g:checkBox id="checkEfetivado" name="efetivado" value="${localDtoMonitoramento?.efetivado}" /> efetivado
    <g:helpTooltip chave="help.efetivado.monitoramento" />
</div>

<div id="divDataEfetivada" class="fieldcontain ${hasErrors(bean: localDtoMonitoramento, field: 'dataEfetivada', 'error')} ">
    <label>Data da <nobr>efetivação
            <g:helpTooltip chave="help.data.efetivacao.monitoramento" />
        </nobr>
    </label>
    <g:textField class="dateMask" name="dataEfetivada" id="dataEfetivada" size="10" maxlength="10" value="${localDtoMonitoramento?.dataEfetivada?.format("dd/MM/yyyy")}"/>
</div>

<div id="divSuspenso" class="fieldcontain ${hasErrors(bean: localDtoMonitoramento, field: 'suspenso', 'error')} ">
    <label>
    </label>
    <g:checkBox id="checkSuspenso" name="suspenso" value="${localDtoMonitoramento?.suspenso}" /> suspenso
    <g:helpTooltip chave="help.suspenso.monitoramento" />
</div>

