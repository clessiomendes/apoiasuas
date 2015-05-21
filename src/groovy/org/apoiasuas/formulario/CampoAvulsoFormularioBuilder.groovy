package org.apoiasuas.formulario

class CampoAvulsoFormularioBuilder {
    CampoFormulario campoFormulario = new CampoFormulario()

    CampoAvulsoFormularioBuilder(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.rehydrate(this, closure.owner, closure).call()
    }

//    void origem(CampoFormulario.Origem valor) { campoFormulario.origem = valor }

    void codigo(String valor) { campoFormulario.codigo = valor }

    void descricao(String valor) { campoFormulario.descricaoPersonalizada = valor }

    void obrigatorio(Boolean valor) { campoFormulario.obrigatorio = valor }

    void tipo(CampoFormulario.Tipo valor) { campoFormulario.tipoPersonalizado = valor }

    void multiplasLinhas(int valor) { campoFormulario.multiplasLinhas = valor }

    void tamanho(Integer valor) { campoFormulario.tamanhoPersonalizado = valor }

    CampoFormulario build() {
        campoFormulario
    }
}
