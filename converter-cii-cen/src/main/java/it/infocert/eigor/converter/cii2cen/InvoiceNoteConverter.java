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
public class InvoiceNoteConverter extends CustomConverter {

    public InvoiceNoteConverter(Reflections reflections, ConversionRegistry conversionRegistry) {
        super(reflections, conversionRegistry);
    }

    //BG0001
    public ConversionResult<BG0000Invoice> toBG0001(Document document, BG0000Invoice invoice, List<ConversionIssue> errors) {
        String xPathBT0021 = "/CrossIndustryInvoice/ExchangedDocument/IncludedNote/SubjectCode";
        String xPathBT0022 = "/CrossIndustryInvoice/ExchangedDocument/IncludedNote/Content";

        List<Element> xPathBT0021elementList = CommonConversionModule.evaluateXpath(document, xPathBT0021);
        List<Element> xPathBT0022elementList = CommonConversionModule.evaluateXpath(document, xPathBT0022);

        if (!xPathBT0021elementList.isEmpty()) {
            for(Element elem : xPathBT0021elementList) {
                Object assignedSubjectCode = transformer("/BG0001/BT0021", invoice, elem.getText(), errors);
            }
        }
        if (!xPathBT0022elementList.isEmpty()) {
            for(Element elem : xPathBT0022elementList) {
                Object assignedContent = transformer("/BG0001/BT0022", invoice, elem.getText(), errors);
            }
        }

        return new ConversionResult<>(errors, invoice);
    }
}
