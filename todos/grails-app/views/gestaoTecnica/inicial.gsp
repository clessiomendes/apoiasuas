<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'familia.label', default: 'Familia')}" />
    <title>Gestão Técnica</title>

    <asset:stylesheet src="pure-css/base.css"/>
    <asset:stylesheet src="pure-css/grids.css"/>
    <!--[if lte IE 8]>
        <asset:stylesheet src="pure-css/grids-responsive-old-ie.css"/>
    <![endif]-->
    <!--[if gt IE 8]><!-->
        <asset:stylesheet src="pure-css/grids-responsive.css"/>
    <!--<![endif]-->
    <asset:stylesheet src="gestaoTecnica/gestao-tecnica.less"/>
</head>
<body>

<g:form action="inicial">
    <div class="fieldcontain">
        <label for="idTecnico">
            Técnico
        </label>
        <g:select id="idTecnico" name="idTecnico" from="${operadores}"
                  optionKey="id" value="${idTecnico}" class="many-to-one"
                  noSelection="['': '(todos)']"
                  onchange="submit();"
        />
    </div>

    <div class="pure-g">

        <div class="pure-u-1 pure-u-md-1-2">
            <div class="conteudos-layout-purecss">
                <h3 class="header-gestao-tecnica">
                    <g:message code="titulo.familias.programas"/>
                    <g:helpTooltip chave="help.marcador.programas"/>
                </h3>
                <g:render template="totaisFamiliasMarcadores" model="${[idTecnico: idTecnico,
                        familiasMarcadores: familiasEmProgramas, totalFamiliasMarcadores: totalFamiliasEmProgramas,
                        helpTotal: "como uma mesma família pode estar em mais de um programa, o total não é necessariamente a soma das famílias em cada programa",
                        actionListagem: "listarFamiliasProgramas"
                ]}"/>
                <g:custom elemento="spam" showif="${idTecnico}" style="font-size: 0.7em;">
                    obs: Contabilizando apenas famílias referenciadas ao técnico escolhido.
                </g:custom>
            </div>
        </div>
        <div class="pure-u-1 pure-u-md-1-2">
            <div class="conteudos-layout-purecss">
                <h3 class="header-gestao-tecnica">
                    <g:message code="titulo.familias.acoes"/>
                    <g:helpTooltip chave="help.marcador.acoes"/>
                </h3>
                <g:render template="totaisFamiliasMarcadores" model="${[idTecnico: idTecnico,
                                                                        familiasMarcadores: familiasComAcoes, totalFamiliasMarcadores: totalFamiliasComAcoes,
                                                                        helpTotal: "como uma mesma família pode ter mais de uma ação proposta, o total não é necessariamente a soma das famílias em cada ação",
                                                                        actionListagem: "listarFamiliasAcoes"
                ]}"/>
                <g:custom elemento="spam" showif="${idTecnico}" style="font-size: 0.7em;">
                    obs: Contabilizando ações selecionadas pelo técnico escolhido
                </g:custom>
            </div>
        </div>
        <div class="pure-u-1 pure-u-md-1-2">
            <div class="conteudos-layout-purecss">
                <h3 class="header-gestao-tecnica">
                    <g:message code="titulo.familias.outros.marcadores"/>
                    <g:helpTooltip chave="help.marcador.outros.marcadores"/>
                </h3>
                <g:render template="totaisFamiliasMarcadores" model="${[idTecnico: idTecnico,
                                                                        familiasMarcadores: familiasComOutrosMarcadores, totalFamiliasMarcadores: totalFamiliasComOutrosMarcadores,
                                                                        helpTotal: "como uma mesma família pode ter mais de uma sinalização, o total não é necessariamente a soma das famílias em cada sinalização",
                                                                        actionListagem: "listarFamiliasOutrosMarcadores"
                ]}"/>
                <g:custom elemento="spam" showif="${idTecnico}" style="font-size: 0.7em;">
                    obs: Contabilizando sinalizações selecionadas pelo técnico escolhido
                </g:custom>
            </div>
        </div>
        <div class="pure-u-1 pure-u-md-1-2">
            <div class="conteudos-layout-purecss">
                <g:render template="totaisMonitoramentos" model="${[idTecnico: idTecnico,
                                                                    monitoramentos: monitoramentos, totalMonitoramentos: totalMonitoramentos]}"/>
            </div>
        </div>
        <div class="pure-u-1 pure-u-md-1-2">
            <div class="conteudos-layout-purecss">
                <h3 class="header-gestao-tecnica">
                    <g:message code="titulo.familias.vulnerabilidades"/>
                    <g:helpTooltip chave="help.marcador.vulnerabilidades"/>
                </h3>
                <g:render template="totaisFamiliasMarcadores" model="${[idTecnico: idTecnico,
                       familiasMarcadores: familiasComVulnerabilidades, totalFamiliasMarcadores: totalFamiliasComVulnerabilidades,
                       helpTotal: "como uma mesma família pode ter mais de uma vulnerabilidade, o total não é necessariamente a soma das famílias em cada vulnerabilidade",
                       actionListagem: "listarFamiliasVulnerabilidades"
                ]}"/>
                <g:custom elemento="spam" showif="${idTecnico}" style="font-size: 0.7em;">
                    obs: Contabilizando vulnerabilidades selecionadas pelo técnico escolhido
                </g:custom>
            </div>
        </div>

    </div>

</g:form>

</body>
</html>