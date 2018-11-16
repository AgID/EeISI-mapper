package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0007Buyer;
import it.infocert.eigor.model.core.model.BT0046BuyerIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0049BuyerElectronicAddressAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BTBG;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BuyerConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0007(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        Element bg7 = rootElement.getChild("BG-7");

        if(Objects.nonNull(bg7)) {
            if (invoice.getBG0007Buyer().isEmpty()) {
                invoice.getBG0007Buyer().add(new BG0007Buyer());
            }
            final BG0007Buyer buyer = invoice.getBG0007Buyer().get(0);

            final List<Element> bt46s = rootElement.getChild("BG-7").getChildren("BT-46");
            bt46s.forEach(bt46 -> {
                final Optional<BTBG> bt = ConverterUtils.getBt.apply("BT0046", bt46);
                if(bt.isPresent()) {
                    buyer.getBT0046BuyerIdentifierAndSchemeIdentifier().add((BT0046BuyerIdentifierAndSchemeIdentifier) bt.get());
                }
            });

            final List<Element> bt47s = rootElement.getChild("BG-7").getChildren("BT-47");
            bt47s.forEach(bt47 -> {
                final Optional<BTBG> bt = ConverterUtils.getBt.apply("BT0047", bt47);
                if(bt.isPresent()) {
                    buyer.getBT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier().add((BT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier) bt.get());
                }
            });

            final List<Element> bt49s = rootElement.getChild("BG-7").getChildren("BT-49");
            bt49s.forEach(bt49 -> {
                final Optional<BTBG> bt = ConverterUtils.getBt.apply("BT0049", bt49);
                if(bt.isPresent()) {
                    buyer.getBT0049BuyerElectronicAddressAndSchemeIdentifier().add((BT0049BuyerElectronicAddressAndSchemeIdentifier) bt.get());
                }
            });
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0007(document, cenInvoice, errors);
    }
}
