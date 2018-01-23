package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0003PrecedingInvoiceReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Preceding Invoice Reference Custom Converter
 */
public class PrecedingInvoiceReferenceConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        if (!cenInvoice.getBG0003PrecedingInvoiceReference().isEmpty()) {
            Element rootElement = document.getRootElement();
            List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
            Namespace rsmNs = rootElement.getNamespace("rsm");
            Namespace ramNs = rootElement.getNamespace("ram");

            Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
            if (supplyChainTradeTransaction == null) {
                supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rsmNs);
                rootElement.addContent(supplyChainTradeTransaction);
            }

            Element applicableHeaderTradeSettlement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeSettlement");
            if (applicableHeaderTradeSettlement == null) {
                applicableHeaderTradeSettlement = new Element("ApplicableHeaderTradeSettlement", ramNs);
                supplyChainTradeTransaction.addContent(applicableHeaderTradeSettlement);
            }

            for (BG0003PrecedingInvoiceReference bg0003 : cenInvoice.getBG0003PrecedingInvoiceReference()) {
                Element invoiceReferencedDocument = new Element("InvoiceReferencedDocument", ramNs);

                if (!bg0003.getBT0025PrecedingInvoiceReference().isEmpty()) {
                    Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
                    issuerAssignedID.setText(bg0003.getBT0025PrecedingInvoiceReference(0).getValue());
                    invoiceReferencedDocument.addContent(issuerAssignedID);
                }

                if (!bg0003.getBT0026PrecedingInvoiceIssueDate().isEmpty()) {
                    Element formattedIssueDateTime = new Element("FormattedIssueDateTime", ramNs);
                    formattedIssueDateTime.setAttribute("format", "102");
                    try {
                        formattedIssueDateTime.setText(JavaLocalDateToStringConverter.newConverter("yyyyMMdd").convert(bg0003.getBT0026PrecedingInvoiceIssueDate(0).getValue()));
                        invoiceReferencedDocument.addContent(formattedIssueDateTime);
                    } catch (IllegalArgumentException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Invalid date format").action("PrecedingInvoiceReferenceConverter").build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }
                applicableHeaderTradeSettlement.addContent(invoiceReferencedDocument);
            }
        }
    }
}