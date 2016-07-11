%{--Javascript para a selecao de abrangencia territorial--}%
<g:javascript>

    //Carrega o css do treeview
    $('head').append('<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css" />');

    //Carrega o javascript do treeview
    $.ajax({
        url: "https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js",
        dataType: "script",
        cache: true,
        success: function() {
            //Uma vez carregado o arquivo javascript, renderiza o componente sobre o div
            $('#div_jstree_abrangenciaTerritorial').jstree({
                //'plugins' : ['checkbox'],
                'core' : {
                    'data' : ${raw(JSONAbrangenciaTerritorial)},
                },
                "rules":{
                    'multiple' : false
                },
                "ui" : {
                    "select_limit" : 1  //only allow one node to be selected at a time
                }//ui
            });//jstree
        }
    });

    $('#div_jstree_abrangenciaTerritorial').on("changed.jstree", function (e, data) {
        var selectedElmsIds = [];
        var selectedElms = data.selected;
        $.each(selectedElms, function() {
            selectedElmsIds.push(this);
        });
        console.log("Ids de territorio de abrangencia selecionados: "+selectedElmsIds.join(","));
        document.getElementById("${org.apoiasuas.AncestralController.JSTREE_HIDDEN_ABRANGENCIA_TERRITORIAL}").value = selectedElmsIds.join(",");
    });

</g:javascript>

<g:hiddenField name="${org.apoiasuas.AncestralController.JSTREE_HIDDEN_ABRANGENCIA_TERRITORIAL}"/>

<div id="div_jstree_abrangenciaTerritorial"></div>
