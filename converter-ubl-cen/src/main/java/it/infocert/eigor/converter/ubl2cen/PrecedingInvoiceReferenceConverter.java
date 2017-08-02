package it.infocert.eigor.converter.ubl2cen;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.StringToJavaLocalDateConverter;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Preceding Invoice Reference Custom Converter
 */
public class PrecedingInvoiceReferenceConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0003(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        BG0003PrecedingInvoiceReference bg0003 = null;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> invoiceDocumentReference = null;
        Element billingReference = findNamespaceChild(rootElement, namespacesInScope, "BillingReference");

        if (billingReference != null) {
        	invoiceDocumentReference = findNamespaceChildren(billingReference, namespacesInScope, "InvoiceDocumentReference");

        	for(Element elem : invoiceDocumentReference) {
        		bg0003 = new BG0003PrecedingInvoiceReference();

        		Element id = findNamespaceChild(elem, namespacesInScope, "ID");
        		Element issueDate = findNamespaceChild(elem, namespacesInScope,"IssueDate");

        		if (id != null) {
        			BT0025PrecedingInvoiceReference bt0025 = new BT0025PrecedingInvoiceReference(id.getText());
        			bg0003.getBT0025PrecedingInvoiceReference().add(bt0025);
        		}
        		if (issueDate != null) {
        			try{
        				BT0026PrecedingInvoiceIssueDate bt0026 = new BT0026PrecedingInvoiceIssueDate(new StringToJavaLocalDateConverter("yyyy-MM-dd").convert(issueDate.getText()));
        				bg0003.getBT0026PrecedingInvoiceIssueDate().add(bt0026);
					}catch (IllegalArgumentException e) {
						errors.add(ConversionIssue.newError(e, e.getMessage()+ "Formato data non valido"));
					}
        		}

        		invoice.getBG0003PrecedingInvoiceReference().add(bg0003);
        	}
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBG0003(document, cenInvoice, errors);
    }
}