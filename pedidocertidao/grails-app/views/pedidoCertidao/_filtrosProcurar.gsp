<%@ page import="org.apoiasuas.pedidocertidao.PedidoCertidaoController" %>
<span class="campo">
    <span class="titulo">Nome ou Cad</span>
    <g:textField name="nomeOuCad" id="inputNomeOuCad" autofocus=""/>
</span>

<span class="campo">
    <span class="titulo">Situação</span>
    <g:select name="situacao" from="${org.apoiasuas.pedidocertidao.PedidoCertidaoController.situacoesPersonalizadas()}"
                         class="many-to-one" noSelection="['': '']"/>
</span>

<span class="mais-filtros">
    <br>
    <span class="campo">
        <span class="titulo">Cartório/Município</span>
        <g:textField name="cartorioOuMinicipio"/>
    </span>

    <span class="campo">
        <span class="titulo"></span>
        <g:checkBox name="cartorioIndefinido"/> Cartório Indefinido
    </span>

    <br>

    <span class="campo">
        <span class="titulo">Responsável</span>
        <g:select name="responsavel" from="${operadores}" optionKey="id" class="many-to-one" noSelection="['': '']"/>
    </span>
</span>
