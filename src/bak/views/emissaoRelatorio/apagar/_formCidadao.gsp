<%@ page import="org.apoiasuas.cidadao.Cidadao" %>

<g:hiddenField name='membros[${i}].id' value='${cidadaoInstance.id}'/>

<fieldset class="embedded">
    <legend class="collapsable"><g:checkBox name="Cidadao_${cidadaoInstance?.id}" class="collapsable"/> ${cidadaoInstance?.nomeCompleto}</legend>

    <div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'nomeCompleto', 'error')} ">
        <label for="nomeCompleto">
            <g:message code="cidadao.nomeCompleto.label" default="Nome completo"/>

        </label>
        <g:textField name="membros[${i}].nomeCompleto" size="60" value="${cidadaoInstance?.nomeCompleto}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'identidade', 'error')} ">
        <label for="identidade">
            <g:message code="cidadao.identidade.label" default="Número documento"/>
        </label>
        <g:textField name="membros[${i}].identidade" size="30" value="${cidadaoInstance?.identidade}"/>
    </div>

%{--GUIA DE FOTO    --}%
    <fieldset class="embedded"><legend class="collapsable" style="cursor:pointer;">Guia de Foto</legend>

        <div align="center"><g:actionSubmit class="save" action="imprimirGuiaFoto" value="Gerar Foto"
                                            onclick="this.form.idCidadaoSelecionado.value = ${cidadaoInstance?.id}; return true"/></div>

    </fieldset>

%{--GUIA DE IDENTIDADE    --}%
    <fieldset class="embedded"><legend class="collapsable" style="cursor:pointer;">Guia de Identidade</legend>

        %{--TODO: Remover espaço extra gerado por dois divs do tipo fieldcontain juntos--}%
        <div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'naturalidade', 'error')} ">
            <div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'UFNaturalidade', 'error')} ">
                <label for="naturalidade">
                    <g:message code="cidadao.naturalidade.label" default="Naturalidade"/>
                </label>
                <g:textField name="membros[${i}].naturalidade" size="30" value="${cidadaoInstance?.naturalidade}"/> &nbsp;-&nbsp;
                <g:textField name="membros[${i}].UFNaturalidade" size="2" value="${cidadaoInstance?.UFNaturalidade}"/>
            </div>
        </div>

        <div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'dataNascimento', 'error')} ">
            <label for="dataNascimento">
                <g:message code="cidadao.dataNascimento.label" default="Data Nascimento"/>

            </label>
            %{--TODO: utilizar um componente visual de escolha de datas ou permitir digitacao livre--}%
            <g:datePicker name="membros[${i}].dataNascimento" precision="day" value="${cidadaoInstance?.dataNascimento}"
                          noSelection="['': '']" default="none"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'nomeMae', 'error')} ">
            <label for="nomeMae">
                <g:message code="cidadao.nomeMae.label" default="Nome da mãe"/>
            </label>
            <g:textField name="membros[${i}].nomeMae" size="60" value="${cidadaoInstance?.nomeMae}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: cidadaoInstance, field: 'nomePai', 'error')} ">
            <label for="nomePai">
                <g:message code="cidadao.nomePai.label" default="Nome do pai"/>
            </label>
            <g:textField name="membros[${i}].nomePai" size="60" value="${cidadaoInstance?.nomePai}"/>
        </div>


        <br><div align="center"><g:actionSubmit class="save" action="imprimirGuiaIdentidade" value="Gerar Identidade"
                        onclick="this.form.idCidadaoSelecionado.value = ${cidadaoInstance?.id}; return true"/></div>

    </fieldset>

</fieldset>
