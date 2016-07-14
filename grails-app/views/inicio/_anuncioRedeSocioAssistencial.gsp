<g:if test="$servicoAnuncio">

    <div class="anuncio-rede-socio-assistencial" title="Um exemplo de serviço, programa ou ação disponível em sua rede sócio-assistencial&#013;Clique para saber mais">

        <style type="text/css" media="screen" scoped>
        .anuncio-rede-socio-assistencial {
            /*background: #f3f3ff url(../images/rede-socio-assistencial.png) 0.5em 50% no-repeat; */
            background: #f3f3ff url(/apoiasuas/assets/rede-socio-assistencial.png) 0.5em 50% no-repeat;
            background-size: 40px 40px;
            cursor: pointer;
            padding: 7px;
            padding-left: 55px;
            border: 3px solid #b2d1ff;
            color: #006dba;
            box-shadow: 0 0 1em #b2d1ff;
            font-size: 0.75em;
            line-height: 1.5;
            margin: 0.7em 2em;
            height: 5.3em; /*fixa o tambanho do anuncio*/
            overflow:hidden /*Corta o conteúdo que não couber no anuncio*/
        }
        .anuncio-rede-socio-assistencial a:link{
            color:#006dba;
            text-decoration: none;
        }
        </style>

        <g:link controller="servico" action="show" id="${servicoAnuncio.id}">
            <b>${servicoAnuncio.apelido}</b><br>
            ${raw(org.apoiasuas.util.StringUtils.toHtmlNoEmptyLines(servicoAnuncio.descricao))}
        %{--<b>Cartão BHBUS / Passe Livre para pessoa com deficiência</b><br>--}%
        %{--Gratuidade para pessoas com deficiência nos ônibus MUNICIPAIS. Cartão BHBUS Benefício Inclusão.<br>--}%
        %{--Quem tem direito: Pessoas com deficiência física, mental, auditiva ou visual, ...<br>--}%
        </g:link>

    </div>

</g:if>