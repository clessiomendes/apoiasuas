%{--
Template a ser usado em formularios de relatorio.
Espera que seja passada, via parametro render(model[dtoFamilia:???, dtoFormulario:???]),
uma instancia da classe Familia e outra da classe Formulario
--}%
<%
    org.apoiasuas.cidadao.Familia localDtoFamilia = dtoFamilia
    org.apoiasuas.formulario.Formulario localDtoFormulario = dtoFormulario
%>

<g:divCampoFormulario instancia="${localDtoFamilia}" definicaoFormulario="${localDtoFormulario}"
                      caminhoPropriedade="telefones" label="Telefone">
    <g:select name="telefoneSelecionado" from="${localDtoFamilia?.telefones}" noSelection="['-1': 'novo']"/>
%{--TODO: esconder/exibir campos de novo telefone dependendo do select telefoneSelecionado--}%
    <g:textField name="novoTelefoneDDD" size="2" value="${DDDpadrao}"/> <g:textField name="novoTelefone"/>
</g:divCampoFormulario>

<g:divCampoFormulario instancia="${localDtoFamilia}" definicaoFormulario="${localDtoFormulario}"
                      caminhoPropriedade="endereco.nomeLogradouro" label="Logradouro">
    <g:textField autofocus="true" name="endereco.tipoLogradouro" size="5"
                 value="${localDtoFamilia?.endereco?.tipoLogradouro}"/>
    <g:textField name="endereco.nomeLogradouro" size="40" value="${localDtoFamilia?.endereco?.nomeLogradouro}"/>
</g:divCampoFormulario>

<g:divCampoFormulario instancia="${localDtoFamilia}" definicaoFormulario="${localDtoFormulario}"
                      caminhoPropriedade="endereco.numero">
    <g:textField name="endereco.numero" size="5" value="${localDtoFamilia?.endereco?.numero}"/>
</g:divCampoFormulario>

<g:divCampoFormulario instancia="${localDtoFamilia}" definicaoFormulario="${localDtoFormulario}"
                      caminhoPropriedade="endereco.complemento">
    <g:textField name="endereco.complemento" value="${localDtoFamilia?.endereco?.complemento}"/>
</g:divCampoFormulario>

<g:divCampoFormulario instancia="${localDtoFamilia}" definicaoFormulario="${localDtoFormulario}"
                      caminhoPropriedade="endereco.bairro">
    <g:textField name="endereco.bairro" value="${localDtoFamilia?.endereco?.bairro}"/>
</g:divCampoFormulario>

<g:divCampoFormulario instancia="${localDtoFamilia}" definicaoFormulario="${localDtoFormulario}"
                      caminhoPropriedade="endereco.CEP">
    <g:textField name="endereco.CEP" value="${localDtoFamilia?.endereco?.CEP}"/>
</g:divCampoFormulario>

