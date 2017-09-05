package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BT0032SellerTaxRegistrationIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CedentePrestatoreConverter implements CustomMapping<FatturaElettronicaType> {
    private static final Logger log = LoggerFactory.getLogger(CedentePrestatoreConverter.class);

    @Override
    public void map(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors) {
        addRegimeFiscale(invoice, fatturaElettronica.getFatturaElettronicaHeader(), errors);
    }

    private void addRegimeFiscale(BG0000Invoice invoice, FatturaElettronicaHeaderType header, List<IConversionIssue> errors) {
        if (!invoice.getBG0004Seller().isEmpty()) {
            BG0004Seller seller = invoice.getBG0004Seller(0);

            CedentePrestatoreType cedentePrestatore = header.getCedentePrestatore();
            if (cedentePrestatore != null) {
                DatiAnagraficiCedenteType datiAnagrafici = cedentePrestatore.getDatiAnagrafici();
                if (datiAnagrafici != null) {
                    if (!seller.getBT0032SellerTaxRegistrationIdentifier().isEmpty()) {
                        BT0032SellerTaxRegistrationIdentifier identifier = seller.getBT0032SellerTaxRegistrationIdentifier(0);
                        if (!seller.getBT0031SellerVatIdentifier().isEmpty() && seller.getBT0031SellerVatIdentifier(0).getValue().startsWith("IT")) {
                            datiAnagrafici.setRegimeFiscale(RegimeFiscaleType.fromValue(identifier.getValue()));
                            log.debug("Mapped BT0031 to RegimeFiscale with value {}", identifier.getValue());
                        } else {
                            datiAnagrafici.setRegimeFiscale(RegimeFiscaleType.RF_18); //FIXME mapping says "will be by default RF00 (inknown- because it is a mandatory" but no RF_00 is present in the enum
                            log.debug("Mapped BT0031 to RegimeFiscale with default value {}", RegimeFiscaleType.RF_18);
                        }
                    } else {
                        datiAnagrafici.setRegimeFiscale(RegimeFiscaleType.RF_18); //FIXME mapping says "will be by default RF00 (inknown- because it is a mandatory" but no RF_00 is present in the enum
                        log.debug("Mapped BT0031 to RegimeFiscale with default value {}", RegimeFiscaleType.RF_18);
                    }
                } else {
                    errors.add(ConversionIssue.newError(new IllegalArgumentException("No DatiAnagrafici was found in current CedentePrestatore")));
                }
            } else {
                errors.add(ConversionIssue.newError(new IllegalArgumentException("No CedentePrestatore was found in current FatturaElettronicaHeader")));
            }
        }


    }

}
