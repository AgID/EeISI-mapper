package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.converter.fattpa2cen.models.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BT0031SellerVatIdentifier;

import java.util.List;

class FattPA2CenMapper {

    static BG0000Invoice mapToCoreInvoice(FatturaElettronicaType fattura) {
        BG0000Invoice coreInvoice = new BG0000Invoice();
        mapHeader(fattura.getFatturaElettronicaHeader(), coreInvoice);
        List<FatturaElettronicaBodyType> body = fattura.getFatturaElettronicaBody();

        return coreInvoice;
    }

    private static void mapHeader(FatturaElettronicaHeaderType header, BG0000Invoice invoice) {
        invoice.getBG0004Seller().add(mapSeller(header.getCedentePrestatore()));
    }

    private static void mapBody(FatturaElettronicaBodyType body, BG0000Invoice invoice) {

    }

    private static BG0004Seller mapSeller(CedentePrestatoreType cedente) {
        BG0004Seller seller = new BG0004Seller();
        StringBuilder sb = new StringBuilder();
        IdFiscaleType idFiscaleIVA = cedente.getDatiAnagrafici().getIdFiscaleIVA();
        sb.append(idFiscaleIVA.getIdPaese());
        sb.append(idFiscaleIVA.getIdCodice());
        seller.getBT0031SellerVatIdentifier().add(new BT0031SellerVatIdentifier(sb.toString()));

        return seller;
    }
}
