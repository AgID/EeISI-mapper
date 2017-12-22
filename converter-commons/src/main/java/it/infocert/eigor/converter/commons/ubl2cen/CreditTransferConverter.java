package it.infocert.eigor.converter.commons.ubl2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.*;
import it.infocert.eigor.api.CustomConverterUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Credit Transfer Custom Converter
 */
public class CreditTransferConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0017(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        BG0017CreditTransfer bg0017 = null;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> payeeFinancialAccount = null;
        Element paymentMeans = findNamespaceChild(rootElement, namespacesInScope, "PaymentMeans");

        if (paymentMeans != null) {
            payeeFinancialAccount = findNamespaceChildren(paymentMeans, namespacesInScope, "PayeeFinancialAccount");

            for (Element elemPayee : payeeFinancialAccount) {
                	
            	bg0017 = new BG0017CreditTransfer();

            	Element id = findNamespaceChild(elemPayee, namespacesInScope, "ID");
                    
            	if (id != null) {
            		BT0084PaymentAccountIdentifier bt0084 = new BT0084PaymentAccountIdentifier(id.getText());
            		bg0017.getBT0084PaymentAccountIdentifier().add(bt0084);
            	}
                    
            	Element name = findNamespaceChild(elemPayee, namespacesInScope, "Name");
            	if (name != null) {
            		BT0085PaymentAccountName bt0085 = new BT0085PaymentAccountName(name.getText());
            		bg0017.getBT0085PaymentAccountName().add(bt0085);
            	}
                    
            	Element financialInstitutionBranch = findNamespaceChild(elemPayee, namespacesInScope, "FinancialInstitutionBranch");
            	if (financialInstitutionBranch != null) {
            		Element idBranch = findNamespaceChild(financialInstitutionBranch, namespacesInScope, "ID");
                    	
            		if (idBranch != null) {
            			BT0086PaymentServiceProviderIdentifier bt0086 = new BT0086PaymentServiceProviderIdentifier(idBranch.getText());
						bg0017.getBT0086PaymentServiceProviderIdentifier().add(bt0086);
            		}
            	}

            	if (!invoice.getBT0016DespatchAdviceReference().isEmpty()) {
					invoice.getBG0016PaymentInstructions(0).getBG0017CreditTransfer().add(bg0017);
				}
            }
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        toBG0017(document, cenInvoice, errors);
    }
}