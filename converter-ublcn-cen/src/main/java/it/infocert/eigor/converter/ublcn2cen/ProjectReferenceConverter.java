package it.infocert.eigor.converter.ublcn2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0011ProjectReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProjectReferenceConverter extends CustomConverterUtils implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(ProjectReferenceConverter.class);

    public ConversionResult<BG0000Invoice> toBG0011(Document document, BG0000Invoice invoice, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        BT0011ProjectReference bt0011;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> projectReferences = findNamespaceChildren(rootElement, namespacesInScope, "AdditionalDocumentReference");

        for (Element elemProj : projectReferences) {
            Element documentTypeCode = findNamespaceChild(elemProj, namespacesInScope, "DocumentTypeCode");
            if (documentTypeCode != null && documentTypeCode.getText().equals("50")) {
                Element idRef = findNamespaceChild(elemProj, namespacesInScope, "ID");
                if (idRef != null) {
                    bt0011 = new BT0011ProjectReference(idRef.getText());
                    invoice.getBT0011ProjectReference().add(bt0011);
                }
            }
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0011(document, cenInvoice, errors, callingLocation);
    }
}
