package org.apoiasuas.cidadao

import grails.transaction.Transactional
import org.apoiasuas.marcador.Acao
import org.apoiasuas.marcador.AcaoFamilia
import org.apoiasuas.marcador.Vulnerabilidade
import org.apoiasuas.marcador.VulnerabilidadeFamilia
import org.apoiasuas.processo.PedidoCertidaoProcessoDTO
import org.apoiasuas.programa.Programa
import org.apoiasuas.programa.ProgramaFamilia

class FamiliaService {

    public static final int MAX_AUTOCOMPLETE_LOGRADOUROS = 10
    def segurancaService
    def pedidoCertidaoProcessoService
    def messageSource
    def marcadorService

    @Transactional
    public Familia grava(Familia familia, MarcadoresCommand programasCommand, List<String> novosProgramas,
                         MarcadoresCommand acoesCommand, List<String> novasAcoes,
                         MarcadoresCommand vulnerabilidadesCommand, List<String> novasVulnerabilidades) {

        marcadorService.validaNovosMarcadores(novasAcoes, Acao.class);
        marcadorService.validaNovosMarcadores(novasVulnerabilidades, Vulnerabilidade.class);
        //FIXME: validar novos programas (alterar campo nome para descricao no mapeamento do objeto)

        marcadorService.gravaMarcadores(programasCommand, familia.programas, familia, Programa.class, ProgramaFamilia.class, novosProgramas);
        marcadorService.gravaMarcadores(acoesCommand, familia.acoes, familia, Acao.class, AcaoFamilia.class, novasAcoes);
        marcadorService.gravaMarcadores(vulnerabilidadesCommand, familia.vulnerabilidades, familia, Vulnerabilidade.class, VulnerabilidadeFamilia.class, novasVulnerabilidades);

        return familia.save()
    }

    @Transactional
    public boolean apaga(Familia familia) {
        familia.delete()
        return true
    }

    @Transactional(readOnly = true)
    List procurarLogradouros(String logradouro) {
        if (! logradouro)
            return []
        //HQL Busca todos os logradouros, primeiro os mais usados
        String hql = 'select a.endereco.nomeLogradouro from Familia a ' +
                'where lower(remove_acento(a.endereco.nomeLogradouro)) like remove_acento(:logradouro) ' +
                'group by a.endereco.nomeLogradouro ' +
                'order by count(*) desc ';
        String logradouroInicia = logradouro.toLowerCase()+'%'
        String logradouroContem = '%'+logradouro.toLowerCase()+'%'

        //Procura logradouros INICIANDO com o texto digitado
        //Usar um LinkedHashSet garante que os resultados nao se repitam
        Set logradouros = new LinkedHashSet(Familia.executeQuery(hql, [logradouro: logradouroInicia]))

        //se existem menos de 10, procura logradouros CONTENDO o texto digitado
        if (logradouros.size() < MAX_AUTOCOMPLETE_LOGRADOUROS) {
            Iterator<String> logradourosContem = Familia.executeQuery(hql, [logradouro: logradouroContem]).iterator()
            while (logradouros.size() < MAX_AUTOCOMPLETE_LOGRADOUROS && logradourosContem.hasNext())
                logradouros << logradourosContem.next()
        }

        return new ArrayList(logradouros)

    }

    public boolean testaAcessoDominio(Familia familia) {
        //Restringir acesso apenas ao servicoSistema que criou a familia
        if (familia.servicoSistemaSeguranca && segurancaService.getServicoLogado() &&
                familia.servicoSistemaSeguranca.id != segurancaService.getServicoLogado().id)
            return false
        return true;
    }

    public Set<String> getNotificacoes(Long idFamilia, Locale locale) {
        if (! idFamilia)
            return []
        Set<String> result = []
        Familia familia = Familia.get(idFamilia);

        //testa se a familia eh acompanhada por algum tecnico
        if (familia.tecnicoReferencia)
            result << messageSource.getMessage("notificacao.familia.acompanhada", [familia.tecnicoReferencia.username].toArray(), locale);

        //testa idades voltadas ao SCFV
        familia.membros.each { Cidadao cidadao ->
            if (cidadao.idade && cidadao.idade < 7)
                result << messageSource.getMessage("notificacao.familia.SCFV.0a6", null, locale);
            if (cidadao.idade && cidadao.idade >= 60)
                result << messageSource.getMessage("notificacao.familia.SCFV.idosos", null, locale);
        }

        List<PedidoCertidaoProcessoDTO> pedidosCertidaoPendentes = pedidoCertidaoProcessoService.pedidosCertidaoPendentes(familia.id)
        if (pedidosCertidaoPendentes)
            result << messageSource.getMessage("notificacao.familia.pedidosCertidao", null, locale);

        return result
    }

}
