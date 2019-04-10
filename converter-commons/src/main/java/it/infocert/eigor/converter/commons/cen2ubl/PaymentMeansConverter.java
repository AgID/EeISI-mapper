package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.conversion.LookUpEnumConversion;
import it.infocert.eigor.api.conversion.converter.*;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid4461PaymentMeansCode;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;

import java.time.LocalDate;
import java.util.List;

public class PaymentMeansConverter implements CustomMapping<Document> {

    protected final static ConversionRegistry conversionRegistry = new ConversionRegistry(
            StringToStringConverter.newConverter(),
            Iso4217CurrenciesFundsCodesToStringConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso4217CurrenciesFundsCodes.class),
            JavaLocalDateToStringConverter.newConverter(),
            Untdid2005DateTimePeriodQualifiersToStringConverter.newConverter(),
            Untdid1001InvoiceTypeCodesToStringConverter.newConverter(),
            BigDecimalToStringConverter.newConverter("0.00"),
            Iso31661CountryCodesToStringConverter.newConverter(),
            IdentifierToStringConverter.newConverter(),
            Untdid4461PaymentMeansCodeToString.newConverter()
    );

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

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
                        paymentMeansCode.setAttribute("name", bt0082.getValue());
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

                if (!cenInvoice.getBT0009PaymentDueDate().isEmpty() && (ErrorCode.Location.UBLCN_OUT.equals(callingLocation) || ErrorCode.Location.PEPPOLCN_OUT.equals(callingLocation))) {
                    String converted = conversionRegistry.convert(LocalDate.class, String.class, cenInvoice.getBT0009PaymentDueDate(0).getValue());
                    Element paymentDueDate = new Element("PaymentDueDate");
                    paymentDueDate.setText(converted);
                    paymentMeans.addContent(paymentDueDate);
                }

                if (!bg0016.getBT0083RemittanceInformation().isEmpty()) {
                    BT0083RemittanceInformation bt0083 = bg0016.getBT0083RemittanceInformation(0);
                    Element paymentID = new Element("PaymentID");
                    paymentID.setText(bt0083.getValue());
                    paymentMeans.addContent(paymentID);
                }

                if (!bg0016.getBG0018PaymentCardInformation().isEmpty()) {
                    BG0018PaymentCardInformation bg0018 = bg0016.getBG0018PaymentCardInformation(0);
                    Element cardAccount = new Element("CardAccount");
                    if (!bg0018.getBT0087PaymentCardPrimaryAccountNumber().isEmpty()) {
                        Element primaryAccountNumberID = new Element("PrimaryAccountNumberID");
                        primaryAccountNumberID.setText(bg0018.getBT0087PaymentCardPrimaryAccountNumber(0).getValue());
                        cardAccount.addContent(primaryAccountNumberID);
                    }
                    Element networkID = new Element("NetworkID");
                    networkID.setText("mandatory network id");
                    cardAccount.addContent(networkID);
                    if (!bg0018.getBT0088PaymentCardHolderName().isEmpty()) {
                        Element holderName = new Element("HolderName");
                        holderName.setText(bg0018.getBT0088PaymentCardHolderName(0).getValue());
                        cardAccount.addContent(holderName);
                    }
                    paymentMeans.addContent(cardAccount);
                }
            }
        }
    }
}