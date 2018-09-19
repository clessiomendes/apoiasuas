<%@ page import="org.apoiasuas.util.Modulos; grails.converters.JSON; org.apoiasuas.pedidocertidao.PedidoCertidaoController; org.apoiasuas.util.SimNao; org.apoiasuas.pedidocertidao.PedidoCertidao" %>
<%
    PedidoCertidao pedidoDTO = pedidoInstance
%>

<script>
    var urlProcurarCidadao = "${createLink(controller: 'cidadao', action:'procurarCidadaoPopup')}";
    var urlMembrosFamilia = "${createLink(controller: 'pedidoCertidao', action:'getMembrosFamiliaJson')}";
    <g:applyCodec encodeAs="none">
        var membrosFamiliaJson = ${membrosFamiliaresJsonDTO}
    </g:applyCodec>
</script>

<g:render template="/mensagensPosGravacao" model="[bean: pedidoDTO]" plugin="${Modulos.NUCLEO}"/>

<g:render template="/baixarArquivo" plugin="${Modulos.NUCLEO}"/>

<g:set var="beanCamposEdicao" scope="request" value="${pedidoDTO}"/>

<g:hiddenField name="id" value="${pedidoDTO?.id}"/>

<g:campoEdicaoSelect titulo="Responsável pelo pedido" name="operadorResponsavel"
                     from="${operadores}" optionKey="id" objectValue="${pedidoDTO?.operadorResponsavel}"
              class="many-to-one" noSelection="['': '']"/>

%{--           Procurar/escolher familia                   --}%
<asset:javascript src="cidadao/procurarCidadao.js"/>
<div class="fieldcontain ${hasErrors(bean: pedidoDTO, field: 'familia', 'error')} ">
    <label>Cadastro Familiar</label>
    <input type="button" class="search field-button" value="Procurar" onclick='popupProcurarCidadao();'/>
    <span id="spanCad">
        ${raw(pedidoDTO.familia?.montaDescricaoHTML())}
    </span>
    <input id="btnLimparFamilia" type="button" class="speed-button-undo" title="Remover família selecionada" onclick="limparFamilia();" />
    <g:hiddenField id="hiddenIdFamilia" name="familia.id" value="${pedidoDTO?.familia?.id}" />
    <g:if test="${pedidoDTO.id == null && pedidoDTO?.familia?.id == null}">
        <span id="spanSemCad">
            &nbsp;&nbsp;<g:checkBox name="familiaSemCadastro"/>família sem cadastro
        </span>
    </g:if>
</div>

<g:layoutSessao titulo="Dados da certidão" icone="formulario/certidoes-w.png">
    <g:campoEdicaoSelect titulo="Tipo" name="tipoCertidao" obrigatorio="true"
                         from="${PedidoCertidao.TipoCertidao.values()}" optionValue="descricao" value="${pedidoDTO?.tipoCertidao}"
                  class="many-to-one" noSelection="['': '']"/>
    <g:campoEdicaoTexto id="txtNomeRegistro" titulo="Nome Completo" obrigatorio="true"
                        name="nomeRegistro" size="60" />
    <br>
    <g:campoEdicaoTexto id="txtDataRegistro" titulo="Data nasc/casam/óbito" name="dataRegistro" class="dateMask" size="8" />
    <g:campoEdicaoTexto titulo="Livro" name="livro" size="8" />
    <g:campoEdicaoTexto titulo="Folha" name="folha" size="8" />
    <g:campoEdicaoTexto titulo="Termo" name="termo" size="8" />
</g:layoutSessao>

<g:layoutSessao titulo="Dados do solicitante" icone="usecases/cidadao-w.png">
    <g:campoEdicaoTexto id="txtNomeSolicitante" titulo="Nome Completo" obrigatorio="true"
                        name="nomeSolicitante" size="60" />
    <br>
    <g:campoEdicaoTexto id="txtMaeSolicitante" titulo="Nome Completo da Mãe" name="maeSolicitante" size="60" quebraLinha="true"/>
    <g:campoEdicaoTexto id="txtPaiSolicitante" titulo="Nome Completo do Pai" name="paiSolicitante" size="60" />
    <br>
    <g:campoEdicaoTexto id="txtIdentidadeSolicitante" titulo="Identidade" name="identidadeSolicitante" size="16" />
    <g:campoEdicaoTexto id="txtCPFSolicitante" titulo="CPF" name="cpfSolicitante" size="16" />
    <g:campoEdicaoTexto id="txtNacionalidadeSolicitante" titulo="Nacionalidade" name="nacionalidadeSolicitante" size="16" />
    <br>
    <g:campoEdicaoTexto id="txtProfissaoSolicitante" titulo="Profissão" name="profissaoSolicitante" size="16" />
    <g:campoEdicaoTexto id="txtEstadoCivilSolicitante" titulo="Estado Civil" name="estadoCivilSolicitante" size="16" />
    <br>
    <g:campoEdicaoSelect titulo="União Estável?" name="uniaoEstavelSolicitante"
                         from="${SimNao.values()}" optionValue="descricao" value="${pedidoDTO?.uniaoEstavelSolicitante}"
                         class="many-to-one" noSelection="['': '']"/>
    <g:campoEdicaoTexto titulo="Nome do convivente" name="conviventeSolicitante" size="60" />
    <br>
    <g:campoEdicaoTexto id="txtEnderecoSolicitante" titulo="Endereço" name="enderecoSolicitante" size="60" />
    <br>
    <g:campoEdicaoTexto id="txtMunicipioSolicitante" titulo="Município" name="municipioSolicitante" size="16" />
    <g:campoEdicaoTexto id="txtUfSolicitante" titulo="UF" name="ufSolicitante" size="2" />
    <br>
    <g:campoEdicaoMemo titulo="Formas de contato" name="contatosSolicitante"
                       helpTooltip="Telefones pessoais e de recado, email, endereço, etc"/>
</g:layoutSessao>

<g:layoutSessao titulo="Dados do cartório" icone="usecases/empresa-w.png">
    <g:campoEdicaoTexto titulo="Municipio" name="municipioCartorio" size="16" />
    <g:campoEdicaoTexto titulo="UF" name="ufCartorio" size="2" />
    <g:campoEdicaoTexto titulo="Nome do cartório"
                        name="nomeCartorio" size="60" placeholder=
                        "ex: Cartório de Registro Civil do Distrito de Pindamonhangaba"/>
    <g:campoEdicaoTexto titulo="Endereço" name="enderecoCartorio" size="60" />
    <br>
    <g:campoEdicaoTexto titulo="Bairro ou Distrito" name="bairroCartorio" size="16" />
    <g:campoEdicaoTexto titulo="CEP" name="cepCartorio" size="16" />
    <g:campoEdicaoMemo titulo="Observações (para o cartório)" name="observacoesCartorio" />
    <g:campoEdicaoMemo titulo="Formas de Contato" name="contatosCartorio" />
</g:layoutSessao>
