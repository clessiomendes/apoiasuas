<%@ page import="org.apoiasuas.cidadao.FamiliaDetalhadoController; org.apoiasuas.cidadao.Cidadao" %>
<!DOCTYPE html>

<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance;
%>

<div style="margin: -0.5em 0.8em 0.5em 0.8em">
    <div style="float: left; display: block">
        <h1 style="margin: 0">Cadastro Familiar ${localDtoFamilia.cad ? '- cad '+localDtoFamilia.cad : '' }</h1>
    </div>
    <div style="float: right;">
        <input type="text" size="10" class="clear-field" oninput="delayProcurarCampoDetalhe(this);"/>
    </div>
    <g:helpTooltip chave="help.procurar.campo.especifico" style="float: right; margin-right: 5px"/>
    <div style="float: right;">
        <h4 class="animated bounceInLeft label-procurar">info específica: </h4>
    </div>
    <div style="clear: both"></div>
</div>

<div id="navFamilia" class="nav sticky-header ${modoCriacao ? 'hidden' : ''}" role="navigation">
    <ul>

        %{-- Montagem do link para os dados da família --}%
        <li><a href="javascript:void(0)" id="linkFamilia" class="links-forms familia-detalhada" onclick="showForm($('#divFamilia'), this);">domicílio</a></li>

    %{-- Montagem dos links para cada membro --}%
        <g:each in="${localDtoFamilia.getMembrosOrdemPadrao(null).findAll { it.id /*somente cidadaos ja criados*/ } }" var="cidadao" status="i">
            <li><a href="javascript:void(0)" class="links-forms hidden cidadao-detalhado"
                   %{--Corta o texto, caso se trate de um membro desabilitado--}%
                   style="${cidadao.desabilitado ? 'text-decoration: line-through' : ''}"
                   title="${FamiliaDetalhadoController.tooltipNome(cidadao)}"
                   onclick="showForm(jQuery('#divMembros\\[${i}\\]'), this);">
                ${FamiliaDetalhadoController.labelNome(cidadao)}
            </a></li>
        </g:each>
    </ul>
</div>