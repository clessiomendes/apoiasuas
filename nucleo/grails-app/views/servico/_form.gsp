<asset:javascript src="context-menu/jquery.ui-contextmenu.js"/>

<%@ page import="org.apoiasuas.redeSocioAssistencial.ServicoController; org.apoiasuas.fileStorage.FileStorageDTO; org.apoiasuas.redeSocioAssistencial.Servico" %>
<%
    Servico localDtoServico = servicoInstance
%>

<script>
    <g:enumToJavascript enum="${FileStorageDTO.FileActions}"/>
</script>

%{--                            Controles da imagem do servico -->                         --}%
<label class="hasmenu" style="float: left" onclick="$(document).contextmenu('open', $(this));">
        <img id="imgServico" class="imagem-servico clicable" src="${imagemServico ?: assetPath([src: 'servico/sem-imagem.png'])}">
</label>
<input type="file" class="hidden" id="inputSelecionarArquivo" name="image" accept="image/*" onchange="imagemServico(this); this.value = null;">
<asset:image id="imgVazia" class="hidden" src="servico/sem-imagem.png"/>
<g:hiddenField name="urlImagem" value="${imagemServico ?: ''}"/>
<g:hiddenField name="fileAction" value="${imagemServico ? FileStorageDTO.FileActions.ATUALIZAR : FileStorageDTO.FileActions.MANTER_ATUAL}"/>

%{--Janela popup de corte da imagem para o serviço--}%
<div style="display: none" id='divImagemServico'>
    <div style="width: 500px; height: 500px">
        <canvas id="canvas">
            Seu navegador é icnompatível com este recurso (HTML5 canvas)
        </canvas>
    </div>
    <br>
    <input type="button" name="update" class="save" value="Selecionar" onclick="imagemSelecionar();" />
    <input type="button" name="update" class="cancel" value="Cancelar" onclick="imagemCancelar();" />
</div>

%{--                            <-- Controles da imagem do serviço                            --}%

%{--
    <g:campoEdicaoTexto titulo="Site na internet" name="site" quebraLinha="true" placeholder="www.meusite.com" size="60" maxlenght="255"/>

    <g:campoEdicaoMemo titulo="Telefones de atendimento ao público" name="telefones" quebraLinha="true" placeholder=
"ex: Candidatos: (11) 2222-5555
Empresas: (11) 2222-6666"/>
--}%

<div class="direita-imagem">

    <g:campoEdicaoTexto titulo="Nome Popular" obrigatorio="true" name="apelido" size="60" maxlenght="60" classesDiv="linha-inteira"/>
    <g:campoEdicaoTexto titulo="Nome Formal" quebraLinha="true" name="nomeFormal" size="60" maxlenght="60" classesDiv="linha-inteira"
                        placeholder= "ex: Associação de Promoção do Jovem Aprendiz"/>

    <g:campoEdicao titulo="Serviço em funcionamento" quebraLinha="true" name="habilitado" classesDiv="linha-inteira">
        <g:checkBox name="habilitado" value="${localDtoServico?.habilitado}"/> sim
    </g:campoEdicao>

</div>

%{--<g:helpTooltip chave="help.servico.descricao"/>--}%
<g:layoutSessao titulo="Descrição detalhada" icone="usecases/info-w.png">
    <g:campoEdicaoMemo titulo="O que é oferecido? Descrição do benefício ou das ações ofertados"
                       name="descricao" quebraLinha="true" placeholder=
                       "ex: Entidade filantrópica de assistência social que oferece capacitação e encaminhamento para trabalho como Jovem Aprendiz"/>
</g:layoutSessao>

<g:layoutSessao titulo="Restrição do público atendido" icone="usecases/publico-w.png">
    <g:campoEdicaoMemo titulo="Quem tem direito? Restrições de renda, idade, escolaridade, sexo, raça, deficiência, etc para ter acesso ao serviço"
                       name="publico" quebraLinha="true" placeholder=
"ex: Adolescentes de 15 a 16 anos, estudando à partir do 8º ano do Ensino Fundamental
Jovens de 18 a 22 anos, estudando à partir do 1º ano do Ensino Médio ou que concluíram os estudos
Exclusivo para famílias com renda inferior a dois salários mínimos"/>
</g:layoutSessao>

<g:layoutSessao titulo="Documentos necessários" icone="usecases/documentos-w.png">
    <g:campoEdicaoMemo titulo="O que levar? Lista dos documentos que devem ser apresentados, como identidade, CPF, histórico escolar, etc"
                       name="documentos" quebraLinha="true" placeholder=
"ex: Xerox e original de:
- identidade
- comprovante de endereço emitido nos últimos 3 meses"/>
</g:layoutSessao>

<g:layoutSessao titulo="Endereços e horários de atendimento" icone="usecases/endereco-w.png">
    <div id="divEnderecos" style="display: table; width: 100%">
        <div class="linhaEnderecos">
            <span class="instrucoes">Endereços e horários em que é oferecido atendimento ao público</span>
        </div>
        <g:each in="${localDtoServico?.enderecos?.split('\\r?\\n')?.findAll { it?.trim() ? true : false }}" var="endereco">
            <div class="linhaEnderecos">
                <g:textField name="${ServicoController.LINHA_ENDERECOS}" value="${endereco}" placeholder=
                "ex: oeste: Rua Pérola, 500, Bairro da Graça. Segunda a sexta, 8:00 às 18:00"/>
                <input type="button" class="speed-button-remover" onclick="removerLinhaEndereco(this);"/>
            </div>
        </g:each>
        <div class="linhaEnderecos">
            <g:textField name="${ServicoController.LINHA_ENDERECOS}" placeholder=
                    "ex: oeste: Rua Pérola, 500, Bairro da Graça. Segunda a sexta, 8:00 às 18:00"/>
            <input type="button" class="speed-button-remover" onclick="removerLinhaEndereco(this);"/>
        </div>
        <div class="linhaEnderecos">
            %{--<tmpl:atualizadoEm campo="contatosInternos"/>--}%
            <input id="btnAdicionarEndereco" type="button" class="speed-button-adicionar"
                   style="display: table-cell" title="Adicionar endereço" onclick="adicionarLinhaEndereco();"/>
        </div>
    </div>

    %{-- div invisivel a ser usado como modelo para o botao btnAdicionarEndereco--}%
    <div id="modeloLinhaEnderecos" class="linhaEnderecos hidden">
        <g:textField name="${ServicoController.LINHA_ENDERECOS}" placeholder=
        "ex: oeste: Rua Pérola, 500, Bairro da Graça. Segunda a sexta, 8:00 às 18:00"/>
        <input type="button" class="speed-button-remover" onclick="removerLinhaEndereco(this);"/>
    </div>
</g:layoutSessao>

<g:layoutSessao titulo="Fluxo do atendimento" icone="usecases/fluxo-w.png">
    <g:campoEdicaoMemo titulo="Como acessar? Presencialmente, por telefone, pelo site, por correio ou por malote. Até X anos deve estar acompanhado de responsável?"
                       name="fluxo" quebraLinha="true" placeholder=
"ex: Inscrições pelo site www.meusite.com/inscricao ou pessoalmente
Menores de 16 anos devem estar acompanhados do pai, mãe ou responsável legal"/>
</g:layoutSessao>

<g:layoutSessao titulo="Contato" icone="usecases/telefone-w.png">
    <g:campoEdicaoTexto titulo="Site na internet" name="site" quebraLinha="true" placeholder="www.meusite.com" size="60" maxlenght="255"/>
    %{--<tmpl:atualizadoEm campo="contatosInternos"/>--}%
    <g:campoEdicaoMemo titulo="Telefones de atendimento ao público" name="telefones" quebraLinha="true" placeholder=
"ex: Candidatos: (11) 2222-5555
Empresas: (11) 2222-6666"/>
    %{--<tmpl:atualizadoEm campo="contatosInternos"/>--}%
    <g:campoEdicaoMemo titulo="Contatos internos (telefones, emails, etc)" name="contatosInternos" quebraLinha="true"
                       helpTooltip="help.servico.contatos.internos" placeholder=
"ex: serviço social: (11) 2222-7777
Maria, assistente social, maria@meusite.com"/>
    %{--<tmpl:atualizadoEm campo="contatosInternos"/>--}%
</g:layoutSessao>

%{--
<g:layoutSessao titulo="Formulários e outros anexos" icone="usecases/anexar-w.png">
    <g:render template="formAnexos" model="${[localDtoServico: localDtoServico]}"/>
</g:layoutSessao>
--}%

<g:layoutSessao titulo="Encaminhamento" icone="usecases/encaminhamento-w.png">
    <fieldset id="fieldsetEncaminhamento" class="embedded" ${localDtoServico.podeEncaminhar ? "" : "disabled"}>
        <legend>
            <g:checkBox name="podeEncaminhar" value="${localDtoServico.podeEncaminhar}" onclick="document.getElementById('fieldsetEncaminhamento').disabled = ! this.checked; return true"/>
            <g:message code="servico.podeEncaminhar" default="Permitir encaminhamento" />
        </legend>
        <g:campoEdicaoMemo titulo="Encaminhamento Padrão" name="encaminhamentoPadrao" quebraLinha="true" placeholder=
"ex: A família de %nome% é atendida em nosso serviço e o jovem demanda inserção no mercado de trabalho. Favor inseri-lo no programa de Jovem Aprendiz desta entidade.

obs: Utilize %nome% para ser automaticamente preenchido com o nome do usuário no momento da emissão do encaminhamento. Também podem ser usados %endereco%, %telefone% e %nis% para serem automaticamente preenchidos a partir do cadastro do usuário."/>
    </fieldset>
</g:layoutSessao>

<g:campoEdicao titulo="Território atendido" quebraLinha="true" obrigatorio="true" name="abrangenciaTerritorial">
    <g:render template="/abrangenciaTerritorial"/>
</g:campoEdicao>

<g:campoEdicao titulo="Data da última verificação das orientações do serviço" quebraLinha="true" name="ultimaVerificacao"
    helpTooltip="Informe a data em que as informações presentes neste serviço foram confirmadas pela última vez">
    <g:textField class="dateMask" id="txtUltimaVerificacao" name="ultimaVerificacao" size="10" maxlength="10"
                 value="${localDtoServico?.ultimaVerificacao?.format("dd/MM/yyyy")}"/>
    %{--<input type="button" class="speed-button-sugestao" onclick="clickSugestao(this);" title="preencher com informação padrão"/>--}%
    <input type="button" class="speed-button-sugestao" onclick="atualizarUltimaVerificacao('${g.formatDate(date: new Date())}');"/> Hoje
</g:campoEdicao>
