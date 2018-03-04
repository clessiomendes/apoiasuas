<g:if test="${servicoAnuncio != null}">
    <div class="anuncio-rede-socio-assistencial" title="Um exemplo de serviço, programa ou ação disponível em sua rede sócio-assistencial&#013;Clique para saber mais">
        <g:link controller="servico" action="show" id="${servicoAnuncio.id}">
            <b>${servicoAnuncio.apelido}</b><br>
            ${raw(org.apoiasuas.util.StringUtils.toHtmlNoEmptyLines(servicoAnuncio.descricao))}
        </g:link>
    </div>
</g:if>