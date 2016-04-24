package org.apoiasuas.importacao

import org.apoiasuas.util.SafeMap

/**
 * Especialização para importação das planilhas de banco de dados de usuários dos CRAS da Prefeitura de Belo Horizonte
 */
class ImportarFamiliasBHService extends ImportarFamiliasService {

    @Override
    /**
     * Necessário comportamento específico para importação de data de nascimento. pode vir de múltiplos campos,
     * "Data Nascimento" ou a junção de "Dia", "Mês" e "Ano".
     */
    protected SafeMap converteListaParaMapa(List<Map> lista, Map camposPreenchidosInvertido, List camposBDDisponiveis) {
        log.debug("executando implementação especializada ImportarFamiliasBHService")
        SafeMap result = new SafeMap(true, camposBDDisponiveis);
        Integer dia = null
        Integer mes = null
        Integer ano = null

        //Converte uma lista de mapas em um mapa
        lista.each {
            it.each {
                if (camposPreenchidosInvertido.get(it.key))
                    result.put("coluna" + camposPreenchidosInvertido.get(it.key), it.value)
                if (it.key == "Dia")
                    dia = it.value ? Integer.valueOf(it.value) : null;
                if (it.key == "Mês")
                    mes = it.value ? Integer.valueOf(it.value) : null;
                if (it.key == "Ano")
                    ano = it.value ? Integer.valueOf(it.value) : null;
            }
        }
        // Ao final, se os campos dia, mes e ano (da data de nascimento) estiverem todos preenchidos, sobrescrever a coluna
        // colunaDataNascimento com a data correspondente
        if (dia != null && mes != null & ano != null) {
            if (ano < 100)
                ano = ano + 1900;
            //antes de armazenar no mapa, converter a data para o formato esperado como vindo do excel
            result.put("colunaDataNascimento", org.apache.poi.ss.usermodel.DateUtil.getExcelDate(new GregorianCalendar(ano, mes-1, dia).time));
        }

        return result
    }

}
