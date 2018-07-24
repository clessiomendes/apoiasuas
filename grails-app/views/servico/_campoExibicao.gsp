<%
    String _helpTooltip = helpTooltip ? (
            g.message(code: helpTooltip) ? g.helpTooltip(chave: helpTooltip) : g.helpTooltip(body: helpTooltip)
    ) : '';

%>
<li ${id ? 'id='+id : ''} class="fieldcontain ${(classeCss ?: '') + (quebraLinha ? ' quebra-linha ' : '')}">
    <span class="property-label">${titulo}${_helpTooltip}</span>
    <span class="property-value">
        ${raw(body())}
    </span>
</li>

