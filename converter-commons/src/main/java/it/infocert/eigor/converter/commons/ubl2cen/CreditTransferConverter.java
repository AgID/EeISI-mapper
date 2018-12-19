package it.infocert.eigor.converter.commons.ubl2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The Credit Transfer Custom Converter
 */
public class CreditTransferConverter extends CustomConverterUtils implements CustomMapping<Document> {
    private final static Logger logger = LoggerFactory.getLogger(CreditTransferConverter.class);

    public ConversionResult<BG0000Invoice> toBG0017(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        final Element rootElement = document.getRootElement();
        final List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        final Element paymentMeans = findNamespaceChild(rootElement, namespacesInScope, "PaymentMeans");

        if (paymentMeans != null) {
            final List<Element> payeeFinancialAccount = findNamespaceChildren(paymentMeans, namespacesInScope, "PayeeFinancialAccount");

            for (Element elemPayee : payeeFinancialAccount) {

                final BG0017CreditTransfer bg0017 = new BG0017CreditTransfer();

                final Element id = findNamespaceChild(elemPayee, namespacesInScope, "ID");

                if (id != null) {
                    final BT0084PaymentAccountIdentifier bt0084 = new BT0084PaymentAccountIdentifier(id.getText());
                    bg0017.getBT0084PaymentAccountIdentifier().add(bt0084);
                    logger.debug("Mapped PayeeFinancialAccount/ID to BT-84 with value: {}", id.getText());
                } else {
                    logger.debug("No PayeeFinancialAccount/ID found");
                }

                final Element name = findNamespaceChild(elemPayee, namespacesInScope, "Name");
                if (name != null) {
                    final BT0085PaymentAccountName bt0085 = new BT0085PaymentAccountName(name.getText());
                    bg0017.getBT0085PaymentAccountName().add(bt0085);
                }

                final Element financialInstitutionBranch = findNamespaceChild(elemPayee, namespacesInScope, "FinancialInstitutionBranch");
                if (financialInstitutionBranch != null) {
                    final Element idBranch = findNamespaceChild(financialInstitutionBranch, namespacesInScope, "ID");

                    if (idBranch != null) {
                        final BT0086PaymentServiceProviderIdentifier bt0086 = new BT0086PaymentServiceProviderIdentifier(idBranch.getText());
                        bg0017.getBT0086PaymentServiceProviderIdentifier().add(bt0086);
                    }
                }

                if (invoice.getBG0016PaymentInstructions().isEmpty()) {
                    invoice.getBG0016PaymentInstructions().add(new BG0016PaymentInstructions());
                }

                invoice.getBG0016PaymentInstructions(0).getBG0017CreditTransfer().add(bg0017);
            }
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0017(document, cenInvoice, errors);
    }
}