import org.apoiasuas.util.Docx4jUtils
import org.docx4j.jaxb.Context
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.Tbl
import org.docx4j.wml.Tc
import org.docx4j.wml.Tr

WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage()
MainDocumentPart mainPart = wordMLPackage.getMainDocumentPart()
//mainPart.addParagraphOfText(formulario.nome)
//if (formulario.descricao)
//    mainPart.addParagraphOfText(formulario.descricao)
mainPart.addParagraphOfText("A seguir, estão listados os campos disponíveis para o formulário. " +
        "Copie e cole cada campo no local desejado. " +
        "Um mesmo campo pode ser repetido em mais de um local no formulário.")

org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
Tbl table = factory.createTbl();

0..3.each {
    Tr tableRow = factory.createTr();

    Tc tableCell = factory.createTc();
    tableCell.getContent().add(wordMLPackage.getMainDocumentPart().createParagraphOfText("conteúdo célula"));
    tableRow.getContent().add(tableCell);

    table.getContent().add(tableRow);
}


wordMLPackage.getMainDocumentPart().addObject(table);

wordMLPackage.save(new java.io.File("c://temp/HelloWord4.docx"));

//formulario.campos.each { campo ->
//    mainPart.addParagraphOfText(campo.descricaoDefault+":")
//    Docx4jUtils.adicionaMergeField(mainPart, campo.origem.toString() + "." + campo.codigoPropriedade)
//}
