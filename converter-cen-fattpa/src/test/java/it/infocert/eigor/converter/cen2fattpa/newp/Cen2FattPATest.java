package it.infocert.eigor.converter.cen2fattpa.newp;

import it.infocert.eigor.api.BinaryConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.DefaultEigorConfigurationLoader;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.converter.cen2fattpa.Cen2FattPA;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.model.*;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class Cen2FattPATest {
    private static final Logger log = LoggerFactory.getLogger(Cen2FattPATest.class);

    private Cen2FattPA converter;

    @Before
    public void setUp() throws ConfigurationException {
        EigorConfiguration conf = new DefaultEigorConfigurationLoader().loadConfiguration();
        converter = new Cen2FattPA(new Reflections("it.infocert"), conf);
        converter.configure();
    }

    @Test
    public void convertTest() throws URISyntaxException, FileNotFoundException, SyntaxErrorInInvoiceFormatException {
        BinaryConversionResult conversionResult = converter.convert(createInvoice());

        assertNotNull(conversionResult.getResult());
    }

    @Test
    public void shouldSupportFatturaPA() {
        assertThat(converter.support("fatturapa"), is(true));
    }

    @Test
    public void shouldSupportedFormatsFatturaPA() {
        assertThat(converter.getSupportedFormats(), contains("fatturapa"));
    }

    private BG0000Invoice createInvoice() {
        BG0000Invoice invoice = new BG0000Invoice();
        invoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber("1"));
        BG0004Seller seller = new BG0004Seller();
        seller.getBT0027SellerName().add(new BT0027SellerName("seller-name"));
        invoice.getBG0004Seller().add(seller);
        BG0025InvoiceLine invoiceLine = new BG0025InvoiceLine();
        invoiceLine.getBT0126InvoiceLineIdentifier().add(new BT0126InvoiceLineIdentifier("1"));
        BG0029PriceDetails priceDetails = new BG0029PriceDetails();
        priceDetails.getBT0149ItemPriceBaseQuantity().add(new BT0149ItemPriceBaseQuantity(1d));
        priceDetails.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().add(new BT0150ItemPriceBaseQuantityUnitOfMeasureCode(UnitOfMeasureCodes.ACRE_ACR));
        invoiceLine.getBG0029PriceDetails().add(priceDetails);
        invoiceLine.getBT0131InvoiceLineNetAmount().add(new BT0131InvoiceLineNetAmount(12d));
        invoice.getBG0025InvoiceLine().add(invoiceLine);
        return invoice;
    }

}