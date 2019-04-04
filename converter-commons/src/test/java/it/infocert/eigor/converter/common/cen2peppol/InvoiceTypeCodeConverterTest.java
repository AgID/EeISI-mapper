package it.infocert.eigor.converter.common.cen2peppol;

import com.google.common.collect.Lists;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.commons.cen2ubl.InvoiceTypeCodeConverter;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0003InvoiceTypeCode;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InvoiceTypeCodeConverterTest {


    private BG0000Invoice invoice;
    private Document doc;
    private InvoiceTypeCodeConverter sut;

    @Before
    public void setUp() {
        invoice = new BG0000Invoice();
        BT0003InvoiceTypeCode bt03 = new BT0003InvoiceTypeCode(Untdid1001InvoiceTypeCode.Code130);
        invoice.getBT0003InvoiceTypeCode().add(bt03);
        this.doc = new Document(new Element("Invoice"));
        this.sut = new InvoiceTypeCodeConverter();
    }

    @Test
    public void shouldMapDefaultTypeConverter() {
        sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.PEPPOL_OUT, null);
        Element name = doc.getRootElement().getChild("InvoiceTypeCode");
        assertEquals("130", name.getText());
    }

}
