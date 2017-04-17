package org.apoiasuas

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.cidadao.Cidadao
import org.apoiasuas.cidadao.CidadaoController
import org.apoiasuas.cidadao.MarcadorService
import org.apoiasuas.cidadao.Monitoramento
import org.apoiasuas.marcador.Acao
import org.apoiasuas.marcador.Marcador
import org.apoiasuas.marcador.OutroMarcador
import org.apoiasuas.marcador.Programa
import org.apoiasuas.marcador.Vulnerabilidade
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.UsuarioSistema

class GestaoTecnicaController extends AncestralController {

    def monitoramentoService;
    def marcadorService

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def inicial(Long idTecnico) {
        //Familias em programas
        Map<Marcador, Long> familiasEmProgramas = marcadorService.qntFamiliasMarcadoresAgrupadas(Programa.class, idTecnico
                , MarcadorService.DestinoFiltroTecnico.FAMILIA);
        Long totalFamiliasEmProgramas = marcadorService.totalFamiliasMarcadores(Programa.class, idTecnico
                , MarcadorService.DestinoFiltroTecnico.MARCADOR);

        //Familias com vulnerabilidades
        Map<Marcador, Long> familiasComVulnerabilidades = marcadorService.qntFamiliasMarcadoresAgrupadas(Vulnerabilidade.class, idTecnico
                , MarcadorService.DestinoFiltroTecnico.MARCADOR);
        Long totalFamiliasComVulnerabilidades = marcadorService.totalFamiliasMarcadores(Vulnerabilidade.class, idTecnico
                , MarcadorService.DestinoFiltroTecnico.MARCADOR);

        //Familias com acoes previstas
        Map<Marcador, Long> familiasComAcoes = marcadorService.qntFamiliasMarcadoresAgrupadas(Acao.class, idTecnico
                , MarcadorService.DestinoFiltroTecnico.MARCADOR);
        Long totalFamiliasComAcoes = marcadorService.totalFamiliasMarcadores(Acao.class, idTecnico
                , MarcadorService.DestinoFiltroTecnico.MARCADOR);

        //Familias com outros marcadores
        Map<Marcador, Long> familiasComOutrosMarcadores = marcadorService.qntFamiliasMarcadoresAgrupadas(OutroMarcador.class, idTecnico
                , MarcadorService.DestinoFiltroTecnico.MARCADOR);
        Long totalFamiliasComOutrosMarcadores = marcadorService.totalFamiliasMarcadores(OutroMarcador.class, idTecnico
                , MarcadorService.DestinoFiltroTecnico.MARCADOR);

        //Monitoramentos
        Map<String, Long> monitoramentos = monitoramentoService.qntMonitoramentosAgruparSituacao(idTecnico);
        Long totalMonitoramentos = monitoramentos.values().sum() ?: 0;

        render view: "inicial", model: [operadores: getTecnicosOrdenadosController(false),
                                        idTecnico: idTecnico,
                                        familiasEmProgramas: familiasEmProgramas, totalFamiliasEmProgramas: totalFamiliasEmProgramas,
                                        familiasComAcoes: familiasComAcoes, totalFamiliasComAcoes: totalFamiliasComAcoes,
                                        familiasComVulnerabilidades: familiasComVulnerabilidades, totalFamiliasComVulnerabilidades: totalFamiliasComVulnerabilidades,
                                        familiasComOutrosMarcadores: familiasComOutrosMarcadores, totalFamiliasComOutrosMarcadores: totalFamiliasComOutrosMarcadores,
                                        monitoramentos: monitoramentos, totalMonitoramentos: totalMonitoramentos
        ]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def listarFamiliasProgramas(Long idMarcador, Long idTecnico) {
        String tituloListagem = message(code:"titulo.familias.programas");
        List<Cidadao> cidadaoInstanceList = marcadorService.getReferenciasFamiliasMarcadores(Programa.class, idMarcador,
                idTecnico, MarcadorService.DestinoFiltroTecnico.FAMILIA);

        if (idMarcador)
            tituloListagem += " - "+Programa.get(idMarcador).descricao;
        if (idTecnico) {
            tituloListagem += " - "+UsuarioSistema.get(idTecnico).username;
            //Remover as referencias tecnicas APENAS NA EXIBIÇÃO DO RESULTADO, para evitar colorir o resultado de vermelho (sinal de familias com referencia)
            cidadaoInstanceList.each {
                it.familia.tecnicoReferencia = null;
            }
        }

        render view: "listarFamilias", model: CidadaoController.modeloProcurarCidadao +
                [tituloListagem: tituloListagem, cidadaoInstanceList: cidadaoInstanceList]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def listarFamiliasVulnerabilidades(Long idMarcador, Long idTecnico) {
        String tituloListagem = message(code:"titulo.familias.vulnerabilidades");
        List<Cidadao> cidadaoInstanceList = marcadorService.getReferenciasFamiliasMarcadores(Vulnerabilidade.class, idMarcador,
                idTecnico, MarcadorService.DestinoFiltroTecnico.MARCADOR);

        if (idMarcador)
            tituloListagem += " - "+Vulnerabilidade.get(idMarcador).descricao;
        if (idTecnico)
            tituloListagem += " - "+UsuarioSistema.get(idTecnico).username;

        render view: "listarFamilias", model: CidadaoController.modeloProcurarCidadao +
                [tituloListagem: tituloListagem, cidadaoInstanceList: cidadaoInstanceList]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def listarFamiliasAcoes(Long idMarcador, Long idTecnico) {
        String tituloListagem = message(code:"titulo.familias.acoes");
        List<Cidadao> cidadaoInstanceList = marcadorService.getReferenciasFamiliasMarcadores(Acao.class, idMarcador,
                idTecnico, MarcadorService.DestinoFiltroTecnico.MARCADOR);

        if (idMarcador)
            tituloListagem += " - "+Acao.get(idMarcador).descricao;
        if (idTecnico)
            tituloListagem += " - "+UsuarioSistema.get(idTecnico).username;

        render view: "listarFamilias", model: CidadaoController.modeloProcurarCidadao +
                [tituloListagem: tituloListagem, cidadaoInstanceList: cidadaoInstanceList]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def listarFamiliasOutrosMarcadores(Long idMarcador, Long idTecnico) {
        String tituloListagem = message(code:"titulo.familias.outros.marcadores");
        List<Cidadao> cidadaoInstanceList = marcadorService.getReferenciasFamiliasMarcadores(OutroMarcador.class, idMarcador,
                idTecnico, MarcadorService.DestinoFiltroTecnico.MARCADOR);

        if (idMarcador)
            tituloListagem += " - "+OutroMarcador.get(idMarcador).descricao;
        if (idTecnico)
            tituloListagem += " - "+UsuarioSistema.get(idTecnico).username;

        render view: "listarFamilias", model: CidadaoController.modeloProcurarCidadao +
                [tituloListagem: tituloListagem, cidadaoInstanceList: cidadaoInstanceList]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def listarMonitoramentos(String situacao, Long idTecnico) {
        String tituloListagem = message(code:"titulo.monitoramentos");

        Monitoramento.Situacao lSituacao = situacao as Monitoramento.Situacao
        List<Monitoramento> monitoramentosInstanceList = monitoramentoService.getMonitoramentos(lSituacao, idTecnico);

        if (lSituacao)
            tituloListagem += " - "+lSituacao.label;
        if (idTecnico)
            tituloListagem += " - "+UsuarioSistema.get(idTecnico).username;

        render view: "listarMonitoramentos", model: [tituloListagem: tituloListagem, monitoramentosInstanceList: monitoramentosInstanceList,
                situacao: situacao, idTecnico: idTecnico] + CidadaoController.modeloProcurarCidadao
        }

}
