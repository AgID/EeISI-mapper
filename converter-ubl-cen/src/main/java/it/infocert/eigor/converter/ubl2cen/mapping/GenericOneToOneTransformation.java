package it.infocert.eigor.converter.ubl2cen.mapping;

import com.amoerie.jstreams.Stream;

import com.amoerie.jstreams.functions.Consumer;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
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
import java.util.List;


/**
 * This class does the generic one to one transformations
 */
public class GenericOneToOneTransformation {

    private static Logger log = LoggerFactory.getLogger(GenericOneToOneTransformation.class);

    private final String xPath;
    private final String bgBtPath;
    private Reflections reflections;

    /**
     * Instantiates a new Generic one to one transformation.
     *
     * @param xPath    the UBL invoice path
     * @param bgBtPath the CEN invoice path
     */
    public GenericOneToOneTransformation(String xPath, String bgBtPath, Reflections reflections) {
        this.xPath = xPath;
        this.bgBtPath = bgBtPath;
        this.reflections = reflections;
    }

    /**
     * Transform.
     *
     * @param document the document
     * @param invoice  the invoice
     */
    public void transform(Document document, BG0000Invoice invoice, final List<Exception> errors) throws SyntaxErrorInInvoiceFormatException {
        final String logPrefix = "(" + xPath + " - " + bgBtPath + ") ";
        log.info(logPrefix + "resolving");

        NodeList nodeList = CommonConversionModule.evaluateXpath(document, xPath);
        final Node item = nodeList.item(0);
        log.info(logPrefix + "item found: " + item);

        if (item != null) {

            InvoiceUtils invoiceUtils = new InvoiceUtils(reflections);

            // find the parent BG
            String bgPath = bgBtPath.substring(0, bgBtPath.lastIndexOf("/"));
            invoiceUtils.ensurePathExists(bgPath, invoice);
            BTBG bg = invoiceUtils.getFirstChild(bgPath, invoice);
            log.info(logPrefix + "BG parent: " + bg);

            // FIXME This is not covering cases where there can be multiple BGs or BTs of the same type
            // if there no child? what?
            if (!invoiceUtils.hasChild(bgBtPath, invoice)) {
                try {
                    // create BT element
                    String btName = bgBtPath.substring(bgBtPath.lastIndexOf("/") + 1);
                    Class<? extends BTBG> btClass = invoiceUtils.getBtBgByName(btName);
                    Constructor<?>[] constructors = btClass.getConstructors();
                    final ArrayList<BTBG> bt = new ArrayList<>(1);

                    com.amoerie.jstreams.functions.Consumer<Constructor<?>> k = new com.amoerie.jstreams.functions.Consumer<Constructor<?>>() {
                        @Override public void consume(final Constructor<?> constructor) {
                            try {
                                if (constructor.getParameterTypes().length == 0) {
                                    bt.add((BTBG) constructor.newInstance());
                                } else {
                                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                                    List<Class<?>> classes = Arrays.asList(parameterTypes);
                                    Stream<Class<?>> classes1 = Stream.create(classes);

                                    classes1.forEach(new Consumer<Class<?>>() {
                                        @Override public void consume(Class<?> paramType) {
                                            // FIXME add data converter...
                                            if (String.class.equals(paramType)) {
                                                try {
                                                    bt.add((BTBG) constructor.newInstance(item.getTextContent()));
                                                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                                    log.error(e.getMessage(), e);
                                                    errors.add(e);
                                                }
                                            } else {
                                                log.error(logPrefix + "paramType is not String: " + paramType);
                                                errors.add(new Exception(logPrefix + "paramType is not String: " + paramType));
                                            }
                                        }
                                    });

                                }
                            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                                log.error(e.getMessage(), e);
                                errors.add(e);
                            }
                        }
                    };
                    Stream.create( Arrays.asList(constructors) ).forEach(k);

                    log.info(logPrefix + "bt element created: " + bt);

                    // add BT element to BG parent
                    if (!bt.isEmpty()) {
                        invoiceUtils.addChild(bg, bt.get(0));
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error(e.getMessage(), e);
                    errors.add(e);
                }
            }
        }
    }

}