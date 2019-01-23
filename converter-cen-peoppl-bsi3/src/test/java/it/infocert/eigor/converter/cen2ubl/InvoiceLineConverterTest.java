package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.cen2peoppl.InvoiceLineConverter;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;
import it.infocert.eigor.model.core.model.BG0029PriceDetails;
import it.infocert.eigor.model.core.model.BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0129InvoicedQuantity;
import it.infocert.eigor.model.core.model.BT0130InvoicedQuantityUnitOfMeasureCode;
import it.infocert.eigor.model.core.model.BT0149ItemPriceBaseQuantity;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InvoiceLineConverterTest {
    private Document document;

    @Before
    public void setUp() throws Exception {
        document = new Document(new Element("Invoice", Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2")));
    }

    @Test
    public void invoiceLineWithBT0128shouldHaveDocumentReferenceAndTypeCode() throws Exception {
        BG0000Invoice cenInvoice = makeCenInvoiceWithBT0128();
        InvoiceLineConverter converter = new InvoiceLineConverter();
        converter.map(cenInvoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.UBL_OUT);

        Element rootElement = document.getRootElement();
        Element invoiceLine = rootElement.getChild("InvoiceLine");

        Element documentReference = invoiceLine.getChild("DocumentReference");

        Element id = documentReference.getChild("ID");
        assertTrue("001".equals(id.getText()));
        assertTrue("321".equals(id.getAttribute("schemeID").getValue()));

        Element documentTypeCode = documentReference.getChild("DocumentTypeCode");
        assertTrue("130".equals(documentTypeCode.getText()));
    }

    @Test
    public void invoiceLineWithBT0149shouldQuantityDividedByBaseQuantity() throws Exception {
        BG0000Invoice cenInvoice = makeCenInvoiceWithBT0129AndBT0149();
        InvoiceLineConverter converter = new InvoiceLineConverter();
        converter.map(cenInvoice, document, new ArrayList<IConversionIssue>(), ErrorCode.Location.UBL_OUT);

        Element rootElement = document.getRootElement();
        Element invoiceLine = rootElement.getChild("InvoiceLine");

        Element invoicedQuantity = invoiceLine.getChild("InvoicedQuantity");

        assertTrue(invoicedQuantity.getText().equals("1.00000000"));
    }

    @Test
    public void invoiceLineWithBT130AndBT150Empty() {
        final BG0000Invoice cenInvoice = makeCenInvoiceWithBT0130AndBT0150();

        InvoiceLineConverter converter = new InvoiceLineConverter();
        converter.map(cenInvoice, document, new ArrayList<>(), ErrorCode.Location.UBL_OUT);

        Element rootElement = document.getRootElement();
        Element invoiceLine = rootElement.getChild("InvoiceLine");

        final Attribute attribute = invoiceLine.getChild("Price").getChild("BaseQuantity").getAttribute("unitCode");
        assertEquals(UnitOfMeasureCodes.TOTE_TE.getCommonCode(), attribute.getValue());
    }


    private BG0000Invoice makeCenInvoiceWithBT0128() {
        BG0000Invoice invoice = new BG0000Invoice();

        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();
        BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier bt0128 = new BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier(new Identifier("321", "001"));
        invoiceLine.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().add(bt0128);

        invoice.getBG0025InvoiceLine().add(invoiceLine);
        return invoice;
    }

    private BG0000Invoice makeCenInvoiceWithBT0129AndBT0149() {
        BG0000Invoice invoice = new BG0000Invoice();

        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();
        BT0129InvoicedQuantity bt0129InvoicedQuantity = new BT0129InvoicedQuantity(new BigDecimal(2));
        invoiceLine.getBT0129InvoicedQuantity().add(bt0129InvoicedQuantity);

        BG0029PriceDetails bg0029PriceDetails = new BG0029PriceDetails();
        BT0149ItemPriceBaseQuantity bt0149ItemPriceBaseQuantity = new BT0149ItemPriceBaseQuantity(new BigDecimal(2));
        bg0029PriceDetails.getBT0149ItemPriceBaseQuantity().add(bt0149ItemPriceBaseQuantity);
        invoiceLine.getBG0029PriceDetails().add(bg0029PriceDetails);

        invoice.getBG0025InvoiceLine().add(invoiceLine);
        return invoice;
    }

    private BG0000Invoice makeCenInvoiceWithBT0130AndBT0150() {
        BG0000Invoice invoice = makeCenInvoiceWithBT0129AndBT0149();

        BG0025InvoiceLine invoiceLine = invoice.getBG0025InvoiceLine().get(0);
        final BT0130InvoicedQuantityUnitOfMeasureCode bt130 = new BT0130InvoicedQuantityUnitOfMeasureCode(UnitOfMeasureCodes.TOTE_TE);
        invoiceLine.getBT0130InvoicedQuantityUnitOfMeasureCode().add(bt130);

        //invoice.getBG0025InvoiceLine().add(invoiceLine);
        return invoice;
    }
}
