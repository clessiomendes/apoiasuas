package org.apoiasuas.processo

import org.apoiasuas.cidadao.Familia
import org.apoiasuas.seguranca.UsuarioSistema

/**
 * Created by admin on 25/05/2016.
 */
class PedidoCertidaoProcessoDTO extends ProcessoDTO {

    public final static String VARIABLE_ID_FAMILIA = "idFamilia"
    Familia familia
    String cadTransiente

    public final static String VARIABLE_ID_OPERADOR_RESPONSAVEL = "idOperadorResponsavel"
    UsuarioSistema operadorResponsavel

    public final static String VARIABLE_DADOS_CERTIDAO = "dadosCertidao"
    String dadosCertidao

    public final static String VARIABLE_ID_FORMULARIO_EMTIDO = "idFormularioEmitido"
    String idFormularioEmitido

    public final static String VARIABLE_CARTORIO = "cartorio"
    String cartorio

    public final static String VARIABLE_NUMERO_AR = "numeroAR"
    String numeroAR

}
