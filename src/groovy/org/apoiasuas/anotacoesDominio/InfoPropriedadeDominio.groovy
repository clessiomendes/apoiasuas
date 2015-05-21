package org.apoiasuas.anotacoesDominio

import org.apoiasuas.formulario.CampoFormulario

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.FIELD])
@interface InfoPropriedadeDominio {
    static final CampoFormulario.Tipo TIPO_DEFAULT = CampoFormulario.Tipo.TEXTO
    static final int TAMANHO_DEFAULT = 60
    String codigo()
    String descricao() default ''
    CampoFormulario.Tipo tipo() default CampoFormulario.Tipo.TEXTO
    int tamanho() default 60
    boolean atualizavel() default true
}

