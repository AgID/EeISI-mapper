package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0018InvoicedObjectIdentifierAndSchemeIdentifier;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BT18Converter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(BT18Converter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        Element root = document.getRootElement();
        if (root != null) {
            if (!cenInvoice.getBT0011ProjectReference().isEmpty()) {

                List<BT0018InvoicedObjectIdentifierAndSchemeIdentifier> bt0018 = cenInvoice.getBT0018InvoicedObjectIdentifierAndSchemeIdentifier();
                for (BT0018InvoicedObjectIdentifierAndSchemeIdentifier elemBt18 : bt0018) {
                    Element additionalDocument = new Element("AdditionalDocumentReference");
                    root.addContent(additionalDocument);

                    Element id = new Element("ID");
                    if (elemBt18 != null && elemBt18.getValue() != null && elemBt18.getValue().getIdentifier() != null) {
                        id.setText(elemBt18.getValue().getIdentifier());
                    }
                    if (elemBt18 != null && elemBt18.getValue() != null && elemBt18.getValue().getIdentificationSchema() != null) {
                        id.setAttribute("schemeID", elemBt18.getValue().getIdentificationSchema());
                    }
                    Element documentTypeCode = new Element("DocumentTypeCode");
                    documentTypeCode.setText("130");

                    additionalDocument.addContent(id);
                    additionalDocument.addContent(documentTypeCode);
                }
            }
        }
    }
}
