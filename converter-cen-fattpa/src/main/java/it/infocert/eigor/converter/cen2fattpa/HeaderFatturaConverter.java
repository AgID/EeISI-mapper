package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.model.*;

import java.util.List;

public class HeaderFatturaConverter implements ICen2FattPAConverter {

    private ObjectFactory factory;
    private FatturaElettronicaHeaderType fatturaElettronicaHeader;
    private BG0000Invoice invoice;
    private List<Exception> errors;


    public HeaderFatturaConverter(ObjectFactory factory, BG0000Invoice invoice, List<Exception> errors) {
        this.factory = factory;
        this.invoice = invoice;
        fatturaElettronicaHeader = factory.createFatturaElettronicaHeaderType();
        this.errors = errors;
    }

    public FatturaElettronicaHeaderType getFatturaElettronicaHeader() {
        return fatturaElettronicaHeader;
    }

    @Override
    public void copyRequiredOne2OneFields() {
        setDatiTrasmissione();
        setCedentePrestatore();
        setCessionarioCommittente();
        setRappresentanteFiscale();
    }

    private void setRappresentanteFiscale() {
        if (!invoice.getBG0011SellerTaxRepresentativeParty().isEmpty()) {
            RappresentanteFiscaleType rappresentanteFiscale = factory.createRappresentanteFiscaleType();
            DatiAnagraficiRappresentanteType datiAnagraficiRappresentante = factory.createDatiAnagraficiRappresentanteType();
            AnagraficaType anagrafica = factory.createAnagraficaType();
            IdFiscaleType idFiscale = factory.createIdFiscaleType();
            datiAnagraficiRappresentante.setIdFiscaleIVA(idFiscale);
            datiAnagraficiRappresentante.setAnagrafica(anagrafica);
            rappresentanteFiscale.setDatiAnagrafici(datiAnagraficiRappresentante);
            fatturaElettronicaHeader.setRappresentanteFiscale(rappresentanteFiscale);

            BG0011SellerTaxRepresentativeParty sellerTaxRepresentativeParty = invoice.getBG0011SellerTaxRepresentativeParty().get(0);

            anagrafica.setDenominazione(sellerTaxRepresentativeParty.getBT0062SellerTaxRepresentativeName().get(0).getValue());

            String value = sellerTaxRepresentativeParty.getBT0063SellerTaxRepresentativeVatIdentifier().get(0).getValue();
            idFiscale.setIdPaese(value.substring(0, 2));
            idFiscale.setIdCodice(value.substring(2));
        }
    }

    private void setCessionarioCommittente() {
        CessionarioCommittenteType cessionarioCommittente = factory.createCessionarioCommittenteType();
        DatiAnagraficiCessionarioType datiAnagraficiCessionario = factory.createDatiAnagraficiCessionarioType();
        AnagraficaType anagraficaCessionario = factory.createAnagraficaType();
        datiAnagraficiCessionario.setAnagrafica(anagraficaCessionario);
        IdFiscaleType idFiscaleBuyer = factory.createIdFiscaleType();
        datiAnagraficiCessionario.setIdFiscaleIVA(idFiscaleBuyer);
        cessionarioCommittente.setDatiAnagrafici(datiAnagraficiCessionario);
        IndirizzoType sedeCessionario = factory.createIndirizzoType();
        cessionarioCommittente.setSede(sedeCessionario);
        fatturaElettronicaHeader.setCessionarioCommittente(cessionarioCommittente);

        try {
            BG0007Buyer buyer = invoice.getBG0007Buyer().get(0);
            anagraficaCessionario.setDenominazione(buyer.getBT0044BuyerName().get(0).getValue());

            String buyerVatId = buyer.getBT0048BuyerVatIdentifier().get(0).getValue();
            idFiscaleBuyer.setIdCodice(Cen2FattPAConverterUtils.getCodeFromVATString(buyerVatId));
            idFiscaleBuyer.setIdPaese(Cen2FattPAConverterUtils.getCountryFromVATString(buyerVatId));


            if (!buyer.getBT0046BuyerIdentifierAndSchemeIdentifier().isEmpty()) {
                for (BT0046BuyerIdentifierAndSchemeIdentifier identifier : buyer.getBT0046BuyerIdentifierAndSchemeIdentifier()) {
                    cessionarioCommittente.getDatiAnagrafici().setCodiceFiscale(identifier.getValue());
                }
            }

            BG0008BuyerPostalAddress buyerPostalAddress = buyer.getBG0008BuyerPostalAddress().get(0);
            StringBuilder sb = new StringBuilder();

            if (!buyerPostalAddress.getBT0050BuyerAddressLine1().isEmpty()) {
                sb.append(buyerPostalAddress.getBT0050BuyerAddressLine1().get(0).getValue());
            }

            if (!buyerPostalAddress.getBT0051BuyerAddressLine2().isEmpty()) {
                sb.append(", ").append(buyerPostalAddress.getBT0051BuyerAddressLine2().get(0).getValue());
            }

            if (!buyerPostalAddress.getBT0163BuyerAddressLine3().isEmpty()) {
                sb.append(", ").append(buyerPostalAddress.getBT0163BuyerAddressLine3().get(0).getValue());
            }

            sedeCessionario.setIndirizzo(sb.toString());

            if (!buyerPostalAddress.getBT0052BuyerCity().isEmpty()) {
                sedeCessionario.setComune(buyerPostalAddress.getBT0052BuyerCity().get(0).getValue());
            }
            if (!buyerPostalAddress.getBT0053BuyerPostCode().isEmpty()) {
                sedeCessionario.setCAP(buyerPostalAddress.getBT0053BuyerPostCode().get(0).getValue());
            }

            if (!buyerPostalAddress.getBT0054BuyerCountrySubdivision().isEmpty()) {
                sedeCessionario.setProvincia(buyerPostalAddress.getBT0054BuyerCountrySubdivision().get(0).getValue());
            }
            sedeCessionario.setNazione(buyerPostalAddress.getBT0055BuyerCountryCode().get(0).getValue().getIso2charCode());
        } catch (Exception e) {
            errors.add(new RuntimeException(IConstants.ERROR_BUYER_INFORMATION, e));
        }
    }

    private void setCedentePrestatore() {
        CedentePrestatoreType cedentePrestatore = factory.createCedentePrestatoreType();
        DatiAnagraficiCedenteType datiAnagraficiCedente = factory.createDatiAnagraficiCedenteType();
        AnagraficaType anagraficaCedente = factory.createAnagraficaType();
        datiAnagraficiCedente.setAnagrafica(anagraficaCedente);
        IdFiscaleType idFiscaleSeller = factory.createIdFiscaleType();
        datiAnagraficiCedente.setIdFiscaleIVA(idFiscaleSeller);
        datiAnagraficiCedente.setRegimeFiscale(RegimeFiscaleType.RF_01);
        cedentePrestatore.setDatiAnagrafici(datiAnagraficiCedente);
        IndirizzoType sedeCedente = factory.createIndirizzoType();
        cedentePrestatore.setSede(sedeCedente);
        fatturaElettronicaHeader.setCedentePrestatore(cedentePrestatore);

        try {
            List<BT0019BuyerAccountingReference> accountingReferences = invoice.getBT0019BuyerAccountingReference();
            if (!accountingReferences.isEmpty()) {
                cedentePrestatore.setRiferimentoAmministrazione(accountingReferences.get(0).getValue());
            }
            BG0004Seller seller = invoice.getBG0004Seller().get(0);
            anagraficaCedente.setDenominazione(seller.getBT0027SellerName().get(0).getValue());

            List<BT0031SellerVatIdentifier> sellerVatIdentifiers = seller.getBT0031SellerVatIdentifier();
            if (!sellerVatIdentifiers.isEmpty()) {
                String sellerVatId = sellerVatIdentifiers.get(0).getValue();
                idFiscaleSeller.setIdCodice(Cen2FattPAConverterUtils.getCodeFromVATString(sellerVatId));
                idFiscaleSeller.setIdPaese(Cen2FattPAConverterUtils.getCountryFromVATString(sellerVatId));
            }

            List<BT0029SellerIdentifierAndSchemeIdentifier> sellerIdentifiers = seller.getBT0029SellerIdentifierAndSchemeIdentifier();
            if (!sellerIdentifiers.isEmpty()) {
                for (BT0029SellerIdentifierAndSchemeIdentifier identifier : sellerIdentifiers) {
                    datiAnagraficiCedente.setCodiceFiscale(identifier.getValue());
                }
            }

            List<BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier> registrationIdentifiers = seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier();
            if (!registrationIdentifiers.isEmpty()) {
                for (BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier registrationIdentifier : registrationIdentifiers) {
                    IscrizioneREAType iscrizioneREA = factory.createIscrizioneREAType();
                    iscrizioneREA.setUfficio(registrationIdentifier.getValue());
                    cedentePrestatore.setIscrizioneREA(iscrizioneREA);
                }
            }

            BG0005SellerPostalAddress sellerPostalAddress = seller.getBG0005SellerPostalAddress().get(0);

            if (!sellerPostalAddress.getBT0035SellerAddressLine1().isEmpty()) {
                sedeCedente.setIndirizzo(sellerPostalAddress.getBT0035SellerAddressLine1().get(0).getValue());
            }
            if (!sellerPostalAddress.getBT0036SellerAddressLine2().isEmpty()) {
                sedeCedente.setIndirizzo(sellerPostalAddress.getBT0036SellerAddressLine2().get(0).getValue());
            }
            if (!sellerPostalAddress.getBT0162SellerAddressLine3().isEmpty()) {
                sedeCedente.setIndirizzo(sellerPostalAddress.getBT0162SellerAddressLine3().get(0).getValue());
            }
            if (!sellerPostalAddress.getBT0038SellerPostCode().isEmpty()) {
                sedeCedente.setCAP(sellerPostalAddress.getBT0038SellerPostCode().get(0).getValue());
            }
            if (!sellerPostalAddress.getBT0037SellerCity().isEmpty()) {
                sedeCedente.setComune(sellerPostalAddress.getBT0037SellerCity().get(0).getValue());
            }
            if (!sellerPostalAddress.getBT0039SellerCountrySubdivision().isEmpty()) {
                sedeCedente.setProvincia(sellerPostalAddress.getBT0039SellerCountrySubdivision().get(0).getValue());
            }
            sedeCedente.setNazione(sellerPostalAddress.getBT0040SellerCountryCode().get(0).getValue().getIso2charCode());

            List<BG0006SellerContact> sellerContacts = seller.getBG0006SellerContact();
            ContattiType contatti = cedentePrestatore.getContatti();
            BG0006SellerContact sellerContact = sellerContacts.get(0);
            if (!sellerContacts.isEmpty()) {
                if (!sellerContact.getBT0042SellerContactTelephoneNumber().isEmpty()) {
                    contatti.setTelefono(sellerContact.getBT0042SellerContactTelephoneNumber().get(0).getValue());
                }
                if (!sellerContact.getBT0043SellerContactEmailAddress().isEmpty()) {
                    contatti.setEmail(sellerContact.getBT0043SellerContactEmailAddress().get(0).getValue());
                }
            }

        } catch (Exception e) {
            errors.add(new RuntimeException(IConstants.ERROR_SELLER_INFORMATION, e));
        }
    }

    private void setDatiTrasmissione() {
        DatiTrasmissioneType datiTrasmissioneType = factory.createDatiTrasmissioneType();
        datiTrasmissioneType.setFormatoTrasmissione(FormatoTrasmissioneType.FPA_12);
//        datiTrasmissioneType.setProgressivoInvio("dummy"); // FIXME no actual mapping, required by XSD
        fatturaElettronicaHeader.setDatiTrasmissione(datiTrasmissioneType);
        IdFiscaleType idFiscaleSeller = factory.createIdFiscaleType();
        datiTrasmissioneType.setIdTrasmittente(idFiscaleSeller);

        try {
            String sellerVatId = invoice.getBG0004Seller().get(0).getBT0031SellerVatIdentifier().get(0).getValue();
            idFiscaleSeller.setIdCodice(Cen2FattPAConverterUtils.getCodeFromVATString(sellerVatId));
            idFiscaleSeller.setIdPaese(Cen2FattPAConverterUtils.getCountryFromVATString(sellerVatId));
            datiTrasmissioneType.setCodiceDestinatario(invoice.getBG0007Buyer().get(0).getBT0049BuyerElectronicAddressAndSchemeIdentifier().get(0).getValue());
        } catch (Exception e) {
            errors.add(new RuntimeException(IConstants.ERROR_TRANSMISSION_INFORMATION, e));
        }
    }

    @Override
    public void copyOptionalOne2OneFields() {
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public void computeMultipleCenElements2FpaField() {
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public void transformFpaFields() {
        throw new RuntimeException("Not implemented!");
    }

}
