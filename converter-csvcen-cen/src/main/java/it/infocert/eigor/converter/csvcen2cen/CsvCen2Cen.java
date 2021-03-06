package it.infocert.eigor.converter.csvcen2cen;

import com.google.common.base.Charsets;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.conversion.ConversionRegistry;
import it.infocert.eigor.api.conversion.LookUpEnumConversion;
import it.infocert.eigor.api.conversion.converter.*;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.api.utils.IReflections;
import it.infocert.eigor.api.utils.Pair;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.enums.Iso31661CountryCodes;
import it.infocert.eigor.model.core.enums.Iso4217CurrenciesFundsCodes;
import it.infocert.eigor.model.core.enums.Untdid1001InvoiceTypeCode;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import it.infocert.eigor.model.core.model.structure.BtBgName;
import it.infocert.eigor.model.core.model.structure.CenStructure;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CsvCen2Cen implements ToCenConversion {

    private final CenStructure cenStructure;
    private final InvoiceUtils utils;

    private final static ConversionRegistry conversionRegistry = new ConversionRegistry(
            CountryNameToIso31661CountryCodeConverter.newConverter(),
            LookUpEnumConversion.newConverter(Iso31661CountryCodes.class),
            StringToJavaLocalDateConverter.newConverter("dd-MMM-yy" ),
            StringToJavaLocalDateConverter.newConverter("yyyy-MM-dd" ),
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
            StringToIdentifierConverter.newConverter()
    );

    private Logger log = LoggerFactory.getLogger(CsvCen2Cen.class);


    public CsvCen2Cen(IReflections reflections) {
        cenStructure = new CenStructure();
        utils = new InvoiceUtils(reflections);
    }

    @Override
    public ConversionResult<BG0000Invoice> convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {

        List<IConversionIssue> errors = new ArrayList<>();

        Iterable<CSVRecord> cenRecordsFromCsv = null;

        // try to parse the CEN CSV file.
        try {
            cenRecordsFromCsv = CSVFormat.RFC4180
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase(true)
                    .parse(new InputStreamReader(sourceInvoiceStream, Charsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stack<BTBG> stack = new Stack<>();
        stack.push(new BG0000Invoice());


        String bgbtIdFromCsv;
        String bgbtValueFromCsv;
        BtBgName btbgName;
        Class<? extends BTBG> btBgClass;
        for (CSVRecord cenRecord : cenRecordsFromCsv) {

            bgbtIdFromCsv = cenRecord.get("BG/BT" );
            bgbtValueFromCsv = cenRecord.get("Value" );
            log.trace("Current item from CSV: {} | {}" , bgbtIdFromCsv, bgbtValueFromCsv);


            // verifies that the name of the bgbt read from csv is a well formed name and that it is actually
            // a BG BTnode in the CEN structure.
            try {
                btbgName = BtBgName.parse(bgbtIdFromCsv);
            } catch (Exception e) {
                throw new SyntaxErrorInInvoiceFormatException(String.format("Record #%d refers to the mispelled CEN element '%s'." ,
                        cenRecord.getRecordNumber(),
                        bgbtIdFromCsv
                ));
            }
            btBgClass = utils.getBtBgByName(btbgName);
            if (btBgClass == null) throw
                    new SyntaxErrorInInvoiceFormatException("Unable to retrieve class for '" + bgbtIdFromCsv + "'" );


            // Instantiate the BTBG corresponding to the current CSV record.
            BTBG btbg = null;
            try {

                // A BG-XX can be instantiated...
                if (btBgClass.getSimpleName().toLowerCase().startsWith("bg" )) {

                    // BGs can be instantiated.
                    btbg = btBgClass.newInstance();

                    // now, keep in mind that bg cannot have a value!
                    if (bgbtValueFromCsv != null && !bgbtValueFromCsv.trim().isEmpty()) {
                        throw new SyntaxErrorInInvoiceFormatException(btbg.denomination() + " cannot have a value, has '" + bgbtValueFromCsv + "' instead." );
                    }

                    // A BT-XX should be instantiated through its constructor...
                } else {

                    // double chacks BThas only one single arg constructor

                    List<Constructor<?>> constructors = Arrays.stream(btBgClass.getConstructors())
                            .filter( c -> c.getParameterTypes().length == 1 )
                            .collect(Collectors.toList());

                    if (constructors.size() != 1) {
                        throw new IllegalArgumentException("Just one constructor with one argument expected, " + constructors.size() + " found instead." );
                    }
                    Constructor<?> constructor = constructors.get(0);

                    // tries to convert the String value read from the file in the type expected by the constructor
                    Class<?> constructorParamType = constructor.getParameterTypes()[0];
                    Object convert = null;
                    try {
                        if (bgbtValueFromCsv.matches("\\w*\\{.*}" )) {
                            final String currentBtValue = bgbtValueFromCsv;

                            final Object taken = Arrays.stream(constructorParamType.getEnumConstants())
                                    .filter(item -> currentBtValue.equals(((Object) item).toString()))
                                    .findFirst().orElse(null);

                            if (taken != null) {
                                convert = taken;
                            } else {
                                final String message = String.format("Cannot convert %s into an enum value of type %s", currentBtValue, constructorParamType.getSimpleName());
                                errors.add(ConversionIssue.newError(new EigorRuntimeException(
                                        message,
                                        ErrorCode.Location.CSVCEN_IN,
                                        ErrorCode.Action.HARDCODED_MAP,
                                        ErrorCode.Error.ILLEGAL_VALUE,
                                        Pair.of(ErrorMessage.SOURCEMSG_PARAM, message),
                                        Pair.of(ErrorMessage.OFFENDINGITEM_PARAM, currentBtValue)
                                )));
                            }
                        } else {
                            convert = conversionRegistry.convert(String.class, constructorParamType, bgbtValueFromCsv);
                        }
                        log.trace("Value from CSV: '{}' has been converted to argument of type '{}' with value '{}'." ,
                                String.valueOf(bgbtValueFromCsv),
                                convert != null ? convert.getClass().getName() : "<null>" ,
                                String.valueOf(convert));
                        // instantiate the BT
                        btbg = (BTBG) constructor.newInstance(convert);
                    } catch (IllegalArgumentException e) {
                        errors.add(ConversionIssue.newWarning(
                                new SyntaxErrorInInvoiceFormatException(String.format("Record #%d contains the item %s = '%s' that should be converted according to the CEN module to a '%s' but such transformation is unknown." ,
                                        cenRecord.getRecordNumber(),
                                        bgbtIdFromCsv,
                                        bgbtValueFromCsv,
                                        constructorParamType.getSimpleName()
                                ))));
                    }

                }
                log.trace("Successfully instantiated: '{}'." , btbg);

            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            if (btbg == null) {
                log.trace("Unable to instantiate item, skipping CSV record." );
                continue;
            }


            // Calculate the path of the BG where we're trying to add the newly created BTBG.
            // This is done because this information is in the stack and if we're not doing that now,
            // we'll loose that info.
            String pathWhereYouAreTryingToPlaceTheBtBg = "";
            for (int i = stack.size() - 1; i >= 0; i--) {
                pathWhereYouAreTryingToPlaceTheBtBg = pathWhereYouAreTryingToPlaceTheBtBg + "/" + stack.get(i);
            }
            pathWhereYouAreTryingToPlaceTheBtBg = pathWhereYouAreTryingToPlaceTheBtBg.replaceAll("/BG-0" , "/" ) + btbgName.toString();
            log.trace("Item will be placed at path '{}'." , pathWhereYouAreTryingToPlaceTheBtBg);

            // It search in the stack a BG that will accept the current BGBT.
            boolean found = false;
            do {
                BTBG parentBg = stack.pop();
                try {
                    boolean added = utils.addChild(parentBg, btbg);
                    if (added) {
                        log.trace("Item '{}' added as child of '{}'." , btbg, parentBg);
                        stack.push(parentBg);
                        if (btbg.denomination().toLowerCase().startsWith("bg" )) {
                            stack.push(btbg);
                        }
                        found = true;
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    found = false;
                }
            } while (!found && !stack.empty());


            // if we browsed the full stack without being able to add
            // the current BT, then that BTwas in the wrong position in the file.
            if (stack.empty()) {
                String pathWhereTheBtBgBelongs = cenStructure.findByName(btbgName).path();
                String umh = btbgName.toString();

                throw new SyntaxErrorInInvoiceFormatException(String.format("Record #%d tries to place a '%s' at '%s', but this element should be placed at '%s' instead." ,
                        cenRecord.getRecordNumber(),
                        umh,
                        pathWhereYouAreTryingToPlaceTheBtBg,
                        pathWhereTheBtBgBelongs
                ));
            }


        }

        // the topmost element in the stack is always the invoice.

        return new ConversionResult<BG0000Invoice>(errors, (BG0000Invoice) stack.get(0));
    }

    @Override
    public boolean support(String format) {
        return "csvcen".equals(format.toLowerCase().trim());
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>(Arrays.asList("csvcen" ));
    }

    @Override
    public String getMappingRegex() {
        return ".+";
    }

    @Override
    public String getName() {
        return "csvcen-cen";
    }

    @Override
    public void configure() throws ConfigurationException {
        // nothing to configure
    }

    public <E extends Enum<E>> void test(Class<E> t) {

    }
}
