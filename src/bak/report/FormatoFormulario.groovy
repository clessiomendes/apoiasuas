package org.apoiasuas.report

/**
 * Created by home64 on 07/02/2015.
 */
enum FormatoFormulario {
    DOCX("docx", "word"), XLSX("xlsx", "excel"), PDF("pdf", "pdf"), XHMTL("html", "tela")

    String extensaoArquivo;
    String displayLabel;

    FormatoFormulario(String extensaoArquivo, String displayLabel) {
        this.extensaoArquivo = extensaoArquivo
        this.displayLabel = displayLabel
    }

    static FormatoFormulario getFromExtensaoArquivo(String extensaoArquivo) {
        FormatoFormulario result = null
        values().each {
            if (it.extensaoArquivo == extensaoArquivo)
                result = it
        }
        return result
    }
}