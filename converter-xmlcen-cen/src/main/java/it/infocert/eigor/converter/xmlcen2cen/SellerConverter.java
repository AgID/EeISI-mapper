package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BT0029SellerIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0034SellerElectronicAddressAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BTBG;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SellerConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0004(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        Element bg4 = rootElement.getChild("BG-4");

        if(Objects.nonNull(bg4)) {
            if (invoice.getBG0004Seller().isEmpty()) {
                invoice.getBG0004Seller().add(new BG0004Seller());
            }
            final BG0004Seller seller = invoice.getBG0004Seller().get(0);

            final List<Element> bt29s = rootElement.getChild("BG-4").getChildren("BT-29");
            bt29s.forEach(bt29 -> {
                final Optional<BTBG> bt = ConverterUtils.getBt.apply("BT0029", bt29);
                if(bt.isPresent()) {
                    seller.getBT0029SellerIdentifierAndSchemeIdentifier().add((BT0029SellerIdentifierAndSchemeIdentifier) bt.get());
                }
            });

            final List<Element> bt30s = rootElement.getChild("BG-4").getChildren("BT-30");
            bt30s.forEach(bt30 -> {
                final BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier sellerIdentifierAndSchemeIdentifier;
                final Optional<BTBG> bt = ConverterUtils.getBt.apply("BT0030", bt30);
                if(bt.isPresent()) {
                    seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier().add((BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier) bt.get());
                }
            });

            final List<Element> bt34s = rootElement.getChild("BG-4").getChildren("BT-34");
            bt34s.forEach(bt34 -> {
                final Optional<BTBG> bt = ConverterUtils.getBt.apply("BT0034", bt34);
                if(bt.isPresent()) {
                    seller.getBT0034SellerElectronicAddressAndSchemeIdentifier().add((BT0034SellerElectronicAddressAndSchemeIdentifier) bt.get());
                }
            });
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBG0004(document, cenInvoice, errors);
    }
}
