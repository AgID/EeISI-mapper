package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.JavaLocalDateToStringConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0003PrecedingInvoiceReference;
import it.infocert.eigor.model.core.model.BT0025PrecedingInvoiceReference;
import it.infocert.eigor.model.core.model.BT0026PrecedingInvoiceIssueDate;
import org.jdom2.Document;
import org.jdom2.Element;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InvoiceDocumentReferenceConverter implements CustomMapping<Document> {
    private static final Logger log = LoggerFactory.getLogger(InvoiceDocumentReferenceConverter.class);

    @Override
    public void map(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

        TypeConverter<LocalDate, String> dateConverter = JavaLocalDateToStringConverter.newConverter();

        Element invoiceElm = document.getRootElement();
        if (invoiceElm != null) {
            if (!invoice.getBG0003PrecedingInvoiceReference().isEmpty()) {
                BG0003PrecedingInvoiceReference bg0003 = invoice.getBG0003PrecedingInvoiceReference(0);
                Element billingReference = new Element("BillingReference");
                Element invoiceDocumentReference = new Element("InvoiceDocumentReference");
                billingReference.addContent(invoiceDocumentReference);

                if (!bg0003.getBT0025PrecedingInvoiceReference().isEmpty() || !bg0003.getBT0026PrecedingInvoiceIssueDate().isEmpty()) {
                    invoiceElm.addContent(billingReference);

                    if (!bg0003.getBT0025PrecedingInvoiceReference().isEmpty()) {
                        BT0025PrecedingInvoiceReference bt0026 = bg0003.getBT0025PrecedingInvoiceReference(0);
                        Element id = new Element("ID");
                        id.setText(bt0026.getValue());
                        invoiceDocumentReference.addContent(id);
                    }

                    if (!bg0003.getBT0026PrecedingInvoiceIssueDate().isEmpty()) {
                        BT0026PrecedingInvoiceIssueDate bt0026 = bg0003.getBT0026PrecedingInvoiceIssueDate(0);
                        Element issueDate = new Element("IssueDate");
                        final LocalDate date = bt0026.getValue();
                        try {
                            issueDate.setText(dateConverter.convert(date));
                            invoiceDocumentReference.addContent(issueDate);
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
                }
            }
        }
    }
}
