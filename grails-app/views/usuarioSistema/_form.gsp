<%@ page import="org.apoiasuas.seguranca.UsuarioSistema" %>



<div class="fieldcontain ${hasErrors(bean: usuarioSistemaInstance, field: 'nomeCompleto', 'error')} required">
	<label for="nomeCompleto">
		<g:message code="usuarioSistema.nomeCompleto.label" default="Nome Completo" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="nomeCompleto" required="" value="${usuarioSistemaInstance?.nomeCompleto}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: usuarioSistemaInstance, field: 'username', 'error')} required">
	<label for="username">
		<g:message code="usuarioSistema.username.label" default="Nome Simplificado" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="username" required="" value="${usuarioSistemaInstance?.username}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: usuarioSistemaInstance, field: 'papel', 'error')} required">
	<label for="perfil">
		<g:message code="usuarioSistema.papel.label" default="Perfil de acesso" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="papel" noSelection="${['':'']}" from="${org.apoiasuas.seguranca.DefinicaoPapeis?.values}" keys="${org.apoiasuas.seguranca.DefinicaoPapeis?.values}" required="" value="${usuarioSistemaInstance?.papel}" valueMessagePrefix="DefinicaoPapeis" />

</div>

<div class="fieldcontain ${hasErrors(bean: usuarioSistemaInstance, field: 'criador', 'error')}">
	<label>
		<g:message code="usuarioSistema.criador.label" default="Criador" />
	</label>
	${usuarioSistemaInstance?.criador?.username?.encodeAsHTML()} em <g:formatDate date="${usuarioSistemaInstance?.dateCreated}" format="dd/MM/yyyy HH:mm" />

</div>

<div class="fieldcontain ${hasErrors(bean: usuarioSistemaInstance, field: 'ultimoAlterador', 'error')}">
	<label>
		<g:message code="usuarioSistema.ultimoAlterador.label" default="Ultimo Alterador" />
	</label>
	${usuarioSistemaInstance?.ultimoAlterador?.username?.encodeAsHTML()} em <g:formatDate date="${usuarioSistemaInstance?.lastUpdated}" format="dd/MM/yyyy HH:mm" />

</div>

