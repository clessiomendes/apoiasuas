package org.apoiasuas.anotacoesDominio

import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.Formulario

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE])
@interface InfoClasseDominio {
    CampoFormulario.Origem codigo();
    String descricaoI18N() default ""
    String descricaoDefault() default ""
//    int ordem() default 99999
}

