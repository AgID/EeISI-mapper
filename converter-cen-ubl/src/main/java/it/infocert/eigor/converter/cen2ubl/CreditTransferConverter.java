package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CreditTransferConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(CreditTransferConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List errors) {
        Element root = document.getRootElement();
        if (root != null) {
            if (!cenInvoice.getBG0016PaymentInstructions().isEmpty()) {
                List<BG0017CreditTransfer> bg0017 = cenInvoice.getBG0016PaymentInstructions(0).getBG0017CreditTransfer();
                for (BG0017CreditTransfer elemBg17 : bg0017) {
                    if (!elemBg17.getBT0084PaymentAccountIdentifier().isEmpty()) {
                        BT0084PaymentAccountIdentifier bt0084 = elemBg17.getBT0084PaymentAccountIdentifier(0);

                        if (bt0084 != null) {
                            Element id = new Element("ID");
                            id.addContent(bt0084.getValue());

                            Element paymentMeans = root.getChild("PaymentMeans");
                            if (paymentMeans == null) {
                                paymentMeans = new Element("PaymentMeans");
                                root.addContent(paymentMeans);
                            }
                            Element payeeFinancialAccount = new Element("PayeeFinancialAccount");
                            paymentMeans.addContent(payeeFinancialAccount);

                            payeeFinancialAccount.addContent(id);

                            if (!elemBg17.getBT0086PaymentServiceProviderIdentifier().isEmpty()) {
                                BT0086PaymentServiceProviderIdentifier bt0086 = elemBg17.getBT0086PaymentServiceProviderIdentifier(0);
                                if (bt0086 != null) {
                                    Element branchID = new Element("ID");
                                    branchID.addContent(bt0086.getValue());

                                    Element financialInstitutionBranch = new Element("FinancialInstitutionBranch");
                                    payeeFinancialAccount.addContent(financialInstitutionBranch);

                                    financialInstitutionBranch.addContent(branchID);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}