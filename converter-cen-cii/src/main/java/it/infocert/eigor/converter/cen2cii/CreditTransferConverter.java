package it.infocert.eigor.converter.cen2cii;

import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;
import it.infocert.eigor.model.core.model.*;
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
        if (!cenInvoice.getBG0016PaymentInstructions().isEmpty()) {
            BG0016PaymentInstructions bg0016 = cenInvoice.getBG0016PaymentInstructions(0);
            Element rootElement = document.getRootElement();
            List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();
            Namespace rsmNs = rootElement.getNamespace("rsm");
            Namespace ramNs = rootElement.getNamespace("ram");

            Element supplyChainTradeTransaction = findNamespaceChild(rootElement, namespacesInScope, "SupplyChainTradeTransaction");
            if (supplyChainTradeTransaction == null) {
                supplyChainTradeTransaction = new Element("SupplyChainTradeTransaction", rsmNs);
                rootElement.addContent(supplyChainTradeTransaction);
            }

            Element applicableHeaderTradeAgreement = findNamespaceChild(supplyChainTradeTransaction, namespacesInScope, "ApplicableHeaderTradeAgreement");
            if (applicableHeaderTradeAgreement == null) {
                applicableHeaderTradeAgreement = new Element("ApplicableHeaderTradeAgreement", ramNs);
                supplyChainTradeTransaction.addContent(applicableHeaderTradeAgreement);
            }

            Element specifiedTradeSettlementPaymentMeans = new Element("SpecifiedTradeSettlementPaymentMeans", ramNs);
            applicableHeaderTradeAgreement.addContent(specifiedTradeSettlementPaymentMeans);

            if(!bg0016.getBT0081PaymentMeansTypeCode().isEmpty()){
                Untdid4461PaymentMeansCode bt0081 = bg0016.getBT0081PaymentMeansTypeCode(0).getValue();
                Element typeCode = new Element("TypeCode", ramNs);
                typeCode.setText(String.valueOf(bt0081.getCode()));
                specifiedTradeSettlementPaymentMeans.addContent(typeCode);
            }

            if(!bg0016.getBT0082PaymentMeansText().isEmpty()){
                BT0082PaymentMeansText bt0082 = bg0016.getBT0082PaymentMeansText(0);
                Element information = new Element("Information", ramNs);
                information.setText(bt0082.getValue());
                specifiedTradeSettlementPaymentMeans.addContent(information);
            }

            if(!bg0016.getBT0083RemittanceInformation().isEmpty()){
                BT0083RemittanceInformation bt0083 = bg0016.getBT0083RemittanceInformation(0);
                Element paymentReference = new Element("PaymentReference", ramNs);
                paymentReference.setText(bt0083.getValue());
                applicableHeaderTradeAgreement.addContent(paymentReference);
            }

            for (BG0017CreditTransfer bg0017 : bg0016.getBG0017CreditTransfer()) {
                Element payeePartyCreditorFinancialAccount = new Element("PayeePartyCreditorFinancialAccount", ramNs);

                if (!bg0017.getBT0084PaymentAccountIdentifier().isEmpty()) {
                    Element ibanID = new Element("IBANID", ramNs);
                    ibanID.setText(bg0017.getBT0084PaymentAccountIdentifier(0).getValue());
                    payeePartyCreditorFinancialAccount.addContent(ibanID);
                }

                if (!bg0017.getBT0085PaymentAccountName().isEmpty()) {
                    Element accountName = new Element("AccountName", ramNs);
                    accountName.setText(bg0017.getBT0085PaymentAccountName(0).getValue());
                    payeePartyCreditorFinancialAccount.addContent(accountName);
                }

                if (!bg0017.getBT0086PaymentServiceProviderIdentifier().isEmpty()) {
                    Element bicid = new Element("BICID", ramNs);
                    bicid.setText(bg0017.getBT0086PaymentServiceProviderIdentifier(0).getValue());
                    Element institution = new Element("PayerSpecifiedDebtorFinancialInstitution", ramNs);
                    institution.addContent(bicid);
                    specifiedTradeSettlementPaymentMeans.addContent(institution);
                }

                specifiedTradeSettlementPaymentMeans.addContent(payeePartyCreditorFinancialAccount);
            }

            for (BG0018PaymentCardInformation bg0018 : bg0016.getBG0018PaymentCardInformation()) {
                Element applicableTradeSettlementFinancialCard = new Element("ApplicableTradeSettlementFinancialCard", ramNs);

                if (!bg0018.getBT0087PaymentCardPrimaryAccountNumber().isEmpty()) {
                    BT0087PaymentCardPrimaryAccountNumber bt0087 = bg0018.getBT0087PaymentCardPrimaryAccountNumber(0);
                    Element id = new Element("ID", ramNs);
                    id.setText(bt0087.getValue());
                    applicableTradeSettlementFinancialCard.addContent(id);
                }

                if (!bg0018.getBT0088PaymentCardHolderName().isEmpty()) {
                    BT0088PaymentCardHolderName bt0088 = bg0018.getBT0088PaymentCardHolderName(0);
                    Element cardholderName = new Element("CardholderName", ramNs);
                    cardholderName.setText(bt0088.getValue());
                    applicableTradeSettlementFinancialCard.addContent(cardholderName);
                }

                specifiedTradeSettlementPaymentMeans.addContent(applicableTradeSettlementFinancialCard);
            }

            for (BG0019DirectDebit bg0019 : bg0016.getBG0019DirectDebit()) {
                if (!bg0019.getBT0090BankAssignedCreditorIdentifier().isEmpty()) {
                    BT0090BankAssignedCreditorIdentifier bt0090 = bg0019.getBT0090BankAssignedCreditorIdentifier(0);
                    Element creditorReferenceID = new Element("CreditorReferenceID", ramNs);
                    creditorReferenceID.setText(bt0090.getValue());
                    applicableHeaderTradeAgreement.addContent(creditorReferenceID);
                }

                if (!bg0019.getBT0091DebitedAccountIdentifier().isEmpty()) {
                    BT0091DebitedAccountIdentifier bt0091 = bg0019.getBT0091DebitedAccountIdentifier(0);
                    Element payerPartyDebtorFinancialAccount = new Element("PayerPartyDebtorFinancialAccount", ramNs);
                    Element ibanID = new Element("IBANID", ramNs);
                    ibanID.setText(bt0091.getValue());
                    payerPartyDebtorFinancialAccount.addContent(ibanID);
                    specifiedTradeSettlementPaymentMeans.addContent(payerPartyDebtorFinancialAccount);
                }
            }
        }
    }
}