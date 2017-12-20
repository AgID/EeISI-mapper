package it.infocert.eigor.converter.csvcen2cen;

import com.amoerie.jstreams.Stream;
import com.amoerie.jstreams.functions.Filter;
import com.google.common.base.Charsets;
import it.infocert.eigor.api.*;
import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
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
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class CsvCen2Cen implements ToCenConversion {

    private final CenStructure cenStructure;
    private final InvoiceUtils utils;

    private final static ConversionRegistry conversionRegistry = new ConversionRegistry(
            new CountryNameToIso31661CountryCodeConverter(),
            new LookUpEnumConversion<>(Iso31661CountryCodes.class),
            new StringToJavaLocalDateConverter("dd-MMM-yy"),
            new StringToJavaLocalDateConverter("yyyy-MM-dd"),
            new StringToUntdid1001InvoiceTypeCodeConverter(),
            new LookUpEnumConversion<>(Untdid1001InvoiceTypeCode.class),
            new StringToIso4217CurrenciesFundsCodesConverter(),
            new LookUpEnumConversion<>(Iso4217CurrenciesFundsCodes.class),
            new StringToUntdid5305DutyTaxFeeCategoriesConverter(),
            new LookUpEnumConversion<>(Untdid5305DutyTaxFeeCategories.class),
            new StringToUnitOfMeasureConverter(),
            new StringToDoublePercentageConverter(),
            new StringToDoubleConverter(),
            new StringToStringConverter(),
            new StringToUntdid5189ChargeAllowanceDescriptionCodesConverter()
    );

    private Logger log = LoggerFactory.getLogger(CsvCen2Cen.class);


    public CsvCen2Cen(Reflections reflections) {
        cenStructure = new CenStructure();
        utils = new InvoiceUtils(reflections);
    }

    @Override
    public ConversionResult<BG0000Invoice> convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {

        List<IConversionIssue> errors = new ArrayList<>();

        Iterable<CSVRecord> cenRecordsFromCsv;

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

            bgbtIdFromCsv = cenRecord.get("BG/BT");
            bgbtValueFromCsv = cenRecord.get("Value");
            log.debug("Current item from CSV: {} | {}", bgbtIdFromCsv, bgbtValueFromCsv);


            // verifies that the name of the bgbt read from csv is a well formed name and that it is actually
            // a BG BTnode in the CEN structure.
            try {
                btbgName = BtBgName.parse(bgbtIdFromCsv);
            } catch (Exception e) {
                throw new SyntaxErrorInInvoiceFormatException(String.format("Record #%d refers to the mispelled CEN element '%s'.",
                        cenRecord.getRecordNumber(),
                        bgbtIdFromCsv
                ));
            }
            btBgClass = utils.getBtBgByName(btbgName);
            if (btBgClass == null) throw
                    new SyntaxErrorInInvoiceFormatException("Unable to retrieve class for '" + bgbtIdFromCsv + "'");


            // Instantiate the BTBG corresponding to the current CSV record.
            BTBG btbg = null;
            try {

                // A BG-XX can be instantiated...
                if (btBgClass.getSimpleName().toLowerCase().startsWith("bg")) {

                    // BGs can be instantiated.
                    btbg = btBgClass.newInstance();


                    // now, keep in mind that bg cannot have a value!
                    if (bgbtValueFromCsv != null && !bgbtValueFromCsv.trim().isEmpty()) {
                        throw new SyntaxErrorInInvoiceFormatException(btbg.denomination() + " cannot have a value, has '" + bgbtValueFromCsv + "' instead.");
                    }

                    // A BT-XX should be instantiated through its constructor...
                } else {

                    // double chacks BThas only one single arg constructor
                    List<Constructor<?>> constructors = Stream.create(Arrays.asList(btBgClass.getConstructors())).filter(new Filter<Constructor<?>>() {
                        @Override
                        public boolean apply(Constructor<?> c) {
                            return c.getParameterTypes().length == 1;
                        }
                    }).toList();
                    if (constructors.size() != 1) {
                        throw new IllegalArgumentException("Just one constructor with one argument expected, " + constructors.size() + " found instead.");
                    }
                    Constructor<?> constructor = constructors.get(0);

                    // tries to convert the String value read from the file in the type expected by the constructor
                    Class<?> constructorParamType = constructor.getParameterTypes()[0];
                    Object convert;
                    try {
                        convert = conversionRegistry.convert(String.class, constructorParamType, bgbtValueFromCsv);
                        log.info("{} - Value from CSV: '{}' has been converted to argument of type '{}' with value '{}'.",
                                String.valueOf(bgbtIdFromCsv),
                                String.valueOf(bgbtValueFromCsv),
                                convert != null ? convert.getClass().getName() : "<null>",
                                String.valueOf(convert));
                        // instantiate the BT
                        btbg = (BTBG) constructor.newInstance(convert);
                    } catch (IllegalArgumentException e) {
                        final SyntaxErrorInInvoiceFormatException ex = new SyntaxErrorInInvoiceFormatException(String.format("Record #%d contains the item %s = '%s' that should be converted according to the CEN module to a '%s' but such transformation is unknown.",
                                cenRecord.getRecordNumber(),
                                bgbtIdFromCsv,
                                bgbtValueFromCsv,
                                constructorParamType.getSimpleName()
                        ));
                        log.error(ex.getMessage(), e);
                        log.trace(e.getMessage(), e);
                        errors.add(ConversionIssue.newWarning(
                                ex));
                    }

                }
                log.trace("Successfully instantiated: '{}'.", btbg);

            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
            if (btbg == null) {
                log.trace("Unable to instantiate item, skipping CSV record.");
                continue;
            }


            // Calculate the path of the BG where we're trying to add the newly created BTBG.
            // This is done because this information is in the stack and if we're not doing that now,
            // we'll loose that info.
            String pathWhereYouAreTryingToPlaceTheBtBg = "";
            for (int i = stack.size() - 1; i >= 0; i--) {
                pathWhereYouAreTryingToPlaceTheBtBg = pathWhereYouAreTryingToPlaceTheBtBg + "/" + stack.get(i);
            }
            pathWhereYouAreTryingToPlaceTheBtBg = pathWhereYouAreTryingToPlaceTheBtBg.replaceAll("/BG-0", "/") + btbgName.toString();
            log.trace("Item will be placed at path '{}'.", pathWhereYouAreTryingToPlaceTheBtBg);

            // It search in the stack a BG that will accept the current BGBT.
            boolean found = false;
            do {
                BTBG parentBg = stack.pop();
                try {
                    boolean added = utils.addChild(parentBg, btbg);
                    if (added) {
                        log.trace("Item '{}' added as child of '{}'.", btbg, parentBg);
                        stack.push(parentBg);
                        if (btbg.denomination().toLowerCase().startsWith("bg")) {
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

                throw new SyntaxErrorInInvoiceFormatException(String.format("Record #%d tries to place a '%s' at '%s', but this element should be placed at '%s' instead.",
                        cenRecord.getRecordNumber(),
                        umh,
                        pathWhereYouAreTryingToPlaceTheBtBg,
                        pathWhereTheBtBgBelongs
                ));
            }


        }

        // the topmost element in the stack is always the invoice.

        return new ConversionResult<>(errors, (BG0000Invoice) stack.get(0));
    }

    @Override
    public boolean support(String format) {
        return "csvcen".equals(format.toLowerCase().trim());
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>(Arrays.asList("csvcen"));
    }

    @Override
    public String getMappingRegex() {
        return ".+";
    }

    @Override
    public String getName() {
        return "csvcen-cen";
    }

    @Override public void configure() throws ConfigurationException {
        // nothing to configure
    }
}
