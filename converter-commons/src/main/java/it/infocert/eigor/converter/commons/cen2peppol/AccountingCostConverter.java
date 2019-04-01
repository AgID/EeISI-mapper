package it.infocert.eigor.converter.commons.cen2peppol;

import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.converter.commons.cen2ubl.FirstLevelElementsConverter;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0019BuyerAccountingReference;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class AccountingCostConverter extends FirstLevelElementsConverter {

    @Override
    public void customMap(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        if (!cenInvoice.getBT0019BuyerAccountingReference().isEmpty()) {
            BT0019BuyerAccountingReference bt0019 = cenInvoice.getBT0019BuyerAccountingReference(0);
            convert("AccountingCost", bt0019.getValue());
        }
    }
}
