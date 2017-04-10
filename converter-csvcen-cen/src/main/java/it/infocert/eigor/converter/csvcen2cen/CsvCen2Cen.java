package it.infocert.eigor.converter.csvcen2cen;

import com.google.common.base.Charsets;
import it.infocert.eigor.api.SyntaxErrorInInvoiceFormatException;
import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.structure.CenStructure;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class CsvCen2Cen implements ToCenConversion {

    public enum Headers {
        BGBT, BusinessTermName, Value, Remarks, Calculations
    }

    @Override public BG0000Invoice convert(InputStream sourceInvoiceStream) throws SyntaxErrorInInvoiceFormatException {

        CenStructure cenStructure = new CenStructure();
        InvoiceUtils utils = new InvoiceUtils( new Reflections("it.infocert") );
        BG0000Invoice result = new BG0000Invoice();

        Iterable<CSVRecord> records = null;
        try {
            records = CSVFormat.RFC4180
                    .withHeader(Headers.class)
                    .parse( new InputStreamReader( sourceInvoiceStream, Charsets.UTF_8 ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (CSVRecord record : records) {
            String bgbt = record.get(Headers.BGBT);

            CenStructure.BtBgNode node = cenStructure.findByName(bgbt);

            if(node.isBg()){
                String path = toPath(node);
                utils.ensurePathExists(path, result);
            }

        }


        return result;

    }

    private String toPath(CenStructure.BtBgNode node) {
        String result = "";
        while(node != null){
            result = "/" + node.getBtOrBg() + node.getNumber() + result;
            node = node.getParent();
        }
        return result;
    }


    @Override public boolean support(String format) {
        return "csvcen".equals( format.toLowerCase().trim() );
    }
}
