package org.apoiasuas

import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import org.apoiasuas.fileStorage.FileStorageDTO
import org.apoiasuas.seguranca.AcessoNegadoPersistenceException
import org.apoiasuas.seguranca.DefinicaoPapeis
import grails.transaction.Transactional
import org.apoiasuas.util.ApoiaSuasException
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
class LinkController extends AncestralController {

    static defaultAction = "list"
    LinkService linkService
    def beforeInterceptor = [action: this.&interceptaSeguranca/*("ola")*/, (ENTITY_CLASS_ENTRY):Link.class, only: ['show','edit', 'delete', 'update', 'save']]

    def exibeLinks() {
        respond Link.findAllByServicoSistemaSeguranca(getServicoCorrente(), params).sort { it.id }
//        respond Link.list(params).sort { it.id }
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 50, 100)
        respond Link.findAllByServicoSistemaSeguranca(getServicoCorrente(), params), model:[linkInstanceCount: Link.count()]
    }

    def show(Link linkInstance) {
        if (! linkInstance)
            return notFound()
        //recarrega objeto para preencher campos transientes
        linkInstance = linkService.getServico(linkInstance.id)
        render view: 'show', model: [ linkInstance: linkInstance ]
    }

    @Transactional
    def save(Link linkInstance) {
        if (! linkInstance)
            return notFound()

        boolean modoCriacao = linkInstance.id == null

        //Upload de arquivo
        FileStorageDTO file = null
        if (linkInstance.tipo?.isFile()) {
            if (request instanceof MultipartHttpServletRequest) {
                MultipartFile multipartFile = ((MultipartHttpServletRequest)request).getFile("file")
                file = new FileStorageDTO(multipartFile.originalFilename, multipartFile.bytes)
            } else {
                throw new ApoiaSuasException("Tipo inesperado de request para upload de arquivos: "+request.getClass().name)
            }
        }
        try {
            linkService.grava(linkInstance, file)
        } catch (ValidationException e) {
            linkInstance.errors = e.errors
            //exibe o formulario novamente em caso de problemas na validacao
            return render(view: modoCriacao ? "create" : "edit" , model: [linkInstance:linkInstance]);
        }
        flash.message = message(code: 'default.updated.message', args: [message(code: 'link.label'), linkInstance.toString()])
        render view: 'show', model: [ linkInstance: linkInstance ]
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def create() {
        Link link = new Link(params)
        render view: 'create', model: [linkInstance: link]
//        respond link
    }

    @Secured([DefinicaoPapeis.STR_USUARIO])
    def edit(Link linkInstance) {
        if (! linkInstance)
            return notFound()
        //recarrega objeto para preencher campos transientes
        linkInstance = linkService.getServico(linkInstance.id)
        render view: 'edit', model: [linkInstance: linkInstance]
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

/*  Testa presença de arquivos/pastas no filesystem

    @Secured([DefinicaoPapeis.STR_USUARIO_LEITURA])
    def teste(String caminho) {
        if (! caminho)
            caminho = System.properties.getProperty('APP_HOME')
        render testaCaminho(caminho)
    }

    private String testaCaminho(String caminho) {
        String result = ""
        Path meuCaminho = Paths.get(caminho)
        result += meuCaminho.toString()
        if (Files.exists(meuCaminho)) {
            result += " ...found"
            File file = new File(caminho);
            String[] directories = file.list(new FilenameFilter() {
                @Override
                public boolean accept(File current, String name) {
                    return new File(current, name).isDirectory();
                }
            });
            directories?.each {
                result += "<br> "+it
            }
        } else
            result += " ...not found"
        return result
    }
*/
}
