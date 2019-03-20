package it.infocert.eigor.converter.cen2peppolcn;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0011ProjectReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProjectReferenceConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(ProjectReferenceConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        Element root = document.getRootElement();
        if (root != null) {
            if (!cenInvoice.getBT0011ProjectReference().isEmpty()) {

                List<BT0011ProjectReference> bt0011 = cenInvoice.getBT0011ProjectReference();
                for (BT0011ProjectReference elemBt11 : bt0011) {
                    Element additionalDocument = new Element("AdditionalDocumentReference");
                    root.addContent(additionalDocument);

                    Element id = new Element("ID");
                    id.setText(elemBt11.getValue());
                    Element documentTypeCode = new Element("DocumentTypeCode");
                    documentTypeCode.setText("50");

                    additionalDocument.addContent(id);
                    additionalDocument.addContent(documentTypeCode);
                }
            }
        }
    }
}
