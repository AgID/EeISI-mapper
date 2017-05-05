package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.converter.cen2fattpa.models.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;

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
    }

    private void setCessionarioCommittente() {
        CessionarioCommittenteType cessionarioCommittente = factory.createCessionarioCommittenteType();
        DatiAnagraficiCessionarioType datiAnagraficiCessionario = factory.createDatiAnagraficiCessionarioType();
        AnagraficaType anagraficaCessionario = factory.createAnagraficaType();
        datiAnagraficiCessionario.setAnagrafica(anagraficaCessionario);
        IdFiscaleType idFiscaleBuyer = factory.createIdFiscaleType();
        datiAnagraficiCessionario.setIdFiscaleIVA(idFiscaleBuyer);
        cessionarioCommittente.setDatiAnagrafici(datiAnagraficiCessionario);
        IndirizzoType sedoCessionario = factory.createIndirizzoType();
        cessionarioCommittente.setSede(sedoCessionario);
        fatturaElettronicaHeader.setCessionarioCommittente(cessionarioCommittente);

        try {
            anagraficaCessionario.setDenominazione(invoice.getBG0007Buyer().get(0).getBT0044BuyerName().get(0).getValue());

            String buyerVatId = invoice.getBG0007Buyer().get(0).getBT0048BuyerVatIdentifier().get(0).getValue();
            idFiscaleBuyer.setIdCodice(Cen2FattPAConverterUtils.getCodeFromVATString(buyerVatId));
            idFiscaleBuyer.setIdPaese(Cen2FattPAConverterUtils.getCountryFromVATString(buyerVatId));

            sedoCessionario.setIndirizzo(invoice.getBG0007Buyer().get(0).getBG0008BuyerPostalAddress().get(0).getBT0050BuyerAddressLine1().get(0).getValue());
            sedoCessionario.setCAP(invoice.getBG0007Buyer().get(0).getBG0008BuyerPostalAddress().get(0).getBT0053BuyerPostCode().get(0).getValue());
            sedoCessionario.setComune(invoice.getBG0007Buyer().get(0).getBG0008BuyerPostalAddress().get(0).getBT0052BuyerCity().get(0).getValue());
            sedoCessionario.setNazione(invoice.getBG0007Buyer().get(0).getBG0008BuyerPostalAddress().get(0).getBT0055BuyerCountryCode().get(0).getValue().getIso2charCode());
        } catch (Exception e) {
            errors.add(new RuntimeException(IConstants.ERROR_BUYER_INFORMATION, e));
        }
    }

    private void setCedentePrestatore() {
        CedentePrestatoreType cedentePrestatoreType = factory.createCedentePrestatoreType();
        DatiAnagraficiCedenteType datiAnagraficiCedenteType = factory.createDatiAnagraficiCedenteType();
        AnagraficaType anagraficaCedente = factory.createAnagraficaType();
        datiAnagraficiCedenteType.setAnagrafica(anagraficaCedente);
        IdFiscaleType idFiscaleSeller = factory.createIdFiscaleType();
        datiAnagraficiCedenteType.setIdFiscaleIVA(idFiscaleSeller);
        datiAnagraficiCedenteType.setRegimeFiscale(RegimeFiscaleType.RF_01);
        cedentePrestatoreType.setDatiAnagrafici(datiAnagraficiCedenteType);
        IndirizzoType sedoCedente = factory.createIndirizzoType();
        cedentePrestatoreType.setSede(sedoCedente);
        fatturaElettronicaHeader.setCedentePrestatore(cedentePrestatoreType);

        try {
            anagraficaCedente.setDenominazione(invoice.getBG0004Seller().get(0).getBT0027SellerName().get(0).getValue());

            String sellerVatId = invoice.getBG0004Seller().get(0).getBT0031SellerVatIdentifier().get(0).getValue();
            idFiscaleSeller.setIdCodice(Cen2FattPAConverterUtils.getCodeFromVATString(sellerVatId));
            idFiscaleSeller.setIdPaese(Cen2FattPAConverterUtils.getCountryFromVATString(sellerVatId));

            sedoCedente.setIndirizzo(invoice.getBG0004Seller().get(0).getBG0005SellerPostalAddress().get(0).getBT0035SellerAddressLine1().get(0).getValue());
            sedoCedente.setCAP(invoice.getBG0004Seller().get(0).getBG0005SellerPostalAddress().get(0).getBT0038SellerPostCode().get(0).getValue());
            sedoCedente.setComune(invoice.getBG0004Seller().get(0).getBG0005SellerPostalAddress().get(0).getBT0037SellerCity().get(0).getValue());
            sedoCedente.setNazione(invoice.getBG0004Seller().get(0).getBG0005SellerPostalAddress().get(0).getBT0040SellerCountryCode().get(0).getValue().getIso2charCode());
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
