<%@ page import="org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial" %>

<%
    AbrangenciaTerritorial abrangenciaTerritorial = abrangenciaTerritorialInstance;
%>


<div class="fieldcontain ${hasErrors(bean: abrangenciaTerritorial, field: 'nome', 'error')} ">
	<label for="nome">
		<g:message code="abrangenciaTerritorial.nome.label" default="Nome" />
        <span class="required-indicator">*</span>
	</label>
	<g:textField size="60" name="nome" required="" value="${abrangenciaTerritorial?.nome}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: abrangenciaTerritorial, field: 'habilitado', 'error')} ">
    <label for="habilitado">
        <g:message code="abrangenciaTerritorial.habilitado.label" default="Habilitado" />
    </label>
    <g:checkBox name="habilitado" value="${abrangenciaTerritorial?.habilitado}" />
</div>

<f:field property="testeMaxSize" bean="${abrangenciaTerritorial}"/>
<f:field property="testeLength" bean="${abrangenciaTerritorial}"/>

<div class="fieldcontain">
    <span id="uf-label" class="property-label"><g:message code="abrangenciaTerritorial.pai.label" default="Subordinado a" /></span>
    <span class="property-value" style="margin-left:25%" aria-labelledby="uf-label">
        <g:render template="/abrangenciaTerritorial"/>
    </span>
</div>
