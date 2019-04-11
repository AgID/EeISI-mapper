package it.infocert.eigor.converter.commons.cen2peppol;

import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0002InvoiceIssueDate;
import it.infocert.eigor.model.core.model.BT0007ValueAddedTaxPointDate;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class DateFormatConverter implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

        Element root = document.getRootElement();

        final Element issueDate = new Element("IssueDate");
        final Element invoiceTypeCode = new Element("TaxPointDate");
        final Element dueDate = new Element("DueDate");


        List<BT0002InvoiceIssueDate> bt02 = cenInvoice.getBT0002InvoiceIssueDate();
        if (!bt02.isEmpty()) {
            for (BT0002InvoiceIssueDate elembt02 : bt02) {
                if (elembt02.getValue() != null) {
                    String formattedDate = elembt02.getValue().toString("yyyy-MM-dd");
                    issueDate.setText(formattedDate);
                    root.addContent(issueDate);
                }
            }
        }

        List<BT0007ValueAddedTaxPointDate> bt07 = cenInvoice.getBT0007ValueAddedTaxPointDate();
        if (!bt07.isEmpty()) {
            for (BT0007ValueAddedTaxPointDate elembt07 : bt07) {
                if (elembt07.getValue() != null) {
                    String formattedDate = elembt07.getValue().toString("yyyy-MM-dd");
                    invoiceTypeCode.setText(formattedDate);
                    root.addContent(invoiceTypeCode);
                }
            }
        }
    }
}
