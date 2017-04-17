package org.apoiasuas.marcador

import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.DefinicaoPapeis

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class MarcadorController extends AncestralController {

    def marcadorService;

    static defaultAction = "list"

    def beforeInterceptor = [action: this.&interceptaSeguranca, (ENTITY_CLASS_ENTRY):Programa.class, only: ['show','edit', 'delete', 'update', 'save']]

    def list(Integer max, Long idServicoSistema) {
        params.max = max ?: 500;
        render view: 'list', model:[programasList: getMarcadores(Programa.class, idServicoSistema, params),
                                    vulnerabilidadesList: getMarcadores(Vulnerabilidade.class, idServicoSistema, params),
                                    acoesList: getMarcadores(Acao.class, idServicoSistema, params),
                                    outrosMarcadoresList: getMarcadores(OutroMarcador.class, idServicoSistema, params),
                                    servicosDisponiveis: segurancaService.superUser ? ServicoSistema.findAll() : [servicoCorrente],
                                    servicoEscolhido: idServicoSistema
        ]
    }

    private ArrayList<Marcador> getMarcadores(Class<Marcador> classeMarcador, Long idServicoSistema, Map params) {
        ServicoSistema servicoSistema = null;
        if (idServicoSistema)
            servicoSistema = ServicoSistema.get(idServicoSistema)
//        return Programa
        //noinspection GrUnresolvedAccess
        return classeMarcador.findAllByServicoSistemaSeguranca(servicoSistema, params).sort { it.descricao?.toLowerCase() }
    }

    def showPrograma(Programa marcador) {
        if (! marcador)
            return notFound()
        render view: 'show', model: getModelExibicao(marcador, "Programa")
    }

    def showVulnerabilidade(Vulnerabilidade marcador) {
        if (! marcador)
            return notFound()
        render view: 'show', model: getModelExibicao(marcador, "Vulnerabilidade")
    }

    def showAcao(Acao marcador) {
        if (! marcador)
            return notFound()
        render view: 'show', model: getModelExibicao(marcador, "Acao")
    }

    def showOutroMarcador(OutroMarcador marcador) {
        if (! marcador)
            return notFound()
        render view: 'show', model: getModelExibicao(marcador, "OutroMarcador")
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def savePrograma(Programa marcador) {
        return save(marcador, "Programa")
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveVulnerabilidade(Vulnerabilidade marcador) {
        return save(marcador, "Vulnerabilidade")
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveAcao(Acao marcador) {
        return save(marcador, "Acao")
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def saveOutroMarcador(OutroMarcador marcador) {
        return save(marcador, "OutroMarcador")
    }

    private def save(Marcador marcador, String entityName) {
        if (! marcador)
            return notFound()

        boolean modoCriacao = marcador.id == null

        //Grava
        if (marcador.validate()) {
            //se não for admin, força o marcador a ser gravado exclusivamente para o servico logado
            if (! segurancaService.superUser)
                marcador.servicoSistemaSeguranca = segurancaService.servicoLogado;
            marcadorService.gravaMarcador(marcador)
        } else {
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: getModelEdicao(marcador, entityName))
        }

        flash.message = message(code: 'default.updated.message', args: [entityName, marcador.descricao])
        render view: 'show', model: getModelExibicao(marcador, entityName)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def createPrograma() {
        return create(new Programa(), "Programa");
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def createVulnerabilidade() {
        return create(new Vulnerabilidade(), "Vulnerabilidade");
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def createAcao() {
        return create(new Acao(), "Acao");
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def createOutroMarcador() {
        return create(new OutroMarcador(), "OutroMarcador");
    }

    private def create(Marcador marcador, String entityName) {
        marcador.habilitado = true;
        render view: 'create', model: getModelEdicao(marcador, entityName)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def editPrograma(Programa marcador) {
        if (! marcador)
            return notFound()
        render view: 'edit', model: getModelEdicao(marcador, "Programa")
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def editVulnerabilidade(Vulnerabilidade marcador) {
        if (! marcador)
            return notFound()
        render view: 'edit', model: getModelEdicao(marcador, "Vulnerabilidade")
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def editAcao(Acao marcador) {
        if (! marcador)
            return notFound()
        render view: 'edit', model: getModelEdicao(marcador, "Acao")
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def editOutroMarcador(OutroMarcador marcador) {
        if (! marcador)
            return notFound()
        render view: 'edit', model: getModelEdicao(marcador, "OutroMarcador")
    }

    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: ["Programa", params.id])
        return redirect(action: "list");
    }

    private Map<String, Object> getModelEdicao(Marcador marcador, String labelMarcador) {
        return getModelExibicao(marcador, labelMarcador) +
                [servicosDisponiveis: segurancaService.superUser ? ServicoSistema.findAll() : [servicoCorrente]]
    }

    private Map<String, Object> getModelExibicao(Marcador marcador, String labelMarcador) {
        Boolean podeAlterar = segurancaService.superUser || marcador.servicoSistemaSeguranca?.id == servicoCorrente.id;
        return [marcadorInstance: marcador, labelMarcador: labelMarcador, podeAlterar: podeAlterar]
    }

}
