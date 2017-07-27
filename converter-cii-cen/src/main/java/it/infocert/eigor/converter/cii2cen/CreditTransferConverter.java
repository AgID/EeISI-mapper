package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;

/**
 * The Credit Transfer Custom Converter
 */
public class CreditTransferConverter extends CustomConverter {

    public CreditTransferConverter() {
        super(new Reflections("it.infocert"), new ConversionRegistry());
    }

    public ConversionResult<BG0000Invoice> toBG0017(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        BG0017CreditTransfer bg0017 = null;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesInScope();
        List<Namespace> namespacesIntrodueced = rootElement.getNamespacesIntroduced();
        List<Namespace> namespacesAdditional = rootElement.getAdditionalNamespaces();
        List<Namespace> tot = new ArrayList<>();
        tot.addAll(namespacesInScope);
        tot.addAll(namespacesIntrodueced);
        tot.addAll(namespacesAdditional);

        List<Element> payeePartyCreditorFinancialAccount = null;
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

                        if (ibanID != null && proprietaryID != null) {

                            BT0084PaymentAccountIdentifier bt0084 = new BT0084PaymentAccountIdentifier(ibanID.getText() + " " + proprietaryID.getText());
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
}