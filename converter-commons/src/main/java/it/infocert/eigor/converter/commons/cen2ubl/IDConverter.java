package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;

import java.util.List;

public class IDConverter extends FirstLevelElementsConverter {

    @Override
    public void customMap(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        if (!invoice.getBT0001InvoiceNumber().isEmpty()) {
            convert("ID", invoice.getBT0001InvoiceNumber(0).getValue());
        }

    }

}
