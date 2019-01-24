package it.infocert.eigor.converter.xmlcen2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0018InvoicedObjectIdentifierAndSchemeIdentifier;
import it.infocert.eigor.model.core.model.BTBG;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;
import java.util.Optional;

public class AdditionalSupportingDocumentsConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0025(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();

        final List<Element> bt18s = rootElement.getChildren("BT-18");
        bt18s.forEach(bt18 -> {
            final Optional<BTBG> bt = ConverterUtils.getBt.apply("BT0018", bt18);
            if(bt.isPresent()) {
                invoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier().add((BT0018InvoicedObjectIdentifierAndSchemeIdentifier) bt.get());
            }
        });

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBG0025(document, cenInvoice, errors);
    }
}
