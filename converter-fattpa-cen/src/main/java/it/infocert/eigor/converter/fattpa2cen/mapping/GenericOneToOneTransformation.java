package it.infocert.eigor.converter.fattpa2cen.mapping;

import it.infocert.eigor.converter.fattpa2cen.models.FatturaElettronicaType;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class GenericOneToOneTransformation {

    private static Logger log = LoggerFactory.getLogger(GenericOneToOneTransformation.class);

    private final String xPathExpression;
    private final String btPath;


    public GenericOneToOneTransformation(String xPathExpression, String btPath) {
        this.xPathExpression = xPathExpression;
        this.btPath = btPath;
    }



    public void transform(Document document, FatturaElettronicaType fattura, BG0000Invoice invoice) {
        InvoiceUtils invoiceUtils = new InvoiceUtils(new Reflections("it.infocert"));

        NodeList nodeList = CommonConversionModule.evaluateXpath(document, xPathExpression);
        Node item = nodeList.item(0);

        if (item != null) {
            String bgPath = btPath.substring(0, btPath.lastIndexOf("/"));
            String btName = btPath.substring(btPath.lastIndexOf("/") + 1);
            invoiceUtils.ensurePathExists(bgPath, invoice);
            BTBG bg = invoiceUtils.getFirstChild(bgPath, invoice);
            Class<? extends BTBG> btClass = invoiceUtils.getBtBgByName(btName);
            try {
                if (!invoiceUtils.hasChild(btPath, invoice)) { //FIXME This is not covering cases where there can be multiple BGs or BTs of the same type
                    Constructor<?>[] constructors = btClass.getConstructors();
                    BTBG bt = null;
                    for (Constructor<?> constructor : constructors) {
                        if (constructor.getParameterCount() == 0) {
                            bt = (BTBG) constructor.newInstance();
                        } else {
                            Class<?>[] parameterTypes = constructor.getParameterTypes();
                            for (Class<?> parameterType : parameterTypes) {
                                if (String.class.equals(parameterType)) {
                                    bt = (BTBG) constructor.newInstance(item.getTextContent());
                                } else {
                                    return;
                                }
                            }
                        }
                    }


                    invoiceUtils.addChild(bg, bt);
                }

            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
            }

        }
    }

}
