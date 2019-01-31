package it.infocert.eigor.converter.commons.cen2ubl;

import com.google.common.collect.Lists;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import static it.infocert.eigor.converter.commons.cen2ubl.Scenarios.invoiceWithAmounts;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LegalMonetaryTotalConverterTest {

    private BG0000Invoice invoice;

    @Before
    public void setUp() {
        this.invoice = invoiceWithAmounts();
    }

    @Test
    public void shouldAddAttributeToAllChildren() throws Exception {
        LegalMonetaryTotalConverter sut = new LegalMonetaryTotalConverter();
        Document doc = new Document(new Element("Invoice"));
        sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.UBL_OUT, null);

        Element amount = doc.getRootElement().getChild("LegalMonetaryTotal");

        for (Element child : amount.getChildren()) {
            assertTrue(child.hasAttributes());
            assertEquals("EUR", child.getAttributeValue("currencyID"));
        }

    }


}
