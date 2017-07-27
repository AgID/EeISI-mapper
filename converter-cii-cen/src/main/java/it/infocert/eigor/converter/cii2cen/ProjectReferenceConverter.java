package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0011ProjectReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.reflections.Reflections;

import java.util.List;

/**
 * The Project Refernce Custom Converter
 */
public class ProjectReferenceConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBT0011(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
        Element child = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");

        if (child != null) {
            Element child1 = findNamespaceChild(child, namespacesInScope, "ApplicableHeaderTradeAgreement");
            if (child1 != null) {
                Element specifiedProcuringProject = findNamespaceChild(child1, namespacesInScope, "SpecifiedProcuringProject");
                if (specifiedProcuringProject != null) {
                    Element id = findNamespaceChild(specifiedProcuringProject, namespacesInScope, "ID");
                    Element name = findNamespaceChild(specifiedProcuringProject, namespacesInScope, "Name");

                    if (id != null) {
                        BT0011ProjectReference bt0011 = null;
                        if (name != null) {
                            bt0011 = new BT0011ProjectReference(id.getText()+" "+name.getText());
                        } else {
                            bt0011 = new BT0011ProjectReference(id.getText()+" Project reference");
                        }
                        invoice.getBT0011ProjectReference().add(bt0011);
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBT0011(document, cenInvoice, errors);
    }
}
