<%@ page import="org.apoiasuas.cidadao.Cidadao" %>

<%
    List<org.apoiasuas.cidadao.Familia> familiaInstanceList = familiaInstanceList;
%>

<script>
    function btnCopyClick(button, conteudo) {
        $(button).fadeOut(350, function() {
            $(button).removeClass('btn-copy-antes').addClass('btn-copy-depois');
            $(button).fadeIn(350);
        });
        copyToClipboard(conteudo);
    }
</script>

<g:hasErrors bean="${familiaInstance}">
    <ul class="errors" role="alert">
        <g:eachError bean="${familiaInstance}" var="error">
            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
        </g:eachError>
    </ul>
</g:hasErrors>

<g:if test="${request['erroCodigoLegado']}">
    <ul class="errors" role="alert">
        <li>${request['erroCodigoLegado']}</li>
    </ul>
</g:if>

<table class="tabelaListagem">
    <thead><tr>
        <th>Data</th>
        %{--<th><g:sortableColumn property="nomeCompleto" title="${message(code: 'cidadao.nomeCompleto.label', default: 'Nome')}" /></th>--}%
        <th>Endereço</th>
        <th>Membro Familiar</th>
        <th></th>
        <th>Nascimento</th>
        <th></th>
        <th></th>
    </tr></thead>
    <tbody>
    <g:set var="corMembro" value="${0}"/>
    <g:set var="corFamilia" value="${0}"/>
    <g:each in="${familiaInstanceList}" var="familia">
        <tr class="${(++corFamilia % 2) == 0 ? 'even' : 'odd'}">
            <g:form action="gravaCodigoLegado" id="${familia.id}" >
                <td rowspan="${familia.getMembrosOrdemPadrao(true).size()}"><g:formatDate date="${familia?.dateCreated}" /></td>
                <td rowspan="${familia.getMembrosOrdemPadrao(true).size()}">${familia.endereco}</td>
                <td class="${(++corMembro % 2) == 0 ? 'even' : 'odd'}">${familia.referencia?.nomeCompleto}</td>
                <td class="${(corMembro % 2) == 0 ? 'even' : 'odd'}">
                    <input type="button" value="Copiar 1" class="btn-copy-antes" onclick="btnCopyClick(this, '${familia.referencia?.copyPaste1}');"/>
                </td>
                <td class="${(corMembro % 2) == 0 ? 'even' : 'odd'}"><g:formatDate date="${familia?.referencia?.dataNascimento}" /></td>
                <td class="${(corMembro % 2) == 0 ? 'even' : 'odd'}">
                    <input type="button" value="Copiar 2" class="btn-copy-antes" onclick="btnCopyClick(this, '${familia.referencia?.copyPaste2}');"/>
                </td>
                <td rowspan="${familia.getMembrosOrdemPadrao(true).size()}" style="text-align: left;">
                    Novo Cad:
                    <g:textField name="codigoLegado" size="5"/>
                    <g:submitButton name="grava" class="save" style="margin-top: 5px" value="Gravar"/>
                </td>
            </g:form>
        </tr>
        <g:each in="${familia.getMembrosOrdemPadrao(true).drop(1)}" var="cidadao">
            <tr>
                %{--<td></td>--}%
                %{--<td></td>--}%
                <td class="${(++corMembro % 2) == 0 ? 'even' : 'odd'}">${cidadao.nomeCompleto}</td>
                <td class="${(corMembro % 2) == 0 ? 'even' : 'odd'}">
                    <input type="button" value="Copiar 1" class="btn-copy-antes" onclick="btnCopyClick(this, '${cidadao.copyPaste1}');"/>
                </td>
                <td class="${(corMembro % 2) == 0 ? 'even' : 'odd'}"><g:formatDate date="${cidadao.dataNascimento}" /></td>
                <td class="${(corMembro % 2) == 0 ? 'even' : 'odd'}">
                    <input type="button" value="Copiar 2" class="btn-copy-antes" onclick="btnCopyClick(this, '${cidadao.copyPaste2}');"/>
                </td>
                %{--<td></td>--}%
            </tr>
        </g:each>
    </g:each>
    </tbody>
</table>
