%{--Javascript para a selecao de abrangencia territorial--}%
<g:javascript>

    $(document).ready(function() {
        $('#div_jstree_abrangenciaTerritorial').jstree({
            //'plugins' : ['checkbox'],
            'core' : {
                'data' : ${raw(JSONAbrangenciaTerritorial)},
                "themes": {
                    "name": "default",
                    "dots": true,
                    "icons": false
                },
            },
            "rules":{
                'multiple' : false
            },
            "checkbox" : {
                "keep_selected_style" : false,
                "whole_node" : false,
                "three_state": false
                //"tie_selection": false
            },
            "plugins" : [ "checkbox" ],
            "ui" : {
                "select_limit" : 1  //only allow one node to be selected at a time
            }//ui
        });//jstree

        //Registra a seleção inicial do componente no campo hidden do formulário
        var selecionados = $('#div_jstree_abrangenciaTerritorial').jstree(true).get_selected();
        if (selecionados.length == 1)
            document.getElementById("${org.apoiasuas.AncestralController.JSTREE_HIDDEN_ABRANGENCIA_TERRITORIAL}").value = selecionados[0];
    });

    $('#div_jstree_abrangenciaTerritorial').on("changed.jstree", function (e, data) {
        var elementosSelecionados = 0;
        $.each(data.selected, function() {
            elementosSelecionados++;
        });

        //mantem selecionado somente o item clicado (o plugin checkbox permite sempre multiplas selecoes)
        if (elementosSelecionados == 0) {
            console.log("Selecionado (nenhum)");
            document.getElementById("${org.apoiasuas.AncestralController.JSTREE_HIDDEN_ABRANGENCIA_TERRITORIAL}").value = null;
            return;
        }

        if (! data.node)
            return;

        if (elementosSelecionados > 1 && data.node) {
            $('#div_jstree_abrangenciaTerritorial').jstree(true).deselect_all(true);
            $('#div_jstree_abrangenciaTerritorial').jstree(true).select_node(data.node.id, true, true);
        }
        console.log("Selecionado "+data.node.id);
        document.getElementById("${org.apoiasuas.AncestralController.JSTREE_HIDDEN_ABRANGENCIA_TERRITORIAL}").value = data.node.id
    });

</g:javascript>

<g:hiddenField name="${org.apoiasuas.AncestralController.JSTREE_HIDDEN_ABRANGENCIA_TERRITORIAL}"/>

<div id="div_jstree_abrangenciaTerritorial"></div>
