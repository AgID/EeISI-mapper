package it.infocert.eigor.converter.fattpa2cen.ciao;

import it.infocert.eigor.converter.fattpa2cen.models.FatturaElettronicaType;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import org.reflections.Reflections;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class GenericOneToOneTransformation {


    private final String xPathExpression;
    private final Class<? extends BTBG> btbgClass;
    private final String btPath;


    public GenericOneToOneTransformation(String xPathExpression, Class<? extends BTBG> btbgClass, String invoicePath) {
        this.xPathExpression = xPathExpression;
        this.btbgClass = btbgClass;
        this.btPath = invoicePath;
    }

    public GenericOneToOneTransformation(String xPathExpression, String btbgClass, String btPath) {
        this.xPathExpression = xPathExpression;
        this.btPath = btPath;
        try {
            this.btbgClass = (Class<? extends BTBG>) Class.forName(btbgClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void transform(Document document, FatturaElettronicaType fattura, BG0000Invoice invoice) {
        InvoiceUtils invoiceUtils = new InvoiceUtils(new Reflections("it.infocert"));

        NodeList nodeList = CommonConversionModule.evaluateXpath(document, xPathExpression);
        Node item = nodeList.item(0);

        if (item != null) {
            String bgPath = btPath.substring(0, btPath.lastIndexOf("/"));
            String btName = btPath.substring(btPath.lastIndexOf("/") + 1);
            invoiceUtils.ensurePathExists(bgPath, invoice);
            BTBG bg = invoiceUtils.getChild(bgPath, invoice);
            Class<? extends BTBG> btClass = invoiceUtils.getBtBgByName(btName);
            try {
                Constructor<? extends BTBG> btConstructor = btClass.getConstructor(String.class);
                BTBG bt = btConstructor.newInstance(item.getTextContent());

                invoiceUtils.addChild(bg, bt);

            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }



}
