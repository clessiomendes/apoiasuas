package org.apoiasuas.cidadao

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import grails.util.GrailsNameUtils
import org.apoiasuas.AncestralController
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.SegurancaService
import org.apoiasuas.util.StringUtils
import org.codehaus.groovy.grails.commons.GrailsApplication

import static org.springframework.http.HttpStatus.*

@Transactional(readOnly = true)
@Secured([DefinicaoPapeis.USUARIO_LEITURA])
class CidadaoController extends AncestralController {

    static defaultAction = "procurarCidadao"

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
        forward controller: GrailsNameUtils.getLogicalName(FamiliaController.class, "Controller"), action: "show"
    }

    def show(Cidadao cidadaoInstance) {
        guardaUltimoCidadaoSelecionado(cidadaoInstance)
        respond cidadaoInstance
    }

    @Secured([DefinicaoPapeis.USUARIO])
    def create() {
        respond new Cidadao(params)
    }

    @Transactional
    @Secured([DefinicaoPapeis.USUARIO])
    def save(Cidadao cidadaoInstance) {
        if (cidadaoInstance == null) {
            notFound()
            return
        }

        if (cidadaoInstance.hasErrors()) {
            respond cidadaoInstance.errors, view: 'create'
            return
        }

        cidadaoInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'cidadao.label', default: 'Cidadao'), cidadaoInstance.id])
                redirect cidadaoInstance
            }
            '*' { respond cidadaoInstance, [status: CREATED] }
        }
    }

    @Secured([DefinicaoPapeis.USUARIO])
    def edit(Cidadao cidadaoInstance) {
        respond cidadaoInstance
    }

    @Transactional
    @Secured([DefinicaoPapeis.USUARIO])
    def update(Cidadao cidadaoInstance) {
        if (cidadaoInstance == null) {
            notFound()
            return
        }

        if (cidadaoInstance.hasErrors()) {
            respond cidadaoInstance.errors, view: 'edit'
            return
        }

        cidadaoInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Cidadao.label', default: 'Cidadao'), cidadaoInstance.id])
                redirect cidadaoInstance
            }
            '*' { respond cidadaoInstance, [status: OK] }
        }
    }

    @Transactional
    @Secured([DefinicaoPapeis.USUARIO])
    def delete(Cidadao cidadaoInstance) {

        if (cidadaoInstance == null) {
            notFound()
            return
        }

        cidadaoInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Cidadao.label', default: 'Cidadao'), cidadaoInstance.id])
                redirect action: "procurarCidadao", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'cidadao.label', default: 'Cidadao'), params.id])
                redirect action: "procurarCidadao", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

@grails.validation.Validateable
class FiltroCidadaoCommand implements Serializable {
    String nomeOuCodigoLegado
    String logradouro
    String numero
//    String codigoLegado
}
