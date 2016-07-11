package org.apoiasuas.redeSocioAssistencial

import grails.converters.JSON
import grails.transaction.Transactional
import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial
import org.hibernate.Hibernate

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

    /**
     * Monta estrutura JSON para o componente visual jctree exibindo todas as abrangencias territoriais disponiveis
     * (exceto, se for o caso, a passada no parametro "ignora") e mantem o componente aberto para alteracao
     */
    @Transactional(readOnly = true)
    public String JSONAbrangenciasTerritoriaisEdicao(AbrangenciaTerritorial ignora, List<AbrangenciaTerritorial> selecionados) {
        List<AbrangenciaTerritorial> result = populaFilhos(null, ignora, selecionados);
        return result as JSON
    }

    /**
     * Metodo recursivo para preencher todos os sub-nos da arvore a partir do passado em "mae" (ou a arvore inteira, se nao for passado nenhum no em "mae")
     */
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

    /**
     * Monta estrutura JSON para o componente visual jctree exibindo apenas a abrangenciaTerritorial escolhida e suas
     * respectivas contenedoras(maes) hierarquicas
     */
    @Transactional(readOnly = true)
    public String JSONAbrangenciasTerritoriaisExibicao(AbrangenciaTerritorial abrangenciaTerritorial) {
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

    /**
     * Retorna uma lista com a hierarquia de abrangencias territoriais que contem a abrangencia solicitada, INCLUIDO A MESMA
     */
    @Transactional(readOnly = true)
    public ArrayList<AbrangenciaTerritorial> getAbrangenciasTerritoriaisMaes(AbrangenciaTerritorial abrangenciaTerritorial) {
        ArrayList<AbrangenciaTerritorial> result = []
        while (abrangenciaTerritorial != null) {
            result << abrangenciaTerritorial;
            abrangenciaTerritorial.attach()
//            Hibernate.initialize(abrangenciaTerritorial)
            abrangenciaTerritorial = abrangenciaTerritorial.mae
        }
        return result
    }


}
