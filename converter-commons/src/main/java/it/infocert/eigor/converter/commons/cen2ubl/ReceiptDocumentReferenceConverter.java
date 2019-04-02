package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class ReceiptDocumentReferenceConverter extends FirstLevelElementsConverter {

    @Override
    public void customMap(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        if (!invoice.getBT0015ReceivingAdviceReference().isEmpty()) {
            final Element despatchDocumentReference = new Element("ReceiptDocumentReference");
            final String value = invoice.getBT0015ReceivingAdviceReference(0).getValue();
            final Element id = new Element("ID").setText(value);
            despatchDocumentReference.addContent(id);
            root.addContent(despatchDocumentReference);
        }
    }
}
