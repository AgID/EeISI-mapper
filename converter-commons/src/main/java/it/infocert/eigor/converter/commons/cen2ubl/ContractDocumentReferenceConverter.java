package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class ContractDocumentReferenceConverter implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

        Element root = document.getRootElement();
        if (root != null) {

            if (!invoice.getBT0012ContractReference().isEmpty()) {
                Element contractDocumentReference = new Element("ContractDocumentReference");
                Element id = new Element("ID");
                contractDocumentReference.addContent(id);
                id.setText(invoice.getBT0012ContractReference(0).getValue());
                root.addContent(contractDocumentReference);
            }
        }
    }
}
