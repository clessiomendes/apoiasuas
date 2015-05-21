import com.sun.org.apache.xerces.internal.jaxp.SAXParserImpl
import fr.opensagres.xdocreport.core.document.SyntaxKind
import fr.opensagres.xdocreport.document.IXDocReport
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry
import fr.opensagres.xdocreport.template.IContext
import fr.opensagres.xdocreport.template.TemplateEngineKind
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata
import org.xml.sax.XMLReader

import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

target(testeXDocReport: "The description of the script goes here!") {
//    System.setProperty("org.xml.sax.driver", org.apache.crimson.parser.XMLReaderImpl.name)

    // 1) Load Docx file by filling Velocity template engine and cache
    // it to the registry
    InputStream entrada = this.class.getResourceAsStream("Encaminhamento-Template.docx");
    IXDocReport report = XDocReportRegistry.getRegistry().loadReport(entrada, TemplateEngineKind.Velocity);

    // 2) Create fields metadata to manage text styling
    FieldsMetadata metadata = report.createFieldsMetadata();
    metadata.addFieldAsTextStyling("comments",
            SyntaxKind.Html);

    // 3) Create context Java model
    IContext context = report.createContext();
    context.put("comments",
            "<i>Text</i> coming from <b>Java context</b>.");

    // 4) Generate report by merging Java model with the Docx
    OutputStream out = new FileOutputStream(new File(
            "DocxTextStylingWithVelocity_Out.docx"));
    report.process(context, out);
}

setDefaultTarget(testeXDocReport)
