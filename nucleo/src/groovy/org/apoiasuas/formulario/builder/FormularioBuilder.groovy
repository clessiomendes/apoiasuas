package org.apoiasuas.formulario.builder

import org.apache.commons.io.IOUtils
import org.apoiasuas.anotacoesDominio.InfoPropriedadeDominio
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.Formulario
import org.apoiasuas.formulario.ModeloFormulario
import org.apoiasuas.formulario.template.FoolTemplate

abstract class BaseFormularioBuilder {
    ArrayList<CampoFormulario> camposBuilder = []

    void campoBancoDeDados( @DelegatesTo(value = CampoFormularioBuilder, strategy = groovy.lang.Closure.DELEGATE_FIRST) groovy.lang.Closure closure) {
        CampoFormulario campoFormulario = new CampoFormularioBuilder(closure).build()
        if (campoFormulario.origem?.avulso)
            throw new RuntimeException("Tipo inválido para o campo de banco de dados '${campoFormulario.codigo}'. Use 'campoAvulso'")
        camposBuilder.add(campoFormulario)
    }

    void campoAvulso( @DelegatesTo(value = CampoAvulsoFormularioBuilder, strategy = groovy.lang.Closure.DELEGATE_FIRST) groovy.lang.Closure closure) {
        CampoFormulario campoFormulario = new CampoAvulsoFormularioBuilder(closure).build()
        campoFormulario.origem = CampoFormulario.Origem.AVULSO
        //Valores default
        if (! campoFormulario.tipo)
            campoFormulario.tipoPersonalizado = InfoPropriedadeDominio.TIPO_DEFAULT
        if (! campoFormulario.tamanho)
            campoFormulario.tamanhoPersonalizado = InfoPropriedadeDominio.TAMANHO_DEFAULT
        camposBuilder.add(campoFormulario)
    }

    void campoDetalhes( @DelegatesTo(value = CampoDetalhesFormularioBuilder, strategy = groovy.lang.Closure.DELEGATE_FIRST) groovy.lang.Closure closure) {
        CampoFormulario campoFormulario = new CampoDetalhesFormularioBuilder(closure).build()
        if (! campoFormulario.origem?.cidadao && ! campoFormulario.origem?.familia)
            throw new RuntimeException("Origem inválida para o campo de detalhes '${campoFormulario.codigo}'. Use 'CIDADAO' ou 'FAMILIA' ")
        //Valores default
        if (! campoFormulario.tipo)
            campoFormulario.tipoPersonalizado = InfoPropriedadeDominio.TIPO_DEFAULT
        if (! campoFormulario.tamanho)
            campoFormulario.tamanhoPersonalizado = InfoPropriedadeDominio.TAMANHO_DEFAULT
        camposBuilder.add(campoFormulario)
    }

}

class GrupoCamposBuilder extends BaseFormularioBuilder {
    GrupoCamposBuilder(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.rehydrate(this, closure.owner, closure).call()
    }

    ArrayList<CampoFormulario> build() { camposBuilder }
}

class FormularioBuilder extends BaseFormularioBuilder {
    Formulario formulario

    FormularioBuilder(Closure closure) {
        formulario = new Formulario()
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.rehydrate(this, closure.owner, closure).call()
    }

    void grupo(String nome, @DelegatesTo(value = GrupoCamposBuilder, strategy = groovy.lang.Closure.DELEGATE_FIRST) groovy.lang.Closure closure) {
        if (! nome)
            throw new RuntimeException("Favor designar um nome para o grupo")
        ArrayList<CampoFormulario> camposGrupo = new GrupoCamposBuilder(closure).build()
        camposGrupo.each { it.grupo = nome }
        camposBuilder.addAll(camposGrupo)
    }

    void importaCampos(Class classe) {
        Closure closure = classe.newInstance().run()
        Formulario formularioTemp = new FormularioBuilder(closure).build()
        formularioTemp.campos.each { campoTemp ->
//            if (! camposBuilder.find { it.caminhoCampo == campoTemp.caminhoCampo })
            boolean campoJaInserido = false
            camposBuilder.each { campo ->
                if (campo.origem == campoTemp.origem && campo.codigo == campoTemp.codigo)
                    campoJaInserido = true
            }
            if (! campoJaInserido)
                camposBuilder << campoTemp
        }
    }

    void nome(String valor) { formulario.nome = valor }

    void tipo(String valor) { formulario.tipo = valor }

    void descricao(String valor) { formulario.descricao = valor }

//    void template(Map valor) {
    void template(String[] valor) {
        valor.eachWithIndex { String nomeArquivo, Integer i ->
            InputStream stream = FoolTemplate.class.getResourceAsStream(nomeArquivo)
            if (stream) {
                //Exclui a extensao do arquivo da descricao
                final int fim = nomeArquivo.indexOf(".") == -1 ? nomeArquivo.length()-1 : nomeArquivo.indexOf(".");
                ModeloFormulario modelo = new ModeloFormulario(descricao: nomeArquivo.substring(0, fim),
                        padrao: i==0, arquivo: IOUtils.toByteArray(stream), formulario: formulario);
                formulario.modelos << modelo
            } else {
                System.out.println("Atenção! Template do formulário ${formulario.nome} não encontrado (${nomeArquivo})")
            }
        }
//        System.out.println("valor2 "+valor2);
/*
*/


//        else
//            log.debug("template do formulário ${formulario.nome} não encontrado (${valor})")
    }

    Formulario build() {
        int ordem = 1
        camposBuilder.each {
            it.ordem = ordem++
            it.formulario = this.formulario
        }
        formulario.campos = []
        formulario.campos.addAll(camposBuilder)
        return formulario
    }
}
