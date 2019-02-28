package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomConverterUtils;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0110InvoiceTotalVatAmount;
import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.List;

import static it.infocert.eigor.converter.cii2cen.Cii2CenUtils.findTaxTotalAmountsByCurrencyID;

public class TaxTotalAmountToBT110Converter extends CustomConverterUtils implements CustomMapping<Document> {

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation, EigorConfiguration eigorConfiguration) {

        String currencyID = null;
        List<Element> invoiceCurrencyCodesXml = Cii2CenUtils.findInvoiceCurrencyCodes(document);
        if(invoiceCurrencyCodesXml.size()!=1) {
            String msg = "Wrong number of <InvoiceCurrencyCode>, expected 1 found " + invoiceCurrencyCodesXml.size() + " instead.";
            addErrorConversionIssue(errors, callingLocation, msg);
            return;
        }

        currencyID = invoiceCurrencyCodesXml.get(0).getText();
        List<Element> taxAmountsByCurrencyCodes = findTaxTotalAmountsByCurrencyID(document, currencyID);

        if(taxAmountsByCurrencyCodes.isEmpty()) {
            // no BT-110
            return;
        }else if(taxAmountsByCurrencyCodes.size() > 2){
            addErrorConversionIssue(errors, callingLocation, "Wrong number of <TaxTotalAmount> for '" + currencyID + "', expected 1 found " + taxAmountsByCurrencyCodes.size() + " instead.");
        }else{
            // ok
            Element element = taxAmountsByCurrencyCodes.get(0);
            BT0110InvoiceTotalVatAmount bt0110DocumentLevelChargeBaseAmount = new BT0110InvoiceTotalVatAmount(new BigDecimal(element.getText()));
            cenInvoice.getBG0022DocumentTotals(0).getBT0110InvoiceTotalVatAmount().add( bt0110DocumentLevelChargeBaseAmount );
        }


    }

    private void addErrorConversionIssue(List<IConversionIssue> errors, ErrorCode.Location callingLocation, String msg) {
        errors.add(ConversionIssue.newError(
                new IllegalArgumentException(msg),
                msg,callingLocation, ErrorCode.Action.HARDCODED_MAP, ErrorCode.Error.INVALID

        ));
    }

}
