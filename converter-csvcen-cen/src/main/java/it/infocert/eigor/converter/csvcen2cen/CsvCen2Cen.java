package it.infocert.eigor.converter.csvcen2cen;

import com.google.common.base.Charsets;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.api.conversion.*;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import it.infocert.eigor.model.core.model.structure.BtBgName;
import it.infocert.eigor.model.core.model.structure.CenStructure;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class CsvCen2Cen implements ToCenConversion {

    private final CenStructure cenStructure;
    private final InvoiceUtils utils;
    private final ConversionRegistry conversionRegistry;

    public CsvCen2Cen() {

        cenStructure = new CenStructure();
        utils = new InvoiceUtils(new Reflections("it.infocert"));
        conversionRegistry = new ConversionRegistry(
                new StringToIso31661CountryCodesConverter(),
                new StringToJavaLocalDateConverter(DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.ENGLISH)),
                new StringToUntdid1001InvoiceTypeCodeConverter(),
                new StringToIso4217CurrenciesFundsCodesConverter(),
                new StringToUntdid5305DutyTaxFeeCategoriesConverter(),
                new StringToUnitOfMeasureConverter(),
                new StringToDoublePercentageConverter(),
                new StringToDoubleConverter(),
                new StringToStringConverter()
        );
    }

    @Override
    public BG0000Invoice convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {

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

            bgbtIdFromCsv = cenRecord.get("BG/BT");
            bgbtValueFromCsv = cenRecord.get("Value");



            // verifies that the name of the bgbt read from csv is a well formed name and that it is actually
            // a BG BT node in the CEN structure.
            try {
                btbgName = BtBgName.parse(bgbtIdFromCsv);
            } catch (Exception e) {
                throw new SyntaxErrorInInvoiceFormatException(String.format("Record #%d refers to the mispelled CEN element '%s'.",
                        cenRecord.getRecordNumber(),
                        bgbtIdFromCsv
                ));
            }
            btBgClass = utils.getBtBgByName(btbgName);
            if(btBgClass == null) throw
                    new SyntaxErrorInInvoiceFormatException("Unable to retrieve class for '" + bgbtIdFromCsv + "'");



            // Instantiate the BTBG corresponding to the current CSV record.
            BTBG btbg = null;
            try {

                // A BG-XX can be instantiated...
                if (btBgClass.getSimpleName().toLowerCase().startsWith("bg")) {
                    // BGs can be instantiated.
                    btbg = btBgClass.newInstance();

                // A BT-XX should be instantiated through its constructor...
                } else {

                    // double chacks BT has only one single arg constructor
                    List<Constructor<?>> constructors = Arrays.stream(btBgClass.getConstructors()).filter(c -> c.getParameterCount() == 1).collect(Collectors.toList());
                    if (constructors.size() != 1) {
                        throw new IllegalArgumentException("Just one constructor with one argument expected, " + constructors.size() + " found instead.");
                    }
                    Constructor<?> constructor = constructors.get(0);

                    // tries to convert the String value read from the file in the type expected by the constructor
                    Class<?> constructorParamType = constructor.getParameterTypes()[0];
                    Object convert = null;
                    try {
                        convert = conversionRegistry.convert(String.class, constructorParamType, bgbtValueFromCsv);
                    } catch (Exception e) {
                        throw new SyntaxErrorInInvoiceFormatException(String.format("Record #%d contains the item %s = '%s' that should be converted according to the CEN module to a '%s' but such transformation is unknown.",
                                cenRecord.getRecordNumber(),
                                bgbtIdFromCsv,
                                bgbtValueFromCsv,
                                constructorParamType.getSimpleName()
                        ));
                    }

                    // instantiate the BT
                    btbg = (BTBG) constructor.newInstance(convert);
                }

            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            if(btbg == null) throw new IllegalStateException("It was not possible to instantiate a BT/BG");


            // Calculate the path of the BG where we're trying to add the newly created BT/BG.
            // This is done because this information is in the stack and if we're not doing that now,
            // we'll loose that info.
            String pathWhereYouAreTryingToPlaceTheBtBg = "";
            for(int i=stack.size() - 1; i>=0; i--){
                pathWhereYouAreTryingToPlaceTheBtBg = pathWhereYouAreTryingToPlaceTheBtBg + "/" + stack.get(i);
            }
            pathWhereYouAreTryingToPlaceTheBtBg = pathWhereYouAreTryingToPlaceTheBtBg.replaceAll("/BG-0000", "/") + btbgName.toString();


            // It search in the stack a BG that will accept the current BG/BT.
            boolean found = false;
            do {
                BTBG parentBg = stack.pop();
                try {
                    boolean added = utils.addChild(parentBg, btbg);
                    if(added) {
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


            // if we browsed the full stack withouth being able to add
            // the current BT, then that BT was in the wrong position in the file.
            if(stack.empty()){
                String pathWhereTheBtBgBelongs = cenStructure.findByName( btbgName ).path();
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
        return (BG0000Invoice) stack.get(0);
    }

    @Override
    public boolean support(String format) {
        return "csvcen".equals(format.toLowerCase().trim());
    }

    @Override
    public Set<String> getSupportedFormats() {
        return new HashSet<>( Arrays.asList("csvcen") );
    }

}
