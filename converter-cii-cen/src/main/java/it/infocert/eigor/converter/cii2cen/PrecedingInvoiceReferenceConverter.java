package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.jdom2.Document;
import org.jdom2.Element;
import org.reflections.Reflections;

import java.util.List;

/**
 * Created by Marco Basilico on 24/07/2017.
 */
public class PrecedingInvoiceReferenceConverter extends CustomConverter {

    public PrecedingInvoiceReferenceConverter(Reflections reflections, ConversionRegistry conversionRegistry) {
        super(reflections, conversionRegistry);
    }

    //BG0003
    //TODO verifica  per elementi multipli
    public ConversionResult<BG0000Invoice> toBG0003(Document document, BG0000Invoice invoice, List<ConversionIssue> errors) {
        String xPathBT0025 = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeSettlement/InvoiceReferencedDocument/IssuerAssignedID";
        String xPathBT0026 = "/CrossIndustryInvoice/SupplyChainTradeTransaction/ApplicableHeaderTradeSettlement/InvoiceReferencedDocument/FormattedIssueDateTime/DateTimeString";

        List<Element> xPathBT0025elementList = CommonConversionModule.evaluateXpath(document, xPathBT0025);
        List<Element> xPathBT0026elementList = CommonConversionModule.evaluateXpath(document, xPathBT0026);

        if (!xPathBT0025elementList.isEmpty()) {
            for(Element elem : xPathBT0025elementList) {
                Object assignedIssuerAssignedID = transformer("/BG0003/BT0025", invoice, elem.getText(), errors);
            }
        }
        if (!xPathBT0025elementList.isEmpty()) {
            for(Element elem : xPathBT0026elementList) {
                Object assignedDateTimeString = transformer("/BG0003/BT0026", invoice, elem.getText(), errors);
            }
        }

        return new ConversionResult<>(errors, invoice);
    }
}
