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

        final Element orderReference = new Element("OrderReference");
        if (!invoice.getBT0013PurchaseOrderReference().isEmpty()) {
            final String value = invoice.getBT0013PurchaseOrderReference(0).getValue();
            final Element id = new Element("ID").setText(value);
            orderReference.addContent(id);
        }

        if (!invoice.getBT0014SalesOrderReference().isEmpty()) {
            final String value = invoice.getBT0014SalesOrderReference(0).getValue();
            final Element salesOrderID = new Element("SalesOrderID").setText(value);
            orderReference.addContent(salesOrderID);
        }
        root.addContent(orderReference);
    }
}
