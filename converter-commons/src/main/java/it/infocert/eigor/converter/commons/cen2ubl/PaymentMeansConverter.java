package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.conversion.converter.Untdid4461PaymentMeansCodeToString;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PaymentMeansConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(PaymentMeansConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        Element root = document.getRootElement();
        if (root != null) {
            if (!cenInvoice.getBG0016PaymentInstructions().isEmpty()) {
                BG0016PaymentInstructions bg0016 = cenInvoice.getBG0016PaymentInstructions(0);

                TypeConverter<Untdid4461PaymentMeansCode, String> paymentMeansCodeToStr = Untdid4461PaymentMeansCodeToString.newConverter();

                Element paymentMeans = root.getChild("PaymentMeans");
                if (paymentMeans == null) {
                    paymentMeans = new Element("PaymentMeans");
                    root.addContent(paymentMeans);
                }

                if (!bg0016.getBT0081PaymentMeansTypeCode().isEmpty()) {
                    BT0081PaymentMeansTypeCode bt0081 = bg0016.getBT0081PaymentMeansTypeCode(0);
                    Element paymentMeansCode = new Element("PaymentMeansCode");
                    if (!bg0016.getBT0082PaymentMeansText().isEmpty()) {
                        BT0082PaymentMeansText bt0082 = bg0016.getBT0082PaymentMeansText(0);
                        paymentMeansCode.setAttribute("Name", bt0082.getValue());
                    }
                    final Untdid4461PaymentMeansCode value = bt0081.getValue();
                    try {
                        paymentMeansCode.setText(paymentMeansCodeToStr.convert(value));
                    } catch (ConversionFailedException e) {
                        errors.add(ConversionIssue.newError(
                                e,
                                e.getMessage(),
                                callingLocation,
                                ErrorCode.Action.HARDCODED_MAP,
                                ErrorCode.Error.ILLEGAL_VALUE,
                                Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, value.toString())
                        ));
                    }
                    paymentMeans.addContent(paymentMeansCode);
                }

                if (!bg0016.getBT0083RemittanceInformation().isEmpty()) {
                    BT0083RemittanceInformation bt0083 = bg0016.getBT0083RemittanceInformation(0);
                    Element paymentID = new Element("PaymentID");
                    paymentID.setText(bt0083.getValue());
                    paymentMeans.addContent(paymentID);
                }
            }
        }
    }
}