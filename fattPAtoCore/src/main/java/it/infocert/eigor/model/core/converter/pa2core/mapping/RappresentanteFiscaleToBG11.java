package it.infocert.eigor.model.core.converter.pa2core.mapping;

import it.infocert.eigor.model.core.model.BG11SellerTaxRepresentativeParty;
import it.infocert.eigor.model.core.model.CoreInvoice;
import org.w3c.dom.Document;

public class RappresentanteFiscaleToBG11{

    public static void convertTo(Document doc, CoreInvoice coreInvoice) {
        BG11SellerTaxRepresentativeParty e = new BG11SellerTaxRepresentativeParty();
        coreInvoice.getBg11SellerTaxRepresentativeParties().add(e);
        NomeRappresentanteFiscaleToBT62.convertTo(doc, coreInvoice);
    }
}
