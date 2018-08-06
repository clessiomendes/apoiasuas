package org.apoiasuas.anotacoesDominio

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.FIELD])
@interface InfoPropriedadeDominio {
    static final org.apoiasuas.formulario.CampoFormulario.Tipo TIPO_DEFAULT = org.apoiasuas.formulario.CampoFormulario.Tipo.TEXTO
    static final int TAMANHO_DEFAULT = 60
    String codigo()
    String descricao() default ''
    org.apoiasuas.formulario.CampoFormulario.Tipo tipo() default org.apoiasuas.formulario.CampoFormulario.Tipo.TEXTO
    int tamanho() default 60
    boolean atualizavel() default true
}

