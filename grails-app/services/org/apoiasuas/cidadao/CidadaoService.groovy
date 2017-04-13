package org.apoiasuas.cidadao

import grails.transaction.Transactional
import org.apache.commons.lang.StringEscapeUtils
import org.apoiasuas.redeSocioAssistencial.RecursosServico
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.util.AmbienteExecucao
import org.apoiasuas.util.StringUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.apoiasuas.util.HqlPagedResultList

import java.util.regex.Pattern

@Transactional(readOnly = true)
class CidadaoService {

    public static final String PARENTESCO_REFERENCIA = "REFERENCIA"
    def segurancaService
    static transactional = false

    @Transactional(readOnly = true)
    public grails.gorm.PagedResultList procurarCidadao(GrailsParameterMap params, FiltroCidadaoCommand filtro) {
        String filtroNome
        String filtroCad
        if (filtro?.nomeOuCad) {
            if (StringUtils.PATTERN_TEM_LETRAS.matcher(filtro.nomeOuCad))
                filtroNome = filtro.nomeOuCad
            else
                filtroCad = filtro.nomeOuCad
        }
        def filtros = [:]

        String hqlCount = "select count(*) from Cidadao a ";
        String hqlList = "from Cidadao a inner join fetch a.familia left join fetch a.familia.tecnicoReferencia ";
//        String hqlList = "from Cidadao a  ";

        String hqlWhere = ' where 1=1 '
        if (!filtroNome) {
            hqlWhere += "and a.referencia = true"
        }


        hqlWhere += ' and a.servicoSistemaSeguranca = :servicoSistema'
        filtros << [servicoSistema: segurancaService.getServicoLogado()]

        if (filtroCad) {
            if (segurancaService.acessoRecursoServico(RecursosServico.IDENTIFICACAO_PELO_CODIGO_LEGADO)) {
                hqlWhere += ' and a.familia.codigoLegado = :cad'
                filtros << [cad: filtroCad]
            } else {
                hqlWhere += ' and a.familia.id = :cad'
                filtros << [cad: filtroCad.toLong()]
            }
        }

        String[] nomes = filtroNome?.split(" ");
        nomes?.eachWithIndex { nome, i ->
            String label = 'nome'+i
            hqlWhere += " and lower(remove_acento(a.nomeCompleto)) like remove_acento(:"+label+")"
            filtros.put(label, '%'+nome?.toLowerCase()+'%')
        }

        String[] logradouros = filtro?.logradouro?.split(" ");
        logradouros?.eachWithIndex { logradouro, i ->
            String label = 'logradouro'+i
            hqlWhere += " and (lower(remove_acento(a.familia.endereco.nomeLogradouro)) like remove_acento(:"+label+")" +
                    " or lower(remove_acento(a.familia.endereco.complemento)) like remove_acento(:"+label+"))"
            filtros.put(label, '%'+logradouro?.toLowerCase()+'%')
        }

        if (filtro?.numero) {
            hqlWhere += ' and a.familia.endereco.numero = :numero'
            filtros << [numero: filtro.numero]
        }

        String hqlOrder = ""
        if (filtroNome)
            hqlOrder = ' order by a.nomeCompleto'
        else if (filtro.logradouro)
            hqlOrder = ' order by ' + AmbienteExecucao.SqlProprietaria.StringToNumber('a.familia.endereco.numero')
        else if (filtro.numero)
            hqlOrder = ' order  by a.familia.endereco.nomeLogradouro, ' + AmbienteExecucao.SqlProprietaria.StringToNumber('a.familia.endereco.numero')

        int count = Cidadao.executeQuery(hqlCount + hqlWhere, filtros)[0]
        List cidadaos = Cidadao.executeQuery(hqlList + hqlWhere + hqlOrder, filtros, params)

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
            log.debug("Endereco da familia "+cidadao.familia.id)
            //Para que o mesmo endereco nao seja reformatado varias vezes, precisamos verificar se ele ja foi processado na lista
            if (cidadao.familia.endereco && ! enderecosFormatados.contains(cidadao.familia.endereco) ) {
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
    public Familia obtemFamiliaPeloCad(String cad, boolean carregaMembros = false) {
        //Se for necessário carregar os membros, passa um parametro determinando o modo fetch como join (familia e cidadaos em uma unica sql)
        Map fetchMap = carregaMembros ? [fetch: [membros: 'join']] : [:];
        ServicoSistema servicoLogado = segurancaService.getServicoLogado();
        Familia result = (servicoLogado.acessoSeguranca.identificacaoPeloCodigoLegado) ?
                Familia.findByCodigoLegadoAndServicoSistemaSeguranca(cad, servicoLogado, fetchMap)
                : Familia.findById(cad, fetchMap);
        return result
    }

    @Transactional(readOnly = true)
    public Cidadao obtemCidadao(Long id, boolean carregaFamilia = false, boolean carregaDemaisMembros = false) {
        Map fetchMap = [:];
        if (carregaFamilia || carregaDemaisMembros)
            fetchMap << [familia: 'join'];
        if (carregaDemaisMembros)
            fetchMap << ['familia.membros': 'join'];
        if (fetchMap)
            fetchMap = [fetch: fetchMap];
        return Cidadao.findById(id, fetchMap);
    }

    @Transactional(readOnly = true)
    public Set<Telefone> obtemTelefonesViaCidadao(Long idCidadao) {
        return obtemCidadao(idCidadao, true)?.familia?.telefones
    }

    @Transactional(readOnly = true)
    public boolean testaAcessoDominio(Cidadao cidadao) {
        log.debug("Testando acesso ao cidadao "+cidadao.nomeCompleto)
        //Restringir acesso apenas ao servicoSistema que criou a familia
        if (cidadao.servicoSistemaSeguranca && segurancaService.getServicoLogado() &&
                cidadao.servicoSistemaSeguranca.id != segurancaService.getServicoLogado().id)
            return false
        return true;
    }

    @Transactional
    public Cidadao grava(Cidadao cidadao) {
        return cidadao.save()
    }

    @Transactional(readOnly = true)
    public boolean podeExcluir(Cidadao cidadao) {
        //cidadao ja excluido
        if (! cidadao.habilitado)
            return false;
        Familia familia = cidadao.familia
        //se for o ultimo cidadão habilitado, não permite excluir
        if (familia.getMembrosOrdemPadrao().size() == 1)
            return false;
        //se for uma referencia familiar, so pode excluir se houver outra referencia
        if (cidadao.referencia)
            return (familia.membros.count { (it.habilitado == true) && (it.referencia == true) } > 1 )
        else
            return true;
    }

}