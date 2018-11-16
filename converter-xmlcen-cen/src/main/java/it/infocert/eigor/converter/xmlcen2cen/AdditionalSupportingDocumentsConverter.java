package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0018InvoicedObjectIdentifierAndSchemeIdentifier;
import org.codehaus.plexus.util.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;
import java.util.Objects;

public class AdditionalSupportingDocumentsConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0025(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();

        final List<Element> bt18s = rootElement.getChildren("BT-18");
        bt18s.forEach(bt18 -> {
            final BT0018InvoicedObjectIdentifierAndSchemeIdentifier invoicedObjectIdentifierAndSchemeIdentifier;
            if(Objects.nonNull(bt18.getAttribute("scheme")) && StringUtils.isNotEmpty(bt18.getAttribute("scheme").getValue())) {
                final String scheme = bt18.getAttribute("scheme").getValue();
                invoicedObjectIdentifierAndSchemeIdentifier = new BT0018InvoicedObjectIdentifierAndSchemeIdentifier(new Identifier(scheme, bt18.getText()));
            } else {
                invoicedObjectIdentifierAndSchemeIdentifier = new BT0018InvoicedObjectIdentifierAndSchemeIdentifier(new Identifier(bt18.getText()));
            }
            invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier().add(invoicedObjectIdentifierAndSchemeIdentifier);
        });

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0025(document, cenInvoice, errors);
    }
}
