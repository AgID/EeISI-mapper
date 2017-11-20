package it.infocert.eigor.converter.ubl2cen;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0123SupportingDocumentDescription;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class AdditionalSupportingDocumentsConverterTest {

    private Document document;

    @Before
    public void setUp() throws Exception {
        document = makeDocumentWithAdditionalDocumentReferenceAndDocumentDescription();
    }


    @Test
    public void shouldHaveBT0123IfAdditionalDocumentReferenceHasDocumentDescription() throws Exception {
        AdditionalSupportingDocumentsConverter converter = new AdditionalSupportingDocumentsConverter();
        BG0000Invoice invoice = new BG0000Invoice();
        converter.map(invoice, document, new ArrayList<IConversionIssue>());
        BT0123SupportingDocumentDescription bt0123 = invoice.getBG0024AdditionalSupportingDocuments(0).getBT0123SupportingDocumentDescription(0);
        assertTrue("TEST".equals(bt0123.getValue()));
    }

    private Document makeDocumentWithAdditionalDocumentReferenceAndDocumentDescription() {
        Document document = new Document();
        Namespace defaultNs = Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2");
        Namespace cacNs = Namespace.getNamespace("cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
        Namespace cbcNs = Namespace.getNamespace("cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");

        Element rootElement = new Element("Invoice", defaultNs);
        rootElement.addNamespaceDeclaration(defaultNs);
        rootElement.addNamespaceDeclaration(cacNs);
        rootElement.addNamespaceDeclaration(cbcNs);
        document.setRootElement(rootElement);

        Element additionalDocumentReference = new Element("AdditionalDocumentReference", cacNs);

        Element documentReference = new Element("DocumentDescription", cbcNs);
        documentReference.setText("TEST");

        additionalDocumentReference.addContent(documentReference);
        rootElement.addContent(additionalDocumentReference);

        return document;
    }

}