package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.Abstract2CenConverter;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.model.core.model.*;
import net.sf.saxon.functions.Abs;
import org.reflections.Reflections;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A fake conversion used to lay out the API general structure.
 */
public class FakeToCenConversion extends Abstract2CenConverter {

    public FakeToCenConversion() {
    }

    public FakeToCenConversion(Reflections reflections) {
        super(reflections);
    }

    @Override public ConversionResult convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {

        BG0006SellerContact sellerContact = new BG0006SellerContact();
        sellerContact.getBT0043SellerContactEmailAddress().add( new BT0043SellerContactEmailAddress("johm@email.com") );

        BG0004Seller seller = new BG0004Seller();
        seller.getBT0029SellerIdentifierAndSchemeIdentifier().add( new BT0029SellerIdentifierAndSchemeIdentifier("IT001122") );
        seller.getBT0032SellerTaxRegistrationIdentifier().add( new BT0032SellerTaxRegistrationIdentifier("IT001122") );
        seller.getBG0006SellerContact().add(sellerContact);

        BG0000Invoice cenInvoice = new BG0000Invoice();
        cenInvoice.getBG0004Seller().add(seller);

        return (ConversionResult<BG0000Invoice>) new ConversionResult(cenInvoice);
    }

    @Override public boolean support(String format) {
        return format!=null && "fake".equals(format);
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>( Arrays.asList("fake") );
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

}
