package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0013DeliveryInformation;
import it.infocert.eigor.model.core.model.BT0071DeliverToLocationIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BTBG;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DeliveryInformationConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0013(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        Element bg13 = rootElement.getChild("BG-13");

        if(Objects.nonNull(bg13)) {
            if (invoice.getBG0013DeliveryInformation().isEmpty()) {
                invoice.getBG0013DeliveryInformation().add(new BG0013DeliveryInformation());
            }
            final BG0013DeliveryInformation deliveryInformation = invoice.getBG0013DeliveryInformation().get(0);
            
            final List<Element> bt71s = rootElement.getChild("BG-13").getChildren("BT-71");
            bt71s.forEach(bt71 -> {
                final Optional<BTBG> bt = ConverterUtils.getBt.apply("BT0071", bt71);
                if(bt.isPresent()) {
                    deliveryInformation.getBT0071DeliverToLocationIdentifierAndSchemeIdentifier().add((BT0071DeliverToLocationIdentifierAndSchemeIdentifier) bt.get());
                }
            });
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0013(document, cenInvoice, errors);
    }
}
