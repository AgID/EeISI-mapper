package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.model.core.enums.Untdid2005DateTimePeriodQualifiers;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InvoicePeriodConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(InvoicePeriodConverter.class);

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        TypeConverter<LocalDate, String> dateConverter = JavaLocalDateToStringConverter.newConverter();

        Element root = document.getRootElement();
        if (root != null) {
            final String INVOICE_PERIOD = "InvoicePeriod";
            Element invoicePeriod = root.getChild(INVOICE_PERIOD);
            if (!invoice.getBG0013DeliveryInformation().isEmpty()) {
                BG0013DeliveryInformation bg0013 = invoice.getBG0013DeliveryInformation(0);
                if (!bg0013.getBG0014InvoicingPeriod().isEmpty()) {
                    BG0014InvoicingPeriod bg0014 = bg0013.getBG0014InvoicingPeriod(0);

                    if (invoicePeriod == null) {
                        invoicePeriod = new Element(INVOICE_PERIOD);
                        root.addContent(invoicePeriod);
                    }

                    if (!bg0014.getBT0073InvoicingPeriodStartDate().isEmpty()) {
                        BT0073InvoicingPeriodStartDate bt0073 = bg0014.getBT0073InvoicingPeriodStartDate(0);
                        Element startDate = new Element("StartDate");
                        final LocalDate date = bt0073.getValue();
                        try {
                            startDate.setText(dateConverter.convert(date));
                            invoicePeriod.addContent(startDate);
                        } catch (ConversionFailedException e) {
                            errors.add(ConversionIssue.newError(
                                    e,
                                    e.getMessage(),
                                    callingLocation,
                                    ErrorCode.Action.HARDCODED_MAP,
                                    ErrorCode.Error.ILLEGAL_VALUE,
                                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                    Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, date.toString())
                            ));
                        }
                    }

                    if (!bg0014.getBT0074InvoicingPeriodEndDate().isEmpty()) {
                        BT0074InvoicingPeriodEndDate bt0074 = bg0014.getBT0074InvoicingPeriodEndDate(0);
                        Element endDate = new Element("EndDate");
                        final LocalDate date = bt0074.getValue();
                        try {
                            endDate.setText(dateConverter.convert(date));
                            invoicePeriod.addContent(endDate);
                        } catch (ConversionFailedException e) {
                            errors.add(ConversionIssue.newError(
                                    e,
                                    e.getMessage(),
                                    callingLocation,
                                    ErrorCode.Action.HARDCODED_MAP,
                                    ErrorCode.Error.ILLEGAL_VALUE,
                                    Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage()),
                                    Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, date.toString())
                            ));
                        }
                    }

                    if (!invoice.getBT0008ValueAddedTaxPointDateCode().isEmpty()) {
                        BT0008ValueAddedTaxPointDateCode bt0008 = invoice.getBT0008ValueAddedTaxPointDateCode(0);
                        Element descriptionCode = new Element("DescriptionCode");
                        Untdid2005DateTimePeriodQualifiers code = bt0008.getValue();
                        descriptionCode.setText(String.valueOf(code.getCode()));
                        invoicePeriod.addContent(descriptionCode);
                    }
                }
            }

        }
    }
}