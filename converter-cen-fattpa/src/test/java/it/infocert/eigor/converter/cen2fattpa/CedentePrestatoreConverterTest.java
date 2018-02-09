package it.infocert.eigor.converter.cen2fattpa;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.cen2fattpa.models.CedentePrestatoreType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaBodyType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaHeaderType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaType;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BT0029SellerIdentifierAndSchemeIdentifier;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CedentePrestatoreConverterTest {

    private BG0000Invoice invoice;
    private CedentePrestatoreConverter sut;

    @Before
    public void setUp() throws Exception {
        invoice = new BG0000Invoice();
        sut = new CedentePrestatoreConverter();
    }

    @Test
    public void shouldConvertBT29WithoutSchemeId() {
        this.invoice = setUpInvoice(invoice);
        final FatturaElettronicaType fattura = setUpFatturaElettronica(new FatturaElettronicaType());
        final String code = "1234567890";
        final BT0029SellerIdentifierAndSchemeIdentifier bt29 = new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier(null, "IT:CF:" + code));
        invoice.getBG0004Seller(0).getBT0029SellerIdentifierAndSchemeIdentifier().add(bt29);
        sut.map(invoice, fattura, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.FATTPA_OUT);
        final String codiceFiscale = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getCodiceFiscale();
        assertEquals(code, codiceFiscale);
    }

    @Test
    public void shouldConvertBT29WithSchemeId() {
        this.invoice = setUpInvoice(invoice);
        final FatturaElettronicaType fattura = setUpFatturaElettronica(new FatturaElettronicaType());
        final String code = "1234567890";
        final BT0029SellerIdentifierAndSchemeIdentifier bt29 = new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier("IT:CF", code));
        invoice.getBG0004Seller(0).getBT0029SellerIdentifierAndSchemeIdentifier().add(bt29);
        sut.map(invoice, fattura, Lists.<IConversionIssue>newArrayList(), ErrorCode.Location.FATTPA_OUT);
        final String codiceFiscale = fattura.getFatturaElettronicaHeader().getCedentePrestatore().getDatiAnagrafici().getCodiceFiscale();
        assertEquals(code, codiceFiscale);
    }

    private BG0000Invoice setUpInvoice(final BG0000Invoice invoice) {
        invoice.getBG0004Seller().add(new BG0004Seller());
        return invoice;
    }

    private FatturaElettronicaType setUpFatturaElettronica(final FatturaElettronicaType fatturaElettronica) {
        final FatturaElettronicaHeaderType header = new FatturaElettronicaHeaderType();
        final FatturaElettronicaBodyType body = new FatturaElettronicaBodyType();
        final CedentePrestatoreType cedentePrestatore = new CedentePrestatoreType();
        header.setCedentePrestatore(cedentePrestatore);
        fatturaElettronica.setFatturaElettronicaHeader(header);
        fatturaElettronica.getFatturaElettronicaBody().add(body);
        return fatturaElettronica;
    }
}