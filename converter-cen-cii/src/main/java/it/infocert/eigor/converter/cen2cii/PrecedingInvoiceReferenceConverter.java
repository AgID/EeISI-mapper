package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.errors.ErrorCode;
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
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
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

            // Map only first BG, the rest go to attachement
            // See https://jira.infocert.it/browse/EISI-189
            if (cenInvoice.getBG0003PrecedingInvoiceReference().size() > 0) {
                BG0003PrecedingInvoiceReference bg0003 = cenInvoice.getBG0003PrecedingInvoiceReference().get(0);
                Element invoiceReferencedDocument = new Element("InvoiceReferencedDocument", ramNs);

                if (!bg0003.getBT0025PrecedingInvoiceReference().isEmpty()) {
                    Element issuerAssignedID = new Element("IssuerAssignedID", ramNs);
                    issuerAssignedID.setText(bg0003.getBT0025PrecedingInvoiceReference(0).getValue());
                    invoiceReferencedDocument.addContent(issuerAssignedID);
                }

                if (!bg0003.getBT0026PrecedingInvoiceIssueDate().isEmpty()) {
                    Element formattedIssueDateTime = new Element("FormattedIssueDateTime", ramNs);
                    try {
                        Namespace qdtNs = rootElement.getNamespace("qdt");
                        Element dateTimeString = new Element("DateTimeString", qdtNs);
                        dateTimeString.setAttribute("format","102");
                        dateTimeString.setText(JavaLocalDateToStringConverter.newConverter("yyyyMMdd").convert(bg0003.getBT0026PrecedingInvoiceIssueDate(0).getValue()));
                        formattedIssueDateTime.addContent(dateTimeString);

                        invoiceReferencedDocument.addContent(formattedIssueDateTime);
                    } catch (IllegalArgumentException | ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(new EigorRuntimeException(
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.INVALID,
                                e
                        )));
                    }
                }
                applicableHeaderTradeSettlement.addContent(invoiceReferencedDocument);
            }
        }
    }
}
