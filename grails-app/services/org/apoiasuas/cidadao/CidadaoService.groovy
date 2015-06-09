package org.apoiasuas.cidadao

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import org.apache.commons.lang.StringEscapeUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.apoiasuas.util.HqlPagedResultList
import org.hibernate.Hibernate

import java.util.regex.Pattern

//TODO: separar servico CidadaoService e FamiliaService
class CidadaoService {

    def segurancaService
    static transactional = false

    @Transactional(readOnly = true)
    grails.gorm.PagedResultList procurarCidadao(GrailsParameterMap params, FiltroCidadaoCommand filtro) {

        String hql = 'from Cidadao a where 1=1 '

        def filtros = [:]

        if (filtro?.codigoLegado) {
            hql += ' and a.familia.codigoLegado = :codigoLegado'
            filtros << [codigoLegado: filtro.codigoLegado]
        }

        String[] nomes = filtro?.nome?.split(" ");
        nomes?.eachWithIndex { nome, i ->
            String label = 'nome'+i
            hql += " and lower(remove_acento(a.nomeCompleto)) like remove_acento(:"+label+")"
            filtros.put(label, '%'+nome?.toLowerCase()+'%')
        }

        String[] logradouros = filtro?.logradouro?.split(" ");
        logradouros?.eachWithIndex { logradouro, i ->
            String label = 'logradouro'+i
            hql += " and (lower(remove_acento(a.familia.endereco.nomeLogradouro)) like remove_acento(:"+label+")" +
                    " or lower(remove_acento(a.familia.endereco.complemento)) like remove_acento(:"+label+"))"
            //TODO: nomeLogradouro or complemento
            filtros.put(label, '%'+logradouro?.toLowerCase()+'%')
        }

        if (filtro?.numero) {
            hql += ' and a.familia.endereco.numero = :numero'
            filtros << [numero: filtro.numero]
        }

        String hqlOrder = filtro.logradouro ? 'order by a.familia.endereco.numero' : ' order by a.nomeCompleto'

        int count = Cidadao.executeQuery("select count(*) " + hql, filtros)[0]
        List cidadaos = Cidadao.executeQuery(hql + ' ' + hqlOrder, filtros, params)

        //Coloca em negrito os termos de busca utilizados
        Iterator<Cidadao> iterator = cidadaos.iterator()
        List<Endereco> enderecosFormatados = []
        while (iterator.hasNext()) {
            Cidadao cidadao = iterator.next()
            cidadao.discard() //NAO gravar alteracoes
            //Escapa caracteres html por questoes de seguranca
            cidadao.nomeCompleto = cidadao.nomeCompleto ? StringEscapeUtils.escapeHtml(cidadao.nomeCompleto) : null
            nomes?.each {
                cidadao.nomeCompleto = cidadao.nomeCompleto?.replaceAll("(?i)" + Pattern.quote(it), '<b>$0</b>')
            }
            //Para que o mesmo endereco nao seja reformatado varias vezes, precisamos verificar se ele ja foi processado na lista
            if (! enderecosFormatados.contains(cidadao.familia.endereco) ) {
                cidadao.familia.endereco.nomeLogradouro = cidadao.familia.endereco.nomeLogradouro ? StringEscapeUtils.escapeHtml(cidadao.familia.endereco.nomeLogradouro) : null
                cidadao.familia.endereco.complemento = cidadao.familia.endereco.complemento ? StringEscapeUtils.escapeHtml(cidadao.familia.endereco.complemento) : null
                logradouros?.each {
                    cidadao.familia.endereco.nomeLogradouro = cidadao.familia.endereco.nomeLogradouro?.replaceAll("(?i)" + Pattern.quote(it), '<b>$0</b>')
                    cidadao.familia.endereco.complemento = cidadao.familia.endereco.complemento?.replaceAll("(?i)" + Pattern.quote(it), '<b>$0</b>')
                }
                enderecosFormatados << cidadao.familia.endereco
            }
        }


        return new HqlPagedResultList(cidadaos, count)
    }

    @Transactional(readOnly = true)
    grails.gorm.PagedResultList procurarCidadaoCriteria(GrailsParameterMap params, FiltroCidadaoCommand filtro) {

        DetachedCriteria inicial = Cidadao.where { }

        if (filtro?.codigoLegado)
            addAll(inicial, Cidadao.where { familia.codigoLegado == filtro.codigoLegado })

        String[] nomes = filtro?.nome?.split(" ");
        nomes?.each { nome ->
            addAll(inicial, Cidadao.where { nomeCompleto =~ "%" + nome + "%" })
        }

        //a composicao "endereco" nao funciona em Where Queries (http://stackoverflow.com/questions/28144231/unable-to-use-composition-embedded-class-in-where-queries)
        String[] logradouros = filtro?.logradouro?.split(" ");
        logradouros?.each { logradouro ->
            addAll(inicial, new DetachedCriteria(Cidadao).build {
                familia {
                    or {
                        ilike 'endereco.nomeLogradouro', "%" + logradouro + "%"
                        ilike 'endereco.complemento', "%" + logradouro + "%"
                    }
                }
            })
        }

        //a composicao "endereco" nao funciona em Where Queries (http://stackoverflow.com/questions/28144231/unable-to-use-composition-embedded-class-in-where-queries)
        if (filtro?.numero)
            addAll(inicial, new DetachedCriteria(Cidadao).build { familia { eq 'endereco.numero', filtro.numero } } )
//            addAll(inicial, Cidadao.where { familia.endereco.numero == filtro.numero })

//        addAll(inicial, new DetachedCriteria(Cidadao).build { familia { order 'endereco.numero' } } )
//        addAll(inicial, new DetachedCriteria(Cidadao).build { order("nomeCompleto") } )

//        return inicial.sort (
        return inicial.list(
                [max: params.max, offset: params.offset, readonly: "true", ]
//                , { order("nomeCompleto") }
//                , { order("familia.codigoLegado") }
//                , { familia { order("codigoLegado") } }
        )
    }

    private void addAll(DetachedCriteria<Cidadao> inicial, DetachedCriteria<Cidadao> adicional) {
        adicional.criteria.each {
            inicial.add(it)
        }
    }

    @Transactional
    def atualizarFamiliaTelefoneCidadao(Familia familia, String novoTelefone, String novoTelefoneDDD) {
        familia.save()
        if (novoTelefone) {
            Telefone telefone = new Telefone()
            telefone.criador = segurancaService.usuarioLogado
            telefone.ultimoAlterador = segurancaService.usuarioLogado
            telefone.numero = novoTelefone
            telefone.DDD = novoTelefoneDDD
            telefone.familia = familia
            telefone.save()
        }
    }

    @Transactional(readOnly = true)
    Familia obtemFamiliaEMembros(String codigoLegado) {
        Familia result = Familia.findByCodigoLegado(codigoLegado)
        log.debug("inicializando colecao de membros")
        if (result)
            Hibernate.initialize(result.membros)
        return result
    }

    @Transactional(readOnly = true)
    Familia obtemFamilia(Long id) {
        log.debug("buscando familia ${id}")
        return id ? Familia.get(id) : null
    }

    @Transactional(readOnly = true)
    Cidadao obtemCidadao(Long id) {
        log.debug("buscando cidadao ${id}")
        Cidadao result = id ? Cidadao.get(id) : null
        return result
    }

    @Transactional(readOnly = true)
    Set<Telefone> obtemTelefonesViaCidadao(Long idCidadao) {
        return Cidadao.get(idCidadao)?.familia?.telefones
    }
}