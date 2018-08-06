import org.apoiasuas.bootstrap.FormularioTesteImportaCampos
import org.apoiasuas.formulario.Formulario
import org.apoiasuas.formulario.builder.FormularioBuilder

//includeTargets << grailsScript("_GrailsInit")
try {
    Closure closure = FormularioTesteImportaCampos.newInstance().run()
    Formulario formulario = new FormularioBuilder(closure).build()
    println formulario
} catch (Throwable t) {
    t.printStackTrace()
}