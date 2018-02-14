package it.infocert.eigor.converter.commons.ubl2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.StringToJavaLocalDateConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0003PrecedingInvoiceReference;
import it.infocert.eigor.model.core.model.BT0025PrecedingInvoiceReference;
import it.infocert.eigor.model.core.model.BT0026PrecedingInvoiceIssueDate;
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

        List<Element> invoiceDocumentReference = null;
        Element billingReference = findNamespaceChild(rootElement, namespacesInScope, "BillingReference");

        if (billingReference != null) {
            invoiceDocumentReference = findNamespaceChildren(billingReference, namespacesInScope, "InvoiceDocumentReference");

            for (Element elem : invoiceDocumentReference) {
                bg0003 = new BG0003PrecedingInvoiceReference();

                Element id = findNamespaceChild(elem, namespacesInScope, "ID");
                Element issueDate = findNamespaceChild(elem, namespacesInScope, "IssueDate");

                if (id != null) {
                    BT0025PrecedingInvoiceReference bt0025 = new BT0025PrecedingInvoiceReference(id.getText());
                    bg0003.getBT0025PrecedingInvoiceReference().add(bt0025);
                }
                if (issueDate != null) {
                    final String text = issueDate.getText();
                    try {
                        BT0026PrecedingInvoiceIssueDate bt0026 = new BT0026PrecedingInvoiceIssueDate(StringToJavaLocalDateConverter.newConverter("yyyy-MM-dd").convert(text));
                        bg0003.getBT0026PrecedingInvoiceIssueDate().add(bt0026);
                    } catch (IllegalArgumentException | ConversionFailedException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(
                                e,
                                ErrorMessage.builder()
                                        .message(e.getMessage())
                                        .location(callingLocation)
                                        .action(ErrorCode.Action.HARDCODED_MAP)
                                        .error(ErrorCode.Error.ILLEGAL_VALUE)
                                        .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                        .addParam(ErrorMessage.OFFENDINGITEM_PARAM, text)
                                        .build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                invoice.getBG0003PrecedingInvoiceReference().add(bg0003);
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0003(document, cenInvoice, errors, callingLocation);
    }
}