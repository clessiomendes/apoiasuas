%{--Javascript para a selecao de abrangencia territorial--}%
<g:javascript>

    $(document).ready(function() {
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
