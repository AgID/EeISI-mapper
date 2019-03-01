package it.infocert.eigor.converter.cii2cen;

import com.google.common.collect.ImmutableMap;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;

import java.util.List;

public class Cii2CenUtils {

    private static final CiiXPathfactory ciiXpathFactory = new CiiXPathfactory();

    public static List<Element> findTaxTotalAmountsByCurrencyID(Document document, String currencyID) {
        String xpathExpression = "/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:TaxTotalAmount[@currencyID=$currencyID]";
        XPathExpression<Element> xp2;
        synchronized (ciiXpathFactory) {
            xp2 = ciiXpathFactory.compile(xpathExpression, Filters.element(), ImmutableMap.of("currencyID", currencyID));
        }
        return xp2.evaluate(document);
    }

    public static List<Element> findTaxCurrencyCodes(Document document) {
        XPathExpression<Element> invoiceCurrencyCodesXPath;
        synchronized (ciiXpathFactory){
            invoiceCurrencyCodesXPath = ciiXpathFactory.compile(
                    "/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:TaxCurrencyCode",
                    Filters.element(),
                    null);
        }
        return invoiceCurrencyCodesXPath.evaluate(document);
    }

    public static List<Element> findInvoiceCurrencyCodes(Document document) {
        XPathExpression<Element> invoiceCurrencyCodesXPath;
        synchronized (ciiXpathFactory) {
            invoiceCurrencyCodesXPath = ciiXpathFactory.compile(
                    "/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:InvoiceCurrencyCode",
                    Filters.element(),
                    null);
        }
        return invoiceCurrencyCodesXPath.evaluate(document);
    }

}
