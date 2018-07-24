<%@ page import="org.apoiasuas.cidadao.FamiliaController; org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.redeSocioAssistencial.RecursosServico" %>
<%
    List<Map> localDtoTelefones = telefonesList;
%>

<script>
    function deleteTelefoneAgendamento(idAtendimento) {
        var actionUrl = "${createLink(action:'deleteTelefoneAtendimento')}/" + idAtendimento;
        jQuery.ajax({
            type:'POST',
            url: actionUrl,
            success: updateTelefones,
//            success:function(data,textStatus){
//                jQuery('#updateTelefones').html(data);
//            },
            error: function(XMLHttpRequest,textStatus,errorThrown){
                alert("Erro removendo telefone (via ajax)");
            }
        });
    }
</script>

<fieldset class="embedded fieldcontain" style="padding: 10px; display: block">
    <legend>${FamiliaController.TELEFONES_CADASTRADOS}</legend>

    <table class="tabelas-telefones">
        <tbody>
            <g:each in="${localDtoTelefones.findAll { it.origem == FamiliaController.TELEFONES_CADASTRADOS}}" status="i" var="telefone">
                <tr><td>
                    <div class="fieldcontain">
                        <span class="property-value">${telefone.numero}</span>
                    </div>
                </td><td>
                    <div class="fieldcontain">
                        <span class="property-value"> <g:formatDate date="${telefone?.data}" format="(MMM/yyyy)"/> </span>
                    </div>
                </td><td>
                    <g:if test="${telefone.observacoes}">
                        <div class="fieldcontain">
                            <span class="property-value">${telefone.observacoes}</span>
                        </div>
                    </g:if>
                </td></tr>
            </g:each>
        </tbody>
    </table>
    <input id="editarTelefones" type="button" value="Atualizar números"  class="edit"
                                 title="Clique para incluir, remover ou alterar telefones no cadastro."
                                 onclick="editTelefones();">
</fieldset>

<fieldset class="embedded fieldcontain" style="padding: 10px; display: block">
    <legend>${FamiliaController.TELEFONES_AGENDAMENTO}</legend>

    <table class="tabelas-telefones">
        <tbody>
            <g:each in="${localDtoTelefones.findAll { it.origem == FamiliaController.TELEFONES_AGENDAMENTO}}" status="i" var="telefone">
                <tr><td>
                    <div class="fieldcontain">
                        <span class="property-value">${telefone.numero}</span>
                    </div>
                </td><td>
                    <div class="fieldcontain">
                        <span class="property-value"> <g:formatDate date="${telefone?.data}" format="(MMM/yyyy)"/> </span>
                    </div>
                </td><td>
                    <g:if test="${telefone.observacoes}">
                        <div class="fieldcontain">
                            <span class="property-value">${telefone.observacoes}</span>
                        </div>
                    </g:if>
                </td><td>
                    <input type="button" id="removerTelefone" class="speed-button-remover" onclick="deleteTelefoneAgendamento(${telefone.idAtendimento}); return false;" title="remover"/>
                </td></tr>
            </g:each>
        </tbody>
    </table>
</fieldset>
