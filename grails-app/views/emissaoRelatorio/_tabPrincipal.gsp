
%{--Listar--}%
<fieldset class="embedded" style="padding: 10px"><legend class="collapsable" style="cursor:pointer;">Listar:</legend>
    <g:radio name="membros" value="" checked="true"/> apenas a referência <br>
    <g:radio name="membros" value="true"/> todos os membros
</fieldset>

%{--Filtrar--}%
<fieldset class="embedded" style="padding: 10px"><legend class="collapsable"
                                                         style="cursor:pointer;">Filtrar:</legend>
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
</fieldset>

