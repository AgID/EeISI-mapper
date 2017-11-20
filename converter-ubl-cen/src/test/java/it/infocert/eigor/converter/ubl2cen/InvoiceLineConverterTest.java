package it.infocert.eigor.converter.ubl2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
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
        document = makeDocumentWithAnInvoiceLineAndTypeCode130();
        cenInvoice = new BG0000Invoice();
    }

    @Test
    public void shouldMapToBT0128IfInvoiceLineDocumentReferenceTypeCodeIs130() throws Exception {
        InvoiceLineConverter converter = new InvoiceLineConverter();
        ConversionResult<BG0000Invoice> result = converter.toBG0025(document, cenInvoice, errors);

        assertTrue(result.hasResult());

        BG0000Invoice resultInvoice = result.getResult();
        BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier bt0128 = resultInvoice.getBG0025InvoiceLine().get(0).getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().get(0);

        assertTrue("001".equals(bt0128.getValue().getIdentifier()));
        assertTrue("321".equals(bt0128.getValue().getIdentificationSchema()));

    }

    private Document makeDocumentWithAnInvoiceLineAndTypeCode130() {

        Document document = new Document();
        Namespace defaultNs = Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2");
        Namespace cacNs = Namespace.getNamespace("cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
        Namespace cbcNs = Namespace.getNamespace("cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");

        Element rootElement = new Element("Invoice", defaultNs);
        rootElement.addNamespaceDeclaration(defaultNs);
        rootElement.addNamespaceDeclaration(cacNs);
        rootElement.addNamespaceDeclaration(cbcNs);
        document.setRootElement(rootElement);

        Element invoiceLine = new Element("InvoiceLine", cacNs);

        Element invoiceLineId = new Element("ID", cbcNs);
        invoiceLineId.setText("001");
        invoiceLine.addContent(invoiceLineId);

        Element documentReference = new Element("DocumentReference", cacNs);

        Element documentRefId = new Element("ID", cbcNs);
        documentRefId.setAttribute("schemeID", "321");
        documentRefId.setText("001");
        documentReference.addContent(documentRefId);

        Element documentTypeCode = new Element("DocumentTypeCode", cbcNs);
        documentTypeCode.setText("130");
        documentReference.addContent(documentTypeCode);

        invoiceLine.addContent(documentReference);

        rootElement.addContent(invoiceLine);


        return document;
    }
}