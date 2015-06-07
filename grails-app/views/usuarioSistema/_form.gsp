<%@ page import="org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.seguranca.UsuarioSistema" %>
<% UsuarioSistema localDtoUsuarioSistema = usuarioSistemaInstance %>
    
<div class="fieldcontain ${hasErrors(bean: localDtoUsuarioSistema, field: 'nomeCompleto', 'error')} required">
	<label for="nomeCompleto">
		<g:message code="usuarioSistema.nomeCompleto.label" default="Nome Completo" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="nomeCompleto" required="" value="${localDtoUsuarioSistema?.nomeCompleto}"/>
</div>

<sec:ifAnyGranted roles="${org.apoiasuas.seguranca.DefinicaoPapeis.SUPER_USER}">
    <div class="fieldcontain ${hasErrors(bean: localDtoUsuarioSistema, field: 'username', 'error')} required">
        <label for="username">
            <g:message code="usuarioSistema.username.label" default="Nome Simplificado"/>
            <span class="required-indicator">*</span>
        </label>
        <g:textField name="username" required="" value="${localDtoUsuarioSistema?.username}"/>
    </div>
    <div class="fieldcontain ${hasErrors(bean: localDtoUsuarioSistema, field: 'papel', 'error')} required">
        <label for="perfil">
            <g:message code="usuarioSistema.papel.label" default="Perfil de acesso" />
            <span class="required-indicator">*</span>
        </label>
        <g:select name="papel" noSelection="${['':'']}" from="${org.apoiasuas.seguranca.DefinicaoPapeis?.values}" keys="${org.apoiasuas.seguranca.DefinicaoPapeis?.values}" required="" value="${localDtoUsuarioSistema?.papel}" valueMessagePrefix="DefinicaoPapeis" />
    </div>
    <div class="fieldcontain ${hasErrors(bean: localDtoUsuarioSistema, field: 'enabled', 'error')}">
        <label></label>
        <g:checkBox name="enabled" value="${localDtoUsuarioSistema?.enabled}"/>
        <g:message code="usuarioSistema.enabled.label" default="Acesso liberado" />
    </div>
</sec:ifAnyGranted>

<div class="fieldcontain ${hasErrors(bean: usuarioSistemaInstance, field: 'password', 'error')}">
    <label for="password1">
        <g:message code="usuarioSistema.digiteSenha.label" default="Escolha uma senha" />
    </label>
    <g:passwordField name="password1"/>
</div>

<div class="fieldcontain ${hasErrors(bean: usuarioSistemaInstance, field: 'password', 'error')}">
    <label for="password2">
        <g:message code="usuarioSistema.redigiteSenha.label" default="Redigite a senha" />
    </label>
    <g:passwordField name="password2"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoUsuarioSistema, field: 'criador', 'error')}">
	<label>
		<g:message code="usuarioSistema.criador.label" default="Criador" />
	</label>
	${localDtoUsuarioSistema?.criador?.username?.encodeAsHTML()} em <g:formatDate date="${localDtoUsuarioSistema?.dateCreated}" format="dd/MM/yyyy HH:mm" />

</div>

<div class="fieldcontain ${hasErrors(bean: localDtoUsuarioSistema, field: 'ultimoAlterador', 'error')}">
	<label>
		<g:message code="usuarioSistema.ultimoAlterador.label" default="Ultimo Alterador" />
	</label>
	${localDtoUsuarioSistema?.ultimoAlterador?.username?.encodeAsHTML()} em <g:formatDate date="${localDtoUsuarioSistema?.lastUpdated}" format="dd/MM/yyyy HH:mm" />

</div>

