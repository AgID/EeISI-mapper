package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.conversion.StringToJavaLocalDateConverter;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.reflections.Reflections;

import java.util.List;

/**
 * The Preceding Invoice Reference Custom Converter
 */
public class PrecedingInvoiceReferenceConverter extends CustomConverter {

    public PrecedingInvoiceReferenceConverter(Reflections reflections, ConversionRegistry conversionRegistry) {
        super(reflections, conversionRegistry);
    }

    public ConversionResult<BG0000Invoice> toBG0003(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        BG0003PrecedingInvoiceReference bg0003 = null;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> invoiceReferenceDocuments = null;
        Element child = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");

        if (child != null) {
            Element child1 = findNamespaceChild(child, namespacesInScope, "ApplicableHeaderTradeSettlement");
            if (child1 != null) {
                invoiceReferenceDocuments = findNamespaceChildren(child1, namespacesInScope, "InvoiceReferencedDocument");

                for(Element elem : invoiceReferenceDocuments) {
                    bg0003 = new BG0003PrecedingInvoiceReference();
                    Element issuerAssignedID = findNamespaceChild(elem, namespacesInScope, "IssuerAssignedID");
                    Element formattedIssueDateTime = findNamespaceChild(elem, namespacesInScope,"FormattedIssueDateTime");
                    Element dateTimeString = null;

                    if (formattedIssueDateTime != null) {
                        dateTimeString = findNamespaceChild(formattedIssueDateTime, namespacesInScope, "DateTimeString");
                    }
                    if (issuerAssignedID != null) {
                        BT0025PrecedingInvoiceReference bt0025 = new BT0025PrecedingInvoiceReference(issuerAssignedID.getText());
                        bg0003.getBT0025PrecedingInvoiceReference().add(bt0025);
                    }
                    if (dateTimeString != null) {
                        BT0026PrecedingInvoiceIssueDate bt0026 = new BT0026PrecedingInvoiceIssueDate(new StringToJavaLocalDateConverter("yyyyMMdd").convert(dateTimeString.getText()));
                        bg0003.getBT0026PrecedingInvoiceIssueDate().add(bt0026);
                    }

                    invoice.getBG0003PrecedingInvoiceReference().add(bg0003);
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }
}