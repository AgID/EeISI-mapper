package it.infocert.eigor.model.core.converter.pa2core.mapping;

import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BT62SellerTaxRepresentativeName;
import it.infocert.eigor.model.core.model.CoreInvoice;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class NomeRappresentanteFiscaleToBT62 {
    private final static String XPATHEXPRESSION = "//RappresentanteFiscale/DatiAnagrafici/Anagrafica/Nome";

    public static void convertTo(Document doc, CoreInvoice coreInvoice) {
        NodeList nodes = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSION);
        String textContent = nodes.item(0).getTextContent();
        List<BT62SellerTaxRepresentativeName> names = new ArrayList<>(1);
        names.add(new BT62SellerTaxRepresentativeName(new Identifier(textContent)));
        coreInvoice.getBg11SellerTaxRepresentativeParties().get(0).setBt62SellerTtaxRepresentativeNames(names);
    }
}
