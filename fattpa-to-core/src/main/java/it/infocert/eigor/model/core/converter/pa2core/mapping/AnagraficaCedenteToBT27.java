package it.infocert.eigor.model.core.converter.pa2core.mapping;

import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BT0027SellerName;
import it.infocert.eigor.model.core.model.CoreInvoice;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class AnagraficaCedenteToBT27 {
    private final static String XPATHEXPRESSIONDENOM = "//CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione";
    private final static String XPATHEXPRESSIONNOME = "//CedentePrestatore/DatiAnagrafici/Anagrafica/Nome";
    private final static String XPATHEXPRESSIONCOGNOME = "//CedentePrestatore/DatiAnagrafici/Anagrafica/Cognome";


    public static void convertTo(Document doc, CoreInvoice coreInvoice) {
        if (CommonConversionModule.hasNode(doc, XPATHEXPRESSIONDENOM)) {
            NodeList nodes = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSIONDENOM);
            String textContent = nodes.item(0).getTextContent();
            List<BT0027SellerName> sellerNames = new ArrayList<>();
            sellerNames.add(new BT0027SellerName(textContent));
            coreInvoice.getBg0004Sellers().get(0).setBt0027SellerNames(sellerNames);
        } else if (CommonConversionModule.hasNode(doc, XPATHEXPRESSIONNOME) && CommonConversionModule.hasNode(doc, XPATHEXPRESSIONCOGNOME)) {
            NodeList nameNodes = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSIONNOME);
            String name = nameNodes.item(0).getTextContent();
            NodeList surnameNodes = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSIONCOGNOME);
            String surname = surnameNodes.item(0).getTextContent();
            List<BT0027SellerName> sellerNames = new ArrayList<>();
            sellerNames.add(new BT0027SellerName(name + " " + surname));
            coreInvoice.getBg0004Sellers().get(0).setBt0027SellerNames(sellerNames);
        }
    }
}
