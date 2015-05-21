%{--
Template a ser usado em formularios de relatorio.
Espera que seja passada, via parametro render(model[dtoCidadao:???, dtoFormulario:???]),
uma instancia da classe Cidadao e outra da classe Formulario
--}%
<%
    org.apoiasuas.cidadao.Cidadao localDtoCidadao = dtoCidadao
    org.apoiasuas.formulario.Formulario localDtoFormulario = dtoFormulario
%>

<g:divCampoFormulario instancia="${localDtoCidadao}" definicaoFormulario="${localDtoFormulario}"
                      caminhoPropriedade="nomeCompleto">
    <g:textField name="nomeCompleto" size="60" value="${localDtoCidadao?.nomeCompleto}"/>
</g:divCampoFormulario>

<g:divCampoFormulario instancia="${localDtoCidadao}" definicaoFormulario="${localDtoFormulario}"
                      caminhoPropriedade="identidade">
    <g:textField name="identidade" size="30" value="${localDtoCidadao?.identidade}"/>
</g:divCampoFormulario>

<g:divCampoFormulario instancia="${localDtoCidadao}" definicaoFormulario="${localDtoFormulario}"
                      caminhoPropriedade="naturalidade">
    <g:textField name="naturalidade" size="30" value="${localDtoCidadao?.naturalidade}"/> &nbsp;-&nbsp;
    <g:textField name="UFNaturalidade" size="2" value="${localDtoCidadao?.UFNaturalidade}"/>
</g:divCampoFormulario>

<g:divCampoFormulario instancia="${localDtoCidadao}" definicaoFormulario="${localDtoFormulario}"
                      caminhoPropriedade="dataNascimento">
%{--TODO: utilizar um componente visual de escolha de datas ou permitir digitacao livre--}%
    <g:datePicker name="dataNascimento" precision="day" value="${localDtoCidadao?.dataNascimento}"
                  noSelection="['': '']" default="none"/>
</g:divCampoFormulario>

<g:divCampoFormulario instancia="${localDtoCidadao}" definicaoFormulario="${localDtoFormulario}"
                      caminhoPropriedade="nomeMae">
    <g:textField name="nomeMae" size="60" value="${localDtoCidadao?.nomeMae}"/>
</g:divCampoFormulario>

<g:divCampoFormulario instancia="${localDtoCidadao}" definicaoFormulario="${localDtoFormulario}"
                      caminhoPropriedade="nomePai">
    <g:textField name="nomePai" size="60" value="${localDtoCidadao?.nomePai}"/>
</g:divCampoFormulario>
