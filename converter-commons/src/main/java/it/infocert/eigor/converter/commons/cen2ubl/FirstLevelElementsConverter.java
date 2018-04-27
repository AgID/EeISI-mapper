package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.conversion.LookUpEnumConversion;
import it.infocert.eigor.api.conversion.converter.*;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import org.jdom2.Document;
import org.jdom2.Element;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FirstLevelElementsConverter implements CustomMapping<Document> {

    private final static Logger logger = LoggerFactory.getLogger(FirstLevelElementsConverter.class);

    private final static ConversionRegistry conversionRegistry = new ConversionRegistry(
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

    private Element root;

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        this.root = document.getRootElement();

        // PEPPOL hardcoding
       /*convert("CustomizationID", "urn:cen.eu:en16931:2017");

        if (!invoice.getBG0002ProcessControl().isEmpty()) {


            BG0002ProcessControl processControl = invoice.getBG0002ProcessControl(0);
            if (processControl.getBT0024SpecificationIdentifier().isEmpty()) {
                convert("CustomizationID", processControl.getBT0024SpecificationIdentifier(0).getValue());
            }
        }*/

        if (!invoice.getBT0001InvoiceNumber().isEmpty()) {
            convert("ID", invoice.getBT0001InvoiceNumber(0).getValue());
        }

        if (!invoice.getBT0002InvoiceIssueDate().isEmpty()) {
            String converted = conversionRegistry.convert(LocalDate.class, String.class, invoice.getBT0002InvoiceIssueDate(0).getValue());
            convert("IssueDate", converted);
        }

        if (!invoice.getBT0009PaymentDueDate().isEmpty() && ErrorCode.Location.UBL_OUT.equals(callingLocation)) {
            String converted = conversionRegistry.convert(LocalDate.class, String.class, invoice.getBT0009PaymentDueDate(0).getValue());
            convert("DueDate", converted);
        }

        if (!invoice.getBT0003InvoiceTypeCode().isEmpty()) {
            Untdid1001InvoiceTypeCode bt0003 = invoice.getBT0003InvoiceTypeCode(0).getValue();
            String converted = conversionRegistry.convert(Untdid1001InvoiceTypeCode.class, String.class, bt0003);

            if (ErrorCode.Location.UBL_OUT.equals(callingLocation)) {
                convert("InvoiceTypeCode", converted);
            }
            if (ErrorCode.Location.UBLCN_OUT.equals(callingLocation)) {
                convert("CreditNoteTypeCode", converted);
            }
        }

        if (!invoice.getBG0001InvoiceNote().isEmpty()) {
            for (BG0001InvoiceNote bg0001 : invoice.getBG0001InvoiceNote()) {
                if (!bg0001.getBT0022InvoiceNote().isEmpty()) {
                    Element note = new Element("Note");
                    String bt0022 = bg0001.getBT0022InvoiceNote(0).getValue();

                    if (bg0001.getBT0021InvoiceNoteSubjectCode().isEmpty()) {
                        note.setText(bt0022);
                    } else {
                        String bt0021 = bg0001.getBT0021InvoiceNoteSubjectCode(0).getValue();
                        note.setText("#" + bt0021 + "#" + bt0022);
                    }

                    root.addContent(note);
                }
            }
        }

        if (!invoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
            String converted = conversionRegistry.convert(Iso4217CurrenciesFundsCodes.class, String.class, invoice.getBT0005InvoiceCurrencyCode(0).getValue());
            convert("DocumentCurrencyCode", converted);
        }

        if (!invoice.getBT0013PurchaseOrderReference().isEmpty()) {
            final String value = invoice.getBT0013PurchaseOrderReference(0).getValue();
            final Element orderReference = new Element("OrderReference");
            final Element id = new Element("ID").setText(value);
            root.addContent(orderReference.setContent(id));
        }
    }

    private void convert(String tag, String value) {
        root.addContent(new Element(tag).setText(value));
    }
}
