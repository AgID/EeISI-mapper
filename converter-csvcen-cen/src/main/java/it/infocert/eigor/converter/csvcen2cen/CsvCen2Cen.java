package it.infocert.eigor.converter.csvcen2cen;

import com.google.common.base.Charsets;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.ToCenConversion;
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
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;


public class CsvCen2Cen implements ToCenConversion {

    private final CenStructure cenStructure;
    private final InvoiceUtils utils;

    public CsvCen2Cen() {
        cenStructure = new CenStructure();
        utils = new InvoiceUtils(new Reflections("it.infocert"));
    }

    @Override
    public BG0000Invoice convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {

        Iterable<CSVRecord> records = null;
        try {
            records = CSVFormat.RFC4180
                    .withHeader(Headers.class)
                    .parse(new InputStreamReader(sourceInvoiceStream, Charsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stack<BTBG> elements = new Stack<>();
        elements.push(new BG0000Invoice());

        boolean firstLine = false;
        for (CSVRecord record : records) {

            if(!firstLine){
                firstLine = true;
                continue;
            }

            String bgbtName = record.get(Headers.BGBT);
            String bgbtValue = record.get(Headers.Value);

            BtBgName btbgNnaammee = BtBgName.parse(bgbtName);

            Class<? extends BTBG> btBgClass = utils.getBtBgByName(btbgNnaammee);

            if(btBgClass == null) throw
                    new SyntaxErrorInInvoiceFormatException("Unable to retrieve class for '" + bgbtName + "'");

            BTBG btbgToAdd = null;
            try {
                if (btBgClass.getSimpleName().toLowerCase().startsWith("bg")) {
                    // BGs can be instantiated.
                    btbgToAdd = btBgClass.newInstance();
                } else {
                    // BTs should be instantiated with a value.


                    List<Constructor<?>> constructors = Arrays.stream(btBgClass.getConstructors()).filter(c -> c.getParameterCount() == 1).collect(Collectors.toList());
                    if (constructors.size() != 1) {
                        throw new IllegalArgumentException("Just one constructor with one argument expected, " + constructors.size() + " found instead.");
                    }

                    Constructor<?> constructor = constructors.get(0);

                    btbgToAdd = (BTBG) constructor.newInstance(bgbtValue);
                }

            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            if(btbgToAdd == null) throw new IllegalStateException("It was not possible to instantiate a BT/BG");

            boolean done = false;
            do {
                BTBG parentBg = elements.pop();
                try {
                    boolean added = utils.addChild(parentBg, btbgToAdd);
                    if(added) {
                        elements.push(parentBg);
                        if (btbgToAdd.denomination().toLowerCase().startsWith("bg")) {
                            elements.push(btbgToAdd);
                        }
                        done = true;
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    done = false;
                }
            } while (!done && !elements.empty());


        }
        return (BG0000Invoice) elements.get(0);


    }

    private String toPath(CenStructure.BtBgNode node) {
        String result = "";
        while (node != null) {
            result = "/" + node.getBtOrBg() + node.getNumber() + result;
            node = node.getParent();
        }
        return result;
    }

    @Override
    public boolean support(String format) {
        return "csvcen".equals(format.toLowerCase().trim());
    }


    public enum Headers {
        BGBT, BusinessTermName, Value, Remarks, Calculations
    }
}
