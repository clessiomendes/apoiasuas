package org.apoiasuas

import grails.transaction.Transactional
import grails.validation.ValidationException
import org.apoiasuas.fileStorage.FileStorageDTO
import org.apoiasuas.fileStorage.FileStorageService
import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorial
import org.apoiasuas.redeSocioAssistencial.AbrangenciaTerritorialService
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.seguranca.SegurancaService

@Transactional(readOnly = true)
class LinkService {

    FileStorageService fileStorageService
    SegurancaService segurancaService
    AbrangenciaTerritorialService abrangenciaTerritorialService

    public static final String BUCKET = "link"

    @Transactional
    public Link grava(Link link, FileStorageDTO file) {

        //Criacao de novo link
        if (! link.id)
            link.servicoSistemaSeguranca = segurancaService.getServicoLogado()

        //Efetua validacoes antes de iniciar gravacao (já que a manipulação do fileStorage não respeita as regras de uma transacao atomica - ou tudo ou nada)
        link.validate();
        if (link.tipo?.isFile()) {
//            if (! (link.fileLabel || file.bytes)) //ou ja existe um arquivo gravado anteriormente ou deve ser fornecido um novo
            //Validacao, o arquivo nao pode ser anulado nem atualizado com nulo
            if (link.fileAction == FileStorageDTO.FileActions.ANULAR || (link.fileAction == FileStorageDTO.FileActions.ATUALIZAR && ! file?.bytes))
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
            if (link.fileAction != FileStorageDTO.FileActions.MANTER_ATUAL) {
                if (link.fileLabel) //Se ja existir um arquivo gravado, apaga antes de substituir
                    fileStorageService.remove(BUCKET, link.fileLabel)
                //Armazena o novo arquivo carregado
                link.fileLabel = fileStorageService.add(BUCKET, file)
            }
            //Obtem o nome do arquivo (previamente armazenado ou o novo) para exibicao na tela
            link.fileName = link.fileLabel ? fileStorageService.getFileName(BUCKET, link.fileLabel) : null
        } else { //Tipo URL
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

    public Link getLink(Long id) {
        if (! id)
            return null
        Link result = Link.get(id);
        if (result.tipo.isFile() && result.fileLabel)
            result.fileName = fileStorageService.getFileName(BUCKET, result.fileLabel);
        return result
    }

    public FileStorageDTO getFile(Long idLink) {
        if (! idLink)
            return null

        Link result = Link.get(idLink);
        if (result.tipo.isFile())
            return fileStorageService.get(BUCKET, result.fileLabel);
        else
            return null
    }

    public boolean testaAcessoDominio(Link link) {
        //Se eh do mesmo servico que criou o link, sempre permitir acesso
        if (link.servicoSistemaSeguranca && segurancaService.getServicoLogado() &&
                link.servicoSistemaSeguranca.id == segurancaService.getServicoLogado().id)
            return true
        //Se eh um link compartilhado, verificar a abrangencia territorial
        if (link.compartilhadoCom) { //Link compartilhado com outros serviços
            List<Long> idsMaes = segurancaService.getAbrangenciasTerritoriaisAcessiveis().collect {it.id}
            if (! idsMaes.contains(link.compartilhadoCom.id))
                return false;
        } else { //Link NAO compartilhado => restringir acesso apenas ao servicoSistema que criou o link
            if (link.servicoSistemaSeguranca && segurancaService.getServicoLogado() &&
                    link.servicoSistemaSeguranca.id != segurancaService.getServicoLogado().id)
                return false
        }
        return true;
    }
}
