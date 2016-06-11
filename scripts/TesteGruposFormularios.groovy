import groovy.transform.TypeChecked
import org.apoiasuas.formulario.definicao.FormularioBase
import org.apoiasuas.formulario.definicao.FormularioCertidoes
import org.apoiasuas.bootstrap.FormularioTesteGrupos
import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.Formulario
import org.apoiasuas.formulario.builder.FormularioBuilder

class FormularioTeste2 extends FormularioBase {
    @TypeChecked
    Closure run() {
        formulario {
            nome 'teste de importacao'
            template "Identidade-Template.docx"
            importaCampos FormularioCertidoes.class
            campoBancoDeDados {
                origem CampoFormulario.Origem.CIDADAO
                codigo 'cpf'
            }
        }
    }
}


//includeTargets << grailsScript("_GrailsInit")
try {
    Closure closure = FormularioTeste2.newInstance().run()
    Formulario formulario = new FormularioBuilder(closure).build()
    println formulario
} catch (Throwable t) {
    t.printStackTrace()
}