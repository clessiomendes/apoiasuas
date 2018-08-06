%{--<span class="property-value">--}%
<span style="display: inline-block; line-height: 1.5em;">
    ${it.referencia ? "Referência: " : ( it.parentescoReferencia ? it.parentescoReferencia + ": " : "" )}
    <g:link controller="cidadao" action="show" id="${it.id}">${it?.nomeCompleto }</g:link>
    ${it.idadeOuAprox() ? ", " + it.idadeOuAprox() + " anos" + (! it.dataNascimento && it.dataNascimentoAproximada ? " (aprox)" : "" ) : ""}
</span>
<br>
