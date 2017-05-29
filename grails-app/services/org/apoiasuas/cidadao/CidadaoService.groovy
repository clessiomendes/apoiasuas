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
    public grails.gorm.PagedResultList procurarCidadao(GrailsParameterMap params, FiltroCidadaoCommand filtrosOperador) {
        String filtroNome
        String filtroCad
        if (filtrosOperador?.nomeOuCad) {
            if (StringUtils.PATTERN_TEM_LETRAS.matcher(filtrosOperador.nomeOuCad))
                filtroNome = filtrosOperador.nomeOuCad
            else
                filtroCad = filtrosOperador.nomeOuCad
        }
        def filtrosHql = [:]

        String hqlCount = "select count(*) from Cidadao a ";
        String hqlList = "select distinct a, a.familia from Cidadao a inner join fetch a.familia left join fetch a.familia.tecnicoReferencia ";
//        String hqlCount = "select count(*) from Cidadao a ";
//        String hqlList = "from Cidadao a inner join fetch a.familia left join fetch a.familia.tecnicoReferencia ";

        String hqlWhere = ' where 1=1 '
        if (! filtroNome) {
            hqlWhere += "and a.referencia = true"
        }

        hqlWhere += ' and a.servicoSistemaSeguranca = :servicoSistema'
        filtrosHql << [servicoSistema: segurancaService.getServicoLogado()]

        if (filtrosOperador.programa) {
            String join = " join a.familia.programas programaFamilia ";
            hqlList += join;
            hqlCount += join;
            hqlWhere += ' and programaFamilia.programa.id = :programa ';
            filtrosHql.put('programa', new Long(filtrosOperador.programa))
        }

        if (filtrosOperador.idade) {
            hqlWhere += ' and '+AmbienteExecucao.SqlProprietaria.idade('a.dataNascimento')+' = :idade ';
            filtrosHql.put('idade', new Integer(filtrosOperador.idade))
        }

        if (filtrosOperador.nis) {
            hqlWhere += ' and a.nis = :nis';
            filtrosHql.put('nis', filtrosOperador.nis)
        }

        if (filtrosOperador.outroMembro) {
            String join = " join a.familia.membros membrosFamiliares ";
            hqlList += join;
            hqlCount += join;
            String[] nomesOutroMembro = filtrosOperador.outroMembro?.split(" ");
            nomesOutroMembro?.eachWithIndex { nome, i ->
                String label = 'outroNome' + i;
                hqlWhere += " and lower(remove_acento(membrosFamiliares.nomeCompleto)) like remove_acento(:" + label + ")"
                filtrosHql.put(label, '%' + nome?.toLowerCase() + '%')
            }
        }

        if (filtroCad) {
            if (segurancaService.acessoRecursoServico(RecursosServico.IDENTIFICACAO_PELO_CODIGO_LEGADO)) {
                hqlWhere += ' and a.familia.codigoLegado = :cad'
                filtrosHql << [cad: filtroCad]
            } else {
                hqlWhere += ' and a.familia.id = :cad'
                filtrosHql << [cad: filtroCad.toLong()]
            }
        }

        String[] nomes = filtroNome?.split(" ");
        nomes?.eachWithIndex { nome, i ->
            String label = 'nome'+i
            hqlWhere += " and lower(remove_acento(a.nomeCompleto)) like remove_acento(:"+label+")"
            filtrosHql.put(label, '%'+nome?.toLowerCase()+'%')
        }

        String[] logradouros = filtrosOperador?.logradouro?.split(" ");
        logradouros?.eachWithIndex { logradouro, i ->
            String label = 'logradouro'+i
            hqlWhere += " and (lower(remove_acento(a.familia.endereco.nomeLogradouro)) like remove_acento(:"+label+")" +
                    " or lower(remove_acento(a.familia.endereco.complemento)) like remove_acento(:"+label+"))"
            filtrosHql.put(label, '%'+logradouro?.toLowerCase()+'%')
        }

        if (filtrosOperador?.numero) {
            hqlWhere += ' and a.familia.endereco.numero = :numero'
            filtrosHql << [numero: filtrosOperador.numero]
        }

        String hqlOrder = ""
        if (filtrosOperador.logradouro)
            hqlOrder = ' order by ' + AmbienteExecucao.SqlProprietaria.StringToNumber('a.familia.endereco.numero')
        else if (filtrosOperador.numero)
            hqlOrder = ' order by a.familia.endereco.nomeLogradouro, ' + AmbienteExecucao.SqlProprietaria.StringToNumber('a.familia.endereco.numero')
        else
            hqlOrder = ' order by a.nomeCompleto';


        int count = Cidadao.executeQuery(hqlCount + hqlWhere, filtrosHql)[0]
        List resultado = Cidadao.executeQuery(hqlList + hqlWhere + hqlOrder, filtrosHql, params)
        List<Cidadao> cidadaos = [];

        //Coloca em negrito os termos de busca utilizados
        Iterator<Cidadao> iterator = resultado.iterator()
        List<Endereco> enderecosFormatados = []
        while (iterator.hasNext()) {
            Cidadao cidadao = iterator.next()[0] //seleciona o cidadao e ignora a familia
            cidadaos << cidadao;
            cidadao.discard() //NAO gravar alteracoes
            cidadao.familia?.discard() //NAO gravar alteracoes
            //Escapa caracteres html por questoes de seguranca
            cidadao.nomeCompleto = cidadao.nomeCompleto ? StringEscapeUtils.escapeHtml(cidadao.nomeCompleto) : null
            nomes?.each {
                cidadao.nomeCompleto = cidadao.nomeCompleto?.replaceAll("(?i)" + Pattern.quote(it), '<b>$0</b>')
            }
            cidadao.familia?.discard();
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
//            telefone.criador = segurancaService.usuarioLogado
//            telefone.ultimoAlterador = segurancaService.usuarioLogado
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
    public boolean nomeDuplicado(Cidadao cidadao) {
        boolean result = false;
        Cidadao.findAllByNomeCompletoIlike(cidadao.nomeCompleto).each {
            if (it.id != cidadao.id)
                result = true;
        };
        return result;
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