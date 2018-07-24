<%@ page import="org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.redeSocioAssistencial.RecursosServico" %>
<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = familiaInstance;
%>

<g:javascript>
    var janelaModalTelefones = new JanelaModalAjax();

    /**
     * Abre popup de edicao de telefones
     */
    function editTelefones() {
        janelaModalTelefones.abreJanela( { titulo: "Alterar telefones", refreshFunction: updateTelefones,
                url: "${createLink(action:'editTelefones', params: [idFamilia: localDtoFamilia.id])}" });
    }

    function updateTelefones() {
        $("#divTelefones").html('<asset:image src="loading.gif"/> carregando...');
        ${remoteFunction(action:'listTelefones', id: localDtoFamilia.id,
            update: [success: 'divListTelefones', failure: 'divListTelefones'],
            onFailure: 'alert("Erro buscando telefones (via ajax)"); alert(textStatus); alert(Object.toHTML(XMLHttpRequest));'
        )};
    }
    //# sourceURL=tabShowTelefones
</g:javascript>

<div id="divListTelefones"/>
    <g:render template="telefone/listTelefones" model="[telefonesList: telefonesList]"/>
</div>
