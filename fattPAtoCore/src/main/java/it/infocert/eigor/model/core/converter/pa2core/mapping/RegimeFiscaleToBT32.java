package it.infocert.eigor.model.core.converter.pa2core.mapping;

import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BT32SellerTaxRegistrationIdentifier;
import it.infocert.eigor.model.core.model.CoreInvoice;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class RegimeFiscaleToBT32 {
    private final static String XPATHEXPRESSION = "//CedentePrestatore/DatiAnagrafici/RegimeFiscale";

    public static void convertTo(Document doc, CoreInvoice coreInvoice) {
        NodeList nodes = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSION);
        String fiscalCode = nodes.item(0).getTextContent();
        List<BT32SellerTaxRegistrationIdentifier> identifiers = new ArrayList<>(1);
        identifiers.add(new BT32SellerTaxRegistrationIdentifier(new Identifier(fiscalCode)));
        coreInvoice.getBg04Sellers().get(0).setBt32SellerTaxRegistrationIdentifiers(identifiers);
    }
}
