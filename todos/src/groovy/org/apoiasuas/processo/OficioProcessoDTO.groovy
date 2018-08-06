package org.apoiasuas.processo

import org.apoiasuas.cidadao.Familia
import org.apoiasuas.seguranca.UsuarioSistema

/**
 * Created by admin on 25/05/2016.
 */
class OficioProcessoDTO extends ProcessoDTO {

    public final static String VARIABLE_ID_FAMILIA = "idFamilia"
    Familia familia

    public final static String VARIABLE_ID_OPERADOR_AUTOR = "idOperadorAutor"
    UsuarioSistema operadorAutor

    public final static String VARIABLE_DESTINATARIO = "destinatario"
    String destinatario

    public final static String VARIABLE_TITULO = "titulo"
    String titulo

    public final static String VARIABLE_NUMERO_OFICIO = "numeroOficio"
    String numeroOficio

}
