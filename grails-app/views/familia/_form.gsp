<asset:javascript src="especificos/marcadores.js"/>

<%@ page import="org.apoiasuas.marcador.Acao; org.apoiasuas.programa.Programa; org.apoiasuas.seguranca.UsuarioSistema; org.apoiasuas.cidadao.Familia" %>

<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance
    org.apoiasuas.cidadao.Endereco enderecoInstance = localDtoFamilia.endereco
%>

<g:javascript>
    $(document).ready(function() {
        inicializaEventos(fieldsetProgramas, divPrograma);
        inicializaEventos(fieldsetAcoes, divAcao);
        inicializaEventos(fieldsetVulnerabilidades, divVulnerabilidade);
    } );
</g:javascript>

<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'codigoLegado', 'error')} ">
    <label for="codigoLegado">
        <g:message code="familia.codigoLegado.label" default="Codigo Legado" />
    </label>
    <g:textField name="codigoLegado" value="${localDtoFamilia?.codigoLegado}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'tecnicoReferencia', 'error')} ">
	<label for="tecnicoReferencia">
		<g:message code="familia.tecnicoReferencia.label" default="Técnico de referência" />
	</label>
	<g:select id="tecnicoReferencia" name="tecnicoReferencia.id" from="${operadores}" optionKey="id" value="${localDtoFamilia?.tecnicoReferencia?.id}" class="many-to-one" noSelection="['': '']"/>
</div>

<fieldset id="fieldsetProgramas" class="fieldsetMarcadores">
	<legend>Programas
		<input type="text" class="input-search"
               title="Digite uma palavra chave para buscar um programa específico.">
        &nbsp;<input type="button" class="btn-adicionar-marcador" style="transform: scale(0.7);"
                     title="Caso ainda não exista, você pode definir um novo programa em execução no seu território">
        &nbsp;<input type="button" class="btn-expandir-marcador" style="transform: scale(0.7);"
                     title="Expandir para ver todos os programas disponíveis">
	</legend>
	<g:each in="${programasDisponiveis}" var="progdisp" status="countMarcadores">
		<span class="marcadores-programa">
			<% Programa programaDisponivel = progdisp; %>
			<g:checkBox class="check-marcadores" name="programasDisponiveis[${countMarcadores}].selected" value="${programaDisponivel.selected}"/>
			${programaDisponivel.nome}
			<g:hiddenField name="programasDisponiveis[${countMarcadores}].id" value="${programaDisponivel.id}"/>
		</span>
	</g:each>
</fieldset>

<fieldset id="fieldsetVulnerabilidades" class="fieldsetMarcadores">
    <legend>Vulnerabilidades identificadas
        <input type="text" class="input-search"
               title="Digite uma palavra chave para buscar uma vulnerabilidade específica.">
        &nbsp;<input type="button" class="btn-adicionar-marcador" style="transform: scale(0.7);"
                     title="Caso ainda não exista, você pode definir uma nova categoria de vulnerabilidades">
        &nbsp;<input type="button" class="btn-expandir-marcador" style="transform: scale(0.7);"
                     title="Expandir para ver todas as vulnerabilidades disponíveis">
    </legend>
    <g:each in="${vulnerabilidadesDisponiveis}" var="marcadordisp" status="countMarcadores">
        <span class="marcadores-vulnerabilidade">
            <% org.apoiasuas.cidadao.Marcador marcadorVulnerabilidade = marcadordisp; %>
            <g:checkBox class="check-marcadores" name="vulnerabilidadesDisponiveis[${countMarcadores}].selected" value="${marcadorVulnerabilidade.selected}"/>
            ${marcadorVulnerabilidade.descricao}
            <g:hiddenField name="vulnerabilidadesDisponiveis[${countMarcadores}].id" value="${marcadorVulnerabilidade.id}"/>
        </span>
    </g:each>
</fieldset>

<fieldset id="fieldsetAcoes" class="fieldsetMarcadores">
	<legend>Ações previstas
		<input type="text" class="input-search"
               title="Digite uma palavra chave para buscar uma ação específica.">
		&nbsp;<input type="button" class="btn-adicionar-marcador" style="transform: scale(0.7);"
					 title="Caso ainda não exista, você pode definir uma nova categoria de ações previstas">
        &nbsp;<input type="button" class="btn-expandir-marcador" style="transform: scale(0.7);"
                     title="Expandir para ver todas as ações disponíveis">
	</legend>
	<g:each in="${acoesDisponiveis}" var="marcadordisp" status="countMarcadores">
		<span class="marcadores-acao">
			<% org.apoiasuas.cidadao.Marcador marcadorAcao = marcadordisp; %>
			<g:checkBox class="check-marcadores" name="acoesDisponiveis[${countMarcadores}].selected" value="${marcadorAcao.selected}"/>
			${marcadorAcao.descricao}
			<g:hiddenField name="acoesDisponiveis[${countMarcadores}].id" value="${marcadorAcao.id}"/>
		</span>
	</g:each>
</fieldset>

<fieldset class="embedded"><legend><g:message code="familia.endereco.label" default="Endereço" /></legend>

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

%{--		TELEFONES

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
