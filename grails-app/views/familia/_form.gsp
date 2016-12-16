<%@ page import="org.apoiasuas.programa.Programa; org.apoiasuas.seguranca.UsuarioSistema; org.apoiasuas.cidadao.Familia" %>

<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance
    org.apoiasuas.cidadao.Endereco enderecoInstance = localDtoFamilia.endereco
%>

%{--
<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'situacaoFamilia', 'error')} required">
	<label for="situacaoFamilia">
		<g:message code="familia.situacaoFamilia.label" default="Situacao Familia" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="situacaoFamilia" from="${org.apoiasuas.cidadao.SituacaoFamilia?.values()}" keys="${org.apoiasuas.cidadao.SituacaoFamilia.values()*.name()}" required="" value="${localDtoFamilia?.situacaoFamilia?.name()}" />
</div>
--}%

<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'codigoLegado', 'error')} ">
    <label for="codigoLegado">
        <g:message code="familia.codigoLegado.label" default="Codigo Legado" />
    </label>
    <g:textField name="codigoLegado" value="${localDtoFamilia?.codigoLegado}"/>
</div>

%{--
<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'familiaAcompanhada', 'error')} ">
    <label for="familiaAcompanhada">
        <g:message code="familia.familiaAcompanhada.label" default="Acampanhada" />
    </label>
    <g:checkBox name="familiaAcompanhada" value="${localDtoFamilia?.familiaAcompanhada}"/>
</div>
--}%

<fieldset id="fieldsetProgramas" class="embedded"}>
    <legend>
        <g:message code="familia.programas" default="Programas, benefícios e projetos" />
    </legend>

    <g:each in="${programasDisponiveis}" var="progdisp" status="i">
        <% Programa programaDisponivel = progdisp %>
        <div class="fieldcontain">
            <label>
                <g:checkBox name="programasdisponiveis[${i}].selected" value="${programaDisponivel.selected}"/>
            </label>
            <g:hiddenField name="programasdisponiveis[${i}].id" value="${programaDisponivel.id}"/>
            ${programaDisponivel.nome ?: programaDisponivel.sigla}
        </div>
    </g:each>
</fieldset>

<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'tecnicoReferencia', 'error')} ">
    <label for="tecnicoReferencia">
        <g:message code="familia.tecnicoReferencia.label" default="Técnico de referência" />
    </label>
    <g:select id="tecnicoReferencia" name="tecnicoReferencia.id" from="${operadores}" optionKey="id" value="${localDtoFamilia?.tecnicoReferencia?.id}" class="many-to-one" noSelection="['': '']"/>
</div>

<fieldset class="embedded"><legend><g:message code="familia.endereco.label" default="Endereco" /></legend>

	<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.tipoLogradouro', 'error')} ">
		<label for="endereco.tipoLogradouro">
			<g:message code="familia.endereco.tipoLogradouro.label" default="Tipo Logradouro" />
		</label>
		<g:textField name="endereco.tipoLogradouro" size="15" maxlength="15" value="${enderecoInstance?.tipoLogradouro}"/>
	</div>

	<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.nomeLogradouro', 'error')} ">
		<label for="endereco.nomeLogradouro">
			<g:message code="familia.endereco.nomeLogradouro.label" default="Nome Logradouro" />
		</label>
		<g:textField name="endereco.nomeLogradouro" size="60" maxlength="60" value="${enderecoInstance?.nomeLogradouro}"/>
	</div>

	<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.numero', 'error')} ">
		<label for="endereco.numero">
			<g:message code="familia.endereco.numero.label" default="Numero" />
		</label>
		<g:textField name="endereco.numero" size="5" maxlength="5" value="${enderecoInstance?.numero}"/>
	</div>

	<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.complemento', 'error')} ">
		<label for="endereco.complemento">
			<g:message code="familia.endereco.complemento.label" default="Complemento" />
		</label>
		<g:textField name="endereco.complemento" size="30" maxlength="30" value="${enderecoInstance?.complemento}"/>
	</div>

	<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.bairro', 'error')} ">
		<label for="endereco.bairro">
			<g:message code="familia.endereco.bairro.label" default="Bairro" />
		</label>
		<g:textField name="endereco.bairro" size="30" maxlength="30" value="${enderecoInstance?.bairro}"/>
	</div>

	<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.CEP', 'error')} ">
		<label for="endereco.CEP">
			<g:message code="familia.endereco.CEP.label" default="CEP" />
		</label>
		<g:textField name="endereco.CEP" size="7" maxlength="10" value="${enderecoInstance?.CEP}"/>
	</div>

	<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.municipio', 'error')} ">
		<label for="endereco.municipio">
			<g:message code="familia.endereco.municipio.label" default="Municipio" />
		</label>
		<g:textField name="endereco.municipio" size="30" maxlength="60" value="${enderecoInstance?.municipio}"/>
	</div>

	<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.UF', 'error')} ">
		<label for="endereco.UF">
			<g:message code="familia.endereco.UF.label" default="UF" />
		</label>
		<g:textField name="endereco.UF" size="2" maxlength="2" value="${enderecoInstance?.UF}"/>
	</div>

</fieldset>

%{--
<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'membros', 'error')} ">
	<label for="membros">
		<g:message code="familia.membros.label" default="Membros" />
		
	</label>
<ul class="one-to-many">
<g:each in="${localDtoFamilia?.membros?}" var="m">
    <li><g:link controller="cidadao" action="show" id="${m.id}">${m?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="cidadao" action="create" params="['familia.id': localDtoFamilia?.id]">${message(code: 'default.add.label', args: [message(code: 'cidadao.label', default: 'Cidadao')])}</g:link>
</li>
</ul>
</div>
--}%

%{--
<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'telefones', 'error')} ">
	<label for="telefones">
		<g:message code="familia.telefones.label" default="Telefones" />
		
	</label>
<ul class="one-to-many">
<g:each in="${localDtoFamilia?.telefones?}" var="t">
    <li><g:link controller="telefone" action="show" id="${t.id}">${t?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="telefone" action="create" params="['familia.id': localDtoFamilia?.id]">${message(code: 'default.add.label', args: [message(code: 'telefone.label', default: 'Telefone')])}</g:link>
</li>
</ul>
</div>
--}%

