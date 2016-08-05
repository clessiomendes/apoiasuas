package org.apoiasuas.cidadao

import grails.transaction.Transactional
import org.apoiasuas.processo.PedidoCertidaoProcessoDTO
import org.apoiasuas.programa.Programa
import org.apoiasuas.programa.ProgramaFamilia

class FamiliaService {

    public static final int MAX_AUTOCOMPLETE_LOGRADOUROS = 10
    def segurancaService
    def pedidoCertidaoProcessoService

    @Transactional
    public Familia grava(Familia familia, ProgramasCommand programasCommand) {
        //Converte e filtra apenas os programas selecionados
        List<Programa> programasDepois = programasCommand.programasdisponiveis.collect { cmd ->
            if (cmd.selected)
                Programa.get(cmd.id)
        }.grep() //limpa nulos
        //Atualiza a lista de programas, removendo ou inserindo APENAS QUANDO NECESSÁRIO
        //Primeiro apaga os excluídos
        List removidos = []
        familia.programas.each { programaFamiliaAntes ->
            if (! programasDepois.contains(programaFamiliaAntes.programa)) {
                programaFamiliaAntes.delete()
                removidos << programaFamiliaAntes
            }
        }
        familia.programas.removeAll(removidos)

        //Depois inclui os novos
        programasDepois.each { programaDepois ->
            if (! familia.programas.find { it.programa == programaDepois } ) {
                ProgramaFamilia programaFamilia = new ProgramaFamilia()
                programaFamilia.programa = programaDepois
                programaFamilia.familia = familia
                familia.programas.add(programaFamilia.save())
            }
        }

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

    public Set<String> getNotificacoes(Long idFamilia) {
        if (! idFamilia)
            return []
        Set<String> result = []
        Familia familia = Familia.get(idFamilia);

        //testa se a familia eh acompanhada por algum tecnico
        if (familia.tecnicoReferencia)
            result << "Família acompanhada por "+familia.tecnicoReferencia.username+"."

        //testa idades voltadas ao SCFV
        familia.membros.each { Cidadao cidadao ->
            if (cidadao.idade && cidadao.idade < 4)
                result << "Família elegível ao SCFV de 0 a 3."
            if (cidadao.idade && cidadao.idade >= 60)
                result << "Família elegível ao SCFV para idosos."
        }

        return result
    }
}
