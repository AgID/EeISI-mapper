package it.infocert.eigor.converter.xmlcen2cen;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.ConverterUtils.NamingRules;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.conversion.LookUpEnumConversion;
import it.infocert.eigor.api.conversion.converter.*;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.enums.*;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

public class XmlCen2Cen extends AbstractToCenConverter {

    private Logger log = LoggerFactory.getLogger(XmlCen2Cen.class);
    private static final String FORMAT = "xmlcen";
    private final DefaultResourceLoader drl = new DefaultResourceLoader();
    private final EigorConfiguration configuration;
    private final InvoiceUtils utils;
    private static final ErrorCode.Action errorAction = ErrorCode.Action.CONFIGURED_MAP;
    private static final ErrorCode.Location callingLocation = ErrorCode.Location.XMLCEN_IN;
    private static final ConversionRegistry conversionRegistry = new ConversionRegistry(
            CountryNameToIso31661CountryCodeConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso31661CountryCodes.class),
            StringToJavaLocalDateConverter.newConverter("dd-MMM-yy"),
            StringToJavaLocalDateConverter.newConverter("yyyy-MM-dd"),
            StringToUntdid1001InvoiceTypeCodeConverter.newConverter(),
            LookUpEnumConversion.newConverter(Untdid1001InvoiceTypeCode.class),
            StringToIso4217CurrenciesFundsCodesConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso4217CurrenciesFundsCodes.class),
            StringToUntdid5305DutyTaxFeeCategoriesConverter.newConverter(),
            LookUpEnumConversion.newConverter(Untdid5305DutyTaxFeeCategories.class),
            StringToUnitOfMeasureConverter.newConverter(),
            StringToBigDecimalPercentageConverter.newConverter(),
            StringToBigDecimalConverter.newConverter(),
            StringToStringConverter.newConverter(),
            StringToUntdid5189ChargeAllowanceDescriptionCodesConverter.newConverter(),
            StringToIdentifierConverter.newConverter(),
            StringToUntdid2005DateTimePeriodQualifiers.newConverter(),
            StringToUntdid4461PaymentMeansCode.newConverter(),
            LookUpEnumConversion.newConverter(Untdid7161SpecialServicesCodes.class)
    );

    private static final String ONE2ONE_MAPPING_PATH = "eigor.converter.xmlcen-cen.mapping.one-to-one";
    private static final String MANY2ONE_MAPPING_PATH = "eigor.converter.xmlcen-cen.mapping.many-to-one";
    private static final String ONE2MANY_MAPPING_PATH = "eigor.converter.xmlcen-cen.mapping.one-to-many";
    private static final String CUSTOM_CONVERTER_MAPPING_PATH = "eigor.converter.xmlcen-cen.mapping.custom";
    private static final HashSet<String> specialBT = new HashSet<>();

    private static final NamingRules namingRules = new NamingRules("xmlcen-cen");

    private XSDValidator xsdValidator;
    private SchematronValidator schematronValidator;

    public XmlCen2Cen(IReflections reflections, EigorConfiguration configuration) {
        super(reflections, conversionRegistry, configuration, ErrorCode.Location.XMLCEN_IN);
        this.configuration = checkNotNull(configuration);
        this.utils = new InvoiceUtils(reflections);
        specialBT.add("BT-48");
        specialBT.add("BT-63");
        specialBT.add("BT-90");
        specialBT.add("BT-128");
        specialBT.add("BT-137");
        specialBT.add("BT-138");
    }

    @Override
    public void configure() throws ConfigurationException {
        super.configure();

        xsdValidator = namingRules.getXsdValidatorFromConfigOrFail("xsd", ErrorCode.Location.XMLCEN_IN, this.configuration, this.drl);
        schematronValidator = namingRules.getSchematronFromConfigOrFail("schematron", ErrorCode.Location.XMLCEN_IN, this.configuration, this.drl);

        configurableSupport.configure();
    }

    @Override
    public ConversionResult<BG0000Invoice> convert(InputStream sourceInvoiceStream) {

        configurableSupport.checkConfigurationOccurred();

        List<IConversionIssue> errors = new ArrayList<>();
        InputStream clonedInputStream = null;

        // xsd validation♠`
        try {
            byte[] bytes = ByteStreams.toByteArray(sourceInvoiceStream);
            clonedInputStream = new ByteArrayInputStream(bytes);

            List<IConversionIssue> validationErrors = new ArrayList<>();

            validationErrors.addAll(xsdValidator.validate(bytes));
            if (validationErrors.isEmpty()) {
                log.info("Xsd validation successful!");
            }


            validationErrors.addAll(schematronValidator.validate(bytes));
            if (validationErrors.isEmpty()) {
                log.info("Xsd validation successful!");
            }


            errors.addAll(validationErrors);
        } catch (IOException | IllegalArgumentException e) {
            errors.add(ConversionIssue.newWarning(e, "Error during validation", ErrorCode.Location.XMLCEN_IN, ErrorCode.Action.GENERIC, ErrorCode.Error.INVALID, Pair.of(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())));
        }

        org.jdom2.Document document;
        try {
            document = getDocument(clonedInputStream);
        } catch (RuntimeException e) {
            throw new EigorRuntimeException(new ErrorMessage(e.getMessage(), ErrorCode.Location.XMLCEN_IN, ErrorCode.Action.GENERIC, ErrorCode.Error.INVALID), e);
        }

        ConversionResult<BG0000Invoice> result = convertXmlToCen(document, errors);
        applyCustomMapping(result.getResult(), document, errors);

        return result;
    }

    private ConversionResult<BG0000Invoice> convertXmlToCen(Document document, List<IConversionIssue> errors) {
        BG0000Invoice invoice = new BG0000Invoice();

        final Element root = document.getRootElement();
        traverseTree(root, invoice, errors);

        return new ConversionResult<>(errors, invoice);
    }

    private void traverseTree(Element root, BTBG bg, List<IConversionIssue> errors) {
        final List<Element> children = root.getChildren();
        children.forEach(child -> {
            Class<? extends BTBG> btBgByName = utils.getBtBgByName(child.getName());
            if (child.getName().startsWith("BT-")) {
                //If the element is a leaf, build it and add it to the bg
                Optional<Constructor<?>> cons = Arrays.stream(btBgByName.getConstructors())
                        //Ignore Identifier and FileReference, custom mappings will handle them
                        .filter(constructor -> Identifier.class.equals(constructor.getParameterTypes()[0]) ||
                                FileReference.class.equals(constructor.getParameterTypes()[0]))
                        .findFirst();
                if (!cons.isPresent()) {
                    try {
                        utils.addChild(bg, buildBT(btBgByName, child, errors));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error(e.getMessage(), e);
                        errors.add(ConversionIssue.newError(e, e.getMessage(), callingLocation, errorAction, ErrorCode.Error.INVALID));
                    }
                } else {
                    //Manage specific BTs with Identifier
                    if (specialBT.contains(child.getName())) {
                        BTBG bt;
                        try {
                            bt = (BTBG) cons.get().newInstance(new Identifier(child.getValue()));
                            utils.addChild(bg, bt);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            log.error(e.getMessage(), e);
                            errors.add(ConversionIssue.newError(e, e.getMessage(), callingLocation, errorAction, ErrorCode.Error.INVALID));
                        }
                    }
                }
            } else {
                //If it's not a leaf is a node, then just create the node and traverse the subtree
                Stream.of(btBgByName.getConstructors())
                        .findFirst()
                        .ifPresent(c -> {
                            try {
                                final BTBG btbg = (BTBG) c.newInstance();
                                utils.addChild(bg, btbg);
                                traverseTree(child, btbg, errors);
                            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                                log.error(e.getMessage(), e);
                                errors.add(ConversionIssue.newError(e, e.getMessage(), callingLocation, errorAction, ErrorCode.Error.INVALID));
                            }
                        });
            }
        });
    }

    @Override
    protected String getOne2OneMappingPath() {
        return configuration.getMandatoryString(ONE2ONE_MAPPING_PATH);
    }

    @Override
    protected String getMany2OneMappingPath() {
        return configuration.getMandatoryString(MANY2ONE_MAPPING_PATH);
    }

    @Override
    protected String getOne2ManyMappingPath() {
        return configuration.getMandatoryString(ONE2MANY_MAPPING_PATH);
    }

    @Override
    protected String getCustomMappingPath() {
        return CUSTOM_CONVERTER_MAPPING_PATH;
    }

    @Override
    public boolean support(String format) {
        if (format == null) {
            log.error("NULL FORMAT");
            return false;
        }
        return FORMAT.equals(format.toLowerCase().trim());
    }

    @Override
    public Set<String> getSupportedFormats() {
        return Sets.newHashSet(FORMAT);
    }

    @Override
    public String getMappingRegex() {
        return "(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?(/(BG)[0-9]{4})?/(BT)[0-9]{4}(-[0-9]{1})?";
    }

    @Override
    public String getName() {
        return "converter-xmlcen-cen";
    }

    private void applyCustomMapping(BG0000Invoice invoice, org.jdom2.Document document, List<IConversionIssue> errors) {
        List<CustomMapping<org.jdom2.Document>> customMappings = CustomMappingLoader.getSpecificTypeMappings(super.getCustomMapping());

        for (CustomMapping<Document> customMapping : customMappings) {
            customMapping.map(invoice, document, errors, ErrorCode.Location.XMLCEN_IN, null);
        }
    }

    private BTBG buildBT(Class<? extends BTBG> btBgByName, Element child, List<IConversionIssue> errors) {
        Constructor<?>[] constructors = btBgByName.getConstructors();
        final Object[] constructorParam = new Object[]{null};
        final ArrayList<BTBG> bt = new ArrayList<>(1);
        Consumer<Constructor<?>> k = constructor -> {
            try {
                if (constructor.getParameterTypes().length == 0) {
                    bt.add((BTBG) constructor.newInstance());
                } else {
                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    List<Class<?>> classes = Arrays.asList(parameterTypes);
                    Stream<Class<?>> classes1 = classes.stream();
                    classes1.forEach(paramType -> {
                        try {
                            constructorParam[0] = conversionRegistry.convert(String.class, paramType, child.getValue());
                            bt.add((BTBG) constructor.newInstance(constructorParam[0]));
                        } catch (IllegalArgumentException e) {
                            errors.add(ConversionIssue.newError(e, e.getMessage(), callingLocation, errorAction, ErrorCode.Error.ILLEGAL_VALUE));
                        } catch (InstantiationException | IllegalAccessException e) {
                            log.error(e.getMessage(), e);
                            errors.add(ConversionIssue.newError(e, e.getMessage(), callingLocation, errorAction, ErrorCode.Error.INVALID));
                        } catch (InvocationTargetException e) {
                            String message = constructorParam[0] == null ?
                                    String.format("%s - Constructor parameter conversion yielded null for %s with value %s",
                                            child.getName(),
                                            paramType.getSimpleName(),
                                            child.getValue()
                                    )
                                    : e.getClass().getSimpleName();
                            log.error(e.getMessage() == null ?
                                            message
                                            : e.getMessage()
                                    , e);
                            errors.add(ConversionIssue.newError(e, message, callingLocation, errorAction, ErrorCode.Error.INVALID));
                        }
                    });
                }
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
                errors.add(ConversionIssue.newError(e, e.getMessage(), callingLocation, errorAction, ErrorCode.Error.INVALID));
            }
        };
        Stream.of(constructors).forEach(k);
        return bt.get(0);
    }

}
