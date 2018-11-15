package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0007Buyer;
import it.infocert.eigor.model.core.model.BT0046BuyerIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0049BuyerElectronicAddressAndSchemeIdentifier;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;
import java.util.Objects;

public class BuyerConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0004(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        Element bg4 = rootElement.getChild("BG-7");

        if(Objects.nonNull(bg4)) {
            if (invoice.getBG0007Buyer().isEmpty()) {
                invoice.getBG0007Buyer().add(new BG0007Buyer());
            }
            final BG0007Buyer buyer = invoice.getBG0007Buyer().get(0);

            final List<Element> bt46s = rootElement.getChild("BG-7").getChildren("BT-46");
            bt46s.forEach(bt46 -> {
                final BT0046BuyerIdentifierAndSchemeIdentifier buyerIdentifierAndSchemeIdentifier;
                if(Objects.nonNull(bt46.getAttribute("scheme"))) {
                    final String scheme = bt46.getAttribute("scheme").getValue();
                    buyerIdentifierAndSchemeIdentifier = new BT0046BuyerIdentifierAndSchemeIdentifier(new Identifier(scheme, bt46.getText()));
                } else {
                    buyerIdentifierAndSchemeIdentifier = new BT0046BuyerIdentifierAndSchemeIdentifier(new Identifier(bt46.getText()));
                }
                buyer.getBT0046BuyerIdentifierAndSchemeIdentifier().add(buyerIdentifierAndSchemeIdentifier);
            });

            final List<Element> bt47s = rootElement.getChild("BG-7").getChildren("BT-47");
            bt47s.forEach(bt47 -> {
                final BT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier buyerLegalRegistrationIdentifierAndSchemeIdentifier;
                if(Objects.nonNull(bt47.getAttribute("scheme"))) {
                    final String scheme = bt47.getAttribute("scheme").getValue();
                    buyerLegalRegistrationIdentifierAndSchemeIdentifier = new BT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier(new Identifier(scheme, bt47.getText()));
                } else {
                    buyerLegalRegistrationIdentifierAndSchemeIdentifier = new BT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier(new Identifier(bt47.getText()));
                }
                buyer.getBT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier().add(buyerLegalRegistrationIdentifierAndSchemeIdentifier);
            });

            final List<Element> bt49s = rootElement.getChild("BG-7").getChildren("BT-49");
            bt49s.forEach(bt49 -> {
                final BT0049BuyerElectronicAddressAndSchemeIdentifier buyerElectronicAddressAndSchemeIdentifier;
                if(Objects.nonNull(bt49.getAttribute("scheme"))) {
                    final String scheme = bt49.getAttribute("scheme").getValue();
                    buyerElectronicAddressAndSchemeIdentifier = new BT0049BuyerElectronicAddressAndSchemeIdentifier(new Identifier(scheme, bt49.getText()));
                } else {
                    buyerElectronicAddressAndSchemeIdentifier = new BT0049BuyerElectronicAddressAndSchemeIdentifier(new Identifier(bt49.getText()));
                }
                buyer.getBT0049BuyerElectronicAddressAndSchemeIdentifier().add(buyerElectronicAddressAndSchemeIdentifier);
            });
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0004(document, cenInvoice, errors);
    }
}
