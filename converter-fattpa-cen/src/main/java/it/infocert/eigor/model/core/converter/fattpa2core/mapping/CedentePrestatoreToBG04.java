package it.infocert.eigor.model.core.converter.fattpa2core.mapping;

import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.w3c.dom.Document;

public class CedentePrestatoreToBG04 {

    public static void convertTo(Document doc, BG0000Invoice coreInvoice) {
        BG0004Seller bg04Seller = new BG0004Seller();
        coreInvoice.getBG0004Seller().add(bg04Seller);
        AnagraficaCedenteToBT27.convertTo(doc, coreInvoice);
        CodiceFiscaleCedenteToBT29.convertTo(doc, coreInvoice);
        IdFiscaleIvaToBT31.convertTo(doc, coreInvoice);
        RegimeFiscaleToBT32.convertTo(doc, coreInvoice);
    }
}
