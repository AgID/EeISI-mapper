package it.infocert.eigor.converter.cen2ubl;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0001InvoiceNote;
import it.infocert.eigor.model.core.model.BG0002ProcessControl;
import it.infocert.eigor.model.core.model.BT0022InvoiceNote;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.List;

public class FirstLevelElementsConverter implements CustomMapping<Document> {

    private final static ConversionRegistry conversionRegistry = new ConversionRegistry(
            new StringToStringConverter(),
            new Iso4217CurrenciesFundsCodesToStringConverter(),
            new LookUpEnumConversion(Iso4217CurrenciesFundsCodes.class),
            new JavaLocalDateToStringConverter(),
            new Untdid2005DateTimePeriodQualifiersToStringConverter(),
            new Untdid1001InvoiceTypeCodesToStringConverter(),
            new DoubleToStringConverter("0.00"),
            new Iso31661CountryCodesToStringConverter(),
            new IdentifierToStringConverter(),
            new Untdid4461PaymentMeansCodeToString()
    );

    private Element root;

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors) {

        this.root = document.getRootElement();

        if (!invoice.getBG0002ProcessControl().isEmpty()) {
            BG0002ProcessControl processControl = invoice.getBG0002ProcessControl(0);
            if(processControl.getBT0024SpecificationIdentifier().isEmpty()) {
               convert("CustomizationID", processControl.getBT0024SpecificationIdentifier(0).getValue());
            }
        }

        if (!invoice.getBT0001InvoiceNumber().isEmpty()) {
            convert("ID", invoice.getBT0001InvoiceNumber(0).getValue());
        }

        if (!invoice.getBT0002InvoiceIssueDate().isEmpty()) {
            String converted = conversionRegistry.convert(LocalDate.class, String.class, invoice.getBT0002InvoiceIssueDate(0).getValue());
            convert("IssueDate", converted);
        }

        if (!invoice.getBT0009PaymentDueDate().isEmpty()) {
            String converted = conversionRegistry.convert(LocalDate.class, String.class, invoice.getBT0009PaymentDueDate(0).getValue());
            convert("DueDate", converted);
        }

        if (!invoice.getBT0003InvoiceTypeCode().isEmpty()) {
            String converted = conversionRegistry.convert(Untdid1001InvoiceTypeCode.class, String.class, invoice.getBT0003InvoiceTypeCode(0).getValue());
            convert("InvoiceTypeCode", converted);
        }

        if (!invoice.getBG0001InvoiceNote().isEmpty()) {
            for (BG0001InvoiceNote bg0001 : invoice.getBG0001InvoiceNote()) {
                if (!bg0001.getBT0022InvoiceNote().isEmpty()) {
                    BT0022InvoiceNote bt0022 = bg0001.getBT0022InvoiceNote(0);
                    Element note = new Element("Note");
                    note.setText(bt0022.getValue());
                    root.addContent(note);
                }
            }
        }

        if (!invoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
            String converted = conversionRegistry.convert(Iso4217CurrenciesFundsCodes.class, String.class, invoice.getBT0005InvoiceCurrencyCode(0).getValue());
            convert("DocumentCurrencyCode", converted);
        }

    }

    private void convert(String tag, String value) {
        root.addContent(new Element(tag).setText(value));
    }
}
