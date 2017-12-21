package it.infocert.eigor.converter.commons.ubl2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BG0005SellerPostalAddress;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PostalAddressConverterTest {
    private BG0000Invoice cenInvoice;
    private Document document;

    @Before
    public void setUp() throws Exception {
        document = makeDocumentWithSupplierPostalAddressAndCountryItaly();
        cenInvoice = new BG0000Invoice();
        cenInvoice.getBG0004Seller().add(new BG0004Seller());
        cenInvoice.getBG0004Seller(0).getBG0005SellerPostalAddress().add(new BG0005SellerPostalAddress());
    }

    @Test
    public void shouldMapToBT0039IfCountryIsItaly() throws Exception {
        PostalAddressConverter converter = new PostalAddressConverter();
        ConversionResult<BG0000Invoice> result = converter.toBT0039(document, cenInvoice, new ArrayList<IConversionIssue>());

        assertTrue(result.hasResult());

        BG0000Invoice resultInvoice = result.getResult();

        String bt0039 = resultInvoice.getBG0004Seller(0).getBG0005SellerPostalAddress(0).getBT0039SellerCountrySubdivision(0).getValue();

        assertEquals("MI", bt0039);

    }

    private Document makeDocumentWithSupplierPostalAddressAndCountryItaly() {

        Document document = new Document();
        Namespace defaultNs = Namespace.getNamespace("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2");
        Namespace cacNs = Namespace.getNamespace("cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
        Namespace cbcNs = Namespace.getNamespace("cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");

        Element rootElement = new Element("Invoice", defaultNs);
        rootElement.addNamespaceDeclaration(defaultNs);
        rootElement.addNamespaceDeclaration(cacNs);
        rootElement.addNamespaceDeclaration(cbcNs);
        document.setRootElement(rootElement);

        Element accountingSupplierParty = new Element("AccountingSupplierParty", cacNs);

        Element party = new Element("Party", cacNs);

        Element postalAddress = new Element("PostalAddress", cacNs);

        Element country = new Element("Country", cacNs);
        Element countryCode = new Element("IdentificationCode", cbcNs);
        countryCode.setText("IT");
        country.addContent(countryCode);

        Element countrySubentity = new Element("CountrySubentity", cbcNs);
        countrySubentity.setText("MI");

        postalAddress.addContent(country);
        postalAddress.addContent(countrySubentity);

        party.addContent(postalAddress);

        accountingSupplierParty.addContent(party);

        rootElement.addContent(accountingSupplierParty);

        return document;
    }

}