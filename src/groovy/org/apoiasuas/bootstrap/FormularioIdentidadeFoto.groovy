package org.apoiasuas.bootstrap

import groovy.transform.TypeChecked
import org.apoiasuas.formulario.FormularioBeneficioEventualService
import org.apoiasuas.formulario.FormularioService

class FormularioIdentidadeFoto extends FormularioBase {

    @Override
    public Class<? extends FormularioService> classeServico() { return FormularioBeneficioEventualService.class }

    @TypeChecked
    Closure run() {
        formulario {
            nome 'Guias de identidade e foto'
            tipo 'Benefícios Eventuais'
            descricao 'Formulário de gratuidade para segunda via de identidade e para fotos 3x4'
            template "IdentidadeFoto-Template.docx"
            importaCampos FormularioIdentidade
            importaCampos FormularioFotos
        }
    }
}
