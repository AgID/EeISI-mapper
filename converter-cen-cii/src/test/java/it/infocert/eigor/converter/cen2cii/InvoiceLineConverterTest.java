package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.assertj.core.util.Lists;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class InvoiceLineConverterTest {
    private final Namespace rsmNs = Namespace.getNamespace("rsm", "urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100");
    private final Namespace ramNs = Namespace.getNamespace("ram", "urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100");
    private final Namespace qdtNs = Namespace.getNamespace("qdt", "urn:un:unece:uncefact:data:standard:QualifiedDataType:100");
    private final Namespace udtNs = Namespace.getNamespace("udt", "urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100");
    private final Namespace xsiNs = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

    private Document document;
    private InvoiceLineConverter converter;

    @Before
    public void setUp() throws Exception {
        document = createInvoiceWithRootNode();
        converter = new InvoiceLineConverter();
    }

    @Test
    public void ifBT0126OrBT0127ThenInvoiceWillHaveAssociatedDocumentLineDocument() {
        BG0000Invoice invoice = createInvoiceWithBT0126AndBT0127();
        converter.map(invoice, document, Lists.<IConversionIssue>newArrayList());

        Element supplyChainTradeTransaction = document.getRootElement().getChild("SupplyChainTradeTransaction", rsmNs);
        assertNotNull(supplyChainTradeTransaction);

        List<Element> includedSupplyChainTradeLineItems = supplyChainTradeTransaction.getChildren("IncludedSupplyChainTradeLineItem", ramNs);
        assertThat(includedSupplyChainTradeLineItems.size(), is(1));

        Element includedSupplyChainTradeLineItem = includedSupplyChainTradeLineItems.get(0);

        Element associatedDocumentLineDocument = includedSupplyChainTradeLineItem.getChild("AssociatedDocumentLineDocument", ramNs);

        Element lineID = associatedDocumentLineDocument.getChild("LineID", ramNs);
        assertThat(lineID.getText(), is("100"));

        Element includedNote = associatedDocumentLineDocument.getChild("IncludedNote", ramNs);
        Element content = includedNote.getChild("Content", ramNs);
        assertThat(content.getText(), is("TESTNOTE"));
    }

    @Test
    public void ifBT0128ThenInvoiceWillHaveAdditionalReferencedDocumentWithTypeCode130() {
        BG0000Invoice invoice = createInvoiceWithBT0128();
        converter.map(invoice, document, Lists.<IConversionIssue>newArrayList());

        Element supplyChainTradeTransaction = document.getRootElement().getChild("SupplyChainTradeTransaction", rsmNs);
        assertNotNull(supplyChainTradeTransaction);

        List<Element> includedSupplyChainTradeLineItems = supplyChainTradeTransaction.getChildren("IncludedSupplyChainTradeLineItem", ramNs);
        assertThat(includedSupplyChainTradeLineItems.size(), is(1));

        Element includedSupplyChainTradeLineItem = includedSupplyChainTradeLineItems.get(0);

        Element specifiedLineTradeSettlement = includedSupplyChainTradeLineItem.getChild("SpecifiedLineTradeSettlement", ramNs);
        Element additionalReferencedDocument = specifiedLineTradeSettlement.getChild("AdditionalReferencedDocument", ramNs);

        Element issuerAssignedID = additionalReferencedDocument.getChild("IssuerAssignedID", ramNs);
        assertThat(issuerAssignedID.getText(), is("TESTISSUERID"));

        Element typeCode = additionalReferencedDocument.getChild("TypeCode", ramNs);
        assertThat(typeCode.getText(),is("130"));

        Element referenceTypeCode = additionalReferencedDocument.getChild("ReferenceTypeCode", ramNs);
        assertThat(referenceTypeCode.getText(), is("ISTEST"));
    }


    private BG0000Invoice createInvoiceWithBT0126AndBT0127() {
        BG0000Invoice invoice = new BG0000Invoice();
        BG0025InvoiceLine bg0025 = new BG0025InvoiceLine();

        BT0126InvoiceLineIdentifier bt0126 = new BT0126InvoiceLineIdentifier("100");
        bg0025.getBT0126InvoiceLineIdentifier().add(bt0126);

        BT0127InvoiceLineNote bt0127 = new BT0127InvoiceLineNote("TESTNOTE");
        bg0025.getBT0127InvoiceLineNote().add(bt0127);


        invoice.getBG0025InvoiceLine().add(bg0025);
        return invoice;
    }

    private BG0000Invoice createInvoiceWithBT0128() {
        BG0000Invoice invoice = new BG0000Invoice();
        BG0025InvoiceLine bg0025 = new BG0025InvoiceLine();

        BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier bt0128 = new BT0128InvoiceLineObjectIdentifierAndSchemeIdentifier(new Identifier("ISTEST","TESTISSUERID"));
        bg0025.getBT0128InvoiceLineObjectIdentifierAndSchemeIdentifier().add(bt0128);

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