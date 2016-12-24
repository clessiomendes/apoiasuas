package org.apoiasuas.cidadao

import grails.transaction.Transactional
import org.apoiasuas.acao.Acao
import org.apoiasuas.acao.AcaoFamilia
import org.apoiasuas.processo.PedidoCertidaoProcessoDTO
import org.apoiasuas.programa.Programa
import org.apoiasuas.programa.ProgramaFamilia

class FamiliaService {

    public static final int MAX_AUTOCOMPLETE_LOGRADOUROS = 10
    def segurancaService
    def pedidoCertidaoProcessoService
    def messageSource

    @Transactional
    public Familia grava(Familia familia, MarcadoresCommand programasCommand, MarcadoresCommand acoesCommand) {

        gravaMarcadores(programasCommand, familia.programas, familia, Programa.class, ProgramaFamilia.class);
        gravaMarcadores(acoesCommand, familia.acoes, familia, Acao.class, AcaoFamilia.class);

        return familia.save()
    }

    @Transactional
    /**
     * Método genérico usado para gravar alterações nas coleções de programas, ações, etc
     */
    private void gravaMarcadores(MarcadoresCommand marcadoresCommand, Set<AssociacaoMarcador> marcadores,
                                 Familia familia, Class<Marcador> classeMarcador, Class<AssociacaoMarcador> classeAssociacaoMarcador) {
        //Converte e filtra apenas os programas selecionados
        List<Marcador> marcadoresDepois = marcadoresCommand.marcadoresDisponiveis.collect { MarcadorCommand cmd ->
            if (cmd.selected)
                classeMarcador.get(cmd.id)
        }.grep() //limpa nulos
        //Atualiza a lista de programas, removendo ou inserindo APENAS QUANDO NECESSÁRIO
        //Primeiro apaga os excluídos
        List removidos = []
        marcadores.each { marcadorFamiliaAntes ->
            if (!marcadoresDepois.contains(marcadorFamiliaAntes.marcador)) {
                marcadorFamiliaAntes.delete()
                removidos << marcadorFamiliaAntes
            }
        }
        marcadores.removeAll(removidos)

        //Depois inclui os novos
        marcadoresDepois.each { marcadorDepois ->
            if (!marcadores.find { it.marcador == marcadorDepois }) {
                AssociacaoMarcador marcadorFamilia = classeAssociacaoMarcador.getConstructor().newInstance();
                marcadorFamilia.marcador = marcadorDepois
                marcadorFamilia.familia = familia
                marcadores.add(marcadorFamilia.save())
            }
        }
    }
/*
    @Transactional
    private void gravaProgramas(MarcadoresCommand programasCommand, Familia familia) {
        //Converte e filtra apenas os programas selecionados
        List<Programa> programasDepois = programasCommand.marcadoresDisponiveis.collect { MarcadorCommand cmd ->
            if (cmd.selected)
                Programa.get(cmd.id)
        }.grep() //limpa nulos
        //Atualiza a lista de programas, removendo ou inserindo APENAS QUANDO NECESSÁRIO
        //Primeiro apaga os excluídos
        List removidos = []
        familia.programas.each { programaFamiliaAntes ->
            if (!programasDepois.contains(programaFamiliaAntes.programa)) {
                programaFamiliaAntes.delete()
                removidos << programaFamiliaAntes
            }
        }
        familia.programas.removeAll(removidos)

        //Depois inclui os novos
        programasDepois.each { programaDepois ->
            if (!familia.programas.find { it.programa == programaDepois }) {
                ProgramaFamilia programaFamilia = new ProgramaFamilia()
                programaFamilia.programa = programaDepois
                programaFamilia.familia = familia
                familia.programas.add(programaFamilia.save())
            }
        }
    }
*/

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
