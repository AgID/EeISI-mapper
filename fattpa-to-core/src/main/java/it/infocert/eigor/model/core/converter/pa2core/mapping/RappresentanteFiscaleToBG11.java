package it.infocert.eigor.model.core.converter.pa2core.mapping;

import it.infocert.eigor.model.core.model.BG0011SellerTaxRepresentativeParty;
import it.infocert.eigor.model.core.model.CoreInvoice;
import org.w3c.dom.Document;

public class RappresentanteFiscaleToBG11{

    public static void convertTo(Document doc, CoreInvoice coreInvoice) {
        BG0011SellerTaxRepresentativeParty e = new BG0011SellerTaxRepresentativeParty();
        coreInvoice.getBg0011SellerTaxRepresentativeParties().add(e);
        NomeRappresentanteFiscaleToBT62.convertTo(doc, coreInvoice);
    }
}
