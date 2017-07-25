package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.reflections.Reflections;

import java.util.List;

/**
 * Created by Marco Basilico on 24/07/2017.
 */
public class SellerConverter extends CustomConverter {

    public SellerConverter(Reflections reflections, ConversionRegistry conversionRegistry) {
        super(reflections, conversionRegistry);
    }

    /*
    mapBt29.type=concatenation
    mapBt29.xml.source.1=/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeAgreement/SellerTradeParty/ID
    mapBt29.xml.source.2=/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeAgreement/SellerTradeParty/GlobalID
    mapBt29.cen.target=/BT0029
    mapBt29.cen.expression=%1 %2
     */

    //BG0004/BT0029
    public ConversionResult<BG0000Invoice> toBT0029(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {
        String xPathBT0029A = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeAgreement/SellerTradeParty/ID";
        String xPathBT0029B = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeAgreement/SellerTradeParty/GlobalID";

        List<Element> xPathBT0029AelementList = CommonConversionModule.evaluateXpath(document, xPathBT0029A);
        List<Element> xPathBT0029BelementList = CommonConversionModule.evaluateXpath(document, xPathBT0029B);

        if (!xPathBT0029AelementList.isEmpty() && !xPathBT0029AelementList.isEmpty()) {
            String target = "";
            for(int i=0; i<xPathBT0029AelementList.size(); i++) {
                target = xPathBT0029AelementList.get(i).getText() + " " + xPathBT0029BelementList.get(i).getText();
                Object assignedSellerIdentifier = transformer("/BG0004/BT0029", invoice, target, errors);
            }
        }

        return new ConversionResult<>(errors, invoice);
    }
}
