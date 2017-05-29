/**
 * Created by clessio on 19/04/2017.
 */

function inicializaTabs($tabs) {
    //primeiro implementa o evento de criacao do componente
    $tabs.on("tabscreate", onTabsCreate);
    //cria o componente
    $tabs.tabs();
}

/**
 * Durante a criação, verificamos se nos formulários contidos dentro de CADA tab existe algum campo marcado com entrada
 * inválida do usuário. Se houver, o tab correspondente (o primeiro) será exibido, evitando-se assim que o usuário fique
 * sem saber onde está o campo com erro).
 * Além disso, muda a cor do título de CADA TAB COM ERRO para vermelho.
 */
function onTabsCreate (event, ui) {
    var $tabs = $(this);

    //Verifica se tem um evento personalizado a ser disparado na criacao
    if (this.customOnCreate)
        this.customOnCreate();

    //Verifica se o componente deve ser aberto automaticamente em algum tab especifico (ordem iniciada em 0)
    if (this.tabMostradoInicialmente && this.tabMostradoInicialmente != '')
        $tabs.tabs("option","active",this.tabMostradoInicialmente);

    var ixTab = 0;
    var tabComErro = -1;
    //itera sobre todos os panels (na ordem inversa, para mostrar sempre o primeiro panel com erro)
    $(".ui-tabs-panel").each(function () {
        if ($(this).find(".error").length > 0) {
            if (tabComErro < 0)
                tabComErro = ixTab;
            $tabs.find(".ui-tabs-anchor").eq(ixTab).css("color","red");
        }
        ixTab++;
    });
    if (tabComErro >=  0)
        $tabs.tabs("option","active",tabComErro);
}