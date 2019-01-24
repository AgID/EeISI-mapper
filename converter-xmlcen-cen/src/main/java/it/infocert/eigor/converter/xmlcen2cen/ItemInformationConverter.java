package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;
import it.infocert.eigor.model.core.model.BG0031ItemInformation;
import it.infocert.eigor.model.core.model.BT0157ItemStandardIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier;
import it.infocert.eigor.model.core.model.BTBG;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ItemInformationConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0031(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        Element bg25 = rootElement.getChild("BG-25");

        if(Objects.nonNull(bg25)) {
            if (invoice.getBG0025InvoiceLine().isEmpty()) {
                invoice.getBG0025InvoiceLine().add(new BG0025InvoiceLine());
            }
            final BG0025InvoiceLine invoiceLine = invoice.getBG0025InvoiceLine().get(0);

            if(Objects.nonNull(invoiceLine)) {
                if (invoiceLine.getBG0031ItemInformation().isEmpty()) {
                    invoiceLine.getBG0031ItemInformation().add(new BG0031ItemInformation());
                }

                final BG0031ItemInformation itemInformation = invoiceLine.getBG0031ItemInformation().get(0);
                final List<Element> bt157s = bg25.getChild("BG-31").getChildren("BT-157");
                bt157s.forEach(bt157 -> {
                    final Optional<BTBG> bt = ConverterUtils.getBt.apply("BT0157", bt157);
                    if(bt.isPresent()) {
                        itemInformation.getBT0157ItemStandardIdentifierAndSchemeIdentifier().add((BT0157ItemStandardIdentifierAndSchemeIdentifier) bt.get());
                    }
                });

                final List<Element> bt158s = bg25.getChild("BG-31").getChildren("BT-158");
                bt158s.forEach(bt158 -> {
                    final Optional<BTBG> bt = ConverterUtils.getBt.apply("BT0158", bt158);
                    if(bt.isPresent()) {
                        itemInformation.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier().add((BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier) bt.get());
                    }
                });
            }
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBG0031(document, cenInvoice, errors);
    }
}
