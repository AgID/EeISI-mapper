package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.models.FatturaElettronicaBodyType;
import it.infocert.eigor.converter.fattpa2cen.models.FatturaElettronicaHeaderType;
import it.infocert.eigor.converter.fattpa2cen.models.FatturaElettronicaType;
import it.infocert.eigor.model.core.model.BG0000Invoice;

import java.util.List;

import static it.infocert.eigor.converter.fattpa2cen.mapping.BG04SellerMapper.mapSeller;
import static it.infocert.eigor.converter.fattpa2cen.mapping.BG07BuyerMapper.mapBuyer;
import static it.infocert.eigor.converter.fattpa2cen.mapping.BG11SellerTaxRepresentativePartyMapper.mapSellerTaxRepresentativeParty;

public class FattPA2CenMapper {

    public static BG0000Invoice mapToCoreInvoice(FatturaElettronicaType fattura) {
        BG0000Invoice coreInvoice = new BG0000Invoice();
        mapHeader(fattura.getFatturaElettronicaHeader(), coreInvoice);
        List<FatturaElettronicaBodyType> body = fattura.getFatturaElettronicaBody();

        return coreInvoice;
    }

    private static void mapHeader(FatturaElettronicaHeaderType header, BG0000Invoice invoice) {
        invoice.getBG0004Seller().add(mapSeller(header.getCedentePrestatore()));
        invoice.getBG0007Buyer().add(mapBuyer(header.getCessionarioCommittente()));
        invoice.getBG0011SellerTaxRepresentativeParty().add(mapSellerTaxRepresentativeParty(header.getRappresentanteFiscale()));
    }

    private static void mapBody(FatturaElettronicaBodyType body, BG0000Invoice invoice) {

    }


}
