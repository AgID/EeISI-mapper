package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.EigorException;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.fattpa.commons.models.DatiTrasmissioneType;
import it.infocert.eigor.fattpa.commons.models.FatturaElettronicaType;
import it.infocert.eigor.fattpa.commons.models.FormatoTrasmissioneType;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0007Buyer;
import it.infocert.eigor.model.core.model.BT0049BuyerElectronicAddressAndSchemeIdentifier;

import java.util.List;

public class PECorCodDestConverter implements CustomMapping<FatturaElettronicaType> {

    private static final String pec = "IT:PEC";
    private static final String coddest = "IT:CODDEST";
    private static final String ipa = "0201";

    @Override
    public void map(BG0000Invoice cenInvoice, FatturaElettronicaType fatturaElettronicaType, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        if (!cenInvoice.getBG0007Buyer().isEmpty()) {
            BG0007Buyer buyer = cenInvoice.getBG0007Buyer(0);
            if (!buyer.getBT0049BuyerElectronicAddressAndSchemeIdentifier().isEmpty()) {
                DatiTrasmissioneType datiTrasmissione = fatturaElettronicaType.getFatturaElettronicaHeader().getDatiTrasmissione();
                if (datiTrasmissione == null) {
                    datiTrasmissione = new DatiTrasmissioneType();
                    fatturaElettronicaType.getFatturaElettronicaHeader().setDatiTrasmissione(datiTrasmissione);
                }
                BT0049BuyerElectronicAddressAndSchemeIdentifier bt49AddressAndScheme = buyer.getBT0049BuyerElectronicAddressAndSchemeIdentifier(0);
                if (bt49AddressAndScheme.getValue().getIdentificationSchema() != null) {

                    String identificationSchema = bt49AddressAndScheme.getValue().getIdentificationSchema().toUpperCase();
                    String identifier = bt49AddressAndScheme.getValue().getIdentifier();
                    mapDestinatario(errors, datiTrasmissione, identificationSchema, identifier);
                    mapFormatoTrasmissione(fatturaElettronicaType, identificationSchema);

                }
            }
        }
    }

    private void mapDestinatario(List<IConversionIssue> errors, DatiTrasmissioneType datiTrasmissione, String identificationSchema, String identifier) {
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
                        .message(String.format("BT-49 SchemeID is not a valid value. Should be %s, %s or %s, was: %s", pec, coddest, ipa, "".equals(identificationSchema.trim()) ? "<empty string>" : identificationSchema ))
                        .location(ErrorCode.Location.FATTPA_OUT)
                        .action(ErrorCode.Action.HARDCODED_MAP)
                        .error(ErrorCode.Error.ILLEGAL_VALUE)
                        .build())));
        }
    }

    private void mapFormatoTrasmissione(final FatturaElettronicaType fatturaElettronica, final String identificationSchema) {
        switch (identificationSchema) {
            case pec:
            case coddest:
                fatturaElettronica.getFatturaElettronicaHeader().getDatiTrasmissione().setFormatoTrasmissione(FormatoTrasmissioneType.FPR_12);
                fatturaElettronica.setVersione(FormatoTrasmissioneType.FPR_12);
                break;

        }
    }
}
