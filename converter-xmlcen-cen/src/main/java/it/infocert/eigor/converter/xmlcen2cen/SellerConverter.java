package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0004Seller;
import it.infocert.eigor.model.core.model.BT0029SellerIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0034SellerElectronicAddressAndSchemeIdentifier;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;
import java.util.Objects;

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
                final BT0029SellerIdentifierAndSchemeIdentifier sellerIdentifierAndSchemeIdentifier;
                if(Objects.nonNull(bt29.getAttribute("scheme"))) {
                    final String scheme = bt29.getAttribute("scheme").getValue();
                    sellerIdentifierAndSchemeIdentifier = new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier(scheme, bt29.getText()));
                } else {
                    sellerIdentifierAndSchemeIdentifier = new BT0029SellerIdentifierAndSchemeIdentifier(new Identifier(bt29.getText()));
                }
                seller.getBT0029SellerIdentifierAndSchemeIdentifier().add(sellerIdentifierAndSchemeIdentifier);
            });

            final List<Element> bt30s = rootElement.getChild("BG-4").getChildren("BT-30");
            bt30s.forEach(bt30 -> {
                final BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier sellerIdentifierAndSchemeIdentifier;
                if(Objects.nonNull(bt30.getAttribute("scheme"))) {
                    final String scheme = bt30.getAttribute("scheme").getValue();
                    sellerIdentifierAndSchemeIdentifier = new BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(new Identifier(scheme, bt30.getText()));
                } else {
                    sellerIdentifierAndSchemeIdentifier = new BT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier(new Identifier(bt30.getText()));
                }
                seller.getBT0030SellerLegalRegistrationIdentifierAndSchemeIdentifier().add(sellerIdentifierAndSchemeIdentifier);
            });

            final List<Element> bt34s = rootElement.getChild("BG-4").getChildren("BT-34");
            bt34s.forEach(bt34 -> {
                final BT0034SellerElectronicAddressAndSchemeIdentifier sellerIdentifierAndSchemeIdentifier;
                if(Objects.nonNull(bt34.getAttribute("scheme"))) {
                    final String scheme = bt34.getAttribute("scheme").getValue();
                    sellerIdentifierAndSchemeIdentifier = new BT0034SellerElectronicAddressAndSchemeIdentifier(new Identifier(scheme, bt34.getText()));
                } else {
                    sellerIdentifierAndSchemeIdentifier = new BT0034SellerElectronicAddressAndSchemeIdentifier(new Identifier(bt34.getText()));
                }
                seller.getBT0034SellerElectronicAddressAndSchemeIdentifier().add(sellerIdentifierAndSchemeIdentifier);
            });
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0004(document, cenInvoice, errors);
    }
}
