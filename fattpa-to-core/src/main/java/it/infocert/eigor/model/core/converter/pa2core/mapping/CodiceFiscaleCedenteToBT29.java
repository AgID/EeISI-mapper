package it.infocert.eigor.model.core.converter.pa2core.mapping;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0029SellerIdentifierAndSchemeIdentifier;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class CodiceFiscaleCedenteToBT29 {
    private final static String XPATHEXPRESSION = "//CedentePrestatore/DatiAnagrafici/CodiceFiscale";

    public static void convertTo(Document doc, BG0000Invoice coreInvoice) {
        NodeList nodeList = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSION);
        String textContent = nodeList.item(0).getTextContent();
        List<BT0029SellerIdentifierAndSchemeIdentifier> sellerIdentifiers = new ArrayList<>();
        sellerIdentifiers.add(new BT0029SellerIdentifierAndSchemeIdentifier(textContent));
        coreInvoice.getBG0004Seller().get(0).getBT0029SellerIdentifierAndSchemeIdentifier().addAll(sellerIdentifiers);
    }
}
