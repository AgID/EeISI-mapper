package it.infocert.eigor.converter.commons.cen2ubl;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;

import java.util.List;

public class DocumentCurrencyCodeConverter extends FirstLevelElementsConverter {
    
    @Override
    public void customMap(BG0000Invoice invoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        if (!invoice.getBT0005InvoiceCurrencyCode().isEmpty()) {
            String converted = conversionRegistry.convert(Iso4217CurrenciesFundsCodes.class, String.class, invoice.getBT0005InvoiceCurrencyCode(0).getValue());
            convert("DocumentCurrencyCode", converted);
        }

    }

}
