<%@ page import="org.apoiasuas.redeSocioAssistencial.RecursosServico; org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.redeSocioAssistencial.ServicoSistema" %>
<%
    ServicoSistema servicoSistema = servicoSistemaInstance
%>

<g:tabs id="tabs" style="margin: 5px;">
    <g:tab id="tabInicial" titulo="serviço">
        <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'nome', 'error')} required">
            <label for="nome">
                <g:message code="servico.nome.label" default="Nome" />
                <span class="required-indicator">*</span>
            </label>
            <g:textField name="nome" size="60" maxlength="80" value="${servicoSistema?.nome}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'telefone', 'error')}">
            <label for="telefone">
                <g:message code="servico.telefone.label" default="Telefone(s)" />
            </label>
            <g:textField name="telefone" size="30" maxlength="30" value="${servicoSistema?.telefone}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'site', 'error')}">
            <label for="site">
                <g:message code="servico.site.label" default="Site na internet" />
            </label>
            <g:textField name="site" size="60" maxlength="80" value="${servicoSistema?.site}"/>
        </div>

        <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'email', 'error')}">
            <label for="email">
                <g:message code="servico.email.label" default="Endereço de email" />
            </label>
            <g:textField name="email" size="60" maxlength="80" value="${servicoSistema?.email}"/>
        </div>

        <br>

        <div class="fieldcontain">
            <label><g:message code="servico.abrangenciaTerritorial.label"/><label>
            <span class="property-value" style="margin-left:25%">
                <g:render template="/abrangenciaTerritorial"/>
            </span>
        </div>
    </g:tab>
    <g:tab id="tabEndereco" titulo="endereço">
        <div class="endereco">
            <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.tipoLogradouro', 'error')} ">
                <label for="endereco.tipoLogradouro">
                    <g:message code="endereco.tipoLogradouro.label" default="Tipo Logradouro" />
                </label>
                <g:textField name="endereco.tipoLogradouro" value="${servicoSistema?.endereco?.tipoLogradouro}"/>
            </div>

            <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.nomeLogradouro', 'error')} ">
                <label for="endereco.nomeLogradouro">
                    <g:message code="endereco.nomeLogradouro.label" default="Nome Logradouro" />
                </label>
                <g:textField name="endereco.nomeLogradouro" value="${servicoSistema?.endereco?.nomeLogradouro}"/>
            </div>

            <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.numero', 'error')} ">
                <label for="endereco.numero">
                    <g:message code="endereco.numero.label" default="Numero" />
                </label>
                <g:textField name="endereco.numero" value="${servicoSistema?.endereco?.numero}"/>
            </div>


            <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.complemento', 'error')} ">
                <label for="endereco.complemento">
                    <g:message code="endereco.complemento.label" default="Complemento" />
                </label>
                <g:textField name="endereco.complemento" value="${servicoSistema?.endereco?.complemento}"/>
            </div>

            <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.bairro', 'error')} ">
                <label for="endereco.bairro">
                    <g:message code="endereco.bairro.label" default="Bairro" />
                </label>
                <g:textField name="endereco.bairro" value="${servicoSistema?.endereco?.bairro}"/>
            </div>

            <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.municipio', 'error')} ">
                <label for="endereco.municipio">
                    <g:message code="endereco.municipio.label" default="Municipio" />

                </label>
                <g:textField name="endereco.municipio" value="${servicoSistema?.endereco?.municipio}"/>
            </div>

            <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.UF', 'error')} ">
                <label for="endereco.UF">
                    <g:message code="endereco.UF.label" default="UF" />
                </label>
                <g:textField name="endereco.UF" value="${servicoSistema?.endereco?.UF}"/>
            </div>

            <div class="fieldcontain ${hasErrors(bean: servicoSistema, field: 'endereco.CEP', 'error')} ">
                <label for="endereco.CEP">
                    <g:message code="endereco.CEP.label" default="CEP" />
                </label>
                <g:textField name="endereco.CEP" value="${servicoSistema?.endereco?.CEP}"/>
            </div>
        </div>
    </g:tab>
    <g:tab id="tabAcesso" titulo="recursos disponíveis" roles="${DefinicaoPapeis.STR_SUPER_USER}">
        <g:campoEdicaoSelect titulo="Token" name="token" beanCamposEdicao="${servicoSistema}"
                             helpTooltip="Identifica o serviço para fins de tratamento especial no código fonte do sistema"
                             from="${ServicoSistema.Tokens.values()}" value="${servicoSistema.token}"
                      class="many-to-one" noSelection="['': '']"/>
        <br>
        %{-- servicoSistema.habilitado (nao esta presente no enum RecursosServico) --}%
        <div class="fieldcontain" style="margin-bottom: 0.2em">
            <g:checkBox name="habilitado" value="${servicoSistema.habilitado}"/>
            acesso liberado ao sistema<br>
        </div><br>
        %{-- demais recursos do enum RecursosServico --}%
        <g:each in="${RecursosServico.recursosDisponiveis()}" var="recurso">
            <div class="fieldcontain" style="margin-bottom: 0.2em">
                <g:checkBox name="recursos.${recurso.name()}" value="${servicoSistema.recursosSelecionados().contains(recurso)}"/>
                ${recurso.descricao}
            </div><br>
        </g:each>
    </g:tab>
</g:tabs>
