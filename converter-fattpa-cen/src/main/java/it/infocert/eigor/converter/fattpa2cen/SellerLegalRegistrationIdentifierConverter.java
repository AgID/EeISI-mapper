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
 * The Seller Legal Registration Identifier Custom Converter
 */
public class SellerLegalRegistrationIdentifierConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBT0030(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        Element fatturaElettronicaHeader = rootElement.getChild("FatturaElettronicaHeader");

        if (fatturaElettronicaHeader != null) {
            Element cessionarioCommittente = fatturaElettronicaHeader.getChild("CessionarioCommittente");
            if (cessionarioCommittente != null) {
                Element datiAnagrafici = cessionarioCommittente.getChild("DatiAnagrafici");
                Element numeroIscrizioneAlbo = null;
                if (datiAnagrafici != null) {
                    numeroIscrizioneAlbo = datiAnagrafici.getChild("NumeroIscrizioneAlbo");
                }
                Element iscrizioneREA = cessionarioCommittente.getChild("IscrizioneREA");
                if (iscrizioneREA != null) {
                    Element ufficio = iscrizioneREA.getChild("Ufficio");
                    Element numeroREA = iscrizioneREA.getChild("NumeroREA");
                    if (ufficio != null) {
                        BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier sellerLegalRegistrationIdentifierAndSchemeIdentifier =
                                new BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(new Identifier(ufficio.getText()));
                        if (invoice.getBG0004Seller().isEmpty()) {
                            invoice.getBG0004Seller().add(new BG0004Seller());
                        }
                        invoice.getBG0004Seller(0).getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier().add(sellerLegalRegistrationIdentifierAndSchemeIdentifier);
                    } else if (numeroREA != null) {
                        BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier sellerLegalRegistrationIdentifierAndSchemeIdentifier =
                                new BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(new Identifier(numeroREA.getText()));
                        if (invoice.getBG0004Seller().isEmpty()) {
                            invoice.getBG0004Seller().add(new BG0004Seller());
                        }
                        invoice.getBG0004Seller(0).getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier().add(sellerLegalRegistrationIdentifierAndSchemeIdentifier);
                    }
                } else if (numeroIscrizioneAlbo != null) {
                    BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier sellerLegalRegistrationIdentifierAndSchemeIdentifier =
                            new BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(new Identifier(numeroIscrizioneAlbo.getText()));
                    if (invoice.getBG0004Seller().isEmpty()) {
                        invoice.getBG0004Seller().add(new BG0004Seller());
                    }
                    invoice.getBG0004Seller(0).getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier().add(sellerLegalRegistrationIdentifierAndSchemeIdentifier);
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBT0030(document, cenInvoice, errors);
    }
}