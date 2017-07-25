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
 * Created by Marco Basilico on 25/07/2017.
 */
public class BuyerIdentifier extends CustomConverter {

    public BuyerIdentifier(Reflections reflections, ConversionRegistry conversionRegistry) {
        super(reflections, conversionRegistry);
    }

    //BG0007/BT0046
    public ConversionResult<BG0000Invoice> toBT0046(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {
        String xPathBT0046A = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeAgreement/BuyerTradeParty/ID";
        String xPathBT0046B = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeAgreement/BuyerTradeParty/GlobalID";

        List<Element> xPathBT0046AelementList = CommonConversionModule.evaluateXpath(document, xPathBT0046A);
        List<Element> xPathBT0046BelementList = CommonConversionModule.evaluateXpath(document, xPathBT0046B);

        if (!xPathBT0046AelementList.isEmpty() && !xPathBT0046AelementList.isEmpty()) {
            String target = "";
            for(int i=0; i<xPathBT0046AelementList.size(); i++) {
                target = xPathBT0046AelementList.get(i).getText() + " " + xPathBT0046BelementList.get(i).getText();
                Object assignedBuyerIdentifier = transformer("/BG0007/BT0046", invoice, target, errors);
            }
        }

        return new ConversionResult<>(errors, invoice);
    }
}
