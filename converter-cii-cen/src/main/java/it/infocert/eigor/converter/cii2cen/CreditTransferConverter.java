package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Credit Transfer Custom Converter
 */
public class CreditTransferConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0017(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        BG0017CreditTransfer bg0017;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> payeePartyCreditorFinancialAccount;
        Element child = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");

        if (child != null) {
            Element applicableHeaderTradeSettlement = findNamespaceChild(child, namespacesInScope, "ApplicableHeaderTradeSettlement");

            if (applicableHeaderTradeSettlement != null) {
                List<Element> specifiedTradeSettlementPaymentMeans = findNamespaceChildren(applicableHeaderTradeSettlement, namespacesInScope, "SpecifiedTradeSettlementPaymentMeans");

                for (Element elemSpecTradeMeans : specifiedTradeSettlementPaymentMeans) {

                    payeePartyCreditorFinancialAccount = findNamespaceChildren(elemSpecTradeMeans, namespacesInScope, "PayeePartyCreditorFinancialAccount");
                    for (Element elem : payeePartyCreditorFinancialAccount) {

                        bg0017 = new BG0017CreditTransfer();

                        Element ibanID = findNamespaceChild(elem, namespacesInScope, "IBANID");
                        Element proprietaryID = findNamespaceChild(elem, namespacesInScope, "ProprietaryID");
                        Element accountName = findNamespaceChild(elem, namespacesInScope, "AccountName");

                        if (ibanID != null) {
                            BT0084PaymentAccountIdentifier bt0084 = new BT0084PaymentAccountIdentifier(ibanID.getText());
                            bg0017.getBT0084PaymentAccountIdentifier().add(bt0084);
                        } else if (proprietaryID != null) {
                            BT0084PaymentAccountIdentifier bt0084 = new BT0084PaymentAccountIdentifier(proprietaryID.getText());
                            bg0017.getBT0084PaymentAccountIdentifier().add(bt0084);
                        }
                        if (accountName != null) {
                            BT0085PaymentAccountName bt0085 = new BT0085PaymentAccountName(accountName.getText());
                            bg0017.getBT0085PaymentAccountName().add(bt0085);
                        }

                        Element payerSpecifiedDebtorFinancialInstitution = findNamespaceChild(elemSpecTradeMeans, namespacesInScope, "PayerSpecifiedDebtorFinancialInstitution");
                        if (payerSpecifiedDebtorFinancialInstitution != null) {
                            Element debtorBICID = findNamespaceChild(payerSpecifiedDebtorFinancialInstitution, namespacesInScope, "BICID");
                            BT0086PaymentServiceProviderIdentifier bt0086 = new BT0086PaymentServiceProviderIdentifier(debtorBICID.getText());
                            bg0017.getBT0086PaymentServiceProviderIdentifier().add(bt0086);
                        }
                        Element payeeSpecifiedCreditorFinancialInstitution = findNamespaceChild(elemSpecTradeMeans, namespacesInScope, "PayeeSpecifiedCreditorFinancialInstitution");
                        if (payeeSpecifiedCreditorFinancialInstitution != null) {
                            Element creditorBICID = findNamespaceChild(payeeSpecifiedCreditorFinancialInstitution, namespacesInScope, "BICID");
                            if (creditorBICID != null) {
                                BT0086PaymentServiceProviderIdentifier bt0086 = new BT0086PaymentServiceProviderIdentifier(creditorBICID.getText());
                                bg0017.getBT0086PaymentServiceProviderIdentifier().add(bt0086);
                            }
                        }
                        invoice.getBG0016PaymentInstructions(0).getBG0017CreditTransfer().add(bg0017);
                    }
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0017(document, cenInvoice, errors);
    }
}