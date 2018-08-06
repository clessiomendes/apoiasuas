package org.apoiasuas.seguranca

import grails.converters.JSON
import grails.transaction.Transactional
import org.apoiasuas.cidadao.Auditoria
import org.apoiasuas.cidadao.Familia
import org.apoiasuas.cidadao.detalhe.CampoDetalhe
import org.apoiasuas.marcador.ProgramaFamilia

@Transactional
class AuditoriaService {

    def segurancaService;

    public static enum Operacao { INCLUSAO, EXCLUSAO, ALTERACAO }

    /**
     * Dados básicos, necessários para qualquer auditoria
     */
    private Auditoria criaAuditoria(Auditoria.Tipo tipo) {
        Auditoria result = new Auditoria();
        result.tipo = tipo;
        result.criador = segurancaService.usuarioLogado;
        result.servicoSistemaSeguranca = segurancaService.servicoLogado;
        return result;
    }

    @Transactional
    private Auditoria registraTecnicoFamilia(Familia familia, UsuarioSistema tecnicoAnterior, UsuarioSistema tecnicoNovo) {
        //Campos basicos comuns a qualquer auditoria
        Auditoria auditoria = criaAuditoria(Auditoria.Tipo.MUDANCA_TECNICO_FAMILIA);

        //Campos especificos do dominio
        auditoria.familia = familia;

        //Campos específicos estruturados em JSON
        CampoDetalhe campoTecnicoAnterior, campoTecnicoNovo;
//        if (tecnicoAnterior)
            campoTecnicoAnterior = CampoDetalhe.newInstance(CampoDetalhe.Tipo.PLAIN.toString(), [valor: tecnicoAnterior?.id]);
//        if (tecnicoNovo)
            campoTecnicoNovo = CampoDetalhe.newInstance(CampoDetalhe.Tipo.PLAIN.toString(), [valor: tecnicoNovo?.id]);
        auditoria.detalhes = [tenicoAnterior: campoTecnicoAnterior?.toJsonMap(), novoTecnico: campoTecnicoNovo?.toJsonMap()] as JSON;

        //Montagem da descrição
        if (! tecnicoAnterior && tecnicoNovo)
            auditoria.descricao = "${tecnicoNovo.username} é o(a) novo(a) técnico(a) de referência"
        else if (tecnicoAnterior && ! tecnicoNovo)
            auditoria.descricao = "${tecnicoAnterior.username} deixou de ser o(a) técnico(a) de referência"
        else if (tecnicoAnterior && tecnicoNovo)
            auditoria.descricao = "Referência técnica passou de ${tecnicoAnterior.username} para ${tecnicoNovo.username}"
        else
            auditoria.descricao = "Família sem técnico de referência"

        auditoria.save();
        log.debug(auditoria.descricao + " - familia: " + familia.id);
        return auditoria;
    }

    @Transactional(readOnly = true)
    public List<Auditoria> listaAuditorias(Familia familia, List<Auditoria.Tipo> filtro) {
        final Map opcoesFind = [sort: "dateCreated", order: "desc", max: 200];
        if (filtro)
            return Auditoria.findAllByServicoSistemaSegurancaAndFamiliaAndTipoInList(
                    segurancaService.servicoLogado, familia, filtro, opcoesFind)
        else
            return Auditoria.findAllByServicoSistemaSegurancaAndFamilia(
                    segurancaService.servicoLogado, familia, opcoesFind);
    }

    @Transactional
    public void auditaFamilia(Familia familia) {
        //Detectando mudanca no tecnico de referencia: AUDITAR
        if (familia.isDirty('tecnicoReferencia'))
            registraTecnicoFamilia(familia, familia.getPersistentValue('tecnicoReferencia'), familia.tecnicoReferencia);

    }

    @Transactional
    public void auditaPrograma(ProgramaFamilia programaFamilia, Operacao operacao) {
        if (! programaFamilia?.programa || ! programaFamilia?.familia || ! operacao)
            return;

        if (operacao == Operacao.ALTERACAO) {
            if (programaFamilia.isDirty('habilitado'))
                //registra auditoria apenas se desabilitar ou reabilitar o programa (tratando como EXCLUSAO ou INCLUSAO
                operacao = programaFamilia.habilitado ? Operacao.INCLUSAO : operacao.EXCLUSAO
            else
                //ignora auditoria nos demais casos
                return;
        }

        //Campos basicos comuns a qualquer auditoria
        Auditoria auditoria = criaAuditoria(Auditoria.Tipo.PROGRAMA_FAMILIA);
        //Se nao foi possivel buscar usuario e servico logado (rotinas batch), usar os campos da familia
        if (! auditoria.criador)
            auditoria.criador = programaFamilia.familia.criador.merge(validate: false, flush: false);
        if (! auditoria.servicoSistemaSeguranca)
            auditoria.servicoSistemaSeguranca = programaFamilia.familia.servicoSistemaSeguranca.merge(validate: false, flush: false);

        //Campos especificos do dominio
        auditoria.familia = programaFamilia.familia;


        if (operacao == Operacao.INCLUSAO) {
            //Na inclusão (apenas), informar o tecnico responsavel
            CampoDetalhe campoTecnico;
            campoTecnico = CampoDetalhe.newInstance(CampoDetalhe.Tipo.PLAIN.toString(), [valor: programaFamilia.tecnico?.id]);
            auditoria.detalhes = [tenico: campoTecnico?.toJsonMap()] as JSON;
            auditoria.descricao = "Família inserida em ${programaFamilia.programa.descricao}"
        } else if (operacao == Operacao.EXCLUSAO)
            auditoria.descricao = "Família desligada de ${programaFamilia.programa.descricao}"

        auditoria.save();
    }
}
