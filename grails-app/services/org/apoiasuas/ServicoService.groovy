package org.apoiasuas

import grails.transaction.Transactional
import org.apache.commons.lang.StringEscapeUtils
import org.apoiasuas.Servico
import org.apoiasuas.util.HqlPagedResultList
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import java.util.regex.Pattern

class ServicoService {

    static final int TAMANHO_DESCRICAO_CORTADA = 150

    @Transactional
    public boolean grava(Servico servico) {
        servico.save()
    }

    @Transactional
    public boolean apaga(Servico servico) {
        servico.delete()
    }

    @Transactional(readOnly = true)
    public Servico getServico(Long idServico) {
        return Servico.get(idServico)
    }

    @Transactional(readOnly = true)
    grails.gorm.PagedResultList procurarServico(String palavraChave, GrailsParameterMap params) {
        String[] palavrasChaves = null
        //remove espacos indesejados, converte para lowercase e divide as palavras
        if (palavraChave) {
            palavraChave = palavraChave.toLowerCase().trim()
            while (palavraChave.indexOf("  ") > -1)
                palavraChave = palavraChave.replaceAll("  "," ")
            palavrasChaves = palavraChave == "" ? null : palavraChave.split(" ")
        }

        String hqlFrom = 'from Servico a '
        String hqlOrder = ' order by a.apelido'
        List servicos
        int count

        if (! palavrasChaves) {
            //Pesquisa sem palavra chave
            count = Servico.executeQuery("select count(*) " + hqlFrom, [:])[0]
            servicos = Servico.executeQuery(hqlFrom + hqlOrder, [:], params)
        } else {
            //Pesquisa com palavra chave
            def filtros = [:]

            String hqlFiltro = "where 1=0"
            palavrasChaves.eachWithIndex { cadaPalavra, i ->
                String label = 'cadaPalavra'+i
                hqlFiltro += " or lower(remove_acento(a.apelido)) like remove_acento(:"+label+")"
                filtros.put(label, '%'+cadaPalavra?.toLowerCase()+'%')
            }
            //Usar um LinkedHashSet garante que os resultados nao se repitam
            LinkedHashSet<Servico> servicosTemp = new LinkedHashSet(Servico.executeQuery(hqlFrom + hqlFiltro + hqlOrder, filtros, params))

            //se existem menos de 20 servicos com a palavra chave no apelido, procura servicos com a palavra chave na descricao
            if (servicosTemp.size() < params.max) {
                hqlFiltro = hqlFiltro.replaceAll("apelido", "descricao")
                Iterator<Servico> servicosDescricao = Servico.executeQuery(hqlFrom + hqlFiltro + hqlOrder, filtros, params).iterator()
                while (servicosTemp.size() < params.max && servicosDescricao.hasNext())
                    servicosTemp << servicosDescricao.next()
            }

            servicos = new ArrayList(servicosTemp)
            count = servicos.size()
        }

        //Formata apelido e descricao para serem exibidos na tela
        Iterator<Servico> iterator = servicos.iterator()
        while (iterator.hasNext()) {
            Servico servico = iterator.next()
            servico.discard() //NAO gravar alteracoes
            servico.descricaoCortada = cortaDescricao(servico.descricao, palavrasChaves)
            //Escapa caracteres html por questoes de seguranca
            servico.apelido = StringEscapeUtils.escapeHtml(servico.apelido)
            servico.descricaoCortada = StringEscapeUtils.escapeHtml(servico.descricaoCortada)
            palavrasChaves?.each { cadaPalavra ->
                //Poe em negrito ocorrencias de cada palavra chave
                servico.apelido = servico.apelido?.replaceAll("(?i)" + Pattern.quote(cadaPalavra), '<b>$0</b>')
                servico.descricaoCortada = servico.descricaoCortada?.replaceAll("(?i)" + Pattern.quote(cadaPalavra), '<b>$0</b>')
            }
        }

        return new HqlPagedResultList(servicos, count)
    }

    /**
     * Corta a descricao para um maximo de 150 caracteres. Usa a primeira palavra chave presente na descricao como
     * criterio do corte, ou seja, o corte deve conter pelo menos uma das palavras.
     */
    private String cortaDescricao(String descricao, String[] palavrasChaves) {
        if (! descricao)
            return descricao
        String descricaoCortada
        int tamanho = descricao.size()
        int primeiraOcorrencia = -1

        //procura pela primeira ocorrencia de qualquer das palavras chaves
        palavrasChaves?.each { cadaPalavra ->
            int pos = cadaPalavra ? descricao.toLowerCase().indexOf(cadaPalavra.toLowerCase()) : -1
            if (pos >= 0) {
                if (primeiraOcorrencia == -1)
                    primeiraOcorrencia = pos
                else
                    primeiraOcorrencia = Math.min(primeiraOcorrencia, pos)
            }
        }

        if (primeiraOcorrencia == -1) {
            //nenhuma palavra chave na descricao
            descricaoCortada = descricao.substring(0, Math.min(tamanho, TAMANHO_DESCRICAO_CORTADA))
        } else {
            int inicio = Math.max(0, Math.round(primeiraOcorrencia - TAMANHO_DESCRICAO_CORTADA / 2) )
            int fim = Math.min(descricao.size(), Math.round(primeiraOcorrencia + TAMANHO_DESCRICAO_CORTADA / 2 ))
            if (inicio == 0)
                fim = Math.min(descricao.size(), TAMANHO_DESCRICAO_CORTADA)
            if (fim == descricao.size())
                inicio = Math.max(0, descricao.size() - TAMANHO_DESCRICAO_CORTADA)
            descricaoCortada = descricao.substring(inicio, fim)
        }

        //acrescenta "..." sinalizando que o texto foi cortado nesta posicao
        String result = descricaoCortada
        if (! descricao.startsWith(descricaoCortada))
            result = "... " + result
        if (! descricao.endsWith(descricaoCortada))
            result = result + " ..."
        return result
    }

}
