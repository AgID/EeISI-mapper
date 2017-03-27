package it.infocert.eigor.model.core.converter.pa2core.mapping;

import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.BT0001InvoiceNumber;
import it.infocert.eigor.model.core.model.CoreInvoice;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class NumeroFatturaToBT01{
    private final static String XPATHEXPRESSION = "//FatturaElettronicaBody/DatiGenerali/DatiGeneraliDocumento/Numero";

    public static void convertTo(Document doc, CoreInvoice coreInvoice) {
        NodeList nodes = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSION);
        String textContent = nodes.item(0).getTextContent();
        coreInvoice.getBt0001InvoiceNumbers().add(new BT0001InvoiceNumber(textContent));
    }
}
