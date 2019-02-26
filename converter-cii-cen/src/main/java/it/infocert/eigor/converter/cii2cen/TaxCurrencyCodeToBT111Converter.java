package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0111InvoiceTotalVatAmountInAccountingCurrency;
import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.List;

public class TaxCurrencyCodeToBT111Converter extends CustomConverterUtils implements CustomMapping<Document> {

    private final CiiXPathfactory CIIXpathFactory;

    public TaxCurrencyCodeToBT111Converter() {
        CIIXpathFactory = new CiiXPathfactory();
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

        String currencyID = null;
        List<Element> taxCurrencyCodes = Cii2CenUtils.findTaxCurrencyCodes(document);
        if(taxCurrencyCodes.size()!=1) {
            // no tax currency code, just return
            return;
        }

        currencyID = taxCurrencyCodes.get(0).getText();
        List<Element> taxAmountsByCurrencyCodes = Cii2CenUtils.findTaxTotalAmountsByCurrencyID(document, currencyID);

        if(taxAmountsByCurrencyCodes.isEmpty()) {
            // no BT-111
            return;
        }else if(taxAmountsByCurrencyCodes.size() > 2){
            addErrorConversionIssue(errors, callingLocation, "Wrong number of <TaxTotalAmount> for '" + currencyID + "', expected 1 found " + taxAmountsByCurrencyCodes.size() + " instead.");
        }else{
            // ok
            Element element = taxAmountsByCurrencyCodes.get(0);
            BT0111InvoiceTotalVatAmountInAccountingCurrency bt111 = new BT0111InvoiceTotalVatAmountInAccountingCurrency(new BigDecimal(element.getText()));
            cenInvoice.getBG0022DocumentTotals(0).getBT0111InvoiceTotalVatAmountInAccountingCurrency().add( bt111 );
        }


    }

    private void addErrorConversionIssue(List<IConversionIssue> errors, ErrorCode.Location callingLocation, String msg) {
        errors.add(ConversionIssue.newError(
                new IllegalArgumentException(msg),
                msg,callingLocation, ErrorCode.Action.HARDCODED_MAP, ErrorCode.Error.INVALID

        ));
    }



}
