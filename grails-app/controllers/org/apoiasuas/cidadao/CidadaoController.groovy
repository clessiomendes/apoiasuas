package org.apoiasuas.cidadao

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import grails.util.GrailsNameUtils
import org.apoiasuas.AncestralController
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.util.StringUtils

import javax.servlet.http.HttpSession

import static org.springframework.http.HttpStatus.*

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class CidadaoController extends AncestralController {

    def beforeInterceptor = [action: this.&interceptaSeguranca, entity:Cidadao.class, only: ['show','edit', 'delete', 'update', 'save']]
    static defaultAction = "procurarCidadao"
    private static final String SESSION_ULTIMO_CIDADAO = "SESSION_ULTIMO_CIDADAO"

    def cidadaoService

    static List<String> getDocumentos(Cidadao cidadao) {
        List<String> result = []
        if (cidadao.nis)
            result << "NIS: "+cidadao.nis
        if (cidadao.cpf)
            result << "CPF: "+cidadao.cpf
        if (cidadao.identidade)
            result << "Identidade: "+cidadao.identidade
        if (cidadao.numeroCTPS)
            result << "CTPS: nº "+cidadao.numeroCTPS + (cidadao.serieCTPS ? " série " + cidadao.serieCTPS : "")
        return result
    }

    def procurarCidadao(FiltroCidadaoCommand filtro) {
        //Preenchimento de numeros no primeiro campo de busca indica pesquisa por codigo legado
        boolean buscaPorCodigoLegado = filtro.nomeOuCodigoLegado && ! StringUtils.PATTERN_TEM_LETRAS.matcher(filtro.nomeOuCodigoLegado)
        params.max = params.max ?: 20
        PagedResultList cidadaos = cidadaoService.procurarCidadao(params, filtro)
        Map filtrosUsados = params.findAll { it.value }

        if (buscaPorCodigoLegado && cidadaos?.resultList?.size() > 0) {
            Cidadao cidadao = cidadaos?.resultList[0]
            redirect(controller: "familia", action: "show", id: cidadao.familia.id)
        } else
            return [cidadaoInstanceList: cidadaos, cidadaoInstanceCount: cidadaos.getTotalCount(), filtro: filtrosUsados ]
    }

    def selecionarFamilia(Familia familiaInstance) {
        redirect(controller: 'familia', action: 'show', params: params)
//        forward controller: GrailsNameUtils.getLogicalName(FamiliaController.class, "Controller"), action: "show"
    }

    def show(Cidadao cidadaoInstance) {
        guardaUltimoCidadaoSelecionado(cidadaoInstance)
        respond cidadaoInstance
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def create() {
        respond new Cidadao(params)
    }

    public static Cidadao getUltimoCidadao(HttpSession session) {
        return session[SESSION_ULTIMO_CIDADAO]
    }

    public static void setUltimoCidadao(HttpSession session, Cidadao cidadao) {
        session[SESSION_ULTIMO_CIDADAO] = cidadao
    }
}

@grails.validation.Validateable
class FiltroCidadaoCommand implements Serializable {
    String nomeOuCodigoLegado
    String logradouro
    String numero
}
