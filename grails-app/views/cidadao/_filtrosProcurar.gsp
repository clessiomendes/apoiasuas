<span class="campo">
    <span class="titulo">Nome ou Cad</span>
    <g:textField name="nomeOuCad" id="inputNomeOuCad" autofocus=""
                          onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"
                          value="${defaultNomePesquisa}"/>
</span>

<span class="campo">
    <span class="titulo">Endereço</span>
    <g:textField name="logradouro" id="inputLogradouro"
                       onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/>
</span>

<span class="campo">
    <span class="titulo">Nº</span>
    <g:textField name="numero" id="inputNumero" style="width: 5em"
             onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/>
</span>

<span class="mais-filtros">
    <br>
    <span class="campo">
        <span class="titulo">NIS</span>
        <g:textField name="nis" id="inputNis"
                     onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/>
    </span>

    <span class="campo">
        <span class="titulo">Programa</span>
        <g:select name="programa" id="inputPrograma" noSelection="${['':'']}" from="${programas.collect{it.descricao}}"
                  keys="${programas.collect{it.id}}"/>
    </span>

    <span class="campo">
        <span class="titulo">Idade</span>
        <g:textField name="idade" id="inputIdade" style="width: 5em"
                     onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/>
    </span>

    <br>
    <span class="campo">
        <span class="titulo hide-on-mobile">Nome de outro membro na mesma familia</span>
        <span class="titulo hide-on-desktop">Outro membro</span>
        <g:textField name="outroMembro" id="inputOutroMembro"
                     onkeydown="requisicaoProcurarCidadao(event, document.getElementById('btnProcurarCidadao'));"/>
    </span>
</span>
