package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;

import java.util.List;

public class InvoiceTypeCodeConverter extends FirstLevelElementsConverter {

    @Override
    public void customMap(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        if (!invoice.getBT0003InvoiceTypeCode().isEmpty()) {
            Untdid1001InvoiceTypeCode bt0003 = invoice.getBT0003InvoiceTypeCode(0).getValue();
            String converted = conversionRegistry.convert(Untdid1001InvoiceTypeCode.class, String.class, bt0003);

            if (ErrorCode.Location.UBL_OUT.equals(callingLocation) || ErrorCode.Location.PEPPOL_OUT.equals(callingLocation)) {
                convert("InvoiceTypeCode", converted);
            }
            if (ErrorCode.Location.UBLCN_OUT.equals(callingLocation) || ErrorCode.Location.PEPPOLCN_OUT.equals(callingLocation)) {
                convert("CreditNoteTypeCode", converted);
            }
        }
    }
}
