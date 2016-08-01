package org.apoiasuas

import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import org.apoiasuas.fileStorage.FileStorageDTO
import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial
import org.apoiasuas.seguranca.DefinicaoPapeis
import grails.transaction.Transactional
import org.apoiasuas.util.ApoiaSuasException
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class LinkController extends AncestralController {

    LinkService linkService

    static defaultAction = "list"
    public static String CHECKBOX_COMPARTILHAR = "compartilhar"

    def beforeInterceptor = [action: this.&interceptaSeguranca/*("ola")*/, (ENTITY_CLASS_ENTRY):Link.class, only: ['show','edit', 'delete', 'update', 'save']]

    def exibeLinks() {
        respond Link.findAllByServicoSistemaSeguranca(getServicoCorrente(), params).sort { it.id }
//        respond Link.list(params).sort { it.id }
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 50, 100)
        respond Link.findAllByServicoSistemaSeguranca(getServicoCorrente(), params).sort { it.id }, model:[linkInstanceCount: Link.count()]
    }

    def show(Link linkInstance) {
        if (! linkInstance)
            return notFound()
        //recarrega objeto para preencher campos transientes
        linkInstance = linkService.getLink(linkInstance.id)
        render view: 'show', model: getModelExibicao(linkInstance)
    }

    @Transactional
    def save(Link linkInstance) {
        if (! linkInstance)
            return notFound()

        boolean modoCriacao = linkInstance.id == null
        linkInstance.fileAction = FileStorageDTO.FileActions.valueOf(request.getParameter("fileAction"))

        //Upload de arquivo
        FileStorageDTO file = null
        if (linkInstance.tipo?.isFile() && linkInstance.fileAction == FileStorageDTO.FileActions.ATUALIZAR) {
            if (request instanceof MultipartHttpServletRequest) {
                MultipartFile multipartFile = ((MultipartHttpServletRequest)request).getFile(FileStorageDTO.INPUT_FILE)
                //FIXME: configurar o servidor de aplicacao para vetar arquivos grandes antes que eles cheguem ao servidor e causem grandes danos. Cuidado para nao vetar os tamanhos de arquivos de importacao de bancos de dados
                if (multipartFile.size > FileStorageDTO.MAX_FILE_SIZE)
                    throw new ApoiaSuasException("Tamanho do arquivo (${multipartFile.size}) maior do que o permitido (${FileStorageDTO.MAX_FILE_SIZE})")

                file = new FileStorageDTO(multipartFile.originalFilename, multipartFile.bytes)
            } else {
                throw new ApoiaSuasException("Tipo inesperado de request para upload de arquivos: "+request.getClass().name)
            }
        }

        try {
            //Definindo a abrangência territorial para compartilhamento do link
            AbrangenciaTerritorial abrangenciaTerritorial = atribuiAbrangenciaTerritorial();
            if (request.getParameter(CHECKBOX_COMPARTILHAR)) {
                if (abrangenciaTerritorial)
                    linkInstance.compartilhadoCom =  abrangenciaTerritorial
                else {
                    linkInstance.errors.rejectValue("compartilhadoCom","","É necessário escolher uma área de abrangência para compartilhar este link.")
                    throw new ValidationException(null, linkInstance.errors)
                }
            } else {
                linkInstance.compartilhadoCom = null
            }

            linkService.grava(linkInstance, file)
        } catch (ValidationException e) {
            linkInstance.errors = e.errors
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: getModelEdicao(linkInstance));
        }
        flash.message = message(code: 'default.updated.message', args: [message(code: 'link.label'), linkInstance.toString()])
        render view: 'show', model: getModelExibicao(linkInstance)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def create() {
        Link link = new Link()
        render view: 'create', model: getModelEdicao(link)
//        respond link
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def edit(Link linkInstance) {
        if (! linkInstance)
            return notFound()
        render view: 'edit', model: getModelEdicao(linkInstance)
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    @Transactional
    def delete(Link linkInstance) {
        if (! linkInstance)
            return notFound()

        linkService.apaga(linkInstance)
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'link.label'), linkInstance.descricao])
        redirect action:"list"
    }

    protected def notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'link.label', default: 'Link'), params.id])
        return redirect(action: "list");
    }

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def downloadFile(Link linkInstance) {
        if (! linkInstance)
            return notFound()
        //obtem referencia para o arquivo a baixar
        FileStorageDTO file = linkService.getFile(linkInstance.id)

        response.contentType = 'application/octet-stream'
        response.setHeader 'Content-disposition', "attachment; filename=\"${file.fileName}\""
        response.outputStream.write(file.bytes)
        response.outputStream.flush()
    }

    private LinkedHashMap<String, Object> getModelEdicao(Link linkInstance) {
        //recarrega objeto para preencher campos transientes
        if (linkInstance?.id)
            linkInstance = linkService.getLink(linkInstance?.id)
        [linkInstance: linkInstance, JSONAbrangenciaTerritorial: getAbrangenciasTerritoriaisEdicao(linkInstance?.compartilhadoCom)]
    }

    private Map<String, Object> getModelExibicao(Link linkInstance) {
        [linkInstance: linkInstance, JSONAbrangenciaTerritorial: getAbrangenciasTerritoriaisExibicao(linkInstance.compartilhadoCom)]
    }

}
