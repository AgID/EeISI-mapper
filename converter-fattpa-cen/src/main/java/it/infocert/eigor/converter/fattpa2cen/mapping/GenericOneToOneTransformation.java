package it.infocert.eigor.converter.fattpa2cen.mapping;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Consumer;
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
import java.util.ArrayList;
import java.util.Arrays;

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
        final Node item = nodeList.item(0);

        if (item != null) {
            String bgPath = btPath.substring(0, btPath.lastIndexOf("/"));
            String btName = btPath.substring(btPath.lastIndexOf("/") + 1);
            invoiceUtils.ensurePathExists(bgPath, invoice);
            BTBG bg = invoiceUtils.getFirstChild(bgPath, invoice);
            Class<? extends BTBG> btClass = invoiceUtils.getBtBgByName(btName);
            try {
                if (!invoiceUtils.hasChild(btPath, invoice)) { //FIXME This is not covering cases where there can be multiple BGs or BTs of the same type
                    Constructor<?>[] constructors = btClass.getConstructors();
                    final ArrayList<BTBG> bt = new ArrayList<>(1);

                    Stream.create(Arrays.asList(constructors)).forEach(new com.amoerie.jstreams.functions.Consumer<Constructor<?>>() {
                        @Override public void consume(final Constructor<?> constructor) {
                            try {
                                if (constructor.getParameterTypes().length == 0) {
                                    bt.add((BTBG) constructor.newInstance());
                                } else {
                                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                                    Stream.create(Arrays.asList(parameterTypes)).forEach(new Consumer<Class<?>>() {
                                                                                             @Override public void consume(Class<?> paramType) {
                                                                                                 if (String.class.equals(paramType)) {
                                                                                                     try {
                                                                                                         bt.add((BTBG) constructor.newInstance(item.getTextContent()));
                                                                                                     } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                                                                                         e.printStackTrace();
                                                                                                     }
                                                                                                 }
                                                                                             }
                                                                                         }

                                    );
                                }
                            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    });

                    if (!bt.isEmpty()) {
                        invoiceUtils.addChild(bg, bt.get(0));
                    }
                }

            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
            }

        }
    }

}
