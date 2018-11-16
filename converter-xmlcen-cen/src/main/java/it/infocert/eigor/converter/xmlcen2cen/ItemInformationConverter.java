package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;
import it.infocert.eigor.model.core.model.BG0031ItemInformation;
import it.infocert.eigor.model.core.model.BT0157ItemStandardIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier;
import org.codehaus.plexus.util.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;
import java.util.Objects;

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
                    final BT0157ItemStandardIdentifierAndSchemeIdentifier itemStandardIdentifierAndSchemeIdentifier;
                    if(Objects.nonNull(bt157.getAttribute("scheme")) && StringUtils.isNotEmpty(bt157.getAttribute("scheme").getValue())) {
                        final String scheme = bt157.getAttribute("scheme").getValue();
                        itemStandardIdentifierAndSchemeIdentifier = new BT0157ItemStandardIdentifierAndSchemeIdentifier(new Identifier(scheme, bt157.getText()));
                    } else {
                        itemStandardIdentifierAndSchemeIdentifier = new BT0157ItemStandardIdentifierAndSchemeIdentifier(new Identifier(bt157.getText()));
                    }
                    itemInformation.getBT0157ItemStandardIdentifierAndSchemeIdentifier().add(itemStandardIdentifierAndSchemeIdentifier);
                });

                final List<Element> bt158s = bg25.getChild("BG-31").getChildren("BT-158");
                bt158s.forEach(bt158 -> {
                    final BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier itemStandardIdentifierAndSchemeIdentifier;
                    if(Objects.nonNull(bt158.getAttribute("scheme")) && StringUtils.isNotEmpty(bt158.getAttribute("scheme").getValue())) {
                        final String scheme = bt158.getAttribute("scheme").getValue();
                        if(Objects.nonNull(bt158.getAttribute("version")) && StringUtils.isNotEmpty(bt158.getAttribute("version").getValue())) {
                            final String version = bt158.getAttribute("version").getValue();
                            itemStandardIdentifierAndSchemeIdentifier = new BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier(new Identifier(scheme, version, bt158.getText()));
                        } else {
                            itemStandardIdentifierAndSchemeIdentifier = new BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier(new Identifier(scheme, bt158.getText()));
                        }
                    } else {
                        itemStandardIdentifierAndSchemeIdentifier = new BT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier(new Identifier(bt158.getText()));
                    }
                    itemInformation.getBT0158ItemClassificationIdentifierAndSchemeIdentifierAndSchemeVersionIdentifier().add(itemStandardIdentifierAndSchemeIdentifier);
                });
            }
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0031(document, cenInvoice, errors);
    }
}
