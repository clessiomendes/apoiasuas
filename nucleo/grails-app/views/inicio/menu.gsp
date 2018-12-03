<%@ page import="org.apoiasuas.seguranca.ASMenuBuilder; org.apoiasuas.InicioController; org.apoiasuas.redeSocioAssistencial.RecursosServico; org.apoiasuas.importacao.ImportacaoFamiliasController; org.apoiasuas.seguranca.DefinicaoPapeis" %>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Apoia SUAS</title>
    <r:require modules="typescript"/>
    <asset:stylesheet src="inicio/menu.less"/>
    <asset:javascript src="cidadao/procurarCidadao.js"/>
    %{--<asset:stylesheet src="animate.css"/>--}%
</head>

<body>

<div id="page-body" style="padding: 0 10px" role="main">

    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <div>
        <label for="nomeOuCad" class="label-menu-procurar">
            Nome ou cadastro de <nobr>usuário <g:helpTooltip chave="buscaUsuario.help"/></nobr>
        </label>
        <nobr>
            <g:textField name="nomeOuCad" id="inputNomeOuCad" autofocus="" size="50" class="input-menu-procurar"
                         onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/>
            <g:link class="input-menu-procurar" onclick="linkProcurarCidadao(this, '${createLink(controller: 'cidadao', action: 'procurarCidadao')}');">
                <input id="btnProcurarCidadao" type="button" class="speed-button-procurar"/>
            </g:link>
        </nobr>
        <div style="clear: both"></div>
    </div>

    <div style="margin-top: 10px">
        <label for="palavraChave" class="label-menu-procurar">
            Procurar no <nobr>ApoiaCRAS <g:helpTooltip chave="buscaCentralizada.help"/></nobr>
        </label>
        <nobr><g:form action="list" controller="buscaCentralizada">
            <g:textField name="palavraChave" size="50" class="input-menu-procurar" placeholder="ex: jovem aprendiz"/>
            %{--<g:textField name="palavraChave" size="50" class="input-menu-procurar" onfocus="if(this.value == 'ex: jovem aprendiz') { this.value = ''; }" value="ex: jovem aprendiz"/>--}%
            <g:submitButton name="list" class="input-menu-procurar speed-button-procurar" value=""/>
        </g:form></nobr>
        <div style="clear: both"></div>
    </div>

    %{--<g:render template="anuncioRedeSocioAssistencial"/>--}%

    <script>
        $(document).ready(function(){
            var imgNovoRecurso = '${assetPath(src: 'novo-recurso-menu.png')}'
            $('.novo-recurso-menu').append($( '<img src="'+imgNovoRecurso+'" class="animmated flash"/>' ));
        });
    </script>


    <div class="divMenu">

            <%
                g.set(var: "_menuBuilder", bean: "menuBuilder")
                org.apoiasuas.seguranca.ASMenuBuilder menuBuilder = _menuBuilder;
            %>
            <g:each in="${menuBuilder.getMenusDisponiveis()}" var="itemMenu">
                %{--${itemMenu}--}%
                <g:linkMenu itemMenu="${itemMenu}"/>
            </g:each>

            <br style="clear: both"/>
            <br style="clear: both"/>
    </div>

    <div style="font-weight: bold; text-align: center; ${ImportacaoFamiliasController.getDataUltimaImportacao(session)?.atrasada ? 'color:red;' : ''}">
        <p>Última importação do cadastro de cidadãos: <g:formatDate format="dd/MM/yyyy HH:mm" date="${ImportacaoFamiliasController.getDataUltimaImportacao(session)?.valor}"/></p>
    </div>

</div>

</body>
</html>
