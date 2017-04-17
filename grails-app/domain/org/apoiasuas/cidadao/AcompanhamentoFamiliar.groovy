package org.apoiasuas.cidadao

class AcompanhamentoFamiliar {

//    Familia familia
    Date dataInicio, dataFim
    String analiseTecnica, resultados

    static mapping = {
//        familia column:'familia', index:'Acompanhamento_Familia_Idx'
        id generator: 'native', params: [sequence: 'sq_acompanhamento_familiar']
        analiseTecnica length: 1000000
        resultados length: 1000000
    }

    static constraints = {
//        familia(nullable: false);
        dataFim validator: { value, acompanhamento ->
            //valida que a data final seja sempre maior que a inicial (caso as duas datas estejam preenchidas)
            if (value && acompanhamento.dataInicio && (value < acompanhamento.dataInicio))
                return false
            return true
        }
    }
}
