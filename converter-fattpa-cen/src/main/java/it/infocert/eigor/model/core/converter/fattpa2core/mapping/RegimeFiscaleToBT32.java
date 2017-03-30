package it.infocert.eigor.model.core.converter.fattpa2core.mapping;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0032SellerTaxRegistrationIdentifier;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class RegimeFiscaleToBT32 {
    private final static String XPATHEXPRESSION = "//CedentePrestatore/DatiAnagrafici/RegimeFiscale";

    public static void convertTo(Document doc, BG0000Invoice coreInvoice) {
        NodeList nodes = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSION);
        String fiscalCode = nodes.item(0).getTextContent();
        List<BT0032SellerTaxRegistrationIdentifier> identifiers = new ArrayList<>(1);
        identifiers.add(new BT0032SellerTaxRegistrationIdentifier(fiscalCode));
        coreInvoice.getBG0004Seller().get(0)
                .getBT0032SellerTaxRegistrationIdentifier().addAll(identifiers);
    }
}
