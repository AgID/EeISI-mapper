package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;
import org.assertj.core.util.Lists;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Test;

import static org.junit.Assert.*;

public class InvoiceLineConverterTest {
    private final Namespace rsmNs = Namespace.getNamespace("rsm", "urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100");
    private final Namespace ramNs = Namespace.getNamespace("ram", "urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100");
    private final Namespace qdtNs = Namespace.getNamespace("qdt", "urn:un:unece:uncefact:data:standard:QualifiedDataType:100");
    private final Namespace udtNs = Namespace.getNamespace("udt", "urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100");
    private final Namespace xsiNs = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");


    @Test
    public void test() {
        AdditionalSupportingDocumentsConverter converter = new AdditionalSupportingDocumentsConverter();
        BG0000Invoice invoice = createInvoiceWithBG0025();
        Document document = createInvoiceWithRootNode();
        converter.map(invoice, document, Lists.<IConversionIssue>newArrayList());

        Element supplyChainTradeTransaction = document.getRootElement().getChild("SupplyChainTradeTransaction", rsmNs);
        assertNotNull(supplyChainTradeTransaction);
    }


    private BG0000Invoice createInvoiceWithBG0025() {
        BG0000Invoice invoice = new BG0000Invoice();
        BG0025InvoiceLine bg0025 = new BG0025InvoiceLine();








        invoice.getBG0025InvoiceLine().add(bg0025);
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