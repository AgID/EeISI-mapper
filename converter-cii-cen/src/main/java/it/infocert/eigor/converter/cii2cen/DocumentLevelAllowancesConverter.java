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
public class DocumentLevelAllowancesConverter extends CustomConverter {

    public DocumentLevelAllowancesConverter(Reflections reflections, ConversionRegistry conversionRegistry) {
        super(reflections, conversionRegistry);
    }

    public ConversionResult<BG0000Invoice> toBG0020(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        String xPathBT0092 = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeSettlement/SpecifiedTradeAllowanceCharge/ActualAmount";
        String xPathBT0093 = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeSettlement/SpecifiedTradeAllowanceCharge/BasisAmount";
        String xPathBT0094 = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeSettlement/SpecifiedTradeAllowanceCharge/CalculationPercent";
        String xPathBT0095A = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeSettlement/SpecifiedTradeAllowanceCharge/CategoryTradeTax/TypeCode";
        String xPathBT0095B = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeSettlement/SpecifiedTradeAllowanceCharge/CategoryTradeTax/CategoryCode";
        String xPathBT0096 = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeSettlement/SpecifiedTradeAllowanceCharge/RateApplicablePercent";
        String xPathBT0097 = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeSettlement/SpecifiedTradeAllowanceCharge/Reason";
        String xPathBT0098 = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeSettlement/SpecifiedTradeAllowanceCharge/ReasonCode";

        List<Element> xPathBT0092elementList = CommonConversionModule.evaluateXpath(document, xPathBT0092);
        List<Element> xPathBT0093elementList = CommonConversionModule.evaluateXpath(document, xPathBT0093);
        List<Element> xPathBT0094elementList = CommonConversionModule.evaluateXpath(document, xPathBT0094);
        List<Element> xPathBT0095AelementList = CommonConversionModule.evaluateXpath(document, xPathBT0095A);
        List<Element> xPathBT0095BelementList = CommonConversionModule.evaluateXpath(document, xPathBT0095B);
        List<Element> xPathBT0096elementList = CommonConversionModule.evaluateXpath(document, xPathBT0096);
        List<Element> xPathBT0097elementList = CommonConversionModule.evaluateXpath(document, xPathBT0097);
        List<Element> xPathBT0098elementList = CommonConversionModule.evaluateXpath(document, xPathBT0098);


        //TODO

        return new ConversionResult<>(errors, invoice);
    }
}
