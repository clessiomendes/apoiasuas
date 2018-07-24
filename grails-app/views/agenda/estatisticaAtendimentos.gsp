<ol class="property-list estatistica" >

    <g:each in="${tecnicos}" var="entry">
        <li class="fieldcontain estatistica">
            <span class="property-label">${entry.key}</span>
            <span class="property-value container-centralizado">
                <span class="conteudo-centralizado">${entry.value}</span>
            </span>
        </li>
    </g:each>

    <br>

    <li class="fieldcontain estatistica-total">
        <span class="property-label">Total no período</span>
        <span class="property-value container-centralizado">
            <span class="conteudo-centralizado">${total ?: 0}</span>
        </span>
    </li>

</ol>

<fieldset class="buttons">
    <a href="javascript:void(0)" class="close" onclick="janelaModal.cancelada();">Fechar</a>
</fieldset>
