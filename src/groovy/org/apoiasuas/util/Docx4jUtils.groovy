package org.apoiasuas.util

import org.docx4j.jaxb.Context
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.BooleanDefaultTrue
import org.docx4j.wml.CTSimpleField
import org.docx4j.wml.P
import org.docx4j.wml.R
import org.docx4j.wml.Tbl
import org.docx4j.wml.Tc
import org.docx4j.wml.Tr

import javax.xml.bind.JAXBElement

/**
 * Created by admin on 18/04/2015.
 */
class Docx4jUtils {

    static MainDocumentPart adicionaTabela(List content, MainDocumentPart mainPart) {
        org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
        Tbl table = factory.createTbl();
        content.each { linha ->
            Tr tableRow = factory.createTr();
            linha.each { celula->
                Tc tableCell = factory.createTc();
                if (celula instanceof String) //objeto ainda nao criado. Criar agora
                    celula = mainPart.createParagraphOfText(celula);
                tableCell.getContent().add(celula);
                tableRow.getContent().add(tableCell);
            }
            table.getContent().add(tableRow);
        }
        mainPart.addObject(table);
        return mainPart
    }

    static org.docx4j.wml.Body geraMergeField(String nome) {

        org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();

        CTSimpleField ctSimple = factory.createCTSimpleField();
        ctSimple.setInstr(" MergeField ${nome} \\* MERGEFORMAT ")

        org.docx4j.wml.RPr RPr = factory.createRPr();
        RPr.setNoProof(new BooleanDefaultTrue());

        org.docx4j.wml.Text t = factory.createText();
        t.setValue(nome);

        R run = factory.createR();
        run.getRunContent().add(RPr);
        run.getRunContent().add(t);

        ctSimple.getParagraphContent().add(run);

        JAXBElement<CTSimpleField> fldSimple = factory.createPFldSimple(ctSimple);

        P paragraph = factory.createP();
        paragraph.getParagraphContent().add(fldSimple);


        org.docx4j.wml.Body body = factory.createBody();
        body.getEGBlockLevelElts().add(paragraph);
//        mainPart.addObject(body);

        return body
    }



}
