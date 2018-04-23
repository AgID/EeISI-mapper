package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PaymentTermsConverter implements CustomMapping<Document> {
    private final static Logger logger = LoggerFactory.getLogger(PaymentTermsConverter.class);

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        Element root = document.getRootElement();
        if (root != null) {
            if (!invoice.getBT0020PaymentTerms().isEmpty()) {
                final Element paymentTerms = new Element("PaymentTerms");
                final String termsValue = invoice.getBT0020PaymentTerms(0).getValue();
                final Element note = new Element("Note").setText(termsValue);
                paymentTerms.addContent(note);
                root.addContent(paymentTerms);
                logger.info("Mapped BT-20 to PaymentTerms/Note with value {}", termsValue);
            } else {
                logger.info("No BT-20 found");
            }
        }
    }
}
