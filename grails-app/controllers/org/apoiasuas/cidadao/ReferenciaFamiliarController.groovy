package org.apoiasuas.cidadao

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.seguranca.DefinicaoPapeis

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class ReferenciaFamiliarController extends AncestralController {

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def edit(Familia familiaInstance) {
        render view: 'edit', model: getModel(familiaInstance)
    }

    private Map getModel(Familia familiaInstance) {
        List<Cidadao> demaisMembros = [];
        Cidadao novaReferencia = null;
        if (params.novaReferencia) {
            familiaInstance.getMembrosOrdemPadrao(true).each {
                if (it.id == params.long('novaReferencia'))
                    novaReferencia = it
                else
                    demaisMembros.add(it);
            }
//            novaReferencia = Cidadao.get(params.novaReferencia);
//            demaisMembros.addAll(familiaInstance.membros.findAll{ it.id != novaReferencia.id });
        }
        return [familiaInstance: familiaInstance, demaisMembros: demaisMembros, novaReferencia: novaReferencia]
    }

    def cancel(Familia familiaInstance) {
        forward(controller: 'familia', action: 'show', id: familiaInstance.id);
    }

    private boolean valida(Familia familiaInstance, String idNovaReferencia, List<String> parentescoMembroList) {
        if (! params.novaReferencia)
            familiaInstance.errors.reject("erro.referencia.familiar")
        boolean parentescoValidadao = true;
        parentescoMembroList.each {
            if (it.trim().empty)
                parentescoValidadao =false
        }
        if (! parentescoValidadao)
            familiaInstance.errors.reject("erro.parentescos.obrigatorios");
        return ! familiaInstance.errors.hasErrors();
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def save(Familia familiaInstance) {
        List<Cidadao> demaisMembros = [];

        List<String> idMembroList = params.list("idMembro");
        List<String> parentescoMembroList = params.list("parentescoMembro");

        if (! valida(familiaInstance, params.novaReferencia, parentescoMembroList))
            return render(view: 'edit', model: getModel(familiaInstance))

        familiaInstance.membros.each { Cidadao membro ->
            if (membro.id == params.getLong("novaReferencia")) {
                membro.referencia = true;
                membro.parentescoReferencia = CidadaoService.PARENTESCO_REFERENCIA;
            } else {
                idMembroList.eachWithIndex { String id, int i ->
                    if (id.toLong() == membro.id) {
                        membro.referencia = false;
                        membro.parentescoReferencia = parentescoMembroList[i]
                    }
                }
            }
        }
        familiaService.gravaFamiliaEMembros(familiaInstance);
        flash.message = "Configuração familiar alterada com sucesso";
        forward(controller: 'familia', action: 'show', id: familiaInstance.id);
    }

}
