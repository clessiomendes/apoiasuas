package org.apoiasuas

import grails.transaction.Transactional
import grails.validation.ValidationException
import org.apoiasuas.fileStorage.FileStorageDTO
import org.apoiasuas.fileStorage.FileStorageService
import org.apoiasuas.processo.ProcessoService
import org.springframework.web.multipart.MultipartFile

@Transactional(readOnly = true)
class LinkService {

    FileStorageService fileStorageService
    private static final String BUCKET = "link"

    @Transactional
    public Link grava(Link link, FileStorageDTO file) {

        //Efetua validacoes antes de iniciar gravacao (já que a manipulação do fileStorage não respeita as regras de uma transacao atomica - ou tudo ou nada)
        link.validate();
        if (link.tipo?.isFile()) {
            if (! (link.fileLabel || file.bytes)) //ou ja existe um arquivo gravado anteriormente ou deve ser fornecido um novo
                link.errors.rejectValue("fileLabel","","Arquivo é obrigatório.")
        }
        if (link.tipo?.isUrl()) {
            if (! link.url)
                link.errors.rejectValue("fileLabel","","Site de internet é obrigatório.")
        }
        if (link.hasErrors())
            throw new ValidationException(null, link.errors)

        //Manipula o sistema de arquivos (adiciona e/ou apaga arquivos conforme necessario
        if (link.tipo?.isFile()) {  //Tipo ARQUIVO
            //Remove eventual url armazenada anteriormente
            link.url = null
            if (file.bytes) {//Foi enviado um novo arquivo
                if (link.fileLabel) //Se ja existir um arquivo gravado, apaga antes de substituir
                    fileStorageService.remove(BUCKET, link.fileLabel)
                //Armazena o novo arquivo carregado
                link.fileLabel = fileStorageService.add(BUCKET, file)
            }
            //Obtem o nome do arquivo (previamente armazenado ou o novo) para exibicao na tela
            link.fileName = link.fileLabel ? fileStorageService.getFileName(BUCKET, link.fileLabel) : null
        } else {
            //limpa eventual arquivo armazenado anteriormente
            if (link.fileLabel) {
                fileStorageService.remove(BUCKET, link.fileLabel)
                link.fileLabel = null
                link.fileName = null
            }
        }

        return link.save()
    }

    @Transactional
    public Link apaga(Link link) {
        if (link.tipo.isFile()) {
            //limpa arquivo armazenado anteriormente
            fileStorageService.remove(BUCKET, link.fileLabel)
        }
        return link.delete()
    }

    public Link getServico(long id) {
        Link result = Link.get(id);
        if (result.tipo.isFile())
            result.fileName = fileStorageService.getFileName(BUCKET, result.fileLabel);
        return result
    }

    public FileStorageDTO getFile(long idLink) {
        Link result = Link.get(idLink);
        if (result.tipo.isFile())
            return fileStorageService.get(BUCKET, result.fileLabel);
        else
            return null
    }



}
