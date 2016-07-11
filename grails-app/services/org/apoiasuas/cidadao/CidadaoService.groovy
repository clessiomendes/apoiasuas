package org.apoiasuas.cidadao

import grails.transaction.Transactional
import org.apache.commons.lang.StringEscapeUtils
import org.apoiasuas.util.AmbienteExecucao
import org.apoiasuas.util.StringUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.apoiasuas.util.HqlPagedResultList
import org.hibernate.Hibernate

import java.util.regex.Pattern

//TODO: separar servico CidadaoService e FamiliaService
class CidadaoService {

    public static final String PARENTESCO_REFERENCIA = "REFERENCIA"
    def segurancaService
    static transactional = false

    @Transactional(readOnly = true)
    grails.gorm.PagedResultList procurarCidadao(GrailsParameterMap params, FiltroCidadaoCommand filtro) {
        String filtroNome
        String filtroCodigoLegado
        if (filtro?.nomeOuCodigoLegado) {
            if (StringUtils.PATTERN_TEM_LETRAS.matcher(filtro.nomeOuCodigoLegado))
                filtroNome = filtro.nomeOuCodigoLegado
            else
                filtroCodigoLegado = filtro.nomeOuCodigoLegado
        }
        def filtros = [:]

        String hql = 'from Cidadao a where 1=1 '
        if (!filtroNome) {
            hql += "and a.parentescoReferencia = :parentesco"
            filtros << [parentesco: PARENTESCO_REFERENCIA]
        }


        hql += ' and a.servicoSistemaSeguranca = :servicoSistema'
        filtros << [servicoSistema: segurancaService.getServicoLogado()]

        if (filtroCodigoLegado) {
            hql += ' and a.familia.codigoLegado = :codigoLegado'
            filtros << [codigoLegado: filtroCodigoLegado]
        }

        String[] nomes = filtroNome?.split(" ");
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
            filtros.put(label, '%'+logradouro?.toLowerCase()+'%')
        }

        if (filtro?.numero) {
            hql += ' and a.familia.endereco.numero = :numero'
            filtros << [numero: filtro.numero]
        }

        String hqlOrder = ""
        if (filtroNome)
            hqlOrder = 'order by a.nomeCompleto'
        else if (filtro.logradouro)
            hqlOrder = 'order by ' + AmbienteExecucao.SqlProprietaria.StringToNumber('a.familia.endereco.numero')
        else if (filtro.numero)
            hqlOrder = 'order by a.familia.endereco.nomeLogradouro, ' + AmbienteExecucao.SqlProprietaria.StringToNumber('a.familia.endereco.numero')

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
    Familia obtemFamilia(String codigoLegado, boolean carregaMembros) {
        Familia result = Familia.findByCodigoLegadoAndServicoSistemaSeguranca(codigoLegado, segurancaService.getServicoLogado())
        log.debug("inicializando colecao de membros")
        if (result && carregaMembros)
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

    public boolean testaAcessoDominio(Cidadao cidadao) {
        //Restringir acesso apenas ao servicoSistema que criou a familia
        if (cidadao.servicoSistemaSeguranca && segurancaService.getServicoLogado() &&
                cidadao.servicoSistemaSeguranca.id != segurancaService.getServicoLogado().id)
            return false
        return true;
    }
}