package it.infocert.eigor.model.core.converter.pa2core.mapping;

import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class CodiceFiscaleCedenteToBT29 {
    private final static String XPATHEXPRESSION = "//CedentePrestatore/DatiAnagrafici/CodiceFiscale";

    public static void convertTo(Document doc, CoreInvoice coreInvoice) {
        NodeList nodeList = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSION);
        String textContent = nodeList.item(0).getTextContent();
        List<BT0029SellerIdentifier> sellerIdentifiers = new ArrayList<>();
        sellerIdentifiers.add(new BT0029SellerIdentifier(textContent));
        coreInvoice.getBg0004Sellers().get(0).setBt0029SellerIdentifiers(sellerIdentifiers);
    }
}
