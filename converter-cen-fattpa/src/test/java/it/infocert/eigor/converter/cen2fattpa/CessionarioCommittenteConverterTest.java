package it.infocert.eigor.converter.cen2fattpa;

import com.google.common.collect.Lists;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.fattpa.commons.models.CessionarioCommittenteType;
import it.infocert.eigor.fattpa.commons.models.FatturaElettronicaBodyType;
import it.infocert.eigor.fattpa.commons.models.FatturaElettronicaHeaderType;
import it.infocert.eigor.fattpa.commons.models.FatturaElettronicaType;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.model.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CessionarioCommittenteConverterTest {

    private BG0000Invoice invoice;
    private CessionarioCommittenteConverter sut;

    @Before
    public void setUp() {
        invoice = new BG0000Invoice();
        sut = new CessionarioCommittenteConverter();
    }


    @Test
    public void shouldConvertBT53FromItalianCode() {
        this.invoice = setUpInvoice(invoice);
        final BG0008BuyerPostalAddress address = this.invoice.getBG0007Buyer(0).getBG0008BuyerPostalAddress(0);
        final String code = "20090";
        address.getBT0053BuyerPostCode().add(new BT0053BuyerPostCode(code));
        address.getBT0055BuyerCountryCode().add(new BT0055BuyerCountryCode(Iso31661CountryCodes.IT));
        final FatturaElettronicaType fattura = setUpFatturaElettronica(new FatturaElettronicaType());
        sut.map(invoice, fattura, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.FATTPA_OUT, null);
        final String cap = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getSede().getCAP();
        assertEquals(code, cap);
    }

    @Test
    public void shouldConvertBT53FromNonItalianCodeSmallerThanFive() {
        this.invoice = setUpInvoice(invoice);
        final BG0008BuyerPostalAddress address = this.invoice.getBG0007Buyer(0).getBG0008BuyerPostalAddress(0);
        final String code = "20090";
        address.getBT0053BuyerPostCode().add(new BT0053BuyerPostCode(code));
        address.getBT0055BuyerCountryCode().add(new BT0055BuyerCountryCode(Iso31661CountryCodes.DK));
        final FatturaElettronicaType fattura = setUpFatturaElettronica(new FatturaElettronicaType());
        sut.map(invoice, fattura, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.FATTPA_OUT, null);
        final String cap = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getSede().getCAP();
        assertEquals(code, cap);
    }

    @Test
    public void shouldConvertBT53FromNonItalianCodeLargerThanFive() {
        this.invoice = setUpInvoice(invoice);
        final BG0008BuyerPostalAddress address = this.invoice.getBG0007Buyer(0).getBG0008BuyerPostalAddress(0);
        final String code = "1234567890";
        address.getBT0053BuyerPostCode().add(new BT0053BuyerPostCode(code));
        address.getBT0055BuyerCountryCode().add(new BT0055BuyerCountryCode(Iso31661CountryCodes.DK));
        final FatturaElettronicaType fattura = setUpFatturaElettronica(new FatturaElettronicaType());
        sut.map(invoice, fattura, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.FATTPA_OUT, null);
        final String cap = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getSede().getCAP();
        assertEquals("99999", cap);
    }

    @Test
    public void shouldConvertBT54FromItalian() {
        this.invoice = setUpInvoice(invoice);
        final BG0008BuyerPostalAddress address = this.invoice.getBG0007Buyer(0).getBG0008BuyerPostalAddress(0);
        final String subdivision = "PC";
        address.getBT0054BuyerCountrySubdivision().add(new BT0054BuyerCountrySubdivision(subdivision));
        address.getBT0055BuyerCountryCode().add(new BT0055BuyerCountryCode(Iso31661CountryCodes.IT));
        final FatturaElettronicaType fattura = setUpFatturaElettronica(new FatturaElettronicaType());
        sut.map(invoice, fattura, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.FATTPA_OUT, null);
        final String provincia = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getSede().getProvincia();
        assertEquals(subdivision, provincia);
    }

    @Test
    public void shouldConvertBT54FromNonItalian() {
        this.invoice = setUpInvoice(invoice);
        final BG0008BuyerPostalAddress address = this.invoice.getBG0007Buyer(0).getBG0008BuyerPostalAddress(0);
        final String subdivision = "PC";
        address.getBT0054BuyerCountrySubdivision().add(new BT0054BuyerCountrySubdivision(subdivision));
        address.getBT0055BuyerCountryCode().add(new BT0055BuyerCountryCode(Iso31661CountryCodes.DK));
        final FatturaElettronicaType fattura = setUpFatturaElettronica(new FatturaElettronicaType());
        sut.map(invoice, fattura, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.FATTPA_OUT, null);
        final String provincia = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getSede().getProvincia();
        assertNull(provincia);
    }

    private BG0000Invoice setUpInvoice(final BG0000Invoice invoice) {
        final BG0004Seller seller = new BG0004Seller();
        invoice.getBG0004Seller().add(seller);
        final BG0007Buyer buyer = new BG0007Buyer();
        final BG0008BuyerPostalAddress buyerPostalAddress = new BG0008BuyerPostalAddress();
        buyer.getBG0008BuyerPostalAddress().add(buyerPostalAddress);
        invoice.getBG0007Buyer().add(buyer);

        return invoice;
    }

    private FatturaElettronicaType setUpFatturaElettronica(final FatturaElettronicaType fatturaElettronica) {
        final FatturaElettronicaHeaderType header = new FatturaElettronicaHeaderType();
        final FatturaElettronicaBodyType body = new FatturaElettronicaBodyType();
        final CessionarioCommittenteType cessionarioCommittente = new CessionarioCommittenteType();
        header.setCessionarioCommittente(cessionarioCommittente);
        fatturaElettronica.setFatturaElettronicaHeader(header);
        fatturaElettronica.getFatturaElettronicaBody().add(body);
        return fatturaElettronica;
    }

}
