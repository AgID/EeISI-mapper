package it.infocert.eigor.converter.ubl2cen.mapping;

import it.infocert.eigor.api.ApplicationContextProvider;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.conversion.StringToJavaLocalDateConverter;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * This class does the generic one to one transformations
 */
public class GenericOneToOneTransformation {

    private static Logger log = LoggerFactory.getLogger(GenericOneToOneTransformation.class);

    private final String xPath;
    private final String bgBtPath;
    private Reflections reflections;
    private StringToJavaLocalDateConverter stringToLocalDateConverter;

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
        stringToLocalDateConverter = new StringToJavaLocalDateConverter(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
    }

    /**
     * Transform.
     *
     * @param document the document
     * @param invoice  the invoice
     */
    public void transform(Document document, BG0000Invoice invoice, List<Exception> errors) throws SyntaxErrorInInvoiceFormatException {
        String logPrefix = "(" + xPath + " - " + bgBtPath + ") ";
        log.info(logPrefix + "resolving");

        NodeList nodeList = CommonConversionModule.evaluateXpath(document, xPath);
        Node item = nodeList.item(0);
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
                    Arrays.stream(constructors).forEach(constructor -> {
                        try {
                            if (constructor.getParameterCount() == 0) {
                                bt.add((BTBG) constructor.newInstance());
                            } else {
                                Class<?>[] parameterTypes = constructor.getParameterTypes();
                                Arrays.stream(parameterTypes).forEach(paramType -> {
                                    // FIXME add data converter...
                                    if (String.class.equals(paramType)) {
                                        try {
                                            bt.add((BTBG) constructor.newInstance(item.getTextContent()));
                                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                            log.error(e.getMessage(), e);
                                            errors.add(e);
                                        }
                                    } else if(LocalDate.class.equals(paramType)) {
                                        try {
                                            bt.add((BTBG) constructor.newInstance(stringToLocalDateConverter.convert(item.getTextContent())));
                                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                            log.error(e.getMessage(), e);
                                            errors.add(e);
                                        }
                                    } else {
                                        log.error(logPrefix + "paramType is not String: " + paramType);
                                        errors.add(new Exception(logPrefix + "paramType is not String: " + paramType));
                                    }
                                });
                            }
                        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                            log.error(e.getMessage(), e);
                            errors.add(e);
                        }
                    });
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