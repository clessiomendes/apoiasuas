package org.apoiasuas.importacao

import grails.async.Promise
import grails.async.Promises
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.apoiasuas.AncestralController
import org.apoiasuas.seguranca.DefinicaoPapeis
import org.apoiasuas.seguranca.SegurancaService
import org.apoiasuas.seguranca.UsuarioSistema
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import uk.co.desirableobjects.ajaxuploader.exception.FileUploadException

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

public class DataUltimaImportacaoDTO {
    public Date valor
    public Boolean atrasada
}
