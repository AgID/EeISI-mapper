package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.AbstractToCenConverter;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static it.infocert.eigor.api.conversion.ConversionRegistry.DEFAULT_REGISTRY;

/**
 * A fake conversion used to lay out the API general structure.
 */
public class FakeToCenConversion extends AbstractToCenConverter {

    public FakeToCenConversion(IReflections reflections, EigorConfiguration configuration) {
        super(reflections, DEFAULT_REGISTRY, configuration, null);
    }

    @Override
    public ConversionResult convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {

        BG0006SellerContact sellerContact = new BG0006SellerContact();
        sellerContact.getBT0043SellerContactEmailAddress().add(new BT0043SellerContactEmailAddress("johm@email.com"));

        BG0004Seller seller = new BG0004Seller();
        seller.getBT0029SellerIdentifierAndSchemeIdentifier().add(new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier("", "IT001122")));
        seller.getBT0032SellerTaxRegistrationIdentifier().add(new BT0032SellerTaxRegistrationIdentifier("IT001122"));
        seller.getBG0006SellerContact().add(sellerContact);

        BG0000Invoice cenInvoice = new BG0000Invoice();
        cenInvoice.getBG0004Seller().add(seller);

        return (ConversionResult<BG0000Invoice>) new ConversionResult(cenInvoice);
    }

    @Override
    public boolean support(String format) {
        return format != null && "fake".equals(format);
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>(Arrays.asList("fake"));
    }

    @Override
    public String getMappingRegex() {
        return ".+";
    }

    @Override
    public String getOne2OneMappingPath() {
        return "/tmp/fakeone2one.properties";
    }

    @Override
    protected String getMany2OneMappingPath() {
        return "/tmp/fakemany2one.properties";
    }

    @Override
    protected String getOne2ManyMappingPath() {
        return "/tmp/fakeone2many.properties";
    }

    @Override
    protected String getCustomMappingPath() {
        return "";
    }

    @Override
    public String getName() {
        return "fake-cen";
    }
}
