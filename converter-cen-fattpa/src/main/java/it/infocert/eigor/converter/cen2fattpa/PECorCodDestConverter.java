package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.EigorException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.converter.cen2fattpa.models.DatiTrasmissioneType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaType;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0007Buyer;
import it.infocert.eigor.model.core.model.BT0049BuyerElectronicAddressAndSchemeIdentifier;

import java.util.List;

public class PECorCodDestConverter implements CustomMapping<FatturaElettronicaType> {

    private static final String pec = "IT:PEC";
    private static final String coddest = "IT:CODDEST";
    private static final String ipa = "IT:IPA";

    @Override
    public void map(BG0000Invoice cenInvoice, FatturaElettronicaType fatturaElettronicaType, List<IConversionIssue> errors) {
        if (!cenInvoice.getBG0007Buyer().isEmpty()) {
            BG0007Buyer buyer = cenInvoice.getBG0007Buyer(0);
            if (!buyer.getBT0049BuyerElectronicAddressAndSchemeIdentifier().isEmpty()) {
                DatiTrasmissioneType datiTrasmissione = fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione();
                BT0049BuyerElectronicAddressAndSchemeIdentifier address = buyer.getBT0049BuyerElectronicAddressAndSchemeIdentifier(0);
                if (address.getValue().getIdentificationSchema() != null) {
                    String identificationSchema = address.getValue().getIdentificationSchema().toUpperCase();
                    String identifier = address.getValue().getIdentifier();
                    switch (identificationSchema) {

                        case pec:
                            datiTrasmissione.setPECDestinatario(identifier);
                            datiTrasmissione.setCodiceDestinatario("0000000");
                            break;
                        case coddest:
                        case ipa:
                            datiTrasmissione.setCodiceDestinatario(identifier);
                            break;
                        default:
                            errors.add(ConversionIssue.newError(new EigorException(ErrorMessage.builder()
                                    .message(String.format("BT-49 SchemeID is not a valid value. Shoudl be %s, %s or %s, was: %s", pec, coddest, ipa, identificationSchema))
                                    .action("PECorCodDestConversion")
                                    .build())));
                    }
                }
            }
        }
    }
}
