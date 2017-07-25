package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.reflections.Reflections;

import java.util.List;

/**
 * Created by Marco Basilico on 25/07/2017.
 */
public class CreditTransferConverter extends CustomConverter {

    public CreditTransferConverter(Reflections reflections, ConversionRegistry conversionRegistry) {
        super(reflections, conversionRegistry);
    }


    public ConversionResult<BG0000Invoice> toBG0017(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        String xPathBT0084A = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeSettlement/SpecifiedTradeSettlementPaymentMeans/PayeePartyCreditorFinancialAccount/IBANID";
        String xPathBT0084B = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeSettlement/SpecifiedTradeSettlementPaymentMeans/PayeePartyCreditorFinancialAccount/ProprietaryID";
        String xPathBT0085 = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeSettlement/SpecifiedTradeSettlementPaymentMeans/PayeePartyCreditorFinancialAccount/AccountName";
        String xPathBT0086A = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeSettlement/SpecifiedTradeSettlementPaymentMeans/PayerSpecifiedDebtorFinancialInstitution/BICID";
        String xPathBT0086B = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeSettlement/SpecifiedTradeSettlementPaymentMeans/PayeeSpecifiedCreditorFinancialInstitution/BICID";

        //TODO

        return new ConversionResult<>(errors, invoice);
    }
}
