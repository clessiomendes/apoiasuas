<g:tabs id="tabs" style="margin: 5px;">
	<g:tab id="tabEditFamilia" titulo="família" template="tabEditFamilia"/>
	<g:tab id="tabMarcadores" titulo="programas, ações..." template="marcador/tabMarcadores" model="[permiteInclusao: 'true']"/>
</g:tabs>

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
