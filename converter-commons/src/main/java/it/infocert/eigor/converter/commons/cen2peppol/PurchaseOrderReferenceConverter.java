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

public class PurchaseOrderReferenceConverter implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        Element root = document.getRootElement();

        String bt13 = evalExpression(() -> cenInvoice.getBT0013PurchaseOrderReference(0).getValue());
        String bt14 = evalExpression(() -> cenInvoice.getBT0014SalesOrderReference(0).getValue());
        String bt10 = evalExpression(() -> cenInvoice.getBT0010BuyerReference(0).getValue());

        Element orderReference = new Element("OrderReference");
        if (bt13 != null) {
            orderReference.addContent(new Element("ID").setText(bt13));
        } else {
            if (bt10 == null) {
                orderReference.addContent(new Element("ID").setText("N/A"));
            } else {
                orderReference.addContent(new Element("ID").setText(bt10));
            }
        }

        if (bt14 != null) {
            orderReference.addContent(new Element("SalesOrderID").setText(bt14));
        }

        root.addContent(orderReference);
    }
}
