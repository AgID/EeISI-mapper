package it.infocert.eigor.converter.commons.ubl2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.List;

/**
 * The Payment Means Custom Converter
 */
public class PaymentMeansConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0016(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        if (invoice.getBG0016PaymentInstructions().isEmpty()) {
            invoice.getBG0016PaymentInstructions().add(new BG0016PaymentInstructions());
        }
        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        Element paymentMeans = findNamespaceChild(rootElement, namespacesInScope, "PaymentMeans");

        if (paymentMeans != null) {
            Element paymentMeansCode = findNamespaceChild(paymentMeans, namespacesInScope, "PaymentMeansCode");

            if (paymentMeansCode != null) {

                BT0081PaymentMeansTypeCode bt0081 = new BT0081PaymentMeansTypeCode(Untdid4461PaymentMeansCode.valueOf("Code" + paymentMeansCode.getValue()));
                invoice.getBG0016PaymentInstructions(0).getBT0081PaymentMeansTypeCode().add(bt0081);

                String paymentMeansCodeName = paymentMeansCode.getAttributeValue("Name");
                if (paymentMeansCodeName != null) {
                    BT0082PaymentMeansText bt0082 = new BT0082PaymentMeansText(paymentMeansCodeName);
                    invoice.getBG0016PaymentInstructions(0).getBT0082PaymentMeansText().add(bt0082);
                }
            }

            Element paymentID = findNamespaceChild(paymentMeans, namespacesInScope, "PaymentID");
            if (paymentID != null) {
                BT0083RemittanceInformation bt0083 = new BT0083RemittanceInformation(paymentID.getValue());
                invoice.getBG0016PaymentInstructions(0).getBT0083RemittanceInformation().add(bt0083);
            }

        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {
        toBG0016(document, cenInvoice, errors);
    }
}
