package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.AttachmentToFileReferenceConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.model.core.model.*;
import org.assertj.core.util.Lists;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class NewApplicableHeaderTradeAgreementConverterTest {
    private final Namespace rsmNs = Namespace.getNamespace("rsm", "urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100");
    private final Namespace ramNs = Namespace.getNamespace("ram", "urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100");
    private final Namespace qdtNs = Namespace.getNamespace("qdt", "urn:un:unece:uncefact:data:standard:QualifiedDataType:100");
    private final Namespace udtNs = Namespace.getNamespace("udt", "urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100");
    private final Namespace xsiNs = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

    @Test
    public void test() {
        NewApplicableHeaderTradeAgreementConverter converter = new NewApplicableHeaderTradeAgreementConverter();
        BG0000Invoice invoice = createInvoiceWithBG0024();
        Document document = createInvoiceWithRootNode();
        converter.map(invoice, document, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.CII_OUT, null);

        Element supplyChainTradeTransaction = document.getRootElement().getChild("SupplyChainTradeTransaction", rsmNs);
        assertNotNull(supplyChainTradeTransaction);

        Element applicableHeaderTradeAgreement = supplyChainTradeTransaction.getChild("ApplicableHeaderTradeAgreement", ramNs);
        assertNotNull(applicableHeaderTradeAgreement);

        List<Element> additionalReferencedDocuments = applicableHeaderTradeAgreement.getChildren("AdditionalReferencedDocument", ramNs);
        assertThat(additionalReferencedDocuments.size(), is(1));

        Element additionalReferencedDocument = additionalReferencedDocuments.get(0);

        Element issuerAssignedID = additionalReferencedDocument.getChild("IssuerAssignedID", ramNs);
        assertThat(issuerAssignedID.getText(), is("TESTID"));

        Element name = additionalReferencedDocument.getChild("Name", ramNs);
        assertThat(name.getText(), is("TEST"));

        Element uriid = additionalReferencedDocument.getChild("URIID", ramNs);
        assertThat(uriid.getText(), is("URITEST"));

        Element attachmentBinaryObject = additionalReferencedDocument.getChild("AttachmentBinaryObject", ramNs);
        assertThat(attachmentBinaryObject.getText(), is("SlZCRVJpMHhMalVOQ2lVTkNqRWdNQ0J2WW1vT3k2SlRaWDliY2dSVnhJVkcuLi50Ykxvc0NoVTJYUmY5eGIvb21zY2dUWS9sWEVoVWI="));

        Attribute mimeCode = attachmentBinaryObject.getAttribute("mimeCode");
        assertNotNull(mimeCode);
        assertThat(mimeCode.getValue(), is("application/pdf"));

        Attribute filename = attachmentBinaryObject.getAttribute("filename");
        assertNotNull(filename);
        assertThat(filename.getValue(), is("test.pdf"));
    }

    private BG0000Invoice createInvoiceWithBG0024() {
        BG0000Invoice invoice = new BG0000Invoice();
        BG0024AdditionalSupportingDocuments bg0024 = new BG0024AdditionalSupportingDocuments();

        BT0122SupportingDocumentReference bt0122 = new BT0122SupportingDocumentReference("TESTID");
        bg0024.getBT0122SupportingDocumentReference().add(bt0122);

        BT0123SupportingDocumentDescription bt0123 = new BT0123SupportingDocumentDescription("TEST");
        bg0024.getBT0123SupportingDocumentDescription().add(bt0123);

        BT0124ExternalDocumentLocation bt0124 = new BT0124ExternalDocumentLocation("URITEST");
        bg0024.getBT0124ExternalDocumentLocation().add(bt0124);

        TypeConverter<Element, FileReference> strToBinConverter = AttachmentToFileReferenceConverter.newConverter(DefaultEigorConfigurationLoader.configuration(), ErrorCode.Location.CII_OUT);
        Element fakeContent = new Element("FakeContent");
        fakeContent.setAttribute("mimeCode", "application/pdf");
        fakeContent.setAttribute("filename", "test.pdf");
        fakeContent.setText("SlZCRVJpMHhMalVOQ2lVTkNqRWdNQ0J2WW1vT3k2SlRaWDliY2dSVnhJVkcuLi50Ykxvc0NoVTJYUmY5eGIvb21zY2dUWS9sWEVoVWI=");
        BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename bt0125 = null;
        try {
            bt0125 = new BT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename(strToBinConverter.convert(fakeContent));
        } catch (ConversionFailedException e) {
            throw new RuntimeException(e);
        }
        bg0024.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().add(bt0125);

        invoice.getBG0024AdditionalSupportingDocuments().add(bg0024);
        return invoice;
    }

    private Document createInvoiceWithRootNode() {
        Element root = new Element("CrossIndustryInvoice", rsmNs);
        root.addNamespaceDeclaration(rsmNs);
        root.addNamespaceDeclaration(ramNs);
        root.addNamespaceDeclaration(qdtNs);
        root.addNamespaceDeclaration(udtNs);
        root.addNamespaceDeclaration(xsiNs);
        root.setAttribute("schemaLocation", "urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100 CrossIndustryInvoice_100pD16B.xsd", xsiNs);
        return new Document(root);
    }
}
