package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class OrderReferenceConverter extends FirstLevelElementsConverter {
    
    @Override
    public void customMap(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        if (!invoice.getBT0013PurchaseOrderReference().isEmpty()) {
            final String value = invoice.getBT0013PurchaseOrderReference(0).getValue();
            final Element orderReference = new Element("OrderReference");
            final Element id = new Element("ID").setText(value);
            root.addContent(orderReference.setContent(id));
        }

    }

}
