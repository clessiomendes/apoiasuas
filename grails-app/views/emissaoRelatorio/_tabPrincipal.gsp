<%@ page import="org.apoiasuas.relatorio.RelatorioService" %>

%{--Listar--}%
<fieldset class="embedded opcoes-relatorio" style="padding: 10px">
    <legend class="collapsable">Listar:</legend>
    <g:radio name="relatorio" value="${RelatorioService.DEFINICAO_CAMPOS.REFERENCIA}" checked="true"/> apenas a referência
    <br><g:radio name="relatorio" value="${RelatorioService.DEFINICAO_CAMPOS.MEMBROS}" /> todos os membros
    %{--FIXME: somente para ServicoSistema de Belo Horizonte--}%
    <br><g:radio name="relatorio" value="${RelatorioService.DEFINICAO_CAMPOS.BD_BH}" /> exportação de Banco de Dados
</fieldset>

<script>
    $(document).ready(function () {
        //evento de mudanco no radio button
        $('input[name="relatorio"]').on('click change', function() {
            if (this.value == '${RelatorioService.DEFINICAO_CAMPOS.BD_BH}') {
                $('#divFiltrosReferenciaMembros').addClass("hidden");
                $('#divFiltrosBDBH').removeClass("hidden");
            } else {
                $('#divFiltrosReferenciaMembros').removeClass("hidden");
                $('#divFiltrosBDBH').addClass("hidden");
            }
        });
    });

</script>

%{--Filtrar--}%
<fieldset class="embedded" style="padding: 10px"><legend class="collapsable"
                                                         style="cursor:pointer;">Filtrar:</legend>
    <div id="divFiltrosReferenciaMembros">
        Técnico de referência:
        <g:select name="tecnicoReferencia" from="${operadores.entrySet()}" optionKey="key" optionValue="value" noSelection="['-1': '']"/>
        <br><br>
        Idade de
        <div style="display: inline" class="fieldcontain ${hasErrors(bean: definicaoListagem, field: 'idadeInicial', 'error')} ">
            <g:textField name="idadeInicial" size="1"/> a
        </div>
        <div style="display: inline" class="fieldcontain ${hasErrors(bean: definicaoListagem, field: 'idadeFinal', 'error')} ">
            <g:textField name="idadeFinal" size="1"/> anos
        </div>
    </div>
    <div id="divFiltrosBDBH" class="hidden">
        Identificador da família (não é o CAD):
        <div style="display: inline" class="fieldcontain ${hasErrors(bean: definicaoListagem, field: 'idFamilia', 'error')} ">
            <g:textField name="idFamilia" size="5"/>
        </div>
    </div>
</fieldset>

