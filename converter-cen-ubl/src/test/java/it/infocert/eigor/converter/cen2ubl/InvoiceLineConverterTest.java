package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;
import it.infocert.eigor.model.core.model.BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class InvoiceLineConverterTest {
    private BG0000Invoice cenInvoice;
    private Document document;
    private List<IConversionIssue> errors;

    @Before
    public void setUp() throws Exception {
        errors = new ArrayList<>();
        document = new Document(new Element("Invoice", Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2")));
        cenInvoice = makeCenInvoiceWithBT0128();
    }

    private BG0000Invoice makeCenInvoiceWithBT0128() {
        BG0000Invoice invoice = new BG0000Invoice();

        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();
        BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier bt0128 = new BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier(new Identifier("321", "001"));
        invoiceLine.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().add(bt0128);

        invoice.getBG0025InvoiceLine().add(invoiceLine);
        return invoice;
    }

    @Test
    public void invoiceLineWithBT0128shouldHaveDocumentReferenceAndTypeCode() throws Exception {
        InvoiceLineConverter converter = new InvoiceLineConverter();
        converter.map(cenInvoice, document, errors);

        Element rootElement = document.getRootElement();
        Element invoiceLine = rootElement.getChild("InvoiceLine");

        Element documentReference = invoiceLine.getChild("DocumentReference");

        Element id = documentReference.getChild("ID");
        assertTrue("001".equals(id.getText()));
        assertTrue("321".equals(id.getAttribute("schemeID").getValue()));

        Element documentTypeCode = documentReference.getChild("DocumentTypeCode");
        assertTrue("130".equals(documentTypeCode.getText()));

    }
}