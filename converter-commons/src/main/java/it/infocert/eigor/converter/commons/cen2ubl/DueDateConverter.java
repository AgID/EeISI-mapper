package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.joda.time.LocalDate;

import java.util.List;

public class DueDateConverter extends FirstLevelElementsConverter {

    @Override
    public void customMap(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        if (!invoice.getBT0009PaymentDueDate().isEmpty() && (ErrorCode.Location.UBL_OUT.equals(callingLocation) || ErrorCode.Location.PEPPOL_OUT.equals(callingLocation))) {
            String converted = conversionRegistry.convert(LocalDate.class, String.class, invoice.getBT0009PaymentDueDate(0).getValue());
            convert("DueDate", converted);
        }
    }
}
