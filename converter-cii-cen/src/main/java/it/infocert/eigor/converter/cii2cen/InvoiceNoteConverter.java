package it.infocert.eigor.converter.cii2cen;

import it.infocert.eigor.api.ConversionIssue;
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
public class InvoiceNoteConverter extends CustomConverter {

    public InvoiceNoteConverter(Reflections reflections, ConversionRegistry conversionRegistry) {
        super(reflections, conversionRegistry);
    }

    //BG0001
    public ConversionResult<BG0000Invoice> toBG0001(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {
        String xPathBT0021 = "/CrossIndustryInvoice/ExchangedDocument/IncludedNote/SubjectCode";
        String xPathBT0022 = "/CrossIndustryInvoice/ExchangedDocument/IncludedNote/Content";

        List<Element> xPathBT0021elementList = CommonConversionModule.evaluateXpath(document, xPathBT0021);
        List<Element> xPathBT0022elementList = CommonConversionModule.evaluateXpath(document, xPathBT0022);

        int index = 0;
        int maxElem = 0;
        int sizeListBT0021 = xPathBT0021elementList.size();
        int sizeListBT0022 = xPathBT0022elementList.size();

        if (sizeListBT0021 != 0 || sizeListBT0022 != 0) {
            if (sizeListBT0021 >= sizeListBT0022) {
                maxElem = sizeListBT0021;
            } else {
                maxElem = sizeListBT0022;
            }
        }
        while(maxElem > index) {
            if(xPathBT0021elementList.size() > index){
                transformer("/BG0001/BT0021", invoice, xPathBT0021elementList.get(index).getText(), errors);
            }
            if(xPathBT0022elementList.size() > index){
                transformer("/BG0001/BT0022", invoice, xPathBT0022elementList.get(index).getText(), errors);
            }
            index++;
        }

        return new ConversionResult<>(errors, invoice);
    }
}
