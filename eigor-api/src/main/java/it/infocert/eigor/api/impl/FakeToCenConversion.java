package it.infocert.eigor.api.impl;

import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.model.core.model.*;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A fake conversion used to lay out the API general structure.
 */
public class FakeToCenConversion implements ToCenConversion {

    @Override public BG0000Invoice convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {

        BG0006SellerContact sellerContact = new BG0006SellerContact();
        sellerContact.getBT0043SellerContactEmailAddress().add( new BT0043SellerContactEmailAddress("johm@email.com") );

        BG0004Seller seller = new BG0004Seller();
        seller.getBT0029SellerIdentifierAndSchemeIdentifier().add( new BT0029SellerIdentifierAndSchemeIdentifier("IT001122") );
        seller.getBT0032SellerTaxRegistrationIdentifier().add( new BT0032SellerTaxRegistrationIdentifier("IT001122") );
        seller.getBG0006SellerContact().add(sellerContact);

        BG0000Invoice cenInvoice = new BG0000Invoice();
        cenInvoice.getBG0004Seller().add(seller);

        return cenInvoice;
    }

    @Override public boolean support(String format) {
        return format!=null && "fake".equals(format);
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>( Arrays.asList("fake") );
    }

}
