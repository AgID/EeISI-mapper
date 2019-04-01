package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CreditTransferConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(CreditTransferConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        Element root = document.getRootElement();
        if (root != null) {
            if (!cenInvoice.getBG0016PaymentInstructions().isEmpty()) {
                BG0016PaymentInstructions bg0016 = cenInvoice.getBG0016PaymentInstructions(0);
                List<BG0017CreditTransfer> bg0017 = cenInvoice.getBG0016PaymentInstructions(0).getBG0017CreditTransfer();
                Element paymentMeans = root.getChild("PaymentMeans");
                for (BG0017CreditTransfer elemBg17 : bg0017) {
                    if (!elemBg17.getBT0084PaymentAccountIdentifier().isEmpty()) {
                        BT0084PaymentAccountIdentifier bt0084 = elemBg17.getBT0084PaymentAccountIdentifier(0);
                        Element id = new Element("ID");
                        id.setText(bt0084.getValue());

                        if (paymentMeans == null) {
                            paymentMeans = new Element("PaymentMeans");
                            root.addContent(paymentMeans);
                        }
                        Element payeeFinancialAccount = new Element("PayeeFinancialAccount");
                        paymentMeans.addContent(payeeFinancialAccount);

                        payeeFinancialAccount.addContent(id);

                        if (!elemBg17.getBT0085PaymentAccountName().isEmpty()) {
                            BT0085PaymentAccountName bt0085 = elemBg17.getBT0085PaymentAccountName(0);
                            Element name = new Element("Name");
                            name.setText(bt0085.getValue());
                            payeeFinancialAccount.addContent(name);
                        }

                        if (!elemBg17.getBT0086PaymentServiceProviderIdentifier().isEmpty()) {
                            BT0086PaymentServiceProviderIdentifier bt0086 = elemBg17.getBT0086PaymentServiceProviderIdentifier(0);
                            Element branchID = new Element("ID");
                            branchID.setText(bt0086.getValue());

                            Element financialInstitutionBranch = new Element("FinancialInstitutionBranch");
                            payeeFinancialAccount.addContent(financialInstitutionBranch);

                            financialInstitutionBranch.addContent(branchID);
                        }
                    }
                }

                Element paymentMandate = paymentMeans.getChild("PaymentMandate");
                if (paymentMandate == null) {
                    paymentMandate = new Element("PaymentMandate");
                }

                if (!bg0016.getBG0019DirectDebit().isEmpty()) {
                    Element bg16Id = new Element("ID");
                    if (bg0016.getBG0019DirectDebit(0).getBT0089MandateReferenceIdentifier().isEmpty()) {
                        bg16Id.setText("NA");
                    } else {
                        BT0089MandateReferenceIdentifier bt89 = bg0016.getBG0019DirectDebit(0).getBT0089MandateReferenceIdentifier(0);
                        bg16Id.setText(bt89.getValue());
                    }

                    paymentMandate.addContent(bg16Id);
                    paymentMeans.addContent(paymentMandate);

                    if (!bg0016.getBG0019DirectDebit(0).getBT0091DebitedAccountIdentifier().isEmpty()) {
                        BT0091DebitedAccountIdentifier bt91 = bg0016.getBG0019DirectDebit(0).getBT0091DebitedAccountIdentifier(0);
                        Element bg19Id = new Element("ID");
                        bg19Id.setText(bt91.getValue());
                        Element payerFinancialAccount = paymentMeans.getChild("PayerFinancialAccount");
                        if (payerFinancialAccount == null) {
                            payerFinancialAccount = new Element("PayerFinancialAccount");
                        }
                        payerFinancialAccount.addContent(bg19Id);
                        paymentMandate.addContent(payerFinancialAccount);
                    }
                }
            }
        }
    }
}
