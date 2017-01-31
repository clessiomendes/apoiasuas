<g:javascript>
	$(document).ready(function() {
		$("#tabs").tabs();
	} );
</g:javascript>

<div id="tabs" style="margin: 5px;">
    <ul>
        <li><a href="#tabEditFamilia">família</a> </li>
        <li><a href="#tabMarcador">programas, ações...</a> </li>
    </ul>
	<div id="tabEditFamilia">
		<g:render template="tabEditFamilia"/>
	</div>
	<div id="tabMarcador">
		<g:render template="tabMarcadores"/>
	</div>
</div>

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
