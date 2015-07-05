<%@ page import="org.apoiasuas.Servico; org.apoiasuas.servico.Servico; org.apoiasuas.formulario.Formulario" %>
<%
    org.apoiasuas.formulario.Formulario localDtoFormulario = dtoFormulario
%>

<g:javascript>

    /**
    * Funcao executada apos a chamada ajax para preencher os campos do formulario com os dados do servico escolhido
    * @param data
    */
    function preencheEncaminhamentos(data) {
        //document.getElementById("avulso.descricao_encaminhamento").value = "any shit";
        //alert(document.getElementById("avulso.descricao_encaminhamento").value);
        document.getElementById("avulso.destino").value = data.nomeFormal;
        document.getElementById("avulso.endereco_destino").value = data.enderecoCompleto;
        //document.getElementById("avulso.descricao_encaminhamento").value = data.nomeFormal;
        if (document.getElementById("avulso.descricao_encaminhamento").value) {
            if (confirm("Sobrescrever o detalhamento do encaminhamento com o padrão para "+data.apelido+"?"))
                document.getElementById("avulso.descricao_encaminhamento").value = data.encaminhamentoPadrao;
        } else
            document.getElementById("avulso.descricao_encaminhamento").value = data.encaminhamentoPadrao;
    }

    /**
    * chamada ajax para obter os dados do cadastro do servico
    */
    function ajaxServico(idServico) {
        ${remoteFunction(controller: 'servico', action: 'getServico', params: "'idServico='+escape(idServico)", onSuccess: 'preencheEncaminhamentos(data)')}
    }

    /**
    * Sempre que carregar a página, submete a chamada ajax identica de selecao de servico
    */
    $(document).ready(function() {
        if (document.getElementById("servico").value != '') {
            ajaxServico(document.getElementById("servico").value)
        }
    });

</g:javascript>

<div class="fieldcontain">
    <label>
        Serviço
    </label>
    <g:select optionKey='id' optionValue="apelido" name="servico" id="servico" from="${org.apoiasuas.Servico.list().sort({it.apelido})}" noSelection="['null': '']"
              value="${idServico ?: ''}" style="max-width:400px;"
              onchange="ajaxServico(this.value)"/>
</div>

<g:each in="${localDtoFormulario.camposAgrupados}" var="grupo" status="i"> %{-- separa os campos em grupos --}%
    <g:if test="${grupo[0].grupo}"> %{-- se o grupo tiver nome, cria uma caixa para ele --}%
        <fieldset class="embedded"><legend class="collapsable" style="cursor:pointer;">${grupo[0].grupo}</legend>
    </g:if>
    <g:each in="${grupo}" var="campo" status="j"> %{-- lista os campos do grupo --}%
        <g:divCampoFormularioCompleto campoFormulario="${campo}" focoInicial="${i*j == 1}"/>
    </g:each>
    <g:if test="${grupo[0].grupo}">
        </fieldset>
    </g:if>
</g:each>

