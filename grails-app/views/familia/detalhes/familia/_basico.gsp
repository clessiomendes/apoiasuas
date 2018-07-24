<%@ page import="org.apoiasuas.seguranca.DefinicaoPapeis; org.apoiasuas.redeSocioAssistencial.RecursosServico; org.apoiasuas.CustomizacoesService" %>
<%
    org.apoiasuas.cidadao.Endereco enderecoInstance = localDtoFamilia.endereco
%>

%{--Se estiver em modo de criação, exigir o nome da RF para a primeira gravacao--}%
<g:fieldcontain showif="${modoCriacao}" class="required ${erroReferencia ? 'error' : ''}">
    <label>
        Nome Completo da RF<g:helpTooltip chave="help.referencia.familiar"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField id="txtNomeReferencia" class="importante" name="nomeReferenciaFamiliar" size="60" maxlength="60" value="${nomeReferencia}"/>
</g:fieldcontain>


%{--  exemplo para   C U S T O M I Z A Ç Õ E S
<g:fieldcontain showto="${[CustomizacoesService.Codigos.BELO_HORIZONTE_HAVAI_VENTOSA]}"
                bean="${localDtoFamilia}" field="endereco.nomeLogradouro">
    <label> Apagar 1</label>
    <g:textField name="nomeReferenciaFamiliar2" size="60" maxlength="60" value="${nomeReferencia}"/>
</g:fieldcontain>
--}%

<div class="nova-linha"></div>

<div class="endereco">

    <g:fieldcontain bean="${localDtoFamilia}" field="endereco.tipoLogradouro">
        <label>Tipo Logradouro<g:helpTooltip chave="help.tipo.logradouro"/></label>
        <g:textField name="endereco.tipoLogradouro" class="importante" size="5" maxlength="15" value="${enderecoInstance?.tipoLogradouro}"/>
    </g:fieldcontain>

    <g:fieldcontain bean="${localDtoFamilia}" field="endereco.nomeLogradouro">
        <label>Nome Logradouro<span class="required-indicator">*</span></label>
        <g:textField name="endereco.nomeLogradouro" class="importante" size="60" maxlength="60" value="${enderecoInstance?.nomeLogradouro}"/>
    </g:fieldcontain>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.numero', 'error')} ">
        <label>Numero</label>
        <g:textField name="endereco.numero" class="importante" size="5" maxlength="7" value="${enderecoInstance?.numero}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.complemento', 'error')} ">
        <label>Complemento</label>
        <g:textField name="endereco.complemento" size="30" maxlength="30" value="${enderecoInstance?.complemento}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.bairro', 'error')} ">
        <label>Bairro</label>
        <g:textField name="endereco.bairro" class="importante" size="30" maxlength="60" value="${enderecoInstance?.bairro}"/>
    </div>

    <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'endereco.CEP', 'error')} ">
        <label>CEP</label>
        <g:textField name="endereco.CEP" size="7" maxlength="10" value="${enderecoInstance?.CEP}"/>
    </div>

    %{--Pergunto o municipio ?--}%
    <g:fieldcontain bean="${localDtoFamilia}" field="endereco.municipio"
            hidefrom="${[CustomizacoesService.Codigos.BELO_HORIZONTE_HAVAI_VENTOSA, CustomizacoesService.Codigos.BELO_HORIZONTE_VISTA_ALEGRE]}">
        <label>Municipio - UF</label>
        <g:textField name="endereco.municipio" class="destinoSugestao1" size="30" maxlength="60" value="${enderecoInstance?.municipio}"/>
        <g:textField name="endereco.UF" class="destinoSugestao2" size="2" maxlength="2" value="${enderecoInstance?.UF}"/>
        <span class="sugestaoPreenchimento">
            <input type="button" class="speed-button-sugestao" onclick="clickSugestao(this);" title="preencher com informação padrão"/>
            <span class="origemSugestao1">${municipioLogado}</span> - <span class="origemSugestao2">${UFLogada}</span>
        </span>
    </g:fieldcontain>
    <sec:access showto="${[CustomizacoesService.Codigos.BELO_HORIZONTE_HAVAI_VENTOSA, CustomizacoesService.Codigos.BELO_HORIZONTE_VISTA_ALEGRE]}">
        <div class="fieldcontain hidden">
            <g:hiddenField name="endereco.municipio" value="${enderecoInstance?.municipio ?: municipioLogado}"/>
            <g:hiddenField name="endereco.UF" value="${enderecoInstance?.UF ?: UFLogada}"/>
        </div>
    </sec:access>

</div>

<div class="nova-linha"></div>

<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'detalhe.referenciaLocalizacao', 'error')} ">
    <label>Referência de localização<g:helpTooltip chave="help.referencia.localizacao"/></label>
    <g:textField name="detalhe.referenciaLocalizacao" size="60" maxlength="255" value="${localDtoFamilia.mapaDetalhes['referenciaLocalizacao']}"/>
</div>

<div class="nova-linha"></div>

%{--TELEFONES--}%
<div class="fieldcontain">
    <g:render template="/familia/telefone/formTelefones" model="${[localDtoFamilia: localDtoFamilia]}"/>
</div>

<div class="nova-linha"></div>

<div class="required fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'criador', 'error')} ">
    <label>
        Responsável pelo Cadastro
        <span class="required-indicator">*</span>
    </label>
    <g:select id="criador" name="criador"
              from="${operadores}" optionKey="id" objectValue="${localDtoFamilia?.criador}"
              class="many-to-one" noSelection="['': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'tecnicoReferencia', 'error')} ">
    <label>Técnico de referência<g:helpTooltip chave="help.tecnico.referencia"/></label>
    <g:select id="tecnicoReferencia" name="tecnicoReferencia"
              from="${operadores}" optionKey="id" objectValue="${localDtoFamilia?.tecnicoReferencia}"
              class="many-to-one" noSelection="['': '']"/>
              %{--from="${operadores}" optionKey="id" value="${localDtoFamilia?.tecnicoReferencia?.id}"--}%
</div>

%{--
O código legado só fica disponível: 1) se o ServicoSistema tem acesso a este recurso, 2) em modo edição e 3) perfil admin
--}%
<g:if test="${modoEdicao}" >
    <sec:ifAnyGranted roles="${DefinicaoPapeis.STR_SUPER_USER}">
        <sec:access acessoServico="${RecursosServico.IDENTIFICACAO_PELO_CODIGO_LEGADO}">
            <div class="fieldcontain ${hasErrors(bean: localDtoFamilia, field: 'codigoLegado', 'error')} ">
                <label>Cad</label>
                <g:textField name="codigoLegado" size="10" value="${localDtoFamilia?.codigoLegado}"/>
            </div>
        </sec:access>
    </sec:ifAnyGranted>
</g:if>
