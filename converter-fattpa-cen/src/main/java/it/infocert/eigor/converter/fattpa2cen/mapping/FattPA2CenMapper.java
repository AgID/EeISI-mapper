package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.models.*;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.List;

import static it.infocert.eigor.converter.fattpa2cen.mapping.BG04SellerMapper.mapSeller;
import static it.infocert.eigor.converter.fattpa2cen.mapping.BG07BuyerMapper.mapBuyer;
import static it.infocert.eigor.converter.fattpa2cen.mapping.BG11SellerTaxRepresentativePartyMapper.mapSellerTaxRepresentativeParty;

public class FattPA2CenMapper {

    public static BG0000Invoice mapToCoreInvoice(FatturaElettronicaType fattura) {
        BG0000Invoice coreInvoice = new BG0000Invoice();
        mapHeader(fattura.getFatturaElettronicaHeader(), coreInvoice);
        mapBody(fattura.getFatturaElettronicaBody(), coreInvoice);

        return coreInvoice;
    }

    private static void mapHeader(FatturaElettronicaHeaderType header, BG0000Invoice invoice) {
        CedentePrestatoreType cedentePrestatore = header.getCedentePrestatore();
        invoice.getBG0004Seller().add(mapSeller(cedentePrestatore));
        invoice.getBG0007Buyer().add(mapBuyer(header.getCessionarioCommittente()));

        RappresentanteFiscaleType rappresentanteFiscale = header.getRappresentanteFiscale();
        if (rappresentanteFiscale != null) {
            invoice.getBG0011SellerTaxRepresentativeParty().add(mapSellerTaxRepresentativeParty(rappresentanteFiscale));
        }

        String riferimentoAmministrazione = mapBT19(cedentePrestatore);
        if (riferimentoAmministrazione != null) {
            invoice.getBT0019BuyerAccountingReference().add(new BT0019BuyerAccountingReference(riferimentoAmministrazione));
        }

        String buyerCode = mapBT49(header.getDatiTrasmissione());
        if (buyerCode != null) {
            invoice.getBG0007Buyer().get(0)
                    .getBT0049BuyerElectronicAddressAndSchemeIdentifier()
                    .add(new BT0049BuyerElectronicAddressAndSchemeIdentifier(buyerCode));
        }
    }

    private static void mapBody(List<FatturaElettronicaBodyType> bodyList, BG0000Invoice invoice) {
        for (FatturaElettronicaBodyType body : bodyList) {
            DatiGeneraliType datiGenerali = body.getDatiGenerali();

            invoice.getBT0001InvoiceNumber()
                    .add(new BT0001InvoiceNumber(mapBT01(datiGenerali)));

            invoice.getBT0002InvoiceIssueDate()
                    .add(new BT0002InvoiceIssueDate(mapBT02(datiGenerali)));


            String documentType = mapBT03(datiGenerali);
            String substring = documentType.substring(documentType.length() - 2);
//            Untdid1001InvoiceTypeCode attribute = Untdid1001InvoiceTypeCode.valueOf(substring); //TODO: Ma sta roba non corrisponde, il TipoDocumento
//            invoice.getBT0003InvoiceTypeCode().add(new BT0003InvoiceTypeCode(attribute));       //TODO: non è mappabile a Untdid1001


        }
    }

    private static String mapBT01(DatiGeneraliType dati) {
        return dati.getDatiGeneraliDocumento().getNumero();
    }

    private static LocalDate mapBT02(DatiGeneraliType dati) {
        XMLGregorianCalendar data = dati.getDatiGeneraliDocumento().getData();
        return data.toGregorianCalendar().toZonedDateTime().toLocalDate();
    }

    private static String mapBT03(DatiGeneraliType dati) {
        return dati.getDatiGeneraliDocumento().getTipoDocumento().value();
    }

    private static String mapBT19(CedentePrestatoreType cedentePrestatore) {
        return cedentePrestatore.getRiferimentoAmministrazione();
    }

    private static String mapBT49(DatiTrasmissioneType datiTrasmissione) {
        String codiceDestinatario = datiTrasmissione.getCodiceDestinatario();
        if (codiceDestinatario.equals("0000000")) {
            return datiTrasmissione.getPECDestinatario();
        } else {
            return codiceDestinatario;
        }

    }

}
