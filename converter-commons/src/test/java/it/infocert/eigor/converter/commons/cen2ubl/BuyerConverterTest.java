package it.infocert.eigor.converter.commons.cen2ubl;

import com.google.common.collect.Lists;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BuyerConverterTest {

    private BG0000Invoice invoice;
    private BuyerConverter sut;
    private Document doc;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();

        BG0007Buyer bg0007 = new BG0007Buyer();
        invoice.getBG0007Buyer().add(bg0007);

        bg0007.getBT0044BuyerName().add(new BT0044BuyerName("BT-44"));

        bg0007.getBT0048BuyerVatIdentifier().add(new BT0048BuyerVatIdentifier(new Identifier("BT-48")));

        this.sut = new BuyerConverter();
        this.doc = new Document(new Element("Invoice"));
    }

    @Test
    public void shouldMapBT0044ToRegistrationName() throws Exception {
        sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.UBL_OUT);

        Element name = doc.getRootElement().getChild("AccountingCustomerParty").getChild("Party").getChild("PartyLegalEntity").getChild("RegistrationName");

        assertEquals("BT-44", name.getText());
    }

    @Test
    public void shouldMapBT0056ToRegistrationNameIgnoringBT0044() throws Exception {

        enrichBG0007BuyerwithBG0009(invoice.getBG0007Buyer(0));

        sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.UBL_OUT);

        Element name = doc.getRootElement().getChild("AccountingCustomerParty").getChild("Party").getChild("PartyLegalEntity").getChild("RegistrationName");

        assertEquals("BT-56", name.getText());
    }

    @Test
    public void shouldMapBT0046ConcatenatingSchemeIdAndIdentifier() {

        enrichBG0007BuyerwithBT0046(invoice.getBG0007Buyer(0));

        sut.map(invoice, doc, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.UBL_OUT);

        Element id = doc.getRootElement().getChild("AccountingCustomerParty").getChild("Party").getChild("PartyIdentification").getChild("ID");

        assertEquals("Test:Schema:Identifier", id.getText());
    }

    private void enrichBG0007BuyerwithBG0009(BG0007Buyer bg0007) {
        BG0009BuyerContact bg0009 = new BG0009BuyerContact();
        bg0007.getBG0009BuyerContact().add(bg0009);
        bg0009.getBT0056BuyerContactPoint().add(new BT0056BuyerContactPoint("BT-56"));
    }

    private void enrichBG0007BuyerwithBT0046(BG0007Buyer bg0007) {
        BT0046BuyerIdentifierAndSchemeIdentifier identifier = new BT0046BuyerIdentifierAndSchemeIdentifier(new Identifier("Test:Schema", "Identifier"));
        bg0007.getBT0046BuyerIdentifierAndSchemeIdentifier().add(identifier);

    }

}