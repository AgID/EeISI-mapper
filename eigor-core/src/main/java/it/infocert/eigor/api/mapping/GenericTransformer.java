package it.infocert.eigor.api.mapping;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Consumer;
import com.google.common.collect.Lists;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.AbstractBT;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.reflections.Reflections;
import org.slf4j.Logger;

import javax.print.Doc;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class GenericTransformer {

    protected static Logger log = null;
    protected ConversionRegistry conversionRegistry;
    protected InvoiceUtils invoiceUtils;

    public GenericTransformer(Reflections reflections, ConversionRegistry conversionRegistry) {
        this.invoiceUtils = new InvoiceUtils(reflections);
        this.conversionRegistry = conversionRegistry;
    }


    protected boolean hasAttribute(Document document, String xPath) {
        List<Element> elements = CommonConversionModule.evaluateXpath(document, xPath);
        if (!elements.isEmpty()) {
            Element element = elements.get(0);
            return element.hasAttributes();
        }
        return false;
    }

    protected Element getSingleNodeFromXpath(Document document, String xPath) {
        List<Element> elements = CommonConversionModule.evaluateXpath(document, xPath);
        if (!elements.isEmpty()) {
            return elements.get(0);
        }
        return null;
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

    protected Object getBtValue(BTBG btbg, List<IConversionIssue> errors) {
        if (btbg instanceof AbstractBT) {
            return ((AbstractBT) btbg).getValue();
        } else {
            errors.add(ConversionIssue.newError(new IllegalAccessException(btbg.denomination() + " is not a BT")));
            return null;
        }
    }

    protected Object addNewCenObjectWithIdentifierToInvoice(String cenPath, BG0000Invoice invoice, Element node, List<IConversionIssue> errors) {
        final Object[] constructorParam = new Object[]{null};

        // find the parent BG
        String bgPath = cenPath.substring(0, cenPath.lastIndexOf("/"));
        invoiceUtils.ensurePathExists(cenPath, invoice);
        BTBG bg;
        if (cenPath.startsWith("/BT")) {
            bg = invoice;
        } else {
            bg = invoiceUtils.getFirstChild(bgPath, invoice);
        }
        log.trace(cenPath + " has BG parent: " + bg);

        // FIXME This is not covering cases where there can be multiple BGs or BTs of the same type
        // if there no child? what?
        if (!invoiceUtils.hasChild(invoice, cenPath)) {
            try {
                // create BT element
                String btName = cenPath.substring(cenPath.lastIndexOf("/") + 1);
                Class<? extends BTBG> btClass = invoiceUtils.getBtBgByName(btName);
                if (btClass == null) {
                    throw new EigorRuntimeException("Unable to find BT with name '" + btName + "'");
                }

                Constructor<? extends BTBG> constructor = btClass.getConstructor(Identifier.class);
                Identifier id = new Identifier(node.getAttributeValue("schemeID"), node.getText());
                BTBG bt = constructor.newInstance(id);
                invoiceUtils.addChild(bg, bt);
                return id;
            } catch (NoSuchMethodException e) {
                errors.add(ConversionIssue.newError(e));
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                errors.add(ConversionIssue.newError(e));
            }
        }
        return null;
    }

    protected Object addNewCenObjectFromStringValueToInvoice(String cenPath, BG0000Invoice invoice, final String xPathText, final List<IConversionIssue> errors) {

        final Object[] constructorParam = new Object[]{null};

        // find the parent BG
        String bgPath = cenPath.substring(0, cenPath.lastIndexOf("/"));
        try {
            invoiceUtils.ensurePathExists(cenPath, invoice);
        } catch (Exception e) {
            errors.add(ConversionIssue.newError(e, e.getMessage(), null, "generic-transformation", null));
            return constructorParam[0];
        }
        BTBG bg;
        if (cenPath.startsWith("/BT")) {
            bg = invoice;
        } else {
            bg = invoiceUtils.getFirstChild(bgPath, invoice);
        }
        log.trace(cenPath + " has BG parent: " + bg);

        // FIXME This is not covering cases where there can be multiple BGs or BTs of the same type
        // if there no child? what?
        if (!invoiceUtils.hasChild(invoice, cenPath)) {
            try {
                // create BT element
                final String btName = cenPath.substring(cenPath.lastIndexOf("/") + 1);
                Class<? extends BTBG> btClass = invoiceUtils.getBtBgByName(btName);
                if (btClass == null) {
                    throw new EigorRuntimeException("Unable to find BT with name '" + btName + "'");
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
                                    @Override
                                    public void consume(Class<?> paramType) {

                                        try {
                                            constructorParam[0] = conversionRegistry.convert(String.class, paramType, xPathText);
                                            try {
                                                bt.add((BTBG) constructor.newInstance(constructorParam[0]));
                                            } catch (InstantiationException | IllegalAccessException e) {
                                                log.error(e.getMessage(), e);
                                                errors.add(ConversionIssue.newError(e));
                                            } catch (InvocationTargetException e) {
                                                String message = constructorParam[0] == null ?
                                                        String.format("%s - Constructor parameter conversion yielded null for %s with value %s",
                                                                btName,
                                                                paramType.getSimpleName(),
                                                                xPathText
                                                        )
                                                        : e.getClass().getSimpleName();
                                                log.error(e.getMessage() == null ?
                                                                message
                                                                : e.getMessage()
                                                        , e);
                                                errors.add(ConversionIssue.newError(e, message, null, "ConstructorParameterConversion", null));

                                            }
                                        } catch (IllegalArgumentException e) {
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

    protected List<Element> getAllXmlElements(String xPath, Document document, int btsSize, String cenPath, List<IConversionIssue> errors) {
        ArrayList<String> xmlSteps = Lists.newArrayList(xPath.substring(1).split("/"));
        String remove //keep it that way, there were some access races without it that still need to be investigated
                = xmlSteps.remove(0);
        boolean attribute = false;
        String last;
        if ((last = xmlSteps.get(xmlSteps.size() - 1)).startsWith("@")) {
            xmlSteps.remove(last);
            attribute = true;
        }

        List<Element> elements = createXmlPathRecursively(document.getRootElement(), xmlSteps, btsSize, new ArrayList<Element>(0));
        if (btsSize > elements.size()) {
            IConversionIssue e = ConversionIssue.newError(
                    new IllegalArgumentException("BTs can not be more than XML elements"),
                    String.format("Found %d %s but only %d %s XML elements were created. " +
                                    "Maybe there is an error in the configuration file or the converted CEN object is not well formed. " +
                                    "Check rule-report.csv for more informations about BT/BGs validation",
                            btsSize,
                            cenPath,
                            elements.size(),
                            xPath
                    )
            );
            errors.add(e);
            log.error(e.getMessage());
            return null;
        }
        return elements;
    }

    protected List<BTBG> getAllBTs(String cenPath, BG0000Invoice invoice, final List<IConversionIssue> errors) {
        String[] cenSteps = cenPath.substring(1).split("/");
        List<BTBG> bts;
        try {
            bts = getBtRecursively(invoice, Lists.newArrayList(cenSteps), new ArrayList<BTBG>(0));
        } catch (Exception e) {
            EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage()).action("GenericTransformer").build());
            errors.add(ConversionIssue.newError(ere));
            return null;
        }
        return bts;
    }

    protected synchronized List<Element> createXmlPathRecursively(Element parent, final ArrayList<String> steps, final int times, final List<Element> leafs) {
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

    public abstract void transformXmlToCen(Document document, BG0000Invoice invoice, final List<IConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException;

    public abstract void transformCenToXml(BG0000Invoice invoice, Document document, final List<IConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException;
}
