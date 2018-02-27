<%@ page import="org.apoiasuas.util.SimNao" %>
<g:fieldcontain bean="${localDtoCidadao}" field="nomeCompleto">
	<label>Nome Completo<g:helpTooltip chave="help.nome.completo"/>
        <span class="required-indicator">*</span>
    </label>
	<g:textField name="${prefixo}nomeCompleto" class="importante txtNomeCompleto" size="40" maxlength="60"
                 onblur="atualizaTituloAba(this);"
                 value="${localDtoCidadao?.nomeCompleto}"/>
</g:fieldcontain>

<g:fieldcontain bean="${localDtoCidadao}" field="nomeSocial">
	<label>Nome Social Completo<g:helpTooltip chave="help.nome.social"/></label>
	<g:textField name="${prefixo}nomeSocial" size="40" maxlength="60" value="${localDtoCidadao?.nomeSocial}"/>
</g:fieldcontain>

<div class="nova-linha"></div>

%{--Só permite alterar se não for a referência--}%
<g:fieldcontain bean="${localDtoCidadao}" field="parentescoReferencia">
	<g:hiddenField name="${prefixo}referencia" value="${localDtoCidadao.referencia}"/>
	<g:if test="${localDtoCidadao.referencia}">
		<label>Parentesco</label>
        Referência Familiar
		<g:hiddenField name="${prefixo}parentescoReferencia" value="${localDtoCidadao.parentescoReferencia}"/>
	</g:if>
	<g:else>
		<label>
            Parentesco <g:helpTooltip chave="cidadao.parentesco.referencia" args="[localDtoCidadao.familia?.referencia?.nomeCompleto ?: '']"/>
		</label>
		<g:textField name="${prefixo}parentescoReferencia" class="importante" value="${localDtoCidadao?.parentescoReferencia}"/>
	</g:else>
</g:fieldcontain>

<g:fieldcontain bean="${localDtoCidadao}" field="dataNascimento" style="min-width: inherit">
	<label>Data de Nascimento</label>
	<g:textField class="importante dateMask txtDataNascimento" name="${prefixo}dataNascimento" size="10" maxlength="10"
                 oninput="vinculosCamposCidadao(this)" value="${localDtoCidadao?.dataNascimento?.format("dd/MM/yyyy")}"/>
</g:fieldcontain>

<g:fieldcontain bean="${localDtoCidadao}" field="idadeAproximada" class="divIdadeAproximada">
	<label>ou Idade Aproximada</label>
	<g:hiddenField name="${prefixo}idadeAproximada_original" value="${localDtoCidadao.idadeAproximada}"/>
	<g:textField name="${prefixo}idadeAproximada" class="txtIdadeAproximada importante integerMask" size="2" maxlength="3" value="${localDtoCidadao.idadeAproximada}"/>
</g:fieldcontain>

<div class="nova-linha"></div>

<g:fieldcontain class="${hasErrors(bean: localDtoCidadao, field: 'nomeMae', 'error')} ${hasErrors(bean: localDtoCidadao, field: 'nomePai', 'error')}">
	<label>Filiação</label>
    <palavra-chave>mãe,pai</palavra-chave>
	<g:textField class="txtNomeMae importante" name="${prefixo}nomeMae" size="40" maxlength="60" value="${localDtoCidadao?.nomeMae}"/>
    <br>
    <g:textField class="txtNomePai importante" name="${prefixo}nomePai" size="40" maxlength="60" value="${localDtoCidadao?.nomePai}"/>
	%{-- campo booleano para detalhe.paiDesconhecido (informar o tipo em um hidden e tratar o valor aa partir do enum SimNao) --}%
	<input type="hidden" name="${prefixo}detalhe.paiDesconhecido_tipo" value="BOOLEAN" id="${prefixo}detalhe.paiDesconhecido_tipo">
    <g:checkBox class="checkPaiDesconhecido" name="${prefixo}detalhe.paiDesconhecido"
				onclick="vinculosCamposCidadao(this)" value="${SimNao.SIM}"
				checked="${localDtoCidadao?.mapaDetalhes['paiDesconhecido']?.asBoolean()}"/> não registrado
</g:fieldcontain>

<div class="nova-linha"></div>

<g:fieldcontain bean="${localDtoCidadao}" field="detalhe.responsavelLegal">
    <label>Nome do responsável legal<g:helpTooltip chave="help.responsavel.legal"/></label>
    <g:textField name="${prefixo}detalhe.responsavelLegal" size="40" maxlength="60" value="${localDtoCidadao.mapaDetalhes['responsavelLegal']}"/>
</g:fieldcontain>

<div class="nova-linha"></div>

<g:fieldcontain bean="${localDtoCidadao}" field="desabilitado">
    <g:checkBox name="${prefixo}desabilitado" value="${localDtoCidadao?.desabilitado}"/>
    Removido do núcleo familiar<g:helpTooltip chave="help.membro.desabilitado"/>
</g:fieldcontain>

