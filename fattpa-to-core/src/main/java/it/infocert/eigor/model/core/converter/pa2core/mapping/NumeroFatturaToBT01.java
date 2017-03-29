package it.infocert.eigor.model.core.converter.pa2core.mapping;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class NumeroFatturaToBT01{
    private final static String XPATHEXPRESSION = "//FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Numero";

    public static void convertTo(Document doc, BG0000Invoice coreInvoice) {
        NodeList nodes = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSION);
        String textContent = nodes.item(0).getTextContent();
        coreInvoice.getBT0001InvoiceNumber().add(new BT0001InvoiceNumber(textContent));
    }
}
