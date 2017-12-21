package it.infocert.eigor.converter.commons.ubl2cen;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0122SupportingDocumentReference;
import it.infocert.eigor.model.core.model.BT0123SupportingDocumentDescription;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class AdditionalSupportingDocumentsConverterTest {


    @Test
    public void shouldHaveBT0123IfAdditionalDocumentReferenceHasDocumentDescription() throws Exception {
        Document document = makeDocumentWithAdditionalDocumentReferenceAndDocumentDescription();
        AdditionalSupportingDocumentsConverter converter = new AdditionalSupportingDocumentsConverter();
        BG0000Invoice invoice = new BG0000Invoice();
        converter.map(invoice, document, new ArrayList<IConversionIssue>());
        BT0123SupportingDocumentDescription bt0123 = invoice.getBG0024AdditionalSupportingDocuments(0).getBT0123SupportingDocumentDescription(0);
        assertTrue("TEST".equals(bt0123.getValue()));
    }

    @Test
    public void shouldHaveBT0122IfAdditionalDocumentReferenceHasDocumentTypeCode916() throws Exception {
        Document document = makeDocumentWithAdditionalDocumentReferenceAndIDAndDocumentTypeCode("916");
        AdditionalSupportingDocumentsConverter converter = new AdditionalSupportingDocumentsConverter();
        BG0000Invoice invoice = new BG0000Invoice();
        converter.map(invoice, document, new ArrayList<IConversionIssue>());
        BT0122SupportingDocumentReference bt0122 = invoice.getBG0024AdditionalSupportingDocuments(0).getBT0122SupportingDocumentReference(0);
        assertTrue("TESTID".equals(bt0122.getValue()));
    }

    @Test
    public void shouldNotHaveBT0122IfAdditionalDocumentReferenceHasDifferentDocumentTypeCode() throws Exception {
        Document document = makeDocumentWithAdditionalDocumentReferenceAndIDAndDocumentTypeCode("666");
        AdditionalSupportingDocumentsConverter converter = new AdditionalSupportingDocumentsConverter();
        BG0000Invoice invoice = new BG0000Invoice();
        converter.map(invoice, document, new ArrayList<IConversionIssue>());
        assertTrue(invoice.getBG0024AdditionalSupportingDocuments(0).getBT0122SupportingDocumentReference().size() == 0);
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

    private Document makeDocumentWithAdditionalDocumentReferenceAndIDAndDocumentTypeCode(String docType) {
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

        Element id = new Element("ID", cbcNs);
        id.setText("TESTID");
        Element documentTypeCode = new Element("DocumentTypeCode", cbcNs);
        documentTypeCode.setText(docType);

        additionalDocumentReference.addContent(id);
        additionalDocumentReference.addContent(documentTypeCode);
        rootElement.addContent(additionalDocumentReference);

        return document;
    }

}