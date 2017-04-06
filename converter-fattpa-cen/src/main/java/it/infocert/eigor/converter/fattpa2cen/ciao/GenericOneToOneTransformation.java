package it.infocert.eigor.converter.fattpa2cen.ciao;

import it.infocert.eigor.converter.fattpa2cen.models.FatturaElettronicaType;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GenericOneToOneTransformation {


    private final String xPathExpression;
    private final Class<? extends BTBG> btbgClass;


    public GenericOneToOneTransformation(String xPathExpression, Class<? extends BTBG> btbgClass) {
        this.xPathExpression = xPathExpression;
        this.btbgClass = btbgClass;
    }

    public void transform(Document document, FatturaElettronicaType fattura, BG0000Invoice invoice) {

        NodeList nodeList = CommonConversionModule.evaluateXpath(document, xPathExpression);
        Node item = nodeList.item(0);

        if (item != null) {

            //invoice/bg4/bg5/bg8/bt1

            //invoice.addChild(item.getTextContent(), btbgClass);
        }
    }
}
