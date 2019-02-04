package it.infocert.eigor.converter.commons.cen2peppol;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BuyerReferenceConverter implements CustomMapping<Document> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        Element root = document.getRootElement();


        {
            final Element buyerReference = new Element("BuyerReference");
            if (cenInvoice.getBT0010BuyerReference().isEmpty()) {
                buyerReference.setText("NA");
            } else {
                buyerReference.setText(cenInvoice.getBT0010BuyerReference(0).getValue());
            }
            root.addContent(buyerReference);
        }

        {
            final Element purchaseOrderReference = new Element("PurchaseOrderReference");
            if (cenInvoice.getBT0013PurchaseOrderReference().isEmpty()) {
                purchaseOrderReference.setText("NA");
            }else{
                purchaseOrderReference.setText(cenInvoice.getBT0013PurchaseOrderReference(0).getValue());
            }
            root.addContent(purchaseOrderReference);
        }

    }
}
