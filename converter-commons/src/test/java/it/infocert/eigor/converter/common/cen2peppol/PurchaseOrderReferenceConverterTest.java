package it.infocert.eigor.converter.common.cen2peppol;

import com.google.common.collect.Lists;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.commons.cen2peppol.PurchaseOrderReferenceConverter;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0013PurchaseOrderReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PurchaseOrderReferenceConverterTest {

    private BG0000Invoice invoice;
    private Document doc;
    private PurchaseOrderReferenceConverter sut;

    @Before
    public void setUp() {
        invoice = new BG0000Invoice();
        this.doc = new Document(new Element("Invoice"));
        this.sut = new PurchaseOrderReferenceConverter();
        BT0013PurchaseOrderReference bt013 = new BT0013PurchaseOrderReference("Hello");
        invoice.getBT0013PurchaseOrderReference().add(bt013);

    }

    @Ignore
    @Test
    public void shouldMapOrderReference() {
        sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.PEPPOL_OUT, null);
        // Element buyerRef = doc.getRootElement().getChild("BuyerReference").getChild("ID");
        Element orderRef = doc.getRootElement().getChild("OrderReference");
        //System.out.println(buyerRef.getText());
        System.out.println(orderRef.getText());


    }
}
