package it.infocert.eigor.converter.ublcn2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0011ProjectReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ProjectReferenceConverterTest {

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
    public void shouldMapToBT0011() {
        ProjectReferenceConverter converter = new ProjectReferenceConverter();
        ConversionResult<BG0000Invoice> result = converter.toBG0011(document, cenInvoice, errors,  ErrorCode.Location.UBLCN_IN);

        assertTrue(result.hasResult());

        BG0000Invoice resultInvoice = result.getResult();
        final BT0011ProjectReference bt0011 = resultInvoice.getBT0011ProjectReference().get(0);

        assertTrue("bt11".equals(bt0011.getValue()));

    }

    private Document makeDocumentWithAnInvoiceLineAndTypeCode130() {

        Document document = new Document();
        Namespace defaultNs = Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2");
        Namespace cacNs = Namespace.getNamespace("cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
        Namespace cbcNs = Namespace.getNamespace("cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");

        Element rootElement = new Element("CreditNote", defaultNs);
        rootElement.addNamespaceDeclaration(defaultNs);
        rootElement.addNamespaceDeclaration(cacNs);
        rootElement.addNamespaceDeclaration(cbcNs);
        document.setRootElement(rootElement);

        Element additionalDocument = new Element("AdditionalDocumentReference", cacNs);

        Element additionalDocumentId = new Element("ID", cbcNs);
        additionalDocumentId.setText("bt11");
        additionalDocument.addContent(additionalDocumentId);

        Element documentTypeCode = new Element("DocumentTypeCode", cbcNs);
        documentTypeCode.setText("50");
        additionalDocument.addContent(documentTypeCode);

        rootElement.addContent(additionalDocument);


        return document;
    }
}
