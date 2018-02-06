package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0002ProcessControl;
import it.infocert.eigor.model.core.model.BT0024SpecificationIdentifier;
import org.assertj.core.util.Lists;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class DocumentContextConverterTest {
    private final Namespace rsmNs = Namespace.getNamespace("rsm", "urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100");
    private final Namespace ramNs = Namespace.getNamespace("ram", "urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100");
    private final Namespace qdtNs = Namespace.getNamespace("qdt", "urn:un:unece:uncefact:data:standard:QualifiedDataType:100");
    private final Namespace udtNs = Namespace.getNamespace("udt", "urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100");
    private final Namespace xsiNs = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

    private Document document;
    private DocumentContextConverter converter;

    @Before
    public void setUp() {
        document = createInvoiceWithRootNode();
        converter = new DocumentContextConverter();
    }

    @Test
    public void ifBT0024ThenInvoiceWillHaveGuidelineSpecifiedDocumentContextParameterId() {
        BG0000Invoice invoice = createInvoiceWithBT0024();
        converter.map(invoice, document, Lists.<IConversionIssue>newArrayList());

        Element exchangedDocumentContext = document.getRootElement().getChild("ExchangedDocumentContext", rsmNs);
        assertNotNull(exchangedDocumentContext);

        Element guidelineSpecifiedDocumentContextParameter = exchangedDocumentContext.getChild("GuidelineSpecifiedDocumentContextParameter", ramNs);
        assertNotNull(guidelineSpecifiedDocumentContextParameter);

        Element guidelineSpecifiedDocumentContextParameterId = guidelineSpecifiedDocumentContextParameter.getChild("ID", ramNs);
        assertThat(guidelineSpecifiedDocumentContextParameterId.getText(), is("TESTID"));
    }

    @Test
    public void ifNoBT0024ThenInvoiceWillHaveDefaultValueForGuidelineSpecifiedDocumentContextParameterId() {
        BG0000Invoice invoice = createInvoiceWithBG0002();
        converter.map(invoice, document, Lists.<IConversionIssue>newArrayList());

        Element exchangedDocumentContext = document.getRootElement().getChild("ExchangedDocumentContext", rsmNs);
        assertNotNull(exchangedDocumentContext);

        Element guidelineSpecifiedDocumentContextParameter = exchangedDocumentContext.getChild("GuidelineSpecifiedDocumentContextParameter", ramNs);
        assertNotNull(guidelineSpecifiedDocumentContextParameter);

        Element guidelineSpecifiedDocumentContextParameterId = guidelineSpecifiedDocumentContextParameter.getChild("ID", ramNs);
        assertThat(guidelineSpecifiedDocumentContextParameterId.getText(), is("urn:cen.eu:en16931:2017"));
    }

    private BG0000Invoice createInvoiceWithBT0024() {
        BG0000Invoice invoice = createInvoiceWithBG0002();

        BT0024SpecificationIdentifier bt0024 = new BT0024SpecificationIdentifier("TESTID");
        invoice.getBG0002ProcessControl(0).getBT0024SpecificationIdentifier().add(bt0024);

        return invoice;
    }

    private BG0000Invoice createInvoiceWithBG0002() {
        BG0000Invoice invoice = new BG0000Invoice();
        BG0002ProcessControl bg0002 = new BG0002ProcessControl();
        invoice.getBG0002ProcessControl().add(bg0002);
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