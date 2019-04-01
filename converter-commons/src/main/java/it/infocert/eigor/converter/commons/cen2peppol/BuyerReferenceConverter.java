package it.infocert.eigor.converter.commons.cen2peppol;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

import static it.infocert.eigor.model.core.InvoiceUtils.evalExpression;

public class BuyerReferenceConverter implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        Element root = document.getRootElement();
        String bt10 = evalExpression(() -> cenInvoice.getBT0010BuyerReference(0).getValue());

        if (bt10 != null) {
            root.addContent(new Element("BuyerReference").setText(bt10));
        }
    }
}
