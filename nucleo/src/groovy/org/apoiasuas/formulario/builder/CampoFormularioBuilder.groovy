package org.apoiasuas.formulario.builder

import org.apoiasuas.formulario.CampoFormulario

class CampoFormularioBuilder {
    CampoFormulario campoFormulario = new CampoFormulario()

    CampoFormularioBuilder(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.rehydrate(this, closure.owner, closure).call()
    }

    void origem(CampoFormulario.Origem valor) { campoFormulario.origem = valor }

    void codigo(String valor) { campoFormulario.codigo = valor }

    void descricao(String valor) { campoFormulario.descricaoPersonalizada = valor }

    void obrigatorio(Boolean valor) { campoFormulario.obrigatorio = valor }

    void listaLogradourosCidadaos(Boolean valor) { campoFormulario.listaLogradourosCidadaos = valor }

    CampoFormulario build() {
        campoFormulario
    }
}
