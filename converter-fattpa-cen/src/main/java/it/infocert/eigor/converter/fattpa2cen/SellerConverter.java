package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

/**
 * The Seller Legal Registration Identifier Custom Converter
 */
public class SellerConverter implements CustomMapping<Document> {

    private static final String rea = "IT:REA";
    private static final String cf = "IT:CF";
    private static final String eori = "IT:EORI";
    private static final String albo = "IT:ALBO";

    public ConversionResult<BG0000Invoice> toBG0004(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        Element fatturaElettronicaHeader = rootElement.getChild("FatturaElettronicaHeader");

        if (fatturaElettronicaHeader != null) {
            Element cedentePrestatore = fatturaElettronicaHeader.getChild("CedentePrestatore");
            if (cedentePrestatore != null) {
                if (invoice.getBG0004Seller().isEmpty()) {
                    invoice.getBG0004Seller().add(new BG0004Seller());
                }
                final BG0004Seller seller = invoice.getBG0004Seller(0);

                String nazioneStr = "";
                Element sede = cedentePrestatore.getChild("Sede");
                if (sede != null) {
                    Element nazione = sede.getChild("Nazione");
                    if (nazione != null) {
                        nazioneStr = nazione.getText();
                    }
                }

                Element datiAnagrafici = cedentePrestatore.getChild("DatiAnagrafici");
                Element numeroIscrizioneAlbo = null;
                if (datiAnagrafici != null) {
                    numeroIscrizioneAlbo = datiAnagrafici.getChild("NumeroIscrizioneAlbo");

                    Element codiceFiscale = datiAnagrafici.getChild("CodiceFiscale");
                    Element anagrafica = datiAnagrafici.getChild("Anagrafica");


                    Element codEORI = null;
                    if (anagrafica != null) {
                        BT0027SellerName bt27 = InvoiceUtils.evalExpression(() -> invoice.getBG0004Seller(0).getBT0027SellerName(0));
                        if(bt27 == null) {

                            String nome;
                            String cognome;
                            String denominazione;

                            Element nomeXml = anagrafica.getChild("Nome");
                            nome = nomeXml==null ? "" : nomeXml.getText();

                            Element cognomeXml = anagrafica.getChild("Cognome");
                            cognome = cognomeXml==null ? "" : cognomeXml.getText();

                            Element denominazioneXml = anagrafica.getChild("Denominazione");
                            denominazione = denominazioneXml==null ? "" : denominazioneXml.getText();

                            String bt27value = String.join(" ", denominazione, nome, cognome).trim();
                            if(bt27value.isEmpty()) bt27value = "N/A";


                            invoice.getBG0004Seller(0).getBT0027SellerName().add( new BT0027SellerName(bt27value) );

                        }



                        codEORI = anagrafica.getChild("CodEORI");
                    }
                    Element alboProfessionale = datiAnagrafici.getChild("AlboProfessionale");
                    BT0029SellerIdentifierAndSchemeIdentifier sellerIdentifierAndSchemeIdentifier;
                    if (anagrafica != null && codEORI != null) {
                        if (nazioneStr.equals("IT")) {
                            sellerIdentifierAndSchemeIdentifier = new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier(eori + ":" + codEORI.getText()));
                        } else {
                            sellerIdentifierAndSchemeIdentifier = new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier(codEORI.getText()));
                        }

                        seller.getBT0029SellerIdentifierAndSchemeIdentifier().add(sellerIdentifierAndSchemeIdentifier);
                    }
                    if (alboProfessionale != null && numeroIscrizioneAlbo != null) {
                        if (nazioneStr.equals("IT")) {
                            sellerIdentifierAndSchemeIdentifier = new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier(albo + ":" + alboProfessionale.getText() + ":" + numeroIscrizioneAlbo.getText()));
                        } else {
                            sellerIdentifierAndSchemeIdentifier = new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier(alboProfessionale.getText() + ":" + numeroIscrizioneAlbo.getText()));
                        }

                        seller.getBT0029SellerIdentifierAndSchemeIdentifier().add(sellerIdentifierAndSchemeIdentifier);
                    }

                }

                Element iscrizioneREA = cedentePrestatore.getChild("IscrizioneREA");
                if (iscrizioneREA != null) {
                    Element ufficio = iscrizioneREA.getChild("Ufficio");
                    Element numeroREA = iscrizioneREA.getChild("NumeroREA");

                    if (nazioneStr.equals("IT")) {
                        if (ufficio != null && numeroREA != null) {
                            BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier sellerLegalRegistrationIdentifierAndSchemeIdentifier =
                                    new BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(new Identifier(rea + ":" + ufficio.getText() + ":" + numeroREA.getText()));
                            if (invoice.getBG0004Seller().isEmpty()) {
                                invoice.getBG0004Seller().add(new BG0004Seller());
                            }
                            seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier().add(sellerLegalRegistrationIdentifierAndSchemeIdentifier);
                        }
                    } else {

                        if (ufficio != null) {
                            BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier sellerLegalRegistrationIdentifierAndSchemeIdentifier =
                                    new BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(new Identifier(ufficio.getText()));
                            if (invoice.getBG0004Seller().isEmpty()) {
                                invoice.getBG0004Seller().add(new BG0004Seller());
                            }
                            seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier().add(sellerLegalRegistrationIdentifierAndSchemeIdentifier);
                        } else if (numeroREA != null) {
                            BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier sellerLegalRegistrationIdentifierAndSchemeIdentifier =
                                    new BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(new Identifier(numeroREA.getText()));
                            if (invoice.getBG0004Seller().isEmpty()) {
                                invoice.getBG0004Seller().add(new BG0004Seller());
                            }
                            seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier().add(sellerLegalRegistrationIdentifierAndSchemeIdentifier);
                        }
                    }
                } else if (numeroIscrizioneAlbo != null) {
                    BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier sellerLegalRegistrationIdentifierAndSchemeIdentifier =
                            new BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(new Identifier(numeroIscrizioneAlbo.getText()));
                    if (invoice.getBG0004Seller().isEmpty()) {
                        invoice.getBG0004Seller().add(new BG0004Seller());
                    }
                    seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier().add(sellerLegalRegistrationIdentifierAndSchemeIdentifier);
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBG0004(document, cenInvoice, errors);
    }
}
