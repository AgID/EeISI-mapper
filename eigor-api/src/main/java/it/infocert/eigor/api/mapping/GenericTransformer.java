package it.infocert.eigor.api.mapping;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Consumer;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import org.jdom2.Document;
import org.jdom2.Element;
import org.reflections.Reflections;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class GenericTransformer {

    protected static Logger log = null;
    protected ConversionRegistry conversionRegistry;
    protected InvoiceUtils invoiceUtils;

    public GenericTransformer(Reflections reflections, ConversionRegistry conversionRegistry) {
        this.invoiceUtils = new InvoiceUtils(reflections);
        this.conversionRegistry = conversionRegistry;
    }

    protected String getNodeTextFromXPath(Document document, String xPath) {
        List<Element> elementList = CommonConversionModule.evaluateXpath(document, xPath);
        String item = null;
        if (!elementList.isEmpty()) {
            item = elementList.get(0).getText();
            log.trace(xPath + "item found: " + item);
        }
        return item;
    }

    protected Object addNewCenObjectFromStringValueToInvoice(String cenPath, BG0000Invoice invoice, final String xPathText, final List<ConversionIssue> errors) {

        final Object[] constructorParam = new Object[]{null};

        // find the parent BG
        String bgPath = cenPath.substring(0, cenPath.lastIndexOf("/"));
        invoiceUtils.ensurePathExists(bgPath, invoice);
        BTBG bg = invoiceUtils.getFirstChild(bgPath, invoice);
        log.trace(cenPath + " has BG parent: " + bg);

        // FIXME This is not covering cases where there can be multiple BGs or BTs of the same type
        // if there no child? what?
        if (!invoiceUtils.hasChild(cenPath, invoice)) {
            try {
                // create BT element
                String btName = cenPath.substring(cenPath.lastIndexOf("/") + 1);
                Class<? extends BTBG> btClass = invoiceUtils.getBtBgByName(btName);
                if(btClass==null) {
                    throw new RuntimeException("Unable to find BT with name '" + btName + "'");
                }

                Constructor<?>[] constructors = btClass.getConstructors();
                final ArrayList<BTBG> bt = new ArrayList<>(1);
                com.amoerie.jstreams.functions.Consumer<Constructor<?>> k = new com.amoerie.jstreams.functions.Consumer<Constructor<?>>() {
                    @Override
                    public void consume(final Constructor<?> constructor) {
                        try {
                            if (constructor.getParameterTypes().length == 0) {
                                bt.add((BTBG) constructor.newInstance());
                            } else {
                                Class<?>[] parameterTypes = constructor.getParameterTypes();
                                List<Class<?>> classes = Arrays.asList(parameterTypes);
                                Stream<Class<?>> classes1 = Stream.create(classes);

                                classes1.forEach(new Consumer<Class<?>>() {
                                    @Override public void consume(Class<?> paramType) {

                                        try {
                                            constructorParam[0] = conversionRegistry.convert(String.class, paramType, xPathText);
                                            try {
                                                bt.add((BTBG) constructor.newInstance(constructorParam[0]));
                                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                                log.error(e.getMessage(), e);
                                                errors.add(ConversionIssue.newError(e));
                                            }
                                        } catch (IllegalArgumentException e) {
                                            log.error("There is no converter registered for: " + paramType, e);
                                            errors.add(ConversionIssue.newError(e));
                                        }
                                    }
                                });

                            }
                        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                            log.error(e.getMessage(), e);
                            errors.add(ConversionIssue.newError(e));
                        }
                    }
                };
                Stream.create(Arrays.asList(constructors)).forEach(k);

                log.trace(cenPath + " - bt element created: " + bt);

                // add BT element to BG parent
                if (!bt.isEmpty()) {
                    invoiceUtils.addChild(bg, bt.get(0));
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
                errors.add(ConversionIssue.newError(e));
            }
        }
        return constructorParam[0];
    }

    public abstract void transformXmlToCen(Document document, BG0000Invoice invoice, final List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException;
    public abstract void transformCenToXml(BG0000Invoice invoice, Document document, final List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException;
}
