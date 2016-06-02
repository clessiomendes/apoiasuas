package org.apoiasuas.redeSocioAssistencial

import grails.converters.JSON
import grails.transaction.Transactional
import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial

class AbrangenciaTerritorialService {

    public static final String ID_TERRITORIOS_ATUACAO = "id_territoriosAtuacao_"

    @Transactional
    boolean gravaAbrangenciaTerritorial(AbrangenciaTerritorial abrangenciaTerritorial) {
        return abrangenciaTerritorial.save()
    }

    @Transactional
    void apagaAbrangenciaTerritorial(AbrangenciaTerritorial abrangenciaTerritorial) {
        abrangenciaTerritorial.delete()
    }

    private List<AbrangenciaTerritorial> populaFilhos(AbrangenciaTerritorial mae, AbrangenciaTerritorial ignora, List<AbrangenciaTerritorial> selecionados) {
        List<AbrangenciaTerritorial> result = []
        List<AbrangenciaTerritorial> tempList = AbrangenciaTerritorial.findAllByMae(mae)
        for (int i = 0; i < tempList.size(); i++) {
            AbrangenciaTerritorial it = tempList[i]
            if (ignora && it.id == ignora.id) {
                //ignora (dã)
            } else {
                if (selecionado(selecionados, it)) {
                    it.componenteVisual.selecionado = true
                }
                populaFilhos(it, ignora, selecionados);
                result += it;
                if (mae)
                    mae.addFilho(it)
            }
        }
        return result
    }

    private boolean selecionado(List<AbrangenciaTerritorial> selecionados, AbrangenciaTerritorial abrangenciaTerritorial) {
        boolean result = false
        selecionados.each {
            if (it) {
                if (it.id == abrangenciaTerritorial.id)
                    result = true
            }
        }
        return result
    }

/*
    private List<AbrangenciaTerritorial> ignoradosEselecionados(List<AbrangenciaTerritorial> filhos, AbrangenciaTerritorial ignora, List<AbrangenciaTerritorial> selecionados) {
        List<AbrangenciaTerritorial> result = []
        filhos.each {
            it.open = true
            if (ignora && it.id == ignora.id) {
                //ignora (dã)
            } else {
                if (it.id in selecionados.collect {it.id} )
                    it.selected = true
                it.filhos = ignoradosEselecionados(it.filhos, ignora, selecionados)
                result += it
            }
        }
        return result
    }
*/

    @Transactional(readOnly = true)
    public String JSONareasAtuacaoDisponiveis(AbrangenciaTerritorial ignora, List<AbrangenciaTerritorial> selecionados) {
        List<AbrangenciaTerritorial> result = populaFilhos(null, ignora, selecionados);
//        Ignorados e selecionados nao ta funcionando
//        List<AbrangenciaTerritorial> resultFiltrado = ignoradosEselecionados(result, ignora, selecionados)
        return result as JSON
/*
        AbrangenciaTerritorial hv = new AbrangenciaTerritorial()
        hv.id = 1
        hv.nome = "Havai-Ventosa"

        AbrangenciaTerritorial mp = new AbrangenciaTerritorial()
        mp.id = 3
        mp.nome = "Morro das Pedras"

        AbrangenciaTerritorial va = new AbrangenciaTerritorial()
        va.id = 2
        va.nome = "Vista Alegre"
        va.filhos << mp

        return [hv, va] as JSON;
*/
    }

    public void registraJSON() {
        JSON.registerObjectMarshaller(AbrangenciaTerritorial) { AbrangenciaTerritorial it ->
            def output = [:]
            output['id'] = ID_TERRITORIOS_ATUACAO + it.id.toString()
            output['text'] = it.nome
            def state = [:]
            if (it.componenteVisual.aberto != null)
                state['open'] = it.componenteVisual.aberto
            if (it.componenteVisual.selecionado != null)
                state['selected'] = it.componenteVisual.selecionado
            if (it.componenteVisual.desabilitado != null)
                state['disabled'] = it.componenteVisual.desabilitado
            output['state'] = state;
            output['text'] = it.nome
            output['children'] = it.filhos
            return output;
        }
    }

    @Transactional(readOnly = true)
    AbrangenciaTerritorial getAbrangenciaTerritorial(long id) {
        return AbrangenciaTerritorial.get(id)
    }

    public String JSONhierarquiaTerritorial(AbrangenciaTerritorial abrangenciaTerritorial) {
        if (! abrangenciaTerritorial)
            return [] as JSON;

        AbrangenciaTerritorial mae = abrangenciaTerritorial.mae
        AbrangenciaTerritorial filho = abrangenciaTerritorial
        filho.componenteVisual.selecionado = true
        while (mae != null) {
            filho.componenteVisual.desabilitado = true
            mae.componenteVisual.aberto = true
            mae.filhos = [filho]
            filho = mae
            mae = mae.mae
        }
        filho.componenteVisual.desabilitado = true
        return filho as JSON
    }
}
