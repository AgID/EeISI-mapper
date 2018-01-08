package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.conversion.TypeConverter;
import it.infocert.eigor.model.core.enums.Untdid2005DateTimePeriodQualifiers;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DeliveryOrInvoicePeriodConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(DeliveryOrInvoicePeriodConverter.class);

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List errors) {

        TypeConverter<LocalDate, String> dateConverter = JavaLocalDateToStringConverter.newConverter();

        Element root = document.getRootElement();
        if (root != null) {
            if (!cenInvoice.getBG0013DeliveryInformation().isEmpty()) {
                BG0013DeliveryInformation bg0013 = cenInvoice.getBG0013DeliveryInformation(0);
                if (!bg0013.getBG0014InvoicingPeriod().isEmpty()) {
                    BG0014InvoicingPeriod bg0014 = bg0013.getBG0014InvoicingPeriod(0);

                    Element invoicePeriod = root.getChild("InvoicePeriod");
                    if (invoicePeriod == null) {
                        invoicePeriod = new Element("InvoicePeriod");
                        root.addContent(invoicePeriod);
                    }

                    if (!bg0014.getBT0073InvoicingPeriodStartDate().isEmpty()) {
                        BT0073InvoicingPeriodStartDate bt0073 = bg0014.getBT0073InvoicingPeriodStartDate(0);
                        Element startDate = new Element("StartDate");
                        try {
                            startDate.setText(dateConverter.convert(bt0073.getValue()));
                            invoicePeriod.addContent(startDate);
                        } catch (ConversionFailedException e) {
                            errors.add(e);
                        }
                    }

                    if (!bg0014.getBT0074InvoicingPeriodEndDate().isEmpty()) {
                        BT0074InvoicingPeriodEndDate bt0074 = bg0014.getBT0074InvoicingPeriodEndDate(0);
                        Element endDate = new Element("EndDate");
                        try {
                            endDate.setText(dateConverter.convert(bt0074.getValue()));
                            invoicePeriod.addContent(endDate);
                        } catch (ConversionFailedException e) {
                            errors.add(e);
                        }
                    }

                    if (!cenInvoice.getBT0008ValueAddedTaxPointDateCode().isEmpty()) {
                        BT0008ValueAddedTaxPointDateCode bt0008 = cenInvoice.getBT0008ValueAddedTaxPointDateCode(0);
                        Element descriptionCode = new Element("DescriptionCode");
                        Untdid2005DateTimePeriodQualifiers code = bt0008.getValue();
                        descriptionCode.setText(code.name());
                        invoicePeriod.addContent(descriptionCode);
                    }
                }
            }

            if (!cenInvoice.getBT0012ContractReference().isEmpty()) {
                Element contractDocumentReference = new Element("ContractDocumentReference");
                Element id = new Element("ID");
                contractDocumentReference.addContent(id);
                id.setText(cenInvoice.getBT0012ContractReference(0).getValue());
                root.addContent(contractDocumentReference);
            }
        }
    }
}