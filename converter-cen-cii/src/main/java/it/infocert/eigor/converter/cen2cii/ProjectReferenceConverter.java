package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Project Reference Custom Converter
 */
public class ProjectReferenceConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        if (!cenInvoice.getBT0011ProjectReference().isEmpty()) {

            Element rootElement = document.getRootElement();
            List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

            Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
            Element applicableHeaderTradeAgreement = null;

            if (supplyChainTradeTransaction == null) {
                supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rootElement.getNamespace("rsm"));
                applicableHeaderTradeAgreement = new Element("ApplicableHeaderTradeAgreement", rootElement.getNamespace("ram"));
                supplyChainTradeTransaction.addContent(applicableHeaderTradeAgreement);
                rootElement.addContent(supplyChainTradeTransaction);
            } else {
                applicableHeaderTradeAgreement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeAgreement");
                if (applicableHeaderTradeAgreement == null) {
                    applicableHeaderTradeAgreement = new Element("ApplicableHeaderTradeAgreement", rootElement.getNamespace("ram"));
                    supplyChainTradeTransaction.addContent(applicableHeaderTradeAgreement);
                }
            }

            Element specifiedProcuringProject = new Element("SpecifiedProcuringProject", rootElement.getNamespace("ram"));
            Element id = new Element("ID", rootElement.getNamespace("ram"));
            id.setText(cenInvoice.getBT0011ProjectReference(0).getValue());
            specifiedProcuringProject.addContent(id);
            applicableHeaderTradeAgreement.addContent(specifiedProcuringProject);
        }
    }
}
