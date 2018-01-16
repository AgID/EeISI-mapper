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

            Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
            Element applicableHeaderTradeSettlement = null;

            if (supplyChainTradeTransaction == null) {
                supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rootElement.getNamespace("rsm"));
                applicableHeaderTradeSettlement = new Element("ApplicableHeaderTradeSettlement", rootElement.getNamespace("ram"));
                supplyChainTradeTransaction.addContent(applicableHeaderTradeSettlement);
                rootElement.addContent(supplyChainTradeTransaction);
            } else {
                applicableHeaderTradeSettlement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeSettlement");
                if (applicableHeaderTradeSettlement == null) {
                    applicableHeaderTradeSettlement = new Element("ApplicableHeaderTradeSettlement", rootElement.getNamespace("ram"));
                    supplyChainTradeTransaction.addContent(applicableHeaderTradeSettlement);
                }
            }

            for (BG0003PrecedingInvoiceReference bg0003 : cenInvoice.getBG0003PrecedingInvoiceReference()) {
                Element invoiceReferencedDocument = new Element("InvoiceReferencedDocument", rootElement.getNamespace("ram"));

                if (!bg0003.getBT0025PrecedingInvoiceReference().isEmpty()) {
                    Element issuerAssignedID = new Element("IssuerAssignedID", rootElement.getNamespace("ram"));
                    issuerAssignedID.setText(bg0003.getBT0025PrecedingInvoiceReference(0).getValue());
                    invoiceReferencedDocument.addContent(issuerAssignedID);
                }

                if (!bg0003.getBT0026PrecedingInvoiceIssueDate().isEmpty()) {
                    Element formattedIssueDateTime = new Element("FormattedIssueDateTime", rootElement.getNamespace("ram"));
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