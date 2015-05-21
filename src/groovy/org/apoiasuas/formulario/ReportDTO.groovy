package org.apoiasuas.formulario

import fr.opensagres.xdocreport.document.IXDocReport
import fr.opensagres.xdocreport.template.IContext
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata

class ReportDTO {
    IXDocReport report
    String nomeArquivo
    IContext context
    FieldsMetadata fieldsMetadata
}
