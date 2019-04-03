
package it.infocert.eigor.converter.common.cen2peppol;

import com.google.common.collect.Lists;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.commons.cen2peppol.InvoiceNoteConverter;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

public class InvoiceNoteConverterTest {

    private BG0000Invoice invoice;
    private Document doc;
    private InvoiceNoteConverter sut;

    @Before
    public void setUp() {
        invoice = new BG0000Invoice();
        invoice.getBG0001InvoiceNote().add(new BG0001InvoiceNote());
        invoice.getBG0002ProcessControl().add(new BG0002ProcessControl());
        invoice.getBG0001InvoiceNote(0).getBT0021InvoiceNoteSubjectCode().add(new BT0021InvoiceNoteSubjectCode("bt0021"));
        invoice.getBG0001InvoiceNote(0).getBT0022InvoiceNote().add(new BT0022InvoiceNote("bt0022"));


        this.doc = new Document(new Element("Invoice"));
        this.sut = new InvoiceNoteConverter();
    }

    @Test
    public void shouldMapDefault() {
        sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.PEPPOL_OUT, null);
        Element name = doc.getRootElement().getChild("Note");
        System.out.println(name.getText());

//		 assertEquals("3", name.getText());
    }
}