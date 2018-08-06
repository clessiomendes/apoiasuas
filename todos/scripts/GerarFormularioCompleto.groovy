import org.apoiasuas.anotacoesDominio.InfoPropriedadeDominio

import org.apoiasuas.formulario.CampoFormulario
import org.apoiasuas.formulario.definicao.FormularioBase

import java.lang.reflect.Field

/**
 * Created by admin on 18/04/2015.
 */

System.out.println("package ${FormularioBase.package}\n" +
        "\n" +
        "import ${groovy.transform.TypeChecked.class.name}\n" +
        "import ${org.apoiasuas.formulario.CampoFormulario.class.name}\n" +
        "import ${CampoFormulario.Tipo.class.name}\n" +
        "\n" +
        "class FormularioXXXXXX extends FormularioBase {\n" +
        "    @TypeChecked\n" +
        "    Closure run() {\n" +
        "        formulario {\n" +
        "            nome 'XXXXXX'\n" +
        "            descricao 'XXXXXX'\n" +
        "            template 'XXXXXX-Template.docx'")

System.out.println "//Campos preenchidos à partir do cadastro. Cada código está atrelado a um campo do cadastro"
camposPersistentes(CampoFormulario.Origem.CIDADAO)
//System.out.println "//Campos de família"
camposPersistentes(CampoFormulario.Origem.FAMILIA)
//System.out.println "//Campos de endereço"
System.out.println("          grupo 'Endereço', {\n")
camposPersistentes(CampoFormulario.Origem.ENDERECO)
System.out.println("          }\n")
System.out.println "//Campos preenchidos à partir de valores default"
campoAvulso(CampoFormulario.class.simpleName+".CODIGO_DATA_PREENCHIMENTO",
        CampoFormulario.Tipo.DATA, null, "Data (preenchimento)")
campoAvulso(CampoFormulario.class.simpleName+".CODIGO_RESPONSAVEL_PREENCHIMENTO",
        CampoFormulario.Tipo.TEXTO, 60, "Responsável (preenchimento)")
System.out.println "//Campo avulso de exemplo"
campoAvulso("'exemplo_campo_avulso'", CampoFormulario.Tipo.TEXTO, 60, "Campo livre, não presente do cadastro")

System.out.println("        }\n" +
        "    }\n" +
        "}")

private void camposPersistentes(CampoFormulario.Origem origem) {
    origem.classeDominio?.getDeclaredFields().each {
        InfoPropriedadeDominio info = it.getAnnotation(InfoPropriedadeDominio.class)
        if (info) {
            System.out.println("            campoBancoDeDados {\n" +
                    "                origem CampoFormulario.Origem.${origem.toString()}\n" +
                    "                codigo '${info.codigo()}'\n" +
                    "            }")
        }
    }
}

def campoAvulso(String codigo, CampoFormulario.Tipo tipo, Integer tamanho, String descricao) {
    System.out.println("            campoAvulso {\n" +
//            "                origem CampoFormulario.Origem.${CampoFormulario.Origem.AVULSO.toString()}\n" +
            "                codigo ${codigo}\n" +
            "                descricao '${descricao}'\n" +
            "                tipo ${CampoFormulario.Tipo.class.simpleName}.${tipo.toString()}\n" +
            (tamanho ? "                tamanho ${tamanho}\n" : "") +
            "            }")
}