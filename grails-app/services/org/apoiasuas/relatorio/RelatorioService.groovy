package org.apoiasuas.relatorio

import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.commons.lang.StringEscapeUtils
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.Endereco
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.programa.Programa
import org.apoiasuas.util.HqlPagedResultList
import org.apoiasuas.util.StringUtils
import org.joda.time.LocalDate

import java.util.regex.Pattern

@Transactional(readOnly = true)
class RelatorioService {

    static final List CABECALHOS_CIDADAO = ["nome completo", "parentesco referência", "data de nascimento", "NIS"]
    static final List CABECALHOS_FAMILIA = ["cad CRAS", "telefones", "tecnico de referência", "endereço", "CEP"] //TODO: adicionar siglas dos programas (maior cuidado, pbf, etc)

    def groovySql

    def geraListagem(OutputStream outputStream, LocalDate dataNascimentoInicial, LocalDate dataNascimentoFinal, String membros, ArrayList<Programa> programasSelecionados) {

        String sqlFrom
        String sqlOrder = ""
        if (membros) {
            sqlFrom = 'select c.nome_completo as "nome completo", c.parentesco_referencia a "parentesco referencia" from cidadao c where 1=1 '
//            hqlOrder = 'order by c.familia.codigoLegado, c.nomeCompleto'
        } else {
            sqlFrom = 'select c.nome_completo as "nome completo", c.parentesco_referencia a "parentesco referencia" from cidadao c where 1=1 '
            sqlFrom = 'select f.codigo_legado as "cad CRAS", u.username as "técnico de referência" from familia f left join usuario_sistema u on f.tecnico_referencia_id = u.id where 1=1 '
//            hqlOrder = 'order by f.codigoLegado'
        }

        def filtros = []

        if (dataNascimentoInicial && membros) {
            sqlFrom += ' and c.data_nascimento >= :dataNascimentoInicial'
            filtros << dataNascimentoInicial
        }

        try {
            List<GroovyRowResult> resultado
            List cabecalhos
            if (membros) {
                resultado = Cidadao.executeQuery(sqlFrom + ' ' + sqlOrder, filtros)
                cabecalhos = CABECALHOS_FAMILIA + CABECALHOS_CIDADAO
            } else {
                resultado = groovySql.rows(sqlFrom + ' ' + sqlOrder, filtros)
//            resultado = Familia.executeQuery(sqlFrom + ' ' + sqlOrder, filtros)
//            cabecalhos = CABECALHOS_FAMILIA
            }

            if (resultado) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "Windows-1252"));

                //Imprime cabecalhos
                writer.append(montaAppend(resultado[0].collect { it.key }))
                writer.newLine()

                resultado.each { row ->
                    if (membros) {

                    } else {
//                    Familia familia = row
//                    writer.append(montaAppend([familia.codigoLegado, null, familia.tecnicoReferencia?.username, familia.endereco.toString(), familia.endereco.CEP]))
                        writer.append(montaAppend(row.collect { it.value } ))
                    }
                    writer.newLine()
                }
                writer.flush()
            }
        } finally {
            groovySql.close()
        }
    }

    private CharSequence montaAppend(List lista) {
        String result = ""
        lista.eachWithIndex { elemento, i ->
            if (elemento)
                result += elemento
            if (i < lista.size())
                result += "\t"
        }
        return result;
    }
}
