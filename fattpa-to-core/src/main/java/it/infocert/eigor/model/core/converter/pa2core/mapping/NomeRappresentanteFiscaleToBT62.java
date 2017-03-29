package it.infocert.eigor.model.core.converter.pa2core.mapping;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0062SellerTaxRepresentativeName;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class NomeRappresentanteFiscaleToBT62 {
    private final static String XPATHEXPRESSION = "//RappresentanteFiscale/DatiAnagrafici/Anagrafica/Nome";

    public static void convertTo(Document doc, BG0000Invoice coreInvoice) {
        NodeList nodes = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSION);
        String textContent = nodes.item(0).getTextContent();
        List<BT0062SellerTaxRepresentativeName> names = new ArrayList<>(1);
        names.add(new BT0062SellerTaxRepresentativeName(textContent));
        coreInvoice.getBG0011SellerTaxRepresentativeParty().get(0)
                .getBT0062SellerTaxRepresentativeName().addAll(names);
    }
}
