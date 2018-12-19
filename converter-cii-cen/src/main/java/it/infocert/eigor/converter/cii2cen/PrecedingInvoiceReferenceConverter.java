package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.StringToJavaLocalDateConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0003PrecedingInvoiceReference;
import it.infocert.eigor.model.core.model.BT0025PrecedingInvoiceReference;
import it.infocert.eigor.model.core.model.BT0026PrecedingInvoiceIssueDate;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Preceding Invoice Reference Custom Converter
 */
public class PrecedingInvoiceReferenceConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0003(Document document, BG0000Invoice invoice, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

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
                        Attribute format = dateTimeString.getAttribute("format");
                        if (format != null && format.getValue().equals("102")) {
                            try {
                                BT0026PrecedingInvoiceIssueDate bt0026 = new BT0026PrecedingInvoiceIssueDate(StringToJavaLocalDateConverter.newConverter ("yyyyMMdd").convert(dateTimeString.getText()));
                                bg0003.getBT0026PrecedingInvoiceIssueDate().add(bt0026);
                            } catch (IllegalArgumentException | ConversionFailedException e) {
                                EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder()
                                        .message("Invalid date format")
                                        .location(callingLocation)
                                        .action(ErrorCode.Action.HARDCODED_MAP)
                                        .error(ErrorCode.Error.ILLEGAL_VALUE)
                                        .build());
                                errors.add(ConversionIssue.newError(ere));
                            }
                        }
                    }

                    invoice.getBG0003PrecedingInvoiceReference().add(bg0003);
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0003(document, cenInvoice, errors, callingLocation);
    }
}