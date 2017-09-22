package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

/**
 * The Buyer Electronic Address Custom Converter
 */
public class BuyerElectronicAddressConverter implements CustomMapping<Document> {

    private static final String pec = "IT:PEC";
    private static final String ipa = "IT:IPA";

    public ConversionResult<BG0000Invoice> toBT0049(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        Element fatturaElettronicaHeader = rootElement.getChild("FatturaElettronicaHeader");

        if (fatturaElettronicaHeader != null) {
            Element datiTrasmissione = fatturaElettronicaHeader.getChild("DatiTrasmissione");
            if (datiTrasmissione != null) {
                Element codiceDestinatario = datiTrasmissione.getChild("CodiceDestinatario");
                Element pecDestinatario = datiTrasmissione.getChild("PECDestinatario");
                if (codiceDestinatario != null) {
                    BT0049BuyerElectronicAddressAndSchemeIdentifier buyerElectronicAddressAndSchemeIdentifier = new BT0049BuyerElectronicAddressAndSchemeIdentifier(new Identifier(ipa, codiceDestinatario.getText()));
                    invoice.getBG0007Buyer(0).getBT0049BuyerElectronicAddressAndSchemeIdentifier().add(buyerElectronicAddressAndSchemeIdentifier);
                } else if (pecDestinatario != null) {
                    BT0049BuyerElectronicAddressAndSchemeIdentifier buyerElectronicAddressAndSchemeIdentifier = new BT0049BuyerElectronicAddressAndSchemeIdentifier(new Identifier(pec, pecDestinatario.getText()));
                    if (invoice.getBG0007Buyer().isEmpty()) {
                        invoice.getBG0007Buyer().add(new BG0007Buyer());
                    }
                    invoice.getBG0007Buyer(0).getBT0049BuyerElectronicAddressAndSchemeIdentifier().add(buyerElectronicAddressAndSchemeIdentifier);
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBT0049(document, cenInvoice, errors);
    }
}