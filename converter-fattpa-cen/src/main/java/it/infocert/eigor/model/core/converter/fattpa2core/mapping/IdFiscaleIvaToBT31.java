package it.infocert.eigor.model.core.converter.fattpa2core.mapping;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0032SellerTaxRegistrationIdentifier;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class IdFiscaleIvaToBT31 {
    private final static String XPATHEXPRESSIONCOUNTRY = "//CedentePrestatore/DatiAnagrafici/IdFiscaleIVA/IdPaese";
    private final static String XPATHEXPRESSIONCODE = "//CedentePrestatore/DatiAnagrafici/IdFiscaleIVA/IdCodice";

    public static void convertTo(Document doc, BG0000Invoice coreInvoice) {
        NodeList countryNodes = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSIONCOUNTRY);
        NodeList codeNodes = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSIONCODE);
        String countryCode = countryNodes.item(0).getTextContent();
        String vatCode = codeNodes.item(0).getTextContent();
        List<BT0032SellerTaxRegistrationIdentifier> identifiers = new ArrayList<>(1);
        identifiers.add(new BT0032SellerTaxRegistrationIdentifier(countryCode + vatCode));
        coreInvoice.getBG0004Seller().get(0).getBT0032SellerTaxRegistrationIdentifier().addAll(identifiers);
    }
}
