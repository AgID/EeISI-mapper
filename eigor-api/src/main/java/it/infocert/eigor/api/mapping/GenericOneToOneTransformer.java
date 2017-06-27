package it.infocert.eigor.api.mapping;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Consumer;
import com.google.common.collect.Lists;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import org.jdom2.Document;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdom2.Element;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generic class to transform both cen objects in XML elements and viceversa,
 * based on a 1-1 configurable mapping
 */
@SuppressWarnings("unchecked")
public class GenericOneToOneTransformer {
    private static Logger log = LoggerFactory.getLogger(GenericOneToOneTransformer.class);
    private final String xPath;
    private final String cenPath;
    private final ConversionRegistry conversionRegistry;
    private final InvoiceUtils invoiceUtils;


    public GenericOneToOneTransformer(String xPath, String cenPath, Reflections reflections, ConversionRegistry conversionRegistry) {
        this.xPath = xPath;
        this.cenPath = cenPath;
        this.conversionRegistry = conversionRegistry;
        this.invoiceUtils = new InvoiceUtils(reflections);
    }

    private List<BTBG> getBtRecursively(BTBG parent, final ArrayList<String> steps, final List<BTBG> bts) {
        List<BTBG> childrenAsList = invoiceUtils.getChildrenAsList(parent, steps.remove(0));
        for (BTBG btbg : childrenAsList) {
            if (btbg.getClass().getSimpleName().startsWith("BG")) {
                getBtRecursively(btbg, (ArrayList<String>) steps.clone(), bts);
            } else {
                bts.add(btbg);
            }
        }

        return bts;
    }

    private synchronized List<Element> createXmlPathRecursively(Element parent, final ArrayList<String> steps, final int times, final List<Element> leafs) {
        if (times == 0) {
            return leafs;
        }

        boolean last = false;
        if (steps.size() == 1) {
            last = true;
        }
        if (!steps.isEmpty()) {
            String tagname = steps.remove(0);
            if (tagname.contains("[") && tagname.contains("]")) {
                tagname = tagname.substring(0, tagname.indexOf("["));
                List<Element> children = parent.getChildren(tagname);
                ArrayList<Element> elements = new ArrayList<>(times);
                int loop = 0;
                if (children.size() < times) {
                    loop = times - children.size();
                } else if (children.isEmpty()) {
                    loop = times;
                }
                elements.addAll(children);
                for (int i = 0; i < loop; i++) {
                    Element el = new Element(tagname);
                    parent.addContent(el);
                    elements.add(el);
                }

                for (Element element : elements) {
                    if (last) {
                        leafs.add(element);
                    } else {
                        createXmlPathRecursively(element, (ArrayList<String>) steps.clone(), times, leafs);
                    }
                }
            } else {
                Element child = parent.getChild(tagname);
                if (child == null) {
                    child = new Element(tagname);
                    parent.addContent(child);
                }
                if (last) {
                    leafs.add(child);
                } else {
                    createXmlPathRecursively(child, (ArrayList<String>) steps.clone(), times, leafs);
                }
            }

        }

        return leafs;
    }


    /**
     * Transform a {@link BG0000Invoice} into an XML DOM that can be later serialized
     *
     * @param invoice  the {@link BG0000Invoice} from which to take the values
     * @param document the {@link Document} to populate with BT values
     * @param errors   a list of {@link ConversionIssue}, to be filled if an error occurs during the conversion
     * @throws SyntaxErrorInInvoiceFormatException
     */

    public void transformCenToXml(BG0000Invoice invoice, Document document, final List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {

        final String logPrefix = "(" + cenPath + " - " + xPath + ") ";
        log.info(logPrefix + "resolving");

        String[] cenSteps = cenPath.substring(1).split("/");
        ArrayList<String> xmlSteps = Lists.newArrayList(xPath.substring(1).split("/"));
        String remove //keep it that way, there were some access races without it that still need to be investigated
                = xmlSteps.remove(0);

        List<BTBG> bts;
        try {
            bts = getBtRecursively(invoice, Lists.newArrayList(cenSteps), new ArrayList<BTBG>(0));
        } catch (Exception e) {
            errors.add(ConversionIssue.newError(e, e.getMessage()));
            return;
        }

        List<Element> elements = createXmlPathRecursively(document.getRootElement(), xmlSteps, bts.size(), new ArrayList<Element>(0));

        if (bts.size() > elements.size()) {
            ConversionIssue e = ConversionIssue.newError(
                    new IllegalArgumentException("BTs can not be more than XML elements"),
                    String.format("Found %d %s but only %d %s XML elements were created. " +
                                    "Maybe there is an error in the configuration file or the converted CEN object is not well formed. " +
                                    "Check rule-report.csv for more informations about BT/BGs validation",
                            bts.size(),
                            cenPath,
                            elements.size(),
                            xPath
                    )
            );
            errors.add(e);
            log.error(e.getMessage());
            return;
        }
        for (int i = 0; i < bts.size(); i++) {
            BTBG btbg = bts.get(i);
            Object value = getBtValue(btbg, errors);
            Element element = elements.get(i);
            if (value != null) {
                Class<?> aClass = value.getClass();
                String converted = conversionRegistry.convert(aClass, String.class, value);
                element.setText(converted);
            }
        }
    }

    private Object getBtValue(BTBG btbg, List<ConversionIssue> errors) {
        try {
            Method getValue = btbg.getClass().getDeclaredMethod("getValue");
            return getValue.invoke(btbg);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            errors.add(ConversionIssue.newError(e));
            return null;
        }
    }

    /**
     * Transform a {@link Document} into an {@link BG0000Invoice}
     *
     * @param document the {@link Document} to read values from
     * @param invoice  the {@link BG0000Invoice} to populate with values from the XML
     * @param errors   a list of {@link ConversionIssue}, to be filled if an error occurs during the conversion
     * @throws SyntaxErrorInInvoiceFormatException
     */
    public void transformXmlToCen(Document document, BG0000Invoice invoice, final List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
        final String logPrefix = "(" + xPath + " - " + cenPath + ") ";
        log.info(logPrefix + "resolving");

        List<Element> elementList = CommonConversionModule.evaluateXpath(document, xPath);

        if (!elementList.isEmpty()) {
            final Element item = elementList.get(0);
            log.info(logPrefix + "item found: " + item);

            // find the parent BG
            String bgPath = cenPath.substring(0, cenPath.lastIndexOf("/"));
            invoiceUtils.ensurePathExists(bgPath, invoice);
            BTBG bg = invoiceUtils.getFirstChild(bgPath, invoice);
            log.info(logPrefix + "BG parent: " + bg);

            // FIXME This is not covering cases where there can be multiple BGs or BTs of the same type
            // if there no child? what?
            if (!invoiceUtils.hasChild(cenPath, invoice)) {
                try {
                    // create BT element
                    String btName = cenPath.substring(cenPath.lastIndexOf("/") + 1);
                    Class<? extends BTBG> btClass = invoiceUtils.getBtBgByName(btName);
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
                                        @Override
                                        public void consume(Class<?> paramType) {

                                            try {
                                                Object constructorParam = conversionRegistry.convert(String.class, paramType, item.getText());
                                                try {
                                                    bt.add((BTBG) constructor.newInstance(constructorParam));
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

                    log.info(logPrefix + "bt element created: " + bt);

                    // add BT element to BG parent
                    if (!bt.isEmpty()) {
                        invoiceUtils.addChild(bg, bt.get(0));
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error(e.getMessage(), e);
                    errors.add(ConversionIssue.newError(e));
                }
            }
        }
    }
}
