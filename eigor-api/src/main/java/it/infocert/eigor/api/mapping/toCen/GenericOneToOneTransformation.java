package it.infocert.eigor.api.mapping.toCen;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Consumer;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.enums.*;
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
    private ConversionRegistry conversionRegistry;

    /**
     * Instantiates a new Generic one to one transformation.
     *
     * @param xPath    the Input invoice path
     * @param bgBtPath the CEN invoice path
     */
    public GenericOneToOneTransformation(String xPath, String bgBtPath, Reflections reflections) {
        this.xPath = xPath;
        this.bgBtPath = bgBtPath;
        this.reflections = reflections;
        conversionRegistry = new ConversionRegistry(
                new CountryNameToIso31661CountryCodeConverter(),
                new LookUpEnumConversion(Iso31661CountryCodes.class),
                new StringToJavaLocalDateConverter("dd-MMM-yy"),
                new StringToJavaLocalDateConverter("yyyy-MM-dd"),
                new StringToUntdid1001InvoiceTypeCodeConverter(),
                new LookUpEnumConversion(Untdid1001InvoiceTypeCode.class),
                new StringToIso4217CurrenciesFundsCodesConverter(),
                new LookUpEnumConversion(Iso4217CurrenciesFundsCodes.class),
                new StringToUntdid5305DutyTaxFeeCategoriesConverter(),
                new LookUpEnumConversion(Untdid5305DutyTaxFeeCategories.class),
                new StringToUnitOfMeasureConverter(),
                new LookUpEnumConversion(UnitOfMeasureCodes.class),
                new StringToDoubleConverter(),
                new StringToStringConverter()
        );
    }

    /**
     * Transform.
     *
     * @param document the document
     * @param invoice  the invoice
     */
    public void transform(Document document, BG0000Invoice invoice, final List<ConversionIssue> errors) throws SyntaxErrorInInvoiceFormatException {
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
                                                Object constructorParam = conversionRegistry.convert(String.class, paramType, item.getTextContent());
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