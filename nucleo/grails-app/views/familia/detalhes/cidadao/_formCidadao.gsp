<%@ page import="org.apoiasuas.cidadao.Cidadao" %>

<%
	org.apoiasuas.cidadao.Cidadao localDtoCidadao = cidadaoInstance;
    String prefixo = prefixoEntidade ?: "membros[$ordForm].";
%>

<div class="erroValidacao"></div>

%{--Gera um input hidden contendo o id do cidadao APENAS SE O CIDADAO JA EXISTIR NO BANCO DE DADOS --}%
<g:custom showif="${localDtoCidadao?.id}" elemento="input" type="hidden" class="id-cidadao" name="${prefixo}id" value="${localDtoCidadao?.id}"/>
<g:hiddenField name="${prefixo}ord" class="ord-cidadao" value="$ordForm"/>

<g:render template="/familia/detalhes/cidadao/basico" model="${[localDtoCidadao: localDtoCidadao, prefixo: prefixo]}"/>
<g:render template="/familia/detalhes/cidadao/documentos" model="${[localDtoCidadao: localDtoCidadao, prefixo: prefixo]}"/>
<g:render template="/familia/detalhes/cidadao/trabalho" model="${[localDtoCidadao: localDtoCidadao, prefixo: prefixo]}"/>
<g:render template="/familia/detalhes/cidadao/educacao" model="${[localDtoCidadao: localDtoCidadao, prefixo: prefixo]}"/>
<g:render template="/familia/detalhes/cidadao/saude" model="${[localDtoCidadao: localDtoCidadao, prefixo: prefixo]}"/>
<g:render template="/familia/detalhes/cidadao/vulnerabilidades" model="${[localDtoCidadao: localDtoCidadao, prefixo: prefixo]}"/>
<g:render template="/familia/detalhes/cidadao/violacoes" model="${[localDtoCidadao: localDtoCidadao, prefixo: prefixo]}"/>
<g:render template="/familia/detalhes/cidadao/outros" model="${[localDtoCidadao: localDtoCidadao, prefixo: prefixo]}"/>
