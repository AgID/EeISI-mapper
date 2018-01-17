package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0017CreditTransfer;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Credit Transfer Custom Converter
 */
public class CreditTransferConverter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors) {
        if (!cenInvoice.getBG0016PaymentInstructions().isEmpty() && !cenInvoice.getBG0016PaymentInstructions(0).getBG0017CreditTransfer().isEmpty()) {
            Element rootElement = document.getRootElement();
            List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

            Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
            Element applicableHeaderTradeAgreement = null;

            if (supplyChainTradeTransaction == null) {
                supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rootElement.getNamespace("rsm"));
                rootElement.addContent(supplyChainTradeTransaction);
            }

            applicableHeaderTradeAgreement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeAgreement");
            if (applicableHeaderTradeAgreement == null) {
                applicableHeaderTradeAgreement = new Element("ApplicableHeaderTradeAgreement", rootElement.getNamespace("ram"));
                supplyChainTradeTransaction.addContent(applicableHeaderTradeAgreement);
            }

            Element specifiedTradeSettlementPaymentMeans = new Element("SpecifiedTradeSettlementPaymentMeans", rootElement.getNamespace("ram"));
            applicableHeaderTradeAgreement.addContent(specifiedTradeSettlementPaymentMeans);

            for (BG0017CreditTransfer bg0017 : cenInvoice.getBG0016PaymentInstructions(0).getBG0017CreditTransfer()) {
                Element payeePartyCreditorFinancialAccount = new Element("PayeePartyCreditorFinancialAccount", rootElement.getNamespace("ram"));

                if (!bg0017.getBT0084PaymentAccountIdentifier().isEmpty()) {
                    Element ibanID = new Element("IBANID", rootElement.getNamespace("ram"));
                    ibanID.setText(bg0017.getBT0084PaymentAccountIdentifier(0).getValue());
                    payeePartyCreditorFinancialAccount.addContent(ibanID);
                }

                if (!bg0017.getBT0085PaymentAccountName().isEmpty()) {
                    Element accountName = new Element("AccountName", rootElement.getNamespace("ram"));
                    accountName.setText(bg0017.getBT0085PaymentAccountName(0).getValue());
                    payeePartyCreditorFinancialAccount.addContent(accountName);
                }

                if (!bg0017.getBT0086PaymentServiceProviderIdentifier().isEmpty()) {
                    Element bicid = new Element("BICID", rootElement.getNamespace("ram"));
                    bicid.setText(bg0017.getBT0086PaymentServiceProviderIdentifier(0).getValue());
                    Element institution = new Element("PayerSpecifiedDebtorFinancialInstitution", rootElement.getNamespace("ram"));
                    institution.addContent(bicid);
                    specifiedTradeSettlementPaymentMeans.addContent(institution);
                }

                specifiedTradeSettlementPaymentMeans.addContent(payeePartyCreditorFinancialAccount);
            }
        }
    }
}